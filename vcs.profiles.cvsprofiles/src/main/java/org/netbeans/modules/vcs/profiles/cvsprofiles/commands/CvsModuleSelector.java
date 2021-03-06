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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.util.*;
import java.awt.*;

import org.openide.*;
import org.openide.util.*;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;

/**
 * The selector of CVS modules
 * @author  Martin Entlicher
 */
public class CvsModuleSelector implements VcsAdditionalCommand, TextOutputListener {
    
    private Hashtable vars;
    private CommandOutputListener stdoutNRListener;
    private CommandOutputListener stderrNRListener; 
    private StringBuffer outputBuffer;
    private volatile boolean cmdSuccess = true;  // By default we hope the module list is in cache
    private volatile boolean dlgSuccess = false;
    private final Object dlgFinishedLock = new Object();
    private boolean isCommand = false;
    //stores results of cvs co -s command
    private HashSet s_resultSet;
    private CvsModuleSelector.CompleteTextListener completeTextListener;
    private ModuleInfo oldInfo;

    private CommandExecutionContext executionContext = null;
    
    /** Creates new CvsModuleSelector */
    public CvsModuleSelector() {
        isCommand = false;
        s_resultSet = new HashSet();
    }
    
    public void setExecutionContext(CommandExecutionContext executionContext) {
        this.executionContext = executionContext;
    }    
     
    public Hashtable getVariables() {
        return vars;
    }
    
    
    /** this method is used to start the selector from the CvsAction popup menu.
     *  This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                      satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                      satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars,String[] args,
    CommandOutputListener stdoutNRListener,
    CommandOutputListener stderrNRListener,
    CommandDataOutputListener stdoutListener, String dataRegex,
    CommandDataOutputListener stderrListener, String errorRegex) {
        isCommand = true;
        String[] returns = execSel(vars, "MODULE", args, stdoutNRListener, stderrNRListener);
        String[] toReturn = new String[1];
        if (returns != null) {
            if (returns.length > 0 && returns[0] != null && returns[0].length() > 0) {
                toReturn[0] = VcsUtilities.arrayToQuotedString(returns, false, (String) vars.get("QUOTE"));  //TODO for cygwin =true
                stdoutListener.outputData(toReturn);
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * This method is used to start the selector.
     * @param vars the VCS variables
     * @param variable the name of the selected variable
     * @param args the command line parametres
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @return the selected values, empty string when the selection was canceled
     *         or null when an error occures.
     */
    public String[] execSel(Hashtable vars, String variable, String[] args,
    CommandOutputListener stdoutNRListener,
    CommandOutputListener stderrNRListener) {
        this.vars = vars;
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;

        final CvsModuleSelectorDialog panel = new CvsModuleSelectorDialog(this, args);
        panel.calledAsCommand(isCommand);  // if run as command -> is called from CvsAction - disable some stuff
        final DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getBundle(CvsModuleSelector.class).getString("CvsModuleSelectorDialog.title"));
        final Dialog fdlg = DialogDisplayer.getDefault().createDialog(dd);
        VcsUtilities.centerWindow(fdlg);
        
