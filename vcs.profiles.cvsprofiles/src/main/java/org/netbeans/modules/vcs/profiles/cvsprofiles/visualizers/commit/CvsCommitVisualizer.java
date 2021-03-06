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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.commit;

import java.awt.Dialog;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import java.util.Iterator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.OutputVisualizer;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

/**
 * The cvs commit visualizer.
 *
 * @author  Richard Gregor
 */
public class CvsCommitVisualizer extends OutputVisualizer {
    
    public static final String UNKNOWN = "commit: nothing known about `";               //NOI18N
    /*
    public static final String EXAM_DIR = "server: Examining";                          //NOI18N
    public static final String CHECKING_IN = "Checking in ";                            //NOI18N
    public static final String REMOVING = "Removing ";                                  //NOI18N
    public static final String NEW_REVISION = "new revision:";                          //NOI18N
    public static final String INITIAL_REVISION = "initial revision:";                  //NOI18N
    public static final String DONE = "done";                                           //NOI18N
    public static final String RCS_FILE = "RCS file: ";                                 //NOI18N
    public static final String ADD = "commit: use `cvs add' to create an entry for ";   //NOI18N       
     */
           
    private CommitInformation commitInformation;
    private boolean isAdding;
    private String fileDirectory;
    
    /**
     * The local path the command run in.
     */
    private String localPath;
    private CommitInfoPanel contentPane = null;
    private int exit = Integer.MIN_VALUE; // unset exit status
    private List outputInfosToShow; // cached information when the command is providing
    // output sooner then the GUI is created.
    private Object outputAccessLock = new Object();
    private CommandOutputTextProcessor.TextOutput errOutput;
    private CommandOutputTextProcessor.TextOutput stdDataOutput;
    private CommandOutputTextProcessor.TextOutput errDataOutput;
    
    /**
     * Creates new CvsCommitVisualizer 
     */
    public CvsCommitVisualizer() {
        super();  
    }
    
    public Map getOutputPanels() {
        debug("getOutputPanel");
        HashMap output = new HashMap();
        contentPane = new CommitInfoPanel(this);
        contentPane.setVcsTask(getVcsTask());
        contentPane.setOutputCollector(getOutputCollector());
        contentPane.showStartCommand();
        //System.out.println("getOutputPanel("+this.hashCode()+"), exit = "+exit);
        if (exit != Integer.MIN_VALUE) {
            // The command already finished!
            setExitStatus(exit);
        }
        output.put("",contentPane);//TODO - what's right name?
        synchronized (outputAccessLock) {
            if (errOutput != null) {
                errOutput.setTextArea(contentPane.getErrOutputArea());
            }
            if (stdDataOutput != null) {
                stdDataOutput.setTextArea(contentPane.getDataStdOutputArea());
            }
            if (errDataOutput != null) {
                errDataOutput.setTextArea(contentPane.getDataErrOutputArea());
            }
        }
        return output;
    }
    
    
    public void open(){
        CommandOutputTopComponent out = CommandOutputTopComponent.getInstance();
        getOutputPanels();
        String title;
        if (files.size() == 1) {
            String filePath = (String) files.iterator().next();
            java.io.File file = new java.io.File(filePath);
            title = java.text.MessageFormat.format(
            NbBundle.getBundle(this.getClass()).getString("CvsCommitVisualizer.title_one"), // NOI18N
            new Object[] { commandName,file.getName()});
        }
        else if (files.size() > 1) {
            title = java.text.MessageFormat.format(
            NbBundle.getBundle(this.getClass()).getString("CvsCommitVisualizer.title_many"), // NOI18N
            new Object[] {commandName, Integer.toString(files.size())});
        }
        else title = commandName;
        out.addVisualizer(title,contentPane, true);
        out.open();
    }
    
    /**
     * This method is called, with the output line.
     * @param line The output line.
     */
    public void stdOutputLine(String line) {
        // Ignored, we get the parsed output from data output.
    }
    
    private File createFile(String fileName) {
        return new File(localPath, fileName);
    }
    
