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

package complete.vss_profile;

import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.jemmy.operators.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.netbeans.jellytools.modules.vcsgeneric.vss.*;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;

/** XTest / JUnit test class performing additional commands and options testing on VSS filesystem.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class AdditionalCommands extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    public static String REFRESH = "VSS|Refresh";
    public static String REFRESH_RECURSIVELY = "VSS|Refresh Recursively";
    public static String REMOVE = "VSS|Remove";
    public static String RECOVER = "VSS|Recover";
    public static String CHECK_OUT = "VSS|Check Out";
    public static String CHECK_IN = "VSS|Check In";
    public static String HISTORY = "VSS|History";
    public static String PROPERTIES = "VSS|Properties";
    public static String GET_LATEST_VERSION = "VSS|Get Latest Version";
    public static String UNDO_CHECK_OUT = "VSS|Undo Check Out";
    public static String workingDirectory;
    public static String userName;
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public AdditionalCommands(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return AdditionalCommands test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        if (Utilities.isUnix()) return suite;
        String zipFile = "C:\\Program Files\\Microsoft Visual Studio\\vss.zip";
        if (!new File(zipFile).exists()) return suite; // This test suite can't run where zip with empty VSS repository is not prepared.
        suite.addTest(new AdditionalCommands("testRemoveFile"));
        suite.addTest(new AdditionalCommands("testRecoverFile"));
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
    
    /** Tries to remove a file from VSS project repository.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testRemoveFile() throws Exception {
        System.out.print(".. Testing removal of a file ..");
        String workingPath = getWorkDirPath();
        workingDirectory = workingPath.substring(0, workingPath.indexOf("AdditionalCommands")) + "RepositoryCreation" + File.separator + "testCreateProjects";
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current]");
        new Action(VERSIONING_MENU + "|" + REMOVE, REMOVE).perform(D_FileNode);
        NbDialogOperator information = new NbDialogOperator("Information");
        new JLabelOperator(information, "The file \"D_File.java\" was removed successfully.");
        information.ok();
        D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Local]");
        System.out.println(". done !");
    }

    /** Tries to recover a file back to VSS project repository.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testRecoverFile() throws Exception {
        System.out.print(".. Testing file recovery ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Local]");
        new Action(VERSIONING_MENU + "|" + RECOVER, RECOVER).perform(D_FileNode);
        NbDialogOperator information = new NbDialogOperator("Information");
        new JLabelOperator(information, "The file \"D_File.java\" was recovered successfully.");
        information.ok();
        D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current]");
        System.out.println(". done !");
    }
}