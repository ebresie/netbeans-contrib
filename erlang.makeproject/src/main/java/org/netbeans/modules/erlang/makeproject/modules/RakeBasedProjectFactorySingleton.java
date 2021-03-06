/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.erlang.makeproject.modules;

import org.netbeans.modules.erlang.makeproject.spi.support.RakeBasedProjectType;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.modules.erlang.makeproject.spi.support.RakeBasedProjectType;
import org.netbeans.modules.erlang.makeproject.spi.support.RakeProjectHelper;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Singleton {@link ProjectFactory} implementation which handles all Ant-based
 * projects by delegating some functionality to registered Ant project types.
 * @author Jesse Glick
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.spi.project.ProjectFactory.class)
public final class RakeBasedProjectFactorySingleton implements ProjectFactory {
    
    public static final String PROJECT_XML_PATH = "nbproject/project.xml"; // NOI18N

    public static final String PROJECT_NS = "http://www.netbeans.org/ns/project/1"; // NOI18N
    
    /** Construct the singleton. */
    public RakeBasedProjectFactorySingleton() {}
    
    private static final Map<Project,Reference<RakeProjectHelper>> project2Helper = new WeakHashMap<Project,Reference<RakeProjectHelper>>();
    private static final Map<RakeProjectHelper,Reference<Project>> helper2Project = new WeakHashMap<RakeProjectHelper,Reference<Project>>();
    private static final Map<RakeBasedProjectType,List<Reference<RakeProjectHelper>>> type2Projects = new HashMap<RakeBasedProjectType,List<Reference<RakeProjectHelper>>>(); //for second part of #42738
    private static final Lookup.Result<RakeBasedProjectType> antBasedProjectTypes;
    private static Map<String,RakeBasedProjectType> antBasedProjectTypesByType = null;
    static {
        antBasedProjectTypes = Lookup.getDefault().lookupResult(RakeBasedProjectType.class);
        antBasedProjectTypes.addLookupListener(new LookupListener() {
            public void resultChanged(LookupEvent ev) {
                synchronized (RakeBasedProjectFactorySingleton.class) {
                    Set<RakeBasedProjectType> oldTypes = type2Projects.keySet();
                    Set<RakeBasedProjectType> removed  = new HashSet<RakeBasedProjectType>(oldTypes);
                    
                    removed.removeAll(antBasedProjectTypes.allInstances());
                    
                    antBasedProjectTypesRemoved(removed);
                    
                    antBasedProjectTypesByType = null;
                }
            }
        });
    }
    
    private static void antBasedProjectTypesRemoved(Set<RakeBasedProjectType> removed) {
        for (RakeBasedProjectType type : removed) {
            List<Reference<RakeProjectHelper>> projects = type2Projects.get(type);
            if (projects != null) {
                for (Reference<RakeProjectHelper> r : projects) {
                    RakeProjectHelper helper = r.get();
                    if (helper != null) {
                        helper.notifyDeleted();
                    }
                }
            }
            type2Projects.remove(type);
        }
    }
    
    private static synchronized RakeBasedProjectType findRakeBasedProjectType(String type) {
        if (antBasedProjectTypesByType == null) {
            antBasedProjectTypesByType = new HashMap<String,RakeBasedProjectType>();
            // No need to synchronize similar calls since this is called only inside
            // ProjectManager.mutex. However dkonecny says that allInstances can
            // trigger a LookupEvent which would clear antBasedProjectTypesByType,
            // so need to initialize that later; and who knows then Lookup changes
            // might be fired.
            for (RakeBasedProjectType abpt : antBasedProjectTypes.allInstances()) {
                antBasedProjectTypesByType.put(abpt.getType(), abpt);
            }
        }
        return antBasedProjectTypesByType.get(type);
    }
    
    public boolean isProject(FileObject dir) {
        File dirF = FileUtil.toFile(dir);
        if (dirF == null) {
            return false;
        }
        // Just check whether project.xml exists. Do not attempt to parse it, etc.
        // Do not use FileObject.getFileObject since that may load other sister files.
        File projectXmlF = new File(new File(dirF, "nbproject"), "project.xml"); // NOI18N
        return projectXmlF.isFile();
    }
    
