/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the ETable module. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2007 Nokia. All Rights Reserved.
 */
package org.netbeans.swing.etable;

/**
 * This class allows to supply alternative implementation of the column
 * selection functionality in ETable.
 *
 * @author David Strupl
 */
public abstract class TableColumnSelector {
    
    /**
     * Presents the user vith a dialog allowing to select visible columns
     * from the available column. The available columns are presented as
     * a tree.
     * @param root root of the displayed tree of the available columns
     * @param selected the originally selected columns
     * @return column names that are selected as visible columns
     */
    public abstract String[] selectVisibleColumns(TreeNode root, String[] selected);
    /**
     * Presents the user vith a dialog allowing to select visible columns
     * from the available column. The available columns are presented as
     * a flat list.
     * @param available all available column names
     * @param selected the originally selected columns
     * @return column names that are selected as visible columns
     */
    public abstract String[] selectVisibleColumns(String[] available, String[] selected);
    
    /**
     * A node in the tree of available column names. Only the leaf nodes
     * represent column names, the non-leaf nodes are categories which
     * cannot be selected by the user.
     */
    public interface TreeNode {
        /**
         * The text displayed to the user as a column name.
         * @return The text displayed to the user as a column name.
         */
        public String getText();
        /**
         * Non-leaf nodes will have children nodes.
         * @return true if the node represents a column, false means a category
         *
         */
        public boolean isLeaf();
        /**
         * Children of this node.
         * @return the array of children nodes, can return null if method
         *     isLeaf() returns true.
         */
        public TreeNode[] getChildren();
    }
}
