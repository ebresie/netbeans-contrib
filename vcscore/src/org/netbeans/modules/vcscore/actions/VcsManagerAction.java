/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.actions;

import org.netbeans.modules.vcscore.ui.fsmanager.*;

import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.openide.*;

import javax.swing.SwingUtilities;
import java.awt.*;
/**
* VCS Manager Action.
*
* @author   Richard Gregor
*/
public class VcsManagerAction extends CallableSystemAction {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -3240759228704433330L;

    private Dialog dlg;
    
    public VcsManagerAction() {
        putValue("noIconInMenu", Boolean.TRUE);
    }
    /** Human presentable name of the action. This should be
     * presented as an item in a menu.
     * @return the name of the action
     */
    public String getName () {
        return NbBundle.getBundle (VcsManagerAction.class).getString ("CTL_VcsManagerActionName");
    }

    /** The action's icon location.
     * @return the action's icon location
     */
    protected String iconResource () {        
        return "org/netbeans/modules/vcscore/actions/VcsManagerActionIcon.gif"; // NOI18N
    }

    /** Help context where to find more about the action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx () {
        return null;
    }

    /** This method is called by one of the "invokers" as a result of
     * some user's action that should lead to actual "performing" of the action.
     * This default implementation calls the assigned actionPerformer if it
     * is not null otherwise the action is ignored.
     */
    public void performAction () {
        if (dlg != null) {
            if (dlg.isShowing()) {
                dlg.toFront();
            } else {
                dlg.show ();
            }
            return ;
        }
        Object[] options = new Object[]{ NbBundle.getMessage(VcsManagerAction.class, "LBL_VcsManagerClose") };     
        DialogDescriptor desc = new DialogDescriptor(getVcsManager(),NbBundle.getBundle (VcsManagerAction.class).getString ("CTL_VcsManagerTitle"));        
        desc.setOptions(options);
        desc.setValue(options[0]);
        desc.setClosingOptions(options);        
        desc.setModal(false);
                                                     
        dlg = DialogDisplayer.getDefault ().createDialog (desc);
        dlg.show ();
     
    }
    
    public void closeVcsManager() {
        if (dlg != null) {
            dlg.setVisible(false);
            dlg.dispose();
            dlg = null;
        }
    }
    
    private VcsManager getVcsManager(){
        return VcsManager.getInstance();
    }
    
    protected boolean asynchronous(){
        return false;
    }
}
