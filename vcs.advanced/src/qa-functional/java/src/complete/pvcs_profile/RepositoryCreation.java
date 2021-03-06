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

package complete.pvcs_profile;

import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
import org.netbeans.test.oo.gui.jelly.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.netbeans.jellytools.modules.vcsgeneric.pvcs.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.*;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.properties.*;

/** XTest / JUnit test class performing repository creation of PVCS filesystem.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class RepositoryCreation extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String CREATE_DATABASE = "PVCS|Create Project Database";
    public static String CREATE_PROJECT = "PVCS|Create Project";
    public static String REFRESH = "PVCS|Refresh";
    public static String ADD = "PVCS|Add";
    public static String workingDirectory;
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public RepositoryCreation(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return RepositoryCreation test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        String exec = Utilities.isUnix() ? "sh -c \"vlog\"" : "cmd /x /c \"vlog\"";
        try { if (Runtime.getRuntime().exec(exec).waitFor() != 0 ) return suite; }
        catch (Exception e) {}
        suite.addTest(new RepositoryCreation("testCreateDatabase"));
        suite.addTest(new RepositoryCreation("testCreateProjects"));
        suite.addTest(new RepositoryCreation("testAddSingleFile"));
        suite.addTest(new RepositoryCreation("testAddMultipleFiles"));
        suite.addTest(new RepositoryCreation("testAddFileWithLock"));
        return suite;
    }
    
    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Method called before each testcase. Sets default timeouts, redirects system
     * output and maps main components.
     */
    protected void setUp() throws Exception {
        String workingDir = getWorkDirPath();
        new File(workingDir).mkdirs();
        File outputFile = new File(workingDir + "/output.txt");
        outputFile.createNewFile();
        File errorFile = new File(workingDir + "/error.txt");
        errorFile.createNewFile();
        PrintWriter outputWriter = new PrintWriter(new FileWriter(outputFile));
        PrintWriter errorWriter = new PrintWriter(new FileWriter(errorFile));
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(new org.netbeans.jemmy.TestOut(System.in, outputWriter, errorWriter));
    }
    
    /** Method will create a file and capture the screen.
     */
    private void captureScreen(String reason) throws Exception {
        File file;
        try {
            file = new File(getWorkDirPath() + "/dump.png");
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch(IOException e) { throw new Exception("Error: Can't create dump file."); }
        PNGEncoder.captureScreen(file.getAbsolutePath());
        throw new Exception(reason);
    }
    
    /** Creates new file or directory at given place.
     * @param name Name of directory or file to create.
     * @throws Any unexpected exception thrown during test.
     */
    public void createFile(String name) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name));
        writer.write("/** This is testing file.\n */\n\n public class Testing_File {\n }\n");
        writer.flush();
        writer.close();
    }

    /** Tries to mount PVCS filesystem and create new project database.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCreateDatabase() throws Exception {
        System.out.print(".. Testing project database creation ..");
        new ActionNoBlock(MOUNT_MENU, "Mount|Version Control|Generic VCS").perform();
        VCSWizardProfile wizard = new VCSWizardProfile();
        String profile = Utilities.isUnix() ? VCSWizardProfile.PVCS_UNIX : VCSWizardProfile.PVCS_WIN_NT;
        int os = Utilities.getOperatingSystem();
        if ((os == Utilities.OS_WIN95) | (os == Utilities.OS_WIN98))
            profile = VCSWizardProfile.PVCS_WIN_95;
        workingDirectory = getWorkDir().getAbsolutePath();
        wizard.setProfile(profile);
        new File(workingDirectory + File.separator + "Repo").mkdirs();
        new File(workingDirectory + File.separator + "Work" + File.separator + "test" + File.separator + "another").mkdirs();
        Thread.currentThread().sleep(10000);
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_PROJECT_DATABASE).clearText();
        wizard.setPVCSProjectDatabase(workingDirectory + File.separator + "Repo");
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_WORKFILES_LOCATION).requestFocus();
        Thread.currentThread().sleep(10000);
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_WORKFILES_LOCATION).clearText();
        wizard.setPVCSWorkfilesLocation(workingDirectory + File.separator + "Work");
        wizard.finish();
        Thread.currentThread().sleep(2000);
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        assertNotNull("Error: Can't select filesystem " + filesystem, filesystemNode);
        new Action(VERSIONING_MENU + "|" + CREATE_DATABASE, CREATE_DATABASE).performMenu(filesystemNode);
        MainWindowOperator.getDefault().waitStatusText("Command Create Project Database finished.");
        System.out.println(". done !");
    }

    /** Tries to mount PVCS filesystem and create new projects.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCreateProjects() throws Exception {
        System.out.print(".. Testing projects creation ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node testNode = new Node( filesystemNode, "test [Local]");
        new Action(VERSIONING_MENU + "|" + CREATE_PROJECT, CREATE_PROJECT).perform(testNode);
        Thread.sleep(5000);
        Node anotherNode = new Node( testNode, "another [Local]");
        new Action(VERSIONING_MENU + "|" + CREATE_PROJECT, CREATE_PROJECT).perform(anotherNode);
        Thread.sleep(5000);
        testNode = new Node( filesystemNode, "test [Current]");
        anotherNode = new Node( testNode, "another [Current]");
        System.out.println(". done !");
    }

    /** Tries to add single file to PVCS project database.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAddSingleFile() throws Exception {
        System.out.print(".. Testing single file addition ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        createFile( workingDirectory + File.separator + "Work" + File.separator + "A_File.java" );
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(filesystemNode);
        Thread.sleep(5000);
        Node fileNode = new Node( filesystemNode, "A_File [Local]");
        new ActionNoBlock(VERSIONING_MENU + "|" + ADD, ADD).perform(fileNode);
        AddCommandOperator addCommand = new AddCommandOperator("A_File.java");
        addCommand.setWorkfileDescription("Auto-generated class file.");
        addCommand.setChangeDescription("Initial revision.");
        addCommand.ok();
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        fileNode = new Node( filesystemNode, "A_File [Current]");
        System.out.println(". done !");
    }

    /** Tries to add more files to PVCS project database at once.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAddMultipleFiles() throws Exception {
        System.out.print(".. Testing multiple files addition ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node testNode = new Node(filesystemNode, "test [Current]");
        Node anotherNode = new Node(testNode, "another [Current]");
        createFile( workingDirectory + File.separator + "Work" + File.separator + "test"  + File.separator + "B_File.java" );
        createFile( workingDirectory + File.separator + "Work" + File.separator + "test"  + File.separator + "another"  + File.separator + "D_File.java" );
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(new Node[] {testNode, anotherNode});
        Thread.currentThread().sleep(10000);
        anotherNode.expand();
        Node B_FileNode = new Node( testNode, "B_File [Local]");
        Node D_FileNode = new Node( anotherNode, "D_File [Local]");
        new ActionNoBlock(VERSIONING_MENU + "|" + ADD, ADD).perform(new Node[] {B_FileNode, D_FileNode});
        AddCommandOperator addCommand = new AddCommandOperator("B_File.java ...");
        addCommand.setWorkfileDescription("Auto-generated class file.");
        addCommand.setChangeDescription("Initial revision.");
        addCommand.ok();
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        B_FileNode = new Node( testNode, "B_File [Current]");
        D_FileNode = new Node( anotherNode, "D_File [Current]");
        System.out.println(". done !");
    }

    /** Tries to add a file to PVCS project database with lock and label.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAddFileWithLock() throws Exception {
        System.out.print(".. Testing file addition with lock and label ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node testNode = new Node(filesystemNode, "test [Current]");
        createFile( workingDirectory + File.separator + "Work" + File.separator + "test"  + File.separator + "C_File.java" );
        createFile( workingDirectory + File.separator + "Work" + File.separator + "test"  + File.separator + "C_File.form" );
        filesystemNode.select();
        new ComboBoxProperty(new PropertySheetOperator(), "Advanced Options").setValue("True");
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(testNode);
        Thread.currentThread().sleep(10000);
        Node C_FileNode = new Node( testNode, "C_File [Local]");
        new java.awt.Robot().keyPress(java.awt.event.KeyEvent.VK_CONTROL);
        new ActionNoBlock(VERSIONING_MENU + "|" + ADD + "...", ADD + "...").perform(C_FileNode);
        new java.awt.Robot().keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
        AddCommandOperator addCommand = new AddCommandOperator("C_File.java");
        addCommand.setWorkfileDescription("Auto-generated form file.");
        addCommand.setChangeDescription("Initial revision.");
        addCommand.setAssignAVersionLabel("My_Version");
        addCommand.checkKeepTheRevisionLocked(true);
        addCommand.checkFloatLabelWithTheTipRevision(true);
        addCommand.ok();
        Thread.sleep(10000);
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(C_FileNode);
        Thread.currentThread().sleep(2000);
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        String children[] = testNode.getChildren();
        int count = children.length;
        boolean found = false;
        for(int i=0; i<count; i++) {
            String child = children[i];
            if (child.startsWith("C_File [Current; 1.1] (") && child.endsWith(")")) found = true;
        }
        if (!found) captureScreen("Error: Unable to find locked C_File [Current; 1.1] (...) file.");
        System.out.println(". done !");
    }
}