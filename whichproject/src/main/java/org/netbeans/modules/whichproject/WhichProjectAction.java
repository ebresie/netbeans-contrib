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

package org.netbeans.modules.whichproject;

import java.awt.Toolkit;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Action which will blink all tabs whichshare the same project as the 
 * current editor tab.
 *
 * @author Tim Boudreau
 */
@ActionID(id = "org.netbeans.modules.whichproject.WhichProjectAction", category = "Project")
@ActionRegistration(displayName = "#LBL_Action")
@ActionReference(path = "Menu/File", position = 1826)
public class WhichProjectAction extends CallableSystemAction {
    
    public void performAction () {
        Mode editorMode = WindowManager.getDefault().findMode("editor");
        boolean didSomething = false;
        if (editorMode != null) {
            TopComponent selTC = editorMode.getSelectedTopComponent();
            if (selTC != null) {
                Project project = projectFor(selTC);
                if (project != null) {
                    TopComponent[] tc = editorMode.getTopComponents();
                    for (int i=0; i < tc.length; i++) {
                        Project p = projectFor(tc[i]);
                        didSomething |= processTc(tc[i], project.equals(p));
                    }
                }
            }
        }
        if (!didSomething) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    protected boolean processTc (TopComponent tc, boolean val) {
        if (val) {
            tc.requestAttention(true);
        }
        return val;
    }

    public String getName () {
        return NbBundle.getMessage ( WhichProjectAction.class, "LBL_Action" ); //NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

    public boolean asynchronous () {
        return false;
    }
    
    private Project projectFor (TopComponent tc) {
        Node[] nodes = tc.getActivatedNodes();
        if (nodes == null) {
            return null;
        }
        DataObject obj = null;
        for (int i=0; i < nodes.length; i++) {
            obj = nodes[i].getCookie(DataObject.class);
            if (obj != null) {
                FileObject file = obj.getPrimaryFile();
                if (obj != null) {
                    Project p = FileOwnerQuery.getOwner(file);
                    if (p != null) {
                        return p;
                    }
                }
            }
        }
        return null;
    }
}

