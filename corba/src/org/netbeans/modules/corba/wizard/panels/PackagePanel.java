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

package org.netbeans.modules.corba.wizard.panels;

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.border.EtchedBorder;
import org.openide.TopManager;
import org.openide.nodes.Node;
import org.openide.filesystems.FileObject;
import org.openide.explorer.view.BeanTreeView;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataFolder;
import org.openide.NotifyDescriptor;
import org.netbeans.modules.corba.wizard.CorbaWizardData;

/** 
 *
 * @author  tzezula
 * @version 
 */
public class PackagePanel extends AbstractWizardPanel implements PropertyChangeListener, VetoableChangeListener, DataFilter {

    //private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;

    private Node root;
    private boolean validIdlName = false;

    /** Creates new form PackagePanel */
    public PackagePanel() {
        initComponents ();
        BeanTreeView tv = new BeanTreeView();
        this.tree.setMinimumSize ( new Dimension (400,250));
        this.tree.setPreferredSize ( new Dimension (400,250));
        this.tree.setBorder (new EtchedBorder());
        this.tree.add (tv, BorderLayout.CENTER);
        this.root = TopManager.getDefault().getPlaces().nodes().repository(this);
        this.tree.getExplorerManager().setRootContext (this.root);
        this.tree.getExplorerManager().addPropertyChangeListener (this);
        this.tree.getExplorerManager().addVetoableChangeListener (this);
        this.packageName.setEditable (false);
    }
  
    public void readCorbaSettings (CorbaWizardData data) {
    }
  
    public void storeCorbaSettings (CorbaWizardData data) {
        Node[] nodes = this.tree.getExplorerManager ().getSelectedNodes ();
        if (nodes == null || nodes.length != 1)
            return;
        DataFolder pcg = (DataFolder) nodes[0].getCookie (DataFolder.class);
        data.setDestinationPackage (pcg);
        data.setName (this.idlName.getText());
    }

    public boolean isValid () {
        if (DEBUG)
            System.out.println("Is valid= " + validIdlName);
        return (this.packageName.getText().length()>0 && this.idlName.getText().length()>0 && validIdlName);
    }
  
    public void propertyChange (PropertyChangeEvent event) {
        Object newValue = event.getNewValue();
        if (newValue != null && (newValue instanceof Node[])){
            Node[] nodes = (Node[]) newValue;
            if (nodes.length == 1) {
                Node node = nodes[0];
                String selection = node.getName();
                node = node.getParentNode();
                while (node != this.root && node != null){
                    selection = node.getDisplayName()+"."+selection;
                    node = node.getParentNode();
                }
                this.packageName.setText (selection);
                this.checkFileNameValidity ();
            }
        }
    }
  
    public void vetoableChange (PropertyChangeEvent event) throws PropertyVetoException {
        Object newValue = event.getNewValue();
        if (newValue != null && (newValue instanceof Node[])){
            Node[] nodes = (Node[]) newValue;
            if (nodes.length != 1)
                throw new PropertyVetoException ("", event);
        }
    }
  
  
  

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        tree = new org.openide.explorer.ExplorerPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        packageName = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        idlName = new javax.swing.JTextField();
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        setPreferredSize(new java.awt.Dimension(500, 340));
        setMinimumSize(new java.awt.Dimension(480, 320));



        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(4, 8, 8, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(tree, gridBagConstraints1);

        jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_Package"));
        jLabel1.setFont(new java.awt.Font ("Dialog", 0, 18));


        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(8, 8, 4, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(jLabel1, gridBagConstraints1);

        jLabel2.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_PackageName"));


        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.insets = new java.awt.Insets(0, 8, 8, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel2, gridBagConstraints1);



        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(0, 0, 8, 8);
        gridBagConstraints1.weightx = 1.0;
        add(packageName, gridBagConstraints1);



        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(jPanel1, gridBagConstraints1);

        jLabel3.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_Name"));
        jLabel3.setLabelFor(idlName);


        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets(4, 8, 4, 4);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel3, gridBagConstraints1);

        idlName.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TIP_NameOfIdlFile"));
        idlName.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    idlNameFocusLost(evt);
                }
            }
                                 );


        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(idlName, gridBagConstraints1);

    }//GEN-END:initComponents

    private void idlNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_idlNameFocusLost
        // Add your handling code here:
        this.checkFileNameValidity ();
    }//GEN-LAST:event_idlNameFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.ExplorerPanel tree;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField packageName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField idlName;
    // End of variables declaration//GEN-END:variables

  
    /** Should the data object be displayed or not?
     * @param obj the data object
     * @return <CODE>true</CODE> if the object should be displayed,
     *    <CODE>false</CODE> otherwise
     */
    public boolean acceptDataObject (DataObject obj) {
        FileObject fobj = obj.getPrimaryFile();
        if (obj.isValid() && fobj.isFolder())
            return true;
        else
            return false;
    }

    private void checkFileNameValidity () {
        try {
            validIdlName = false;
            Node[] nodes = this.tree.getExplorerManager ().getSelectedNodes ();
            if (nodes == null || nodes.length != 1)
                return;
            if (nodes[0] == this.root) // The repository node is selected, giving up
                return;
            DataFolder pcg = (DataFolder) nodes[0].getCookie (DataFolder.class);
            FileObject folder = pcg.getPrimaryFile ();
            FileObject[] files = folder.getChildren();
            String name = this.idlName.getText();
            for (int i=0; i<files.length; i++) {
                if (files[i].isData() && files[i].getExt().equals("idl") && files[i].getName().equals(name)){
                    NotifyDescriptor descriptor = new NotifyDescriptor.Message (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("MSG_FileAlreadyExists"));
                    TopManager.getDefault().notify (descriptor);
                    return;
                }
            }
            validIdlName = true;
        }finally {
            this.fireChange (this);
        }
    }
}
