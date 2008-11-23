/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.contrib.testng.output;

import java.awt.EventQueue;
import java.util.Collection;
import javax.swing.Action;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.contrib.testng.actions.RerunFailedTestsAction;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Marian Petras
 */
final class RootNode extends AbstractNode {

    /** */
    static final String name = "TestNG results root node";               //NOI18N
    /** constant meaning "information about passed tests not displayed" */
    static final int ALL_PASSED_ABSENT = 0;
    /** constant meaning "information about some passed tests not displayed" */
    static final int SOME_PASSED_ABSENT = 1;
    /** constant meaning "information about all passed tests displayed */
    static final int ALL_PASSED_DISPLAYED = 2;
    /**
     */
    private final RootNodeChildren children;
    /**
     */
    private volatile boolean filtered;
    /** */
    private volatile String message;
    private volatile int totalTests = 0;
    private volatile int failures = 0;
    private volatile int skips = 0;
    private volatile int interruptedTests = 0;
    private volatile int elapsedTimeMillis = 0;
    private volatile int detectedPassedTests = 0;
    private boolean sessionFinished;
    private InstanceContent ic;

    private RootNode(final boolean filtered, InstanceContent ic) {
        super(new RootNodeChildren(filtered), new AbstractLookup(ic));
        this.filtered = filtered;
        this.ic = ic;
        children = (RootNodeChildren) getChildren();
        setName(name);   //used by tree cell renderer to recognize the root node
        setIconBaseWithExtension(
                "org/netbeans/modules/contrib/testng/resources/empty.gif");     //NOI18N
    }

    /**
     * Creates a new instance of RootNode
     */
    public RootNode(final boolean filtered) {
        this(filtered, new InstanceContent());
    }

    /**
     */
    void displayMessage(final String msg) {
        assert EventQueue.isDispatchThread();

        /* Called from the EventDispatch thread */

        this.message = msg;
        updateDisplayName();
    }

    /**
     * Updates the display when the session is finished.
     *
     * @param  msg  optional message to be displayed (e.g. notice that
     *              the sessions has been interrupted); or {@code null}
     */
    void displayMessageSessionFinished(final String msg) {
        sessionFinished = true;
        displayMessage(msg);
    }

    /**
     * Displays a message that a given test suite is running.
     *
     * @param  suiteName  name of the running test suite,
     *                    or {@code ANONYMOUS_SUITE} for anonymous suites
     *
     * @see  ResultDisplayHandler#ANONYMOUS_SUITE
     */
    void displaySuiteRunning(final String suiteName) {
        assert EventQueue.isDispatchThread();

        /* Called from the EventDispatch thread */

        children.displaySuiteRunning(suiteName);
    }

    /**
     */
    TestsuiteNode displayReport(final Report report) {
        assert EventQueue.isDispatchThread();

        /* Called from the EventDispatch thread */

        updateStatistics(report);
        updateDisplayName();
        return children.displayReport(report);
    }

    /**
     */
    void displayReports(final Collection<Report> reports) {
        assert EventQueue.isDispatchThread();

        /* Called from the EventDispatch thread */

        for (Report report : reports) {
            updateStatistics(report);
        }
        updateDisplayName();
        children.displayReports(reports);
    }

    /**
     */
    private void updateStatistics(final Report report) {
        totalTests += report.totalTests;
        failures += report.failures;
        skips += report.skips;
        detectedPassedTests += report.detectedPassedTests;
        interruptedTests += report.interruptedTests;
        elapsedTimeMillis += report.elapsedTimeMillis;
        ic.add(report);
    }

    /**
     */
    void setFiltered(final boolean filtered) {
        assert EventQueue.isDispatchThread();

        if (filtered == this.filtered) {
            return;
        }
        this.filtered = filtered;

        Children lChildren = getChildren();
        if (lChildren != Children.LEAF) {
            ((RootNodeChildren) lChildren).setFiltered(filtered);
        }
    }

