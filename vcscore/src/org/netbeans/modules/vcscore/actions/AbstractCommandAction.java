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

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/** Action that keeps a list of vcs supported activated nodes, subclasses can delegate the recognition of such
 * nodes to this class to achieve performance improvement.
 * (splits the nodes to fileobjects)
 *  Vcs Enabled fileobjects are recognized by the fileobject attribute named
 *  "VcsActionAttributeCookie", which value should be an instance of the CommandActionSupporter class.
 *
 *
 * @author  Milos Kleint
 * @deprecated It's here only for backward compatibility.. I guess it can be removed..
 */
public class AbstractCommandAction extends NodeAction {

    /**
     * Name of a FileObject attribute. Needs to be set on primary file of a node(dataobject)
     * in order to trigger the GeneralCommandAction and it's suclasses.
     * The value of the attribute is the 
     * @deprecated
     */
    
    public static final String VCS_ACTION_ATTRIBUTE = "VcsActionAttributeCookie"; //NOI18N
   
    /** 
     * a property accessible via the getValue() method.
     * for VcsGroup nodes it holds the description of the group. Otherwise null.
     * Can be used within the CommandActionSupporters in the modules.
     * @deprecated
     */
    public static final String GROUP_DESCRIPTION_PROP = "GROUP_DESCRIPTION"; //NOI18N
    /** 
     * a property accessible via the getValue() method.
     * for VcsGroup nodes it holds the display name of the group. Otherwise null.
     * Can be used within the CommandActionSupporters in the modules. eg. for commit message.
     * @deprecated
     */
    public static final String GROUP_NAME_PROP = "GROUP_NAME"; //NOI18N
    
    
    
    static final long serialVersionUID = 3425234373723671084L;    
    
    public AbstractCommandAction() {
        super();
//        System.out.println("constructor abstract");
    }
    
    protected boolean enable (Node[] nodes) {
        return false;
    }
    
    
    protected void performAction (Node[] nodes) {
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (JavaCvsCommandAction.class);
    }
    public String getName () {
        return "";
    }
    

    protected String iconResource () {
        return null;
    }
    
    
}
