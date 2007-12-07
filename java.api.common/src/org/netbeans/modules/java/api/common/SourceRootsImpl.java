/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.java.api.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.text.MessageFormat;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;
import org.openide.util.Mutex;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.modules.java.api.common.ant.UpdateHelper;

/**
 * This class represents a project source roots. It is used to obtain roots as Ant properties, FileObject's
 * or URLs.
 * @author Tomas Zezula
 */
final class SourceRootsImpl implements SourceRoots {

    private final UpdateHelper helper;
    private final PropertyEvaluator evaluator;
    private final ReferenceHelper refHelper;
    private final String projectConfigurationNamespace;
    private final String elementName;
    private final String newRootNameTemplate;
    private List<String> sourceRootProperties;
    private List<String> sourceRootNames;
    private List<FileObject> sourceRoots;
    private List<URL> sourceRootURLs;
    private final PropertyChangeSupport support;
    private final ProjectMetadataListener listener;
    private final boolean isTest;
    private final File projectDir;

    public SourceRootsImpl(UpdateHelper helper, PropertyEvaluator evaluator, ReferenceHelper refHelper,
            String projectConfigurationNamespace, String elementName, boolean isTest, String newRootNameTemplate) {
        assert helper != null;
        assert evaluator != null;
        assert refHelper != null;
        assert projectConfigurationNamespace != null;
        assert elementName != null;
        assert newRootNameTemplate != null;

        this.helper = helper;
        this.evaluator = evaluator;
        this.refHelper = refHelper;
        this.projectConfigurationNamespace = projectConfigurationNamespace;
        this.elementName = elementName;
        this.isTest = isTest;
        this.newRootNameTemplate = newRootNameTemplate;
        this.projectDir = FileUtil.toFile(this.helper.getAntProjectHelper().getProjectDirectory());
        this.support = new PropertyChangeSupport(this);
        this.listener = new ProjectMetadataListener();
        this.evaluator.addPropertyChangeListener(WeakListeners.propertyChange(this.listener, this.evaluator));
        this.helper.getAntProjectHelper().addAntProjectListener(
                WeakListeners.create(AntProjectListener.class, this.listener, this.helper));
    }


    public String[] getRootNames() {
        return ProjectManager.mutex().readAccess(new Mutex.Action<String[]>() {
            public String[] run() {
                synchronized (SourceRootsImpl.this) {
                    if (sourceRootNames == null) {
                        readProjectMetadata();
                    }
                }
                return sourceRootNames.toArray(new String[sourceRootNames.size()]);
            }
        });
    }

    public String[] getRootProperties() {
        return ProjectManager.mutex().readAccess(new Mutex.Action<String[]>() {
            public String[] run() {
                synchronized (SourceRootsImpl.this) {
                    if (sourceRootProperties == null) {
                        readProjectMetadata();
                    }
                    return sourceRootProperties.toArray(new String[sourceRootProperties.size()]);
                }
            }
        });
    }

    public FileObject[] getRoots() {
        return ProjectManager.mutex().readAccess(new Mutex.Action<FileObject[]>() {
                public FileObject[] run() {
                    synchronized (this) {
                        // local caching
                        if (sourceRoots == null) {
                            String[] srcProps = getRootProperties();
                            List<FileObject> result = new ArrayList<FileObject>();
                            for (String p : srcProps) {
                                String prop = evaluator.getProperty(p);
                                if (prop != null) {
                                    FileObject f = helper.getAntProjectHelper().resolveFileObject(prop);
                                    if (f == null) {
                                        continue;
                                    }
                                    if (FileUtil.isArchiveFile(f)) {
                                        f = FileUtil.getArchiveRoot(f);
                                    }
                                    result.add(f);
                                }
                            }
                            sourceRoots = Collections.unmodifiableList(result);
                        }
                    }
                    return sourceRoots.toArray(new FileObject[sourceRoots.size()]);
                }
        });
    }

