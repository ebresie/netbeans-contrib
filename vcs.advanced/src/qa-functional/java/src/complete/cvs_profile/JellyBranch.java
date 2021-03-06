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

package complete.cvs_profile;

import complete.GenericStub.GenericNode;
import java.io.File;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.actions.CustomizeAction;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.VCSFilesystemCustomizerDialog;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSVersioningBranchNode;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSVersioningFileNode;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSVersioningVersionNode;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.jellytools.modules.vcsgeneric.cvs_profile.*;


public class JellyBranch extends CVSStub {
    
    public JellyBranch(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyBranch("configure"));
        suite.addTest(new JellyBranch("testInit"));
        suite.addTest(new JellyBranch("testCheckOut"));
        suite.addTest(new JellyBranch("testAdd"));
        suite.addTest(new JellyBranch("testCommit"));
        suite.addTest(new JellyBranch("testVersioning"));
        suite.addTest(new JellyBranch("testAddTag"));
        suite.addTest(new JellyBranch("testAddBranchTag"));
        suite.addTest(new JellyBranch("testCommitToBranch"));
        suite.addTest(new JellyBranch("testUpdateHead"));
        suite.addTest(new JellyBranch("testRemoveStickyTagFromHead"));
        suite.addTest(new JellyBranch("testCommitHead"));
        suite.addTest(new JellyBranch("testUpdateFromBranch"));
        suite.addTest(new JellyBranch("testRemoveStickyTagFromBranch"));
        suite.addTest(new JellyBranch("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    static String tInitDir = "initdir", tInitSubDir = "initsubdir";
    GenericNode InitDir, InitSubDir, Directory, File, SubDir, SubFile, Text1, Text2, Binary;
    
    public void prepareServer (String dir) {
        new File(dir + "/" + tInitDir).mkdirs();
        new File(dir + "/" + tInitDir + "/" + tInitSubDir).mkdirs();
    }
    
    public void createStructure() {
        InitDir = new GenericNode (root, tInitDir);
        InitSubDir = new GenericNode (InitDir, tInitSubDir);
        Directory = new GenericNode (root, "directory");
        File = new GenericNode (Directory, "file");
        SubDir = new GenericNode (Directory, "subdir");
        SubFile = new GenericNode (SubDir, "subfile");
        Text1 = new GenericNode (InitDir, "text1");
        Text2 = new GenericNode (InitDir, "text2");
        Binary = new GenericNode (InitSubDir, "binary");
    }
    
    public void prepareClient () {
        // advanced mode
        new CustomizeAction ().performPopup (root.cvsNode ());
        VCSFilesystemCustomizerDialog dia = new VCSFilesystemCustomizerDialog ();
        dia.checkAdvancedMode(true);
        dia.close ();
        dia.waitClosed ();
    }

    public void configure () {
        super.configure();
    }

    public void testUnmount() {
        new FilesystemNode(repository.tree(), root.node ()).unmount();
        new Node (repository.tree (), "").waitChildNotPresent(root.node ());
    }
    
    public void testInit() {
        root.cvsNode ().cVSInit ();
        history.getWaitCommand("Init", root.history ());
    }
    
    public void testCheckOut() {
        root.cvsNode ().cVSCheckOut();
        CVSCheckoutFolderAdvDialog co = new CVSCheckoutFolderAdvDialog();
        co.setModuleS(hRoot);
        co.checkPruneEmptyFolders(false);
        sleep(1000);
        getLog ().println (co.cbPruneEmptyFolders ().isSelected ());
        co.oK();
        root.waitHistory ("Check Out");
        
        // workaround - probably jelly issue - if not used, popup menu does not work in versioning frame
//        VCSCommandsOutputOperator voo = new VCSCommandsOutputOperator ("Check Out");
//        closeAllVCSWindows();
        
        InitDir.waitStatus(null);
        refreshRecursively(root);
        InitDir.waitStatus(null);
        InitSubDir.waitStatus(null);
    }

    public void testAdd() {
        SubDir.mkdirs ();
        File.save ("");
        SubFile.save ("");
        
        refreshRecursively (root);
        Directory.cvsNode ();
        SubDir.cvsNode ();
        File.cvsNode ();
        SubFile.cvsNode ();
//        Directory.waitStatus("Local"); // stabilization
//        SubDir.waitStatus("Local"); // stabilization
        File.waitStatus("Local");
        SubFile.waitStatus("Local");
        
/* commented due to issue #27582 - failure of add command
        new CVSFileNode(repository.tree(), nDirectory).cVSAdd();
        CVSAddFolderAdvDialog add = new CVSAddFolderAdvDialog();
        add.setFileDescription("Initial state");
        add.addAllLocalFilesInFolderContents();
        add.oK();
        new NbDialogOperator("Information").ok();
        assertTrue("Add folder command failed", history.waitCommand("Add", hDirectory));
        waitStatus(null, nDirectory);
        waitStatus(null, nSubDir);
        waitStatus("Locally Added", nFile);
        waitStatus("Local", nSubFile);
*/
        // workaround for issue #27582
        Directory.cvsNode ().cVSAdd();
        CVSAddFolderAdvDialog add = new CVSAddFolderAdvDialog();
        add.setFileDescription("Initial state");
        add.oK();
//        assertInformationDialog (null);
        Directory.waitHistory ("Add");
        Directory.waitStatus(null);
        SubDir.waitStatus("Local");
        File.waitStatus("Local");
        SubFile.waitStatus("Local");
        File.cvsNode ().cVSAdd();
        CVSAddFileAdvDialog addf = new CVSAddFileAdvDialog();
        addf.setFileDescription("Initial state");
        addf.oK();
        File.waitHistory ("Add");
        Directory.waitStatus(null);
        SubDir.waitStatus("Local");
        File.waitStatus("Locally Added", false);
        SubFile.waitStatus("Local");
    }
    
    public void testCommit () {
        File.waitStatus("Locally Added", false);
        File.cvsNode ().cVSCommit();
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog ();
        co.txtEnterReason().setCaretPosition(0);
        co.txtEnterReason().typeText("Initial commit");
        co.oK ();
        File.waitHistory ("Commit");
        File.waitStatus("Up-to-date; 1.1");
    }

    public void testVersioning () {
        closeAllVCSWindows ();
        root.cvsNode ().versioningExplorer();
        newVersioningFrame();
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), File.node ()).refreshRevisions();
        File.waitHistory("REVISION_LIST");
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), File.node ());
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), File.node () + "|1.1  Initial commit");
    }

    public void testAddTag () {
        closeAllVCSWindows ();
        File.cvsNode ().versioningExplorer();
        newVersioningFrame ();
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), File.node ()).cVSBranchingAndTaggingAddTag();
        CVSAddTagFileAdvDialog at = new CVSAddTagFileAdvDialog ();
        at.setTagName("Tag1");
        at.oK ();
        File.waitHistory ("Add Tag");
        assertInformationDialog (null);
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), File.node ()).refreshRevisions();
//        new org.netbeans.jellytools.actions.Action (null, "Refresh Revisions").performPopup (new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile));
        File.waitHistory ("REVISION_LIST");
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), File.node () + "|1.1  Initial commit");
    }
    
    public void testAddBranchTag () {
        closeAllVCSWindows ();
        File.cvsNode ().versioningExplorer();
        newVersioningFrame ();
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), File.node ()).cVSBranchingAndTaggingAddTag();
        CVSAddTagFileAdvDialog at = new CVSAddTagFileAdvDialog ();
        at.select ();
        CVSRevisionSelectorDialog rev = new CVSRevisionSelectorDialog ();
        rev.lstCVSRevisionSelector().clickOnItem("1.1  Tag1");
        rev.oK ();
        at.setTagName("Tag2");
        at.checkBranchTag(true);
        at.oK ();
        File.waitHistory ("Add Tag");
        assertInformationDialog (null);
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), File.node ()).refreshRevisions();
        File.waitHistory ("REVISION_LIST");

        // workaround
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1]");
        Node nn = new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1]|1.1  Initial commit");
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1]|1.1  Initial commit").select ();
        vfo.treeVersioningTreeView ().clickOnPath (new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1]|1.1  Initial commit").getTreePath(), 2); // workaround
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1]|1.1  Initial commit").expand ();
        String[] strs = nn.getChildren();
        if (strs != null) {
            info.println ("--- Children Count: " + strs.length + " ---");
            for (int a = 0; a < strs.length; a ++)
                info.println (strs[a]);
        } else
            info.println ("--- No Children ---");
        new CVSVersioningBranchNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1] (Tag2)|1.1  Initial commit|1.1.2").select ();
        vfo.treeVersioningTreeView ().clickOnPath (new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1]|1.1  Initial commit|1.1.2").getTreePath(), 2);
        new CVSVersioningBranchNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1] (Tag2)|1.1  Initial commit|1.1.2").expand ();
        
        new CVSVersioningBranchNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1] (Tag2)|1.1  Initial commit|1.1.2|1.1.2.1  Branch Commit").select ();
    }
    
    public void testCommitToBranch () {
        File.save ("text 1.1.2.1\n");
        refresh (Directory);

        closeAllVCSWindows();
        File.cvsNode ().versioningExplorer();
        newVersioningFrame ();
        new CVSVersioningFileNode (vfo.treeVersioningTreeView(), Directory.node ()).cVSRefresh();
        Directory.waitHistory ("Refresh");
        new CVSVersioningFileNode (vfo.treeVersioningTreeView(), File.node () + " [Locally Modified; 1.1]").cVSCommit();
        
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog ();
        co.select ();
        CVSBranchSelectorDialog bs = new CVSBranchSelectorDialog ();
        bs.lstCVSBranchSelector().clickOnItem ("1.1.2  Tag2");
        bs.oK ();
        co.txtEnterReason().setCaretPosition(0);
        co.txtEnterReason().typeText("Branch Commit");
        co.oK ();

        File.waitHistory ("Commit");
        File.waitStatus ("Up-to-date; 1.1.2.1");
        File.waitVersion (null);
        refresh (Directory);
        File.waitStatus ("Up-to-date; 1.1.2.1");
        File.waitVersion ("Tag2");

        // stabilization
        closeAllVCSWindows ();
        File.cvsNode ().versioningExplorer();
        newVersioningFrame ();

        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1.2.1] (Tag2)").refreshRevisions();
        File.waitHistory ("REVISION_LIST");
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1.2.1] (Tag2)");
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1.2.1] (Tag2)|1.1  Initial commit");
        new CVSVersioningBranchNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1.2.1] (Tag2)|1.1  Initial commit|1.1.2 (Tag2)");
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), File.node () + " [Up-to-date; 1.1.2.1] (Tag2)|1.1  Initial commit|1.1.2 (Tag2)|1.1.2.1  Branch Commit");
    }
    
    public void testUpdateHead () {
        File.cvsNode ();
        File.waitStatus ("Up-to-date; 1.1.2.1");
        File.waitVersion ("Tag2");
        File.cvsNode ().cVSUpdate ();
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog ();
        up.select();
        CVSRevisionSelectorDialog rev = new CVSRevisionSelectorDialog ();
        rev.lstCVSRevisionSelector().clickOnItem ("HEAD");
        rev.oK ();
        up.oK ();
        File.waitHistory ("Update");
        File.waitStatus ("Up-to-date; 1.1");
        File.waitVersion ("HEAD");
    }
    
    public void testRemoveStickyTagFromHead () {
        refresh (File);
        File.waitStatus ("Up-to-date; 1.1");
        File.waitVersion ("HEAD");
        File.cvsNode ().cVSUpdate ();
        CVSUpdateFileAdvDialog upd = new CVSUpdateFileAdvDialog ();
        upd.checkResetStickyTagsDates(true);
        upd.oK();
        File.waitHistory ("Update");
        assertInformationDialog (null);
        File.waitStatus ("Up-to-date; 1.1");
        File.waitVersion (null);
    }
    
    public void testCommitHead () {
        File.save ("text 1.2\n");

        refresh (File);
        File.waitStatus ("Locally Modified; 1.1");
        File.waitVersion (null);
        
        File.cvsNode ().cVSCommit();
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog ();
        co.txtEnterReason().setCaretPosition(0);
        co.txtEnterReason().typeText("HEAD Commit");
        co.oK ();
        File.waitHistory ("Commit");
        File.waitStatus ("Up-to-date; 1.2");
        File.waitVersion (null);
    }
    
    public void testUpdateFromBranch () {
        File.waitStatus ("Up-to-date; 1.2");
        File.waitVersion (null);
        File.cvsNode ().cVSUpdate();
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog ();
        up.select();
        CVSRevisionSelectorDialog rev = new CVSRevisionSelectorDialog ();
        rev.lstCVSRevisionSelector().clickOnItem ("1.1.0.2  Tag2");
        rev.oK ();
        up.oK ();
        File.waitHistory ("Update");
        File.waitStatus ("Up-to-date; 1.1.2.1");
        File.waitVersion ("Tag2");
    }
    
    public void testRemoveStickyTagFromBranch () {
        refresh (File);
        File.waitStatus ("Up-to-date; 1.1.2.1");
        File.waitVersion ("Tag2");
        CVSUpdateFileAdvDialog upd = new CVSUpdateFileAdvDialog ();
        upd.checkResetStickyTagsDates(true);
        upd.oK();
        File.waitHistory ("Update");
        assertInformationDialog (null);
        File.waitStatus ("Up-to-date; 1.2");
        File.waitVersion (null);
    }
    
}