    private void processUnknownFile(String line) {
        commitInformation = new CommitInformation();
        commitInformation.setType(commitInformation.UNKNOWN);
        int index = line.indexOf('\'');
        String fileName = line.substring(0, index).trim();
        commitInformation.setFile(createFile(fileName));
        outputDone();
    }
    
    private void processFile(String fileName, String commitInfo, String revision) {
        commitInformation = new CommitInformation();
        commitInformation.setType(commitInfo);
        commitInformation.setFile(createFile(fileName));
        commitInformation.setRevision(revision);
        outputDone();
    }
    
    public void outputDone() {        
        //System.out.println("outputDone("+this.hashCode()+") ENTERED, fic = "+fileInfoContainer+", cp = "+(contentPane != null)+", oi = "+outputInfosToShow);
        if (contentPane != null) {
            if (outputInfosToShow != null) {
                for (Iterator it = outputInfosToShow.iterator(); it.hasNext(); ) {
                    CommitInformation info = (CommitInformation) it.next();
                    contentPane.showFileInfoGenerated(info);
                }
                outputInfosToShow = null;
            }
        }
        
        if (commitInformation != null) {
            if (contentPane != null) {
                contentPane.showFileInfoGenerated(commitInformation);
            } else {
                if (outputInfosToShow == null) {
                    outputInfosToShow = new LinkedList();
                }
                outputInfosToShow.add(commitInformation);
            }
            commitInformation = null;
        }
        
        //System.out.println("outputDone("+this.hashCode()+") EXITED, fic = "+fileInfoContainer+", cp = "+(contentPane != null)+", oi = "+outputInfosToShow);
    }
    
    /** @return false to open immediatelly.
     */
    public boolean openAfterCommandFinish() {
        return false;
    }
    
    public boolean doesDisplayError() {
        return true;
    }
    
    public void setExitStatus(int exit) {
        debug("exit: "+exit);
        //System.out.println("setExitStatus("+this.hashCode()+") ("+exit+"), cp = "+(contentPane != null));
        this.exit = exit;
        if (contentPane != null) { // Check whether we have the GUI created
            if (outputInfosToShow != null) {
                outputDone(); // show cached infos
            }
            contentPane.showFinishedCommand(exit);
        }
    }    
 
    
    /**
     * Receive a line of error output.
     */
    public void errOutputLine(final String line) {
        // to prevent deadlocks, append output in the AWT thread
        synchronized (outputAccessLock) {
            if (errOutput == null) {
                errOutput = CommandOutputTextProcessor.getDefault().createOutput();
                if (contentPane != null) {
                    errOutput.setTextArea(contentPane.getErrOutputArea());
                }
            }
            errOutput.addText(line+'\n');
        }
        if (line.indexOf(UNKNOWN) >= 0) {
            processUnknownFile(line.substring(line.indexOf(UNKNOWN) + UNKNOWN.length()).trim());
        }
    }
    
    /**
     * Receive the data output.
     */
    public void stdOutputData(final String[] data) {
        synchronized (outputAccessLock) {
            if (stdDataOutput == null) {
                stdDataOutput = CommandOutputTextProcessor.getDefault().createOutput();
                if (contentPane != null) {
                    stdDataOutput.setTextArea(contentPane.getDataStdOutputArea());
                }
            }
            stdDataOutput.addText(VcsUtilities.arrayToString(data)+'\n');
        }
        if (data.length < 9) return ;
        if (data[8] != null) {
            processFile(data[0], data[8], data[2]);
        }
    }
    
    /**
     * Receive the error data output.
     */
    public void errOutputData(final String[] data) {
        synchronized (outputAccessLock) {
            if (errDataOutput == null) {
                errDataOutput = CommandOutputTextProcessor.getDefault().createOutput();
                if (contentPane != null) {
                    errDataOutput.setTextArea(contentPane.getDataErrOutputArea());
                }
            }
            errDataOutput.addText(VcsUtilities.arrayToString(data)+'\n');
        }
    }
    
    
    private static boolean DEBUG = false;
    private static void debug(String msg){
        if(DEBUG)
            System.err.println("CvsCommitVisualizer: "+msg);
    }
    
    
    
}
