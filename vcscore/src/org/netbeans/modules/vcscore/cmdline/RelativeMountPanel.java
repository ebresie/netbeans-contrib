/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.cmdline;

import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import org.openide.WizardDescriptor;
import org.openide.util.*;

import org.netbeans.modules.vcscore.*;
/**
 *
 * @author  mkleint
 * @version 
 */
public class RelativeMountPanel extends javax.swing.JPanel implements TreeSelectionListener {//, javax.swing.event.TreeExpansionListener {
    
    static final long serialVersionUID =-6389940806020132699L;
    //DefaultMutableTreeNode rootNode;
    //String cvsRoot;
    
    public RelativeMountPanel() {
        initComponents ();
        //isValid = false;
        addTreeListeners();
        //trRelMount.addTreeSelectionListener(this);
        //trRelMount.addTreeExpansionListener(this);
        //cvsRoot = "";
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        lbRelMount = new javax.swing.JLabel();
        txRelMount = new javax.swing.JTextField();
        lbWaiting = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        trRelMount = new javax.swing.JTree();
        setLayout(new java.awt.BorderLayout());
        setDoubleBuffered(false);
        setPreferredSize(new java.awt.Dimension(419, 176));
        setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        setMinimumSize(new java.awt.Dimension(263, 176));
        
        jPanel1.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        lbRelMount.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcscore/cmdline/Bundle").getString("NewCvsCustomizer.lbRelMount.text"));
          gridBagConstraints1 = new java.awt.GridBagConstraints();
          gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
          gridBagConstraints1.insets = new java.awt.Insets(6, 12, 11, 5);
          gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
          jPanel1.add(lbRelMount, gridBagConstraints1);
          
          
        txRelMount.setPreferredSize(new java.awt.Dimension(120, 20));
          txRelMount.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
          txRelMount.setMinimumSize(new java.awt.Dimension(100, 20));
          txRelMount.setEditable(false);
          gridBagConstraints1 = new java.awt.GridBagConstraints();
          gridBagConstraints1.gridx = 1;
          gridBagConstraints1.gridy = 0;
          gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
          gridBagConstraints1.ipadx = 37;
          gridBagConstraints1.ipady = 1;
          gridBagConstraints1.insets = new java.awt.Insets(6, 7, 11, 11);
          gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
          gridBagConstraints1.weightx = 1.0;
          gridBagConstraints1.weighty = 1.0;
          jPanel1.add(txRelMount, gridBagConstraints1);
          
          
        add(jPanel1, java.awt.BorderLayout.SOUTH);
        
        
        
        add(lbWaiting, java.awt.BorderLayout.NORTH);
        
        
        jPanel2.setLayout(new java.awt.GridLayout(1, 1, 10, 1));
        
        
          trRelMount.setMaximumSize(new java.awt.Dimension(9999, 9999));
            trRelMount.setLargeModel(true);
            trRelMount.setShowsRootHandles(true);
            jScrollPane1.setViewportView(trRelMount);
            
            jPanel2.add(jScrollPane1);
          
          
        add(jPanel2, java.awt.BorderLayout.CENTER);
        
    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbRelMount;
    private javax.swing.JTextField txRelMount;
    private javax.swing.JLabel lbWaiting;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree trRelMount;
    // End of variables declaration//GEN-END:variables
    
    public String getName() {
        return org.openide.util.NbBundle.getBundle(RelativeMountPanel.class).getString("RelativeMountPanel.mountDialogLabel");
    }
    
    public void setInfoLabel(String text) {
        lbWaiting.setText(text);
    }
    
    //public void setLast(boolean last) {
    //    isLastPanel = last;
    //}
/** last panel check.
 * @return true if last else false.
 */    
    
    //public boolean isLast() {
    //    D("is last");
    //    return isLastPanel;
    //}
    
    //public int getNext() {
    //    return 2;
    //}
/** Get the component displayed in this panel.
 * @return the component
 */
    public java.awt.Component getComponent() {
        return this;
    }
/** Help for this panel.
 * When the panel is active, this is used as the help for the wizard dialog.
 * @return the help or <code>null</code> if no help is supplied
 */
    public HelpCtx getHelp() {
        return null;
    }
    
    protected void setEnabledTree(boolean enabled) {
        trRelMount.setEnabled(enabled);
    }
    
    protected void setTreeModel(TreeModel model) {
        trRelMount.setModel(model);
    }
    
    public String getRelMount() {
/*  TODO - return null if nothing selected      
        DefaultMutableTreeNode nd = (DefaultMutableTreeNode)trRelMount.getSelectionPath().getLastPathComponent();
        MyFile fl = (MyFile)nd.getUserObject();
        MyFile rootFl = (MyFile)cvsRoot.getUserObject();
        String toWrite = fl.getAbsolutePath().substring(.getAbsolutePath().length());
            if (toWrite.startsWith(File.separator)) {
                toWrite = toWrite.substring(File.separator.length());
            }
 */
        return txRelMount.getText();
    }

    /*
    private void doMarkParent(DefaultMutableTreeNode parent, File current) {
       MyFile fl = (MyFile)parent.getUserObject(); 
       if (!parent.equals(rootNode)) {
         fl.setLocal(false);
       }
       else { // special handling of root node..
          if (fl.isLocal()) return; // already failed to be cvs dir
          if (current.getName().equalsIgnoreCase("CVS")) return; // not a real subdir of root
          File[] files = current.listFiles(new FileFilter()  {
              public boolean accept(File f) {
                 if (f.isDirectory() && f.getName().equalsIgnoreCase("CVS")) return true;
                 return false;
              }
              
          });
          if (files.length > 0) { //found cvs dir in subdircetory
             File cvsDir = files[0];
             String rootStr = returnCvsRoot(new File(cvsDir.getAbsolutePath() + File.separator + "Root"));
             if (rootStr.equals("")) {
                fl.setLocal(true);
                return;
             }
             if (cvsRoot.equals("")) { cvsRoot = rootStr;}
             else { 
                 if (!cvsRoot.equals(rootStr)) { // subdirs checked out from different cvs servers
                     fl.setLocal(true);
                 }
             }
          }
          else { // one subdir of root is not a cvs dir..
              fl.setLocal(true);
          }
       }
    }
    
    private String returnCvsRoot(File fileName) {
       String toReturn = "";
       if (fileName.exists() && fileName.canRead() ){
            BufferedReader in = null;
            try {
              in= new BufferedReader
                   (new InputStreamReader
                    (new BufferedInputStream
                     (new FileInputStream(fileName))));
                toReturn =in.readLine();
                in.close();
            }catch (IOException e){
                toReturn = "";
            } finally {
                try {
                  if (in != null) { in.close(); in = null;}
                }
                catch (IOException exc) {}
            }
        }
      return toReturn;
    }
     */

    private void addTreeListeners() {
        trRelMount.addTreeSelectionListener(this);
        trRelMount.addTreeWillExpandListener(new TreeWillExpandListener() {
            public void treeWillExpand(TreeExpansionEvent evt) {
                TreePath path = evt.getPath();
                final MyTreeNode node = (MyTreeNode) path.getLastPathComponent();
                Runnable treeBuild = new Runnable() {
                    public void run() {
                        folderTreeNodes(node);
                    }
                };
                new Thread(treeBuild, "Mount Panel Tree Build").start();
            }
            public void treeWillCollapse(TreeExpansionEvent evt) {
            }
        });
    }

    private void createTree(String rootString) {
        setTreeModel(new DefaultTreeModel(new MyTreeNode()));
        File rootDir = new File(rootString);
        if (rootDir.isDirectory()) {
            final MyTreeNode root = new MyTreeNode(rootDir);
            Runnable treeBuild = new Runnable() {
                public void run() {
                    //recursiveTreeNodes(root);
                    folderTreeNodes(root);
                }
            };
            new Thread(treeBuild, "Mount Panel Tree Build").start();
            DefaultTreeCellRenderer  def = new MyTreeCellRenderer();
            trRelMount.setCellRenderer(def);
            trRelMount.setModel(new DefaultTreeModel(root));
        }
    }
    
    private void folderTreeNodes(final MyTreeNode parent) {
        boolean hasChild = false;
        synchronized (trRelMount) {
            parent.removeAllChildren();
            File parentFile = (File) parent.getUserObject();
            MyTreeNode child;
            File childFile;
            File[] list = parentFile.listFiles();
            Arrays.sort(list);
            for (int index = 0; index < list.length; index++) {
                if (list[index].isDirectory() && list[index].exists()) {
                    childFile = list[index];
                    if (!childFile.getName().equals("CVS")) { //CVS dirs go out..
                        hasChild = true;
                        child = new MyTreeNode(new File(childFile.getAbsolutePath()));
                        child.setAllowsChildren(true);
                        parent.add(child);
                    }
                }
            }
            if (!hasChild) {
                parent.setAllowsChildren(false);
                trRelMount.collapsePath(new TreePath(parent.getPath()));
            }
        }
        ((DefaultTreeModel) trRelMount.getModel()).nodeStructureChanged(parent);
        if (hasChild) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    trRelMount.scrollPathToVisible(new TreePath(((MyTreeNode) parent.getLastChild()).getPath()));
                    trRelMount.scrollPathToVisible(new TreePath(((MyTreeNode) parent.getFirstChild()).getPath()));
                }
            });
        }
    }
    
    /*
    private void recursiveTreeNodes(DefaultMutableTreeNode parent) {
        File parentFile = (File)parent.getUserObject();
        DefaultMutableTreeNode child;
        File childFile;
        boolean hasChild = false;
        File[] list = parentFile.listFiles();
        if (list == null) return;
        for (int index = 0; index < list.length; index++) {
            if (list[index].isDirectory() && list[index].exists()) {
                childFile = list[index];
                if (!childFile.getName().equals("CVS")) { //CVS dirs go out..
                    hasChild = true;
                    child = new DefaultMutableTreeNode(new MyFile(childFile.getAbsolutePath()));
                    parent.add(child);
                    recursiveTreeNodes(child);
                }
                doMarkParent(parent, childFile);
            }
        }
    }
     */
    
    public void valueChanged(TreeSelectionEvent e) {
        TreePath path = trRelMount.getSelectionPath();
        if (path != null) {
            MyTreeNode node = (MyTreeNode) path.getLastPathComponent();
            File selFile = (File) node.getUserObject();
            MyTreeNode rootNode = (MyTreeNode) node.getRoot();
            File rootFile = (File) rootNode.getUserObject();
            if (rootFile.getAbsolutePath().equals(selFile.getAbsolutePath())) {
                txRelMount.setText("");
                return;
            }
            String toWrite = selFile.getAbsolutePath().substring(rootFile.getAbsolutePath().length());
            if (toWrite.startsWith(File.separator)) {
                toWrite = toWrite.substring(File.separator.length());
            }
            txRelMount.setText(toWrite);
        } else {
            txRelMount.setText("");  
        }
    }
    
    /**
     * Initialize the tree.
     * @param rootDir the root directory of the tree
     * @param relMount the initial relative mount point
     */
    public void initTree(String rootDir, String relMount) {
        //if (fs != null) {
            createTree(rootDir);
            //trRelMount.setSelectionModel(new MySelectionModel()); // because of not allowing to select local dirs
            trRelMount.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //}
        setInitSelect(relMount);
        txRelMount.setText(relMount);
    }
    
    private void setInitSelect(String pathToNode) {
        pathToNode = pathToNode.replace(File.separatorChar, '/');
        StringTokenizer token = new StringTokenizer(pathToNode, "/", false);
        MyTreeNode parent = (MyTreeNode) trRelMount.getModel().getRoot();
        TreePath path = new TreePath(parent);
        MyTreeNode child;
        File childFile;
        String directoryName;
        outerWhile:
            while (token.hasMoreTokens()) {
                directoryName = token.nextToken();
                try {
                    child = (MyTreeNode) parent.getFirstChild();
                    do {
                        if (child == null) break;
                        childFile = (File) child.getUserObject();
                        if (childFile.getName().equals(directoryName)) {
                            parent = child;
                            path = path.pathByAddingChild(child);
                            continue outerWhile;
                        }
                        child = (MyTreeNode) parent.getChildAfter(child);
                    } while (true);
                } catch (NoSuchElementException exc){
                }
            }
        trRelMount.setSelectionPath(path);
    }
    
    private static void D(String debug) {
//        System.out.println("Cust4MountPanel(): "+debug);
    }

    /*
    public void treeCollapsed(final javax.swing.event.TreeExpansionEvent p1) {
       //for root node.. if not enabled, unselect everything upon colapsing
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)p1.getPath().getLastPathComponent();
        MyFile fl = (MyFile)node.getUserObject();
        if (fl.isLocal()) {
            trRelMount.clearSelection();
        }
    }

    public void treeExpanded(final javax.swing.event.TreeExpansionEvent p1) {
        //DO nothing
    }
     */
    
    private class MyTreeNode extends DefaultMutableTreeNode {
        
        static final long serialVersionUID = 2478352100056378993L;
        
        public MyTreeNode() {
            super();
        }
        
        public MyTreeNode(Object userObject) {
            super(userObject);
        }

        public boolean isLeaf() {
            return !getAllowsChildren();
        }
    }
    
    private class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        String DEFAULT_FOLDER = "/org/openide/resources/defaultFolder.gif";
        String DEFAULT_OPEN_FOLDER = "/org/openide/resources/defaultFolderOpen.gif";

        static final long serialVersionUID = -1075722862531035018L;
        
        
        /** Finds the component that is capable of drawing the cell in a tree.
        * @param value value can be either Node or a value produced by models (like
        *   NodeTreeModel, etc.)
        * @return component to draw the value
        */
        public Component getTreeCellRendererComponent(
            JTree tree, Object value,
            boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus 
        ) { 
            Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
                if (node != null) {
                    Object userObj = node.getUserObject();
                    if (userObj != null) {
                        File file = (File) userObj;
                        label.setText(file.getName());
                    }
                }
                if (!expanded) {
                    java.net.URL url1 = this.getClass().getResource(DEFAULT_FOLDER);
                    label.setIcon(new ImageIcon(url1));
                } else {
                    java.net.URL url2 = this.getClass().getResource(DEFAULT_OPEN_FOLDER);
                    label.setIcon(new ImageIcon(url2));
//                  label.setIcon(new ImageIcon(new java.net.URL("org.netbeans.openide.resources.defaultFolder.gif")));
                }

            }
            return comp;
        }
        
    }
    
    /*
    class MyFile extends java.io.File {
        private boolean local;

        public MyFile(String absolutePath) {
          super(absolutePath);
          local = true;
        }
        public void setLocal(boolean loc) {
          local = loc;  
        }
        public boolean isLocal() {
          return local;  
        }
    }
    
    class MySelectionModel extends DefaultTreeSelectionModel {
       public MySelectionModel() {
         super();
         setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
       }
       
       public void setSelectionPath(TreePath path) {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
         MyFile userObj = (MyFile)node.getUserObject();
         if (userObj.isLocal()) {
             super.clearSelection();
             return;
         }
         super.setSelectionPath(path);
       }
    }
     */
}