    public URL[] getRootURLs() {
        return ProjectManager.mutex().readAccess(new Mutex.Action<URL[]>() {
            public URL[] run() {
                synchronized (this) {
                    // local caching
                    if (sourceRootURLs == null) {
                        List<URL> result = new ArrayList<URL>();
                        for (String srcProp : getRootProperties()) {
                            String prop = evaluator.getProperty(srcProp);
                            if (prop != null) {
                                File f = helper.getAntProjectHelper().resolveFile(prop);
                                try {
                                    URL url = f.toURI().toURL();
                                    if (!f.exists()) {
                                        url = new URL(url.toExternalForm() + "/"); // NOI18N
                                    } else if (f.isFile()) {
                                        // file cannot be a source root (archives are not supported as source roots).
                                        continue;
                                    }
                                    assert url.toExternalForm().endsWith("/") : "#90639 violation for " + url + "; "
                                            + f + " exists? " + f.exists() + " dir? " + f.isDirectory()
                                            + " file? " + f.isFile();
                                    result.add(url);
                                } catch (MalformedURLException e) {
                                    Exceptions.printStackTrace(e);
                                }
                            }
                        }
                        sourceRootURLs = Collections.unmodifiableList(result);
                    }
                }
                return sourceRootURLs.toArray(new URL[sourceRootURLs.size()]);
            }
        });
    }

    private Map<URL, String> getRootsToProps() {
        return ProjectManager.mutex().readAccess(new Mutex.Action<Map<URL, String>>() {
            public Map<URL, String> run() {
                Map<URL, String> result = new HashMap<URL, String>();
                for (String srcProp : getRootProperties()) {
                    String prop = evaluator.getProperty(srcProp);
                    if (prop != null) {
                        File f = helper.getAntProjectHelper().resolveFile(prop);
                        try {
                            URL url = f.toURI().toURL();
                            if (!f.exists()) {
                                url = new URL(url.toExternalForm() + "/"); // NOI18N
                            } else if (f.isFile()) {
                                // file cannot be a source root (archives are not supported as source roots).
                                continue;
                            }
                            assert url.toExternalForm().endsWith("/") : "#90639 violation for " + url + "; "
                                    + f + " exists? " + f.exists() + " dir? " + f.isDirectory()
                                    + " file? " + f.isFile();
                            result.put(url, srcProp);
                        } catch (MalformedURLException e) {
                            Exceptions.printStackTrace(e);
                        }
                    }
                }
                return result;
            }
        });
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }


    public void putRoots(final URL[] roots, final String[] labels) {
        ProjectManager.mutex().writeAccess(
                new Runnable() {
                    public void run() {
                        Map<URL, String> oldRoots2props = getRootsToProps();
                        Map<URL, String> newRoots2lab = new HashMap<URL, String>();
                        for (int i = 0; i < roots.length; i++) {
                            newRoots2lab.put(roots[i], labels[i]);
                        }
                        Element cfgEl = helper.getPrimaryConfigurationData(true);
                        NodeList nl = cfgEl.getElementsByTagNameNS(projectConfigurationNamespace, elementName);
                        assert nl.getLength() == 1 : "Illegal project.xml"; //NOI18N
                        Element ownerElement = (Element) nl.item(0);
                        // remove all old roots
                        NodeList rootsNodes =
                                ownerElement.getElementsByTagNameNS(projectConfigurationNamespace, "root");    //NOI18N
                        while (rootsNodes.getLength() > 0) {
                            Element root = (Element) rootsNodes.item(0);
                            ownerElement.removeChild(root);
                        }
                        // remove all unused root properties
                        List<URL> newRoots = Arrays.asList(roots);
                        Map<URL, String> propsToRemove = new HashMap<URL, String>(oldRoots2props);
                        propsToRemove.keySet().removeAll(newRoots);
                        EditableProperties props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                        props.keySet().removeAll(propsToRemove.values());
                        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
                        // add the new roots
                        Document doc = ownerElement.getOwnerDocument();
                        oldRoots2props.keySet().retainAll(newRoots);
                        for (URL newRoot : newRoots) {
                            String rootName = oldRoots2props.get(newRoot);
                            if (rootName == null) {
                                // root is new generate property for it
                                props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                                String[] names = newRoot.getPath().split("/");  //NOI18N
                                rootName = MessageFormat.format(
                                        newRootNameTemplate, new Object[] {names[names.length - 1], ""}); // NOI18N
                                int rootIndex = 1;
                                while (props.containsKey(rootName)) {
                                    rootIndex++;
                                    rootName = MessageFormat.format(
                                            newRootNameTemplate, new Object[] {names[names.length - 1], rootIndex});
                                }
                                File f = FileUtil.normalizeFile(new File(URI.create(newRoot.toExternalForm())));
                                File projDir = FileUtil.toFile(helper.getAntProjectHelper().getProjectDirectory());
                                String path = f.getAbsolutePath();
                                String prjPath = projDir.getAbsolutePath() + File.separatorChar;
                                if (path.startsWith(prjPath)) {
                                    path = path.substring(prjPath.length());
                                } else {
                                    path = refHelper.createForeignFileReference(
                                            f, JavaProjectConstants.SOURCES_TYPE_JAVA);
                                    props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                                }
                                props.put(rootName, path);
                                helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
                            }
                            Element newRootNode = doc.createElementNS(projectConfigurationNamespace, "root"); //NOI18N
                            newRootNode.setAttribute("id", rootName);    //NOI18N
                            String label = newRoots2lab.get(newRoot);
                            if (label != null
                                    && label.length() > 0
                                    && !label.equals(getRootDisplayName(null, rootName))) { //NOI18N
                                newRootNode.setAttribute("name", label); //NOI18N
                            }
                            ownerElement.appendChild(newRootNode);
                        }
                        helper.putPrimaryConfigurationData(cfgEl, true);
                    }
                }
        );
    }