    public Project loadProject(FileObject projectDirectory, ProjectState state) throws IOException {
        if (FileUtil.toFile(projectDirectory) == null) {
            return null;
        }
        FileObject projectFile = projectDirectory.getFileObject(PROJECT_XML_PATH);
        //#54488: Added check for virtual
        if (projectFile == null || !projectFile.isData() || projectFile.isVirtual()) {
            return null;
        }
        File projectDiskFile = FileUtil.toFile(projectFile);
        //#63834: if projectFile exists and projectDiskFile does not, do nothing:
        if (projectDiskFile == null) {
            return null;
        }
        Document projectXml;
        try {
            projectXml = XMLUtil.parse(new InputSource(projectDiskFile.toURI().toString()), false, true, Util.defaultErrorHandler(), null);
        } catch (SAXException e) {
            IOException ioe = (IOException) new IOException(projectDiskFile + ": " + e.toString()).initCause(e);
            Exceptions.attachLocalizedMessage(ioe,NbBundle.getMessage(RakeBasedProjectFactorySingleton.class,
                                                                        "RakeBasedProjectFactorySingleton.parseError",
                                                                        projectDiskFile.getAbsolutePath(), e.getMessage()));
            throw ioe;
        }
        Element projectEl = projectXml.getDocumentElement();
        if (!"project".equals(projectEl.getLocalName()) || !PROJECT_NS.equals(projectEl.getNamespaceURI())) { // NOI18N
            return null;
        }
        Element typeEl = Util.findElement(projectEl, "type", PROJECT_NS); // NOI18N
        if (typeEl == null) {
            return null;
        }
        String type = Util.findText(typeEl);
        if (type == null) {
            return null;
        }
        RakeBasedProjectType provider = findRakeBasedProjectType(type);
        if (provider == null) {
            return null;
        }
        RakeProjectHelper helper = HELPER_CALLBACK.createHelper(projectDirectory, projectXml, state, provider);
        Project project = provider.createProject(helper);
        project2Helper.put(project, new WeakReference<RakeProjectHelper>(helper));
        helper2Project.put(helper, new WeakReference<Project>(project));
        List<Reference<RakeProjectHelper>> l = type2Projects.get(provider);
        
        if (l == null) {
            type2Projects.put(provider, l = new ArrayList<Reference<RakeProjectHelper>>());
        }
        
        l.add(new WeakReference<RakeProjectHelper>(helper));
        
        return project;
    }
    
    public void saveProject(Project project) throws IOException, ClassCastException {
        Reference<RakeProjectHelper> helperRef = project2Helper.get(project);
        if (helperRef == null) {
            throw new ClassCastException(project.getClass().getName());
        }
        RakeProjectHelper helper = helperRef.get();
        assert helper != null : "RakeProjectHelper collected for " + project;
        HELPER_CALLBACK.save(helper);
    }
    
    /**
     * Get the project corresponding to a helper.
     * For use from {@link RakeProjectHelper}.
     * @param helper an Ant project helper object
     * @return the corresponding project
     */
    public static Project getProjectFor(RakeProjectHelper helper) {
        Reference<Project> projectRef = helper2Project.get(helper);
        assert projectRef != null : "Found a Project reference for " + helper;
        Project p = projectRef.get();
        assert p != null : "Found a non-null Project for " + helper;
        return p;
    }
    
    /**
     * Get the helper corresponding to a project.
     * For use from {@link ProjectGenerator}.
     * @param project an Ant-based project
     * @return the corresponding Ant project helper object, or null if it is unknown
     */
    public static RakeProjectHelper getHelperFor(Project p) {
        Reference<RakeProjectHelper> helperRef = project2Helper.get(p);
        return helperRef != null ? helperRef.get() : null;
    }
    
    /**
     * Callback to create and access RakeProjectHelper objects from outside its package.
     */
    public interface RakeProjectHelperCallback {
        RakeProjectHelper createHelper(FileObject dir, Document projectXml, ProjectState state, RakeBasedProjectType type);
        void save(RakeProjectHelper helper) throws IOException;
    }
    /** Defined in RakeProjectHelper's static initializer. */
    public static RakeProjectHelperCallback HELPER_CALLBACK;
    static {
        Class<?> c = RakeProjectHelper.class;
        try {
            Class.forName(c.getName(), true, c.getClassLoader());
        } catch (ClassNotFoundException e) {
            assert false : e;
        }
        assert HELPER_CALLBACK != null;
    }
    
}
