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
import org.netbeans.jellytools.modules.vcscore.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.*;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.properties.*;
import javax.swing.text.*;
import java.awt.Color;
import java.util.Date;

/** XTest / JUnit test class performing additional features testing on PVCS filesystem.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class AdditionalFeatures extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String GET = "PVCS|Get";
    public static String PUT = "PVCS|Put";
    public static String DIFF = "Diff";
    public static String REFRESH_REVISIONS = "Refresh Revisions";
    public static String VERSIONING_EXPLORER = "Versioning Explorer";
    public static String ADD_TO_GROUP = "Include in VCS Group|<Default Group>";
    public static String VCS_GROUPS = "VCS Groups";
    public static String workingDirectory;
    public static String userName;
    private static final Color MODIFIED_COLOR = new Color(160, 200, 255);
    private static final Color NEW_COLOR = new Color(180, 255, 180);
    private static final Color REMOVED_COLOR = new Color(255, 160, 180);
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public AdditionalFeatures(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return RegularDevelopment test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        String exec = Utilities.isUnix() ? "sh -c \"vlog\"" : "cmd /x /c \"vlog\"";
        try { if (Runtime.getRuntime().exec(exec).waitFor() != 0 ) return suite; }
        catch (Exception e) {}
        suite.addTest(new AdditionalFeatures("testViewOldRevision"));
        suite.addTest(new AdditionalFeatures("testCompareRevisions"));
        suite.addTest(new AdditionalFeatures("testAddToGroup"));
        suite.addTest(new AdditionalFeatures("testCheckinGroup"));
        suite.addTest(new AdditionalFeatures("testVerifyGroup"));
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
    
    /** Tries to view an old revision of a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testViewOldRevision() throws Exception {
        System.out.print(".. Testing view old revision ..");
        String workingPath = getWorkDirPath();
        workingDirectory = workingPath.substring(0, workingPath.indexOf("AdditionalFeatures")) + "RepositoryCreation" + File.separator + "testCreateDatabase";
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Current]");
        filesystemNode.select();
        new ComboBoxProperty(new PropertySheetOperator(), "Advanced Options").setValue("False");
        new Action(VERSIONING_MENU + "|" + VERSIONING_EXPLORER, VERSIONING_EXPLORER).perform(A_FileNode);
        VersioningFrameOperator versioningExplorer = new VersioningFrameOperator();
        filesystemNode = new Node(versioningExplorer.treeVersioningTreeView(), filesystem);
        new Node(filesystemNode, "A_File.java [Current]|1.1  Three lines have changed.").select();
        new OpenAction().perform(new Node(filesystemNode, "A_File.java [Current]|1.0  Initial revision."));
        String editorContents = new EditorOperator("A_File.java 1.0").getText();
        if (!editorContents.equals("/** This is testing file.\n */\n\n public class Testing_File {\n }\n"))
            captureScreen("Error: Incorrect version of A_File was opened.");
        versioningExplorer.close();
        System.out.println(". done !");
    }

    /** Tries to compare two old revisions of a file.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCompareRevisions() throws Exception {
        System.out.print(".. Testing two revisions comparison ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node A_FileNode = new Node( filesystemNode, "A_File [Current]");
        new Action(VERSIONING_MENU + "|" + VERSIONING_EXPLORER, VERSIONING_EXPLORER).perform(A_FileNode);
        VersioningFrameOperator versioningExplorer = new VersioningFrameOperator();
        filesystemNode = new Node(versioningExplorer.treeVersioningTreeView(), filesystem);
        new Action(null, DIFF).perform(new Node(filesystemNode, "A_File.java [Current]|1.0  Initial revision."));
        versioningExplorer.close();
        TopComponentOperator editor = new TopComponentOperator(new EditorWindowOperator(), "Diff: A_File.java");
        JEditorPaneOperator headRevision = new JEditorPaneOperator(editor, 0);
        JEditorPaneOperator workingRevision = new JEditorPaneOperator(editor, 1);
        String headRevisionContents = "/** This is testing file.\n */\n\n public class Testing_File {\n\n }\n";
        String workingRevisionContents = "/** This is testing A_File.java file.\n */\n\n public class Testing_File {\n     int i;\n }\n";
        if (!headRevisionContents.equals(headRevision.getText()) | !workingRevisionContents.equals(workingRevision.getText()))
            captureScreen("Error: Incorrect diff contents.");
        StyledDocument headRevisionDocument = (StyledDocument) headRevision.getDocument();
        StyledDocument workingRevisionDocument = (StyledDocument) workingRevision.getDocument();
        Color headRevisionLine = (Color) headRevisionDocument.getLogicalStyle(1).getAttribute(StyleConstants.ColorConstants.Background);
        Color workingRevisionLine = (Color) workingRevisionDocument.getLogicalStyle(1).getAttribute(StyleConstants.ColorConstants.Background);
        if (!headRevisionLine.equals(MODIFIED_COLOR) | !workingRevisionLine.equals(MODIFIED_COLOR))
            captureScreen("Error: Incorrect color of modified line.");
        int thirdLineHeadOffset = 30;
        int thirdLineWorkingOffset = 42;
        headRevisionLine = (Color) headRevisionDocument.getLogicalStyle(thirdLineHeadOffset).getAttribute(StyleConstants.ColorConstants.Background);
        Style lineStyle = workingRevisionDocument.getLogicalStyle(thirdLineWorkingOffset);
        if (!headRevisionLine.equals(REMOVED_COLOR) | (lineStyle != null))
            captureScreen("Error: Incorrect color of removed line.");
        int fifthLineHeadOffset = 60;
        int fifthLineWorkingOffset = 72;
        lineStyle = headRevisionDocument.getLogicalStyle(fifthLineHeadOffset);
        workingRevisionLine = (Color) workingRevisionDocument.getLogicalStyle(fifthLineWorkingOffset).getAttribute(StyleConstants.ColorConstants.Background);
        if ((lineStyle != null) | !workingRevisionLine.equals(NEW_COLOR))
            captureScreen("Error: Incorrect color of new line.");
        System.out.println(". done !");
    }

    /** Tries to add a file into VCS group.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAddToGroup() throws Exception {
        System.out.print(".. Testing file addition to VCS group ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node B_FileNode = new Node( filesystemNode, "test [Current]|B_File [Current]");
        new Action(VERSIONING_MENU + "|" + ADD_TO_GROUP, ADD_TO_GROUP).perform(B_FileNode);
        new Action(VERSIONING_MENU + "|" + VCS_GROUPS, null).performMenu();
        VCSGroupsFrameOperator groupsWindow = new VCSGroupsFrameOperator();
        new Node(groupsWindow.treeVCSGroupsTreeView(), "<Default Group>|B_File [Current]").select();
        System.out.println(". done !");
    }

    /** Tries to do checkin from VCS group.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCheckinGroup() throws Exception {
        System.out.print(".. Testing checking in from VCS group ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node B_FileNode = new Node( filesystemNode, "test [Current]|B_File [Current]");
        new Action(VERSIONING_MENU + "|" + GET, GET).perform(B_FileNode);
        GetCommandOperator getCommand = new GetCommandOperator("B_File.java");
        getCommand.checkLockForTheCurrentUser(true);
        getCommand.checkCheckOutWritableWorkfile(true);
        getCommand.ok();
        Thread.sleep(5000);
        BufferedWriter writer = new BufferedWriter(new FileWriter(workingDirectory + File.separator + "Work" + File.separator + "test" + File.separator + "B_File.java"));
        writer.write("/** This is testing B_File.java file.\n */\n public class B_File {\n     int i = 1;\n }\n");
        writer.flush();
        writer.close();
        new Action(VERSIONING_MENU + "|" + VCS_GROUPS, null).performMenu();
        VCSGroupsFrameOperator groupsWindow = new VCSGroupsFrameOperator();
        Node defaultGroup = new Node(groupsWindow.treeVCSGroupsTreeView(), "<Default Group>");
        defaultGroup.select();
        new TextFieldProperty(new PropertySheetOperator(), "Description").setValue("Checked in from VCS group.");
        defaultGroup.select();
        new Action(null, PUT).performPopup(defaultGroup);
        PutCommandOperator putCommand = new PutCommandOperator("B_File.java");
        String changeDescription = putCommand.getChangeDescription();
        putCommand.setChangeDescription("Checked in from VCS group.");
        putCommand.ok();
        if (!changeDescription.equals("Checked in from VCS group.\n"))
            captureScreen("Error: Group description was not propagated into checkin dialog.");
        Thread.sleep(2000);
        new Action(VERSIONING_MENU + "|" + VERSIONING_EXPLORER, VERSIONING_EXPLORER).perform(B_FileNode);
        VersioningFrameOperator versioningExplorer = new VersioningFrameOperator();
        filesystemNode = new Node(versioningExplorer.treeVersioningTreeView(), filesystem);
        new Action(null, REFRESH_REVISIONS).perform(new Node(filesystemNode, "test [Current]|B_File.java [Current]"));
        MainWindowOperator.getDefault().waitStatusText("Command REVISION_LIST finished.");
        new Node(filesystemNode, "test [Current]|B_File.java [Current]|1.1  Checked in from VCS group.").select();
        versioningExplorer.close();
        System.out.println(". done !");
    }

    /** Tries to verify and correct VCS group.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testVerifyGroup() throws Exception {
        System.out.print(".. Testing VCS group verification ..");
        new Action(VERSIONING_MENU + "|" + VCS_GROUPS, null).performMenu();
        VCSGroupsFrameOperator groupsWindow = new VCSGroupsFrameOperator();
        groupsWindow.verifyVCSGroup("<Default Group>");
        GroupVerificationOperator verifyDialog = new GroupVerificationOperator();
        verifyDialog.checkRemoveFilesFromGroup(true);
        if (!verifyDialog.tabNotChangedFiles().getModel().getValueAt(0, 0).equals("B_File"))
            captureScreen("Error: B_File [Current] was not taken for correction.");
        verifyDialog.correctGroup();
        Thread.sleep(2000);
        if (new Node(groupsWindow.treeVCSGroupsTreeView(), "<Default Group>").getChildren().length != 0)
            captureScreen("Error: VCS group was not corrected.");
        groupsWindow.close();
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        new UnmountFSAction().perform(filesystemNode);
        Thread.currentThread().sleep(5000);
        if (filesystemNode.isPresent()) captureScreen("Error: Unable to unmount filesystem.");
        System.out.println(". done !");
    }
}