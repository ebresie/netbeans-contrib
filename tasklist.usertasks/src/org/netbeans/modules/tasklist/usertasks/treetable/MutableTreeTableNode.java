/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.treetable;

import javax.swing.tree.MutableTreeNode;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableNode;

/**
 * A Mutable node for a TreeTable
 *
 * @author Tim Lebedkov
 */
public interface MutableTreeTableNode extends MutableTreeNode, TreeTableNode {
    /**
     * Sets the value for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public void setValueAt(Object aValue, int column);
}