    /**
     */
    private void updateDisplayName() {
        assert EventQueue.isDispatchThread();

        /* Called from the EventDispatch thread */

        final Class bundleRefClass = RootNode.class;
        String msg;

        if (totalTests == 0) {
            if (sessionFinished) {
                msg = NbBundle.getMessage(bundleRefClass,
                        "MSG_TestsInfoNoTests");      //NOI18N
            } else {
                msg = null;
            }
        } else if ((failures == 0) && (skips == 0) && (interruptedTests == 0)) {
            msg = NbBundle.getMessage(bundleRefClass,
                    "MSG_TestsInfoAllOK", //NOI18N
                    Integer.valueOf(totalTests));
        } else {
            StringBuilder buf = new StringBuilder(40);
            buf.append(NbBundle.getMessage(bundleRefClass,
                    "MSG_PassedTestsInfo", //NOI18N
                    totalTests - failures - skips - interruptedTests));
            if ((failures != 0) || (skips != 0)) {
                buf.append(", ");                                       //NOI18N
                buf.append(NbBundle.getMessage(bundleRefClass,
                        "MSG_FailedTestsInfo", //NOI18N
                        failures));
            }
            if (skips != 0) {
                buf.append(", ");                                       //NOI18N
                buf.append(NbBundle.getMessage(bundleRefClass,
                        "MSG_ErrorTestsInfo", //NOI18N
                        skips));
            }
            if (interruptedTests != 0) {
                buf.append(", ");                                       //NOI18N
                buf.append(NbBundle.getMessage(bundleRefClass,
                        "MSG_InterruptedTestsInfo",//NOI18N
                        interruptedTests));
            }
            buf.append('.');
            msg = buf.toString();
        }

        if (totalTests != 0) {
            assert msg != null;
            final int successDisplayedLevel = getSuccessDisplayedLevel();
            switch (successDisplayedLevel) {
                case SOME_PASSED_ABSENT:
                    msg += ' ';
                    msg += NbBundle.getMessage(
                            bundleRefClass,
                            "MSG_SomePassedNotDisplayed");  //NOI18N
                    break;
                case ALL_PASSED_ABSENT:
                    msg += ' ';
                    msg += NbBundle.getMessage(
                            bundleRefClass,
                            "MSG_PassedNotDisplayed");      //NOI18N
                    break;
                case ALL_PASSED_DISPLAYED:
                    break;
                default:
                    assert false;
                    break;
            }
        }

        if (this.message != null) {
            if (msg == null) {
                msg = this.message;
            } else {
                msg = msg + ' ' + message;
            }
        }

        setDisplayName(msg);
    }

    /**
     * Returns information whether information about passed tests is displayed.
     *
     * @return  one of constants <code>ALL_PASSED_DISPLAYED</code>,
     *                           <code>SOME_PASSED_ABSENT</code>,
     *                           <code>ALL_PASSED_ABSENT</code>
     */
    int getSuccessDisplayedLevel() {
        int reportedPassedTestsCount = totalTests - failures - skips - interruptedTests;
        if (detectedPassedTests >= reportedPassedTestsCount) {
            return ALL_PASSED_DISPLAYED;
        } else if (detectedPassedTests == 0) {
            return ALL_PASSED_ABSENT;
        } else {
            return SOME_PASSED_ABSENT;
        }
    }

    @Override
    public Action[] getActions(boolean context) {
        if (getLookup().lookup(Project.class) == null) {
            Collection<? extends Report> rs = getLookup().lookupAll(Report.class);
            for (Report r : rs) {
                if (r.getSourceClassPath() != null) {
                    ClassPath srcClassPath = r.getSourceClassPath();
                    String suiteClassName = r.suiteClassName;
                    String suiteFileName = suiteClassName.replace('.', '/') + ".java"; //NOI18N
                    FileObject suiteFile = srcClassPath.findResource(suiteFileName);
                    if (suiteFile != null) {
                        ic.add(FileOwnerQuery.getOwner(suiteFile));
                        break;
                    }
                }
            }
        }
        return new Action[] {
                    SystemAction.get(RerunFailedTestsAction.class)
                };
    }
}