    public String getRootDisplayName(String rootName, String propName) {
        if (rootName == null || rootName.length() == 0) {
            // if the prop is src.dir use the default name
            if (isTest && "test.src.dir".equals(propName)) { //NOI18N
                rootName = DEFAULT_TEST_LABEL;
            } else if (!isTest && "src.dir".equals(propName)) { //NOI18N
                rootName = DEFAULT_SOURCE_LABEL;
            } else {
                // if the name is not given, it should be either a relative path in the project dir
                // or absolute path when the root is not under the project dir
                String propValue = evaluator.getProperty(propName);
                File sourceRoot = propValue == null ? null : helper.getAntProjectHelper().resolveFile(propValue);
                rootName = createInitialDisplayName(sourceRoot);
            }
        }
        return rootName;
    }

    public String createInitialDisplayName(File sourceRoot) {
        String rootName;
        if (sourceRoot != null) {
            String srPath = sourceRoot.getAbsolutePath();
            String pdPath = projectDir.getAbsolutePath() + File.separatorChar;
            if (srPath.startsWith(pdPath)) {
                rootName = srPath.substring(pdPath.length());
            } else {
                rootName = sourceRoot.getAbsolutePath();
            }
        } else {
            rootName = isTest ? DEFAULT_TEST_LABEL : DEFAULT_SOURCE_LABEL;
        }
        return rootName;
    }

    public boolean isTest() {
        return isTest;
    }

    private void resetCache(boolean isXMLChange, String propName) {
        boolean fire = false;
        synchronized (this) {
            // in case of change reset local cache
            if (isXMLChange) {
                sourceRootProperties = null;
                sourceRootNames = null;
                sourceRoots = null;
                sourceRootURLs = null;
                fire = true;
            } else if (propName == null || (sourceRootProperties != null && sourceRootProperties.contains(propName))) {
                sourceRoots = null;
                sourceRootURLs = null;
                fire = true;
            }
        }
        if (fire) {
            if (isXMLChange) {
                support.firePropertyChange(PROP_ROOT_PROPERTIES, null, null);
            }
            support.firePropertyChange(PROP_ROOTS, null, null);
        }
    }

    private void readProjectMetadata() {
        Element cfgEl = helper.getPrimaryConfigurationData(true);
        NodeList nl = cfgEl.getElementsByTagNameNS(projectConfigurationNamespace, elementName);
        assert nl.getLength() == 0 || nl.getLength() == 1 : "Illegal project.xml"; //NOI18N
        List<String> rootProps = new ArrayList<String>();
        List<String> rootNames = new ArrayList<String>();
        // it can be 0 in the case when the project is created by J2SEProjectGenerator and not yet customized
        if (nl.getLength() == 1) {
            NodeList roots =
                    ((Element) nl.item(0)).getElementsByTagNameNS(projectConfigurationNamespace, "root"); //NOI18N
            for (int i = 0; i < roots.getLength(); i++) {
                Element root = (Element) roots.item(i);
                String value = root.getAttribute("id"); //NOI18N
                assert value.length() > 0 : "Illegal project.xml";
                rootProps.add(value);
                value = root.getAttribute("name"); //NOI18N
                rootNames.add(value);
            }
        }
        sourceRootProperties = Collections.unmodifiableList(rootProps);
        sourceRootNames = Collections.unmodifiableList(rootNames);
    }

    private class ProjectMetadataListener implements PropertyChangeListener, AntProjectListener {

        public void propertyChange(PropertyChangeEvent evt) {
            resetCache(false, evt.getPropertyName());
        }

        public void configurationXmlChanged(AntProjectEvent ev) {
            resetCache(true, null);
        }

        public void propertiesChanged(AntProjectEvent ev) {
            // handled by propertyChange
        }
    }

}