        Runnable showRunnable = new Runnable() {
            public void run() {
                fdlg.setVisible(true);
                dlgSuccess = (dd.getValue() == DialogDescriptor.OK_OPTION);
                synchronized (dlgFinishedLock) {
                    dlgFinishedLock.notify();
                }
            }
        };
        javax.swing.SwingUtilities.invokeLater(showRunnable);
        synchronized (dlgFinishedLock) {
            try {
                dlgFinishedLock.wait();
            } catch (InterruptedException e) {
                dlgSuccess = false;
            }
        }
        if (dlgSuccess && cmdSuccess) {
            return panel.getSelection();
        } else {
            if (dlgSuccess) return null;
            else {
                String[] toReturn = {""};
                return toReturn;
            }
        }
    }
    
    boolean runCommand(String[] args) {        
        if (args.length != 2) {
            if (stderrNRListener != null) stderrNRListener.outputLine("Bad number of arguments. "+
            "Expecting two arguments: cvs co -s AND cvs co -c");
            return false;
        }        
  
        Hashtable varsCopy1 = new Hashtable(vars);
        CommandSupport cmdSupp = executionContext.getCommandSupport(args[0]);
        Command cmd1 = cmdSupp.createCommand();
        if (cmd1 instanceof VcsDescribedCommand) {
            ((VcsDescribedCommand) cmd1).setAdditionalVariables(varsCopy1);
            ((VcsDescribedCommand) cmd1).addTextOutputListener(this);
        }
        CommandTask task1 = cmd1.execute(); 
        cmdSupp = executionContext.getCommandSupport(args[1]);
        Command cmd2 = cmdSupp.createCommand();
        Hashtable varsCopy2 = new Hashtable(vars);
        completeTextListener = new CvsModuleSelector.CompleteTextListener();
        if (cmd2 instanceof VcsDescribedCommand) {
            ((VcsDescribedCommand) cmd2).setAdditionalVariables(varsCopy2);
            ((VcsDescribedCommand) cmd2).addTextOutputListener(completeTextListener);
        }
        CommandTask task2 = cmd2.execute(); 
        try {
            task1.waitFinished(0);
            task2.waitFinished(0);  
        } catch (InterruptedException iexc) {
            task1.stop();
            task2.stop(); 
            return false;
        }
        boolean modulesStatGetStatus = task1.getExitStatus() == CommandTask.STATUS_SUCCEEDED;
        boolean modulesAllGetStatus = task2.getExitStatus() == CommandTask.STATUS_SUCCEEDED;
        return modulesStatGetStatus && modulesAllGetStatus;
    }
    
    private Vector getModules() {
        Vector modules = new Vector();
        completeTextListener.mergeStatuses();       
        HashMap map = completeTextListener.getResultMap();
        Collection values = map.values();
        Iterator it = values.iterator();
        while(it.hasNext()){
            ModuleInfo info = (ModuleInfo)it.next();
            modules.addElement(info.toVector());
        }       
        
        return modules;

    }    
     
    public Vector getModulesList(String[] args) {
        Vector modules = null; 
        cmdSuccess = runCommand(args);
        if (cmdSuccess) modules = getModules();
        return modules;
    }
    
    public void outputLine(String line) {        
        if (line.startsWith(" ") || line.startsWith("\t")) //not a line with module
            return;
        line = line.replace('\t',' ');
        StringTokenizer lineTok = new StringTokenizer(line, " ", false);
        if (lineTok.countTokens() < 2)
            return;
        ModuleInfo info = new ModuleInfo(lineTok.nextToken());
        info.setStatus(lineTok.nextToken());
        s_resultSet.add(info);   
    }
    
    
    private final class ModuleInfo{
        private String name;
        private String status;
        private String type;
        private String paths;
        
        public ModuleInfo(String name){
            this(name,null,null,null);
        }
        
        public ModuleInfo(String name, String status, String type, String paths){
            this.name = name;
            this.status = status;
            this.type = type;
            this.paths = paths;
        }
        
        public void setName(String name){
            this.name = name;
        }
        
        public String getName(){
            return name;
        }
        
        public void setStatus(String status){
            this.status = status;
        }
        
        public String getStatus(){
            return status;
        }
        
        public void setType(String type){
            this.type = type;
        }
        
        public String getType(){
            return type;
        }
        
        public void setPaths(String paths){
            this.paths = paths;
        }
        
        public String getPaths(){
            return paths;
        }
        
        public Vector toVector(){
            Vector vc = new Vector();
            vc.add(getName());
            vc.add(getStatus());
            vc.add(getType());
            vc.add(getPaths());
            return vc;
        }
    }
    
    private final class CompleteTextListener implements TextOutputListener{
        //stores results of cvs co -c command
        private HashMap c_resultMap;
        
        public CompleteTextListener(){
            c_resultMap = new HashMap();
        }
        
        public void outputLine(String line) {            
            StringTokenizer lineTok = new StringTokenizer(line, " ", false);
            if (line.startsWith(" ") || line.startsWith("\t")){                
                if (oldInfo != null){
                  String oldpaths=oldInfo.getPaths();
                  while(lineTok.hasMoreTokens())
                      oldpaths += lineTok.nextToken()+" ";       //paths
                  oldInfo.setPaths(oldpaths);
                }
                  return;
            }
            line = line.replace('\t',' ');            
            if (lineTok.countTokens() < 2)
                return;
            ModuleInfo info = new ModuleInfo(lineTok.nextToken());
            String paths="";
            if(lineTok.hasMoreTokens()){
                String typeOrPath = lineTok.nextToken();
                if(typeOrPath.startsWith("-"))          //NOI18N
                    info.setType(typeOrPath);           //type
                else
                    paths += typeOrPath+" ";
            }            
            while(lineTok.hasMoreTokens())
                paths += lineTok.nextToken()+" ";       //paths
            info.setPaths(paths);
            c_resultMap.put(info.getName(),info);
            oldInfo = info;
        }
        
        public void mergeStatuses(){
            Iterator s_it = s_resultSet.iterator();            
            while(s_it.hasNext()){
                ModuleInfo s_info = (ModuleInfo)s_it.next();
                ModuleInfo c_info = (ModuleInfo)c_resultMap.get(s_info.getName());
                if(c_info != null)
                    c_info.setStatus(s_info.getStatus());
            }               
        }
        
        public HashMap getResultMap(){
            return c_resultMap;
        }
    }
    
}
