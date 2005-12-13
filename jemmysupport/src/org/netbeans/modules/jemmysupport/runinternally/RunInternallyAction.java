/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.runinternally;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.queries.FileBuiltQuery;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.spi.project.ActionProvider;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** Used to run jemmy test internally in the same JVM as IDE.
 * @author Jiri.Skrivanek@sun.com
 */
public class RunInternallyAction extends NodeAction {
    
    private final String scriptFilename = "build"+System.currentTimeMillis(); // NOI18N
    
    
    /** Not to show icon in main menu. */
    public RunInternallyAction() {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }
    
    /** method performing the action
     * @param nodes selected nodes
     */
    protected void performAction(Node[] nodes) {
        // release lock (it may be locked from previous run)
        synchronized (compileLock) {
            try {
                compileLock.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(SwingUtilities.isEventDispatchThread()) {
            // do not block AWT thread
            Thread actionThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        executeSelectedMainClass();
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
                    }
                }
            });
            actionThread.start();
        } else {
            executeSelectedMainClass();
        }
    }
    
    /** Action is enabled when a main class is selected and it is a class
     * which can be compiled.
     * @param node selected nodes
     * @return true if a compilable main class is selected
     */
    public boolean enable(Node[] node) {
        Lookup context = Utilities.actionsGlobalContext();
        if(getSelectedMainClass(context) != null) {
            DataObject dObj = getSelectedDataObject(context);
            FileObject fObj = dObj.getPrimaryFile();
            FileBuiltQuery.Status status = FileBuiltQuery.getStatus(fObj);
            return status != null;
        }
        return false;
    }
    
    /** method returning name of the action
     * @return String name of the action
     */
    public String getName() {
        return NbBundle.getMessage(RunInternallyAction.class, "LBL_RunInternallyAction"); // NOI18N
    }
    
    /** method returning icon for the action
     * @return String path to action icon
     */
    protected String iconResource() {
        return "org/netbeans/modules/jemmysupport/resources/runInternally.png"; // NOI18N
    }
    
    /** method returning action Help Context
     * @return action Help Context
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(RunInternallyAction.class);
    }
    
    /** Always return false - no need to run asynchronously. */
    protected boolean asynchronous() {
        return false;
    }
    
    /**************************************************************************/
    private void executeSelectedMainClass() {
        Lookup context = Utilities.actionsGlobalContext();
        DataObject dObj = getSelectedDataObject(context);
        FileObject fObj = dObj.getPrimaryFile();
        String classname = getSelectedMainClass(context);
        
        FileBuiltQuery.Status builtStatus = FileBuiltQuery.getStatus(fObj);
        if(builtStatus.isBuilt()) {
            // it is built, so execute
            execute(fObj, classname);
        } else {
            // if not built add listener to wait for the end of compilation
            CompileListener listener = new CompileListener();
            builtStatus.addChangeListener(listener);
            
            String projectName = ProjectUtils.getInformation(FileOwnerQuery.getOwner(fObj)).getName();
            // "myproject (compile-single)"
            String outputTarget = MessageFormat.format(
                    NbBundle.getBundle("org.apache.tools.ant.module.run.Bundle").getString("TITLE_output_target"),
                    new Object[] {projectName, null, "compile-single"});  // NOI18N
            // "Build of myproject (compile-single) failed."
            String failedMessage = MessageFormat.format(
                    NbBundle.getBundle("org.apache.tools.ant.module.run.Bundle").getString("FMT_target_failed_status"),
                    new Object[] {outputTarget});
            
            StatusListener statusListener = new StatusListener(failedMessage);
            StatusDisplayer.getDefault().addChangeListener(statusListener);
            try {
                // try to compile
                // This cannot be called because it is not made public in manifest
                //Actions.compileSingle().actionPerformed(null);
                Project project = FileOwnerQuery.getOwner(fObj);
                ActionProvider ap = (ActionProvider)project.getLookup().lookup(ActionProvider.class );
                ap.invokeAction(ActionProvider.COMMAND_COMPILE_SINGLE, context);
                
                //wait until compilation finishes
                synchronized (compileLock) {
                    // wait max. 30 seconds but it should be released sooner from CompileListener
                    // if compilation succeeded or StatusListener if compilation failed
                    compileLock.wait(30000);
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            } finally {
                builtStatus.removeChangeListener(listener);
                StatusDisplayer.getDefault().removeChangeListener(statusListener);
            }
            if(builtStatus.isBuilt()) {
                // finally if really built, execute it
                execute(fObj, classname);
            }
        }
    }
    
    /** Returns selected data object. We expect that only one is selected. If
     * more or none is selected then returns null.
     */
    private DataObject getSelectedDataObject(Lookup context) {
        Collection dataObjects = context.lookup(new Lookup.Template(DataObject.class)).allInstances();
        if(dataObjects != null && dataObjects.size() == 1) {
            // only one object has to be selected
            return (DataObject)dataObjects.iterator().next();
        }
        return null;
    }
    
    /** Returns fully qualified name of class to be executed. It returns null
     * if no suitable class is selected.
     */
    private String getSelectedMainClass(Lookup context) {
        DataObject dObj = getSelectedDataObject(context);
        if(dObj == null) {
            return null;
        }
        FileObject fObj = dObj.getPrimaryFile();
        // following code taken from org.netbeans.modules.java.j2seproject.J2SEProjectUtil.hasMainMethod()
        JavaModel.getJavaRepository().beginTrans(false);
        try {
            JavaModel.setClassPath(fObj);
            Resource res = JavaModel.getResource(fObj);
            if(res == null) {
                return null;
            }
            if(!res.getMain().isEmpty()) {
                return ((JavaClass)res.getMain().get(0)).getName();
            }
        } finally {
            JavaModel.getJavaRepository().endTrans();
        }
        return null;
    }
    
    private Object compileLock = new Object();
    
    /** Listener to wait for compilation success. */
    class CompileListener implements ChangeListener {
        
        public void stateChanged(ChangeEvent evt) {
            synchronized (compileLock) {
                try {
                    compileLock.notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /** Listener to wait for compilation failure. */
    class StatusListener implements ChangeListener {
        private String failedMessage;
        public StatusListener(String failedMessage) {
            this.failedMessage = failedMessage;
        }
        public synchronized void stateChanged(ChangeEvent evt) {
            if(StatusDisplayer.getDefault().getStatusText().equals(failedMessage)) {
                // release lock because compile failed
                synchronized (compileLock) {
                    try {
                        compileLock.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /** Returns build script with run-internally target. */
    private FileObject getScript() {
        FileObject tmpFolderFO = FileUtil.toFileObject(FileUtil.normalizeFile(
                new File(System.getProperty("java.io.tmpdir"))));
        FileObject scriptFO = tmpFolderFO.getFileObject(scriptFilename+".xml");
        if(scriptFO == null || !scriptFO.isValid()) {
            URL url = this.getClass().getResource("build.xml"); // NOI18N
            FileObject templateScriptFO = URLMapper.findFileObject(url);
            try {
                FileUtil.copyFile(templateScriptFO, tmpFolderFO, scriptFilename);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
            }
            scriptFO = tmpFolderFO.getFileObject(scriptFilename+".xml"); // NOI18N
            FileUtil.toFile(scriptFO).deleteOnExit();
        }
        return scriptFO;
    }
    
    /** Returns EXECUTE classpath of given FileObject. */
    private String getClasspath(FileObject fObj) {
        ClassPath classpath = ClassPath.getClassPath(fObj, ClassPath.EXECUTE);
        StringBuffer result = new StringBuffer();
        for (Iterator it = classpath.entries().iterator(); it.hasNext();) {
            URL entryUrl = ((ClassPath.Entry)it.next()).getURL();
            if("jar".equals(entryUrl.getProtocol())) { // NOI18N
                entryUrl = FileUtil.getArchiveFile(entryUrl);
            }
            result.append(new File(URI.create(entryUrl.toExternalForm())).getAbsolutePath());
            if(it.hasNext()) {
                result.append(File.pathSeparatorChar);
            }
        }
        return result.toString();
    }

    /** Gets IDE's system class loader, adds given classpath and invokes main
     * method of given class. It uses RunInternallyTask.
     */
    private void execute(FileObject fObj, String classname) {
        try {
            String[] targets = {"run-internally"}; // NOI18N
            Properties props = new Properties();
            props.setProperty("run-internally.classname", classname); // NOI18N
            props.setProperty("run-internally.cp", getClasspath(fObj)); // NOI18N
            ExecutorTask task = ActionUtils.runTarget(getScript(), targets, props);
        } catch (IllegalArgumentException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
        }
    }
}

