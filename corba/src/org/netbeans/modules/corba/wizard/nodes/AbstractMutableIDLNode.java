/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard.nodes;

import java.io.OutputStream;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.NamedKey;
/** 
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
abstract public class AbstractMutableIDLNode extends AbstractNode {

    protected SystemAction[] actions;
    protected final NamedKey key;
  
    /** Creates new AbstractMutableIDLNode */
    public AbstractMutableIDLNode(Children children, NamedKey key) {
        super (children);
        this.key = key;
    }
  
    public boolean canCut () {
        return false;
    }
  
    public boolean canCopy () {
        return false;
    }
  
    public boolean canDestroy () {
        return true;
    }
  
    public boolean canRename () {
        return true;
    }
  
    public SystemAction[] getActions () {
        if (this.actions == null)
        this.actions = this.createActions();
        return this.actions;
    }
  
    public void destroy () {
        Node node = this.getParentNode ();
        if (node == null)
            return;
        Children cld = node.getChildren ();
        if (! (cld instanceof MutableChildren))
            return;
        ((MutableChildren)cld).removeKey (this.key);
    }
  
    public abstract String generateSelf (int indent);
  
    public abstract SystemAction[] createActions ();
}
