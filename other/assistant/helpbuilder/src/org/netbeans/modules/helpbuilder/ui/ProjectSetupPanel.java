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


package org.netbeans.modules.helpbuilder.ui;

import org.netbeans.modules.helpbuilder.*;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;
import org.openide.ErrorManager;

import org.openide.WizardDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.RepositoryNodeFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** 
 *
 * @author  Richard Gregor
 */
public class ProjectSetupPanel extends javax.swing.JPanel implements DocumentListener, DataFilter, PropertyChangeListener, VetoableChangeListener{
    private final ProjectSetup descriptor; 
    private final static String PROP_TARGET_LOCATION = "targetLocation";
    private Node rootNode;
    /** last DataFolder object that can be returned */
    private DataFolder df;
    private String selectedPath = "";    
    private Node selectedNode;
    private static String absPath;
    
    /** Create the wizard panel and set up some basic properties. */
    public ProjectSetupPanel(final ProjectSetup descriptor) {
        this.descriptor = descriptor;
        initComponents ();                        
        setName(NbBundle.getMessage(ProjectSetupPanel.class, "TITLE_ProjectSetupPanel")); 
        
        rootNode = createPackagesNode();         
        packagesPanel.getExplorerManager().setRootContext(rootNode);
        packagesPanel.getExplorerManager().addPropertyChangeListener(this);
        packagesPanel.getExplorerManager().addVetoableChangeListener(this);

        // registers itself to listen to changes in the content of document
        txtFolder.getDocument().addDocumentListener(this);
        txtFolder.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        
    }
      
    /** Creates node that displays all packages.
    */
    private Node createPackagesNode () {
        return RepositoryNodeFactory.getDefault().repository(this);
    }
    
    // --- VISUAL DESIGN OF PANEL ---

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        packagesPanel = new org.openide.explorer.ExplorerPanel();
        txtDesc = new javax.swing.JTextArea();
        beanTreeView1 = new org.openide.explorer.view.BeanTreeView();
        lblFolder = new javax.swing.JLabel();
        txtFolder = new javax.swing.JTextField();
        btnCreate = new javax.swing.JButton();
        lblLocation = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        setMinimumSize(new java.awt.Dimension(700, 417));
        setPreferredSize(new java.awt.Dimension(700, 417));
        packagesPanel.setLayout(new java.awt.GridBagLayout());

        txtDesc.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Button.background"));
        txtDesc.setColumns(60);
        txtDesc.setEditable(false);
        txtDesc.setForeground((java.awt.Color) javax.swing.UIManager.getDefaults().get("RadioButtonMenuItem.acceleratorForeground"));
        txtDesc.setLineWrap(true);
        txtDesc.setRows(2);
        txtDesc.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("txtSetupPanel"));
        txtDesc.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 11);
        packagesPanel.add(txtDesc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 11, 6, 11);
        packagesPanel.add(beanTreeView1, gridBagConstraints);

        lblFolder.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_lblPath_mnc").charAt(0));
        lblFolder.setLabelFor(txtFolder);
        lblFolder.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("LBL_PRojectSetupPanel.targetPathLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 0);
        packagesPanel.add(lblFolder, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        packagesPanel.add(txtFolder, gridBagConstraints);

        btnCreate.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnCreate_mnc").charAt(0));
        btnCreate.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("LBL_ProjectSetupPanel.btnCreate"));
        btnCreate.setEnabled(false);
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 11);
        packagesPanel.add(btnCreate, gridBagConstraints);

        lblLocation.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_lblLocation_mnc").charAt(0));
        lblLocation.setLabelFor(beanTreeView1);
        lblLocation.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("LBL_ProjectSetupPanel.lblLocation"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 0);
        packagesPanel.add(lblLocation, gridBagConstraints);

        add(packagesPanel, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        
        FileObject fo = df.getPrimaryFile();
        String dfPath = fo.getPath();
        String absPath = txtFolder.getText();
        int index = absPath.indexOf(dfPath);
        final String name = absPath.substring(index+dfPath.length()+1);
        debug("name: "+name);   
        try{
            DataFolder.create(df,name);
            //fo.createFolder(name);      
        }catch(IOException ep){
            ErrorManager.getDefault().notify(ep);
        }
                  
        final ExplorerManager em = packagesPanel.getExplorerManager();
        try{           
            SwingUtilities.invokeLater (new Runnable () {
                
                public void run () {
                    try{
                        em.setSelectedNodes(getNewNode(name));    
                      /*  StringTokenizer stoken = new StringTokenizer(name,File.separator);
                        String names[] = new String[stoken.countTokens()];
                        for(int i = 0; stoken.hasMoreTokens(); i++){
                            names[i] = stoken.nextToken();
                        }
                        Node[] node = new Node[]{NodeOp.findPath(selectedNode,names)};
                        em.setSelectedNodes(node);*/
                    }catch(PropertyVetoException pve){
                        ErrorManager.getDefault().notify(pve);
                    }catch(Exception ep){
                        //ignore
                    }                   
                }
            });        
        } catch (Exception e) { // InterruptedException, InvocationTargetException
            ErrorManager.getDefault().notify (e);
            return;
        }                    
        
        updatePath();
        enableCreateButton();
        
    }//GEN-LAST:event_btnCreateActionPerformed

    private Node[] getNewNode(String folderName){
        debug("selectedNode:"+selectedNode.getName());
        debug("getNewNode:"+folderName);        
        Node[] node = new Node[1];
      //  Node[] arr  = nodes[0].getChildren().getNodes();
        Node[] arr  = selectedNode.getChildren().getNodes();
        debug("length:"+arr.length);
        for(int i=0 ; i < arr.length; i++){
            String name = arr[i].getName();           
            debug("getNewNode nodename:"+name);
            if (folderName.endsWith(name)){
                node[0] = arr[i];
                break;
            }
        }       
        return node;
    }
    
    public boolean isValid(){
        debug("isValid");
        Node[] arr = packagesPanel.getExplorerManager().getSelectedNodes();
        if(arr.length == 0)
            return false;
        else{
            return (!btnCreate.isEnabled());
        }
           // return true;
    }
    
      
    public boolean acceptDataObject(org.openide.loaders.DataObject obj) {
        return obj instanceof DataFolder;
    }
    
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        debug("changedUpdate");
        enableCreateButton();
       // updatePath();
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        debug("insertUpdate");
        enableCreateButton();
        //updatePath();
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        debug("removeUpdate");
        enableCreateButton();
      //  updatePath();
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        debug("propertyChange:"+evt.getPropertyName());
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            //causes validity checking
            descriptor.fireChangeEvent();  
            Node[] arr = packagesPanel.getExplorerManager().getSelectedNodes();            
            if (!isVisible()) {                
                return;
            }
            if (arr.length == 1) {
                df = (DataFolder)arr[0].getCookie(DataFolder.class);
                if (df != null) {                    
                    selectedPath = df.getPrimaryFile().getPath();
                    selectedNode = arr[0];
                    enableCreateButton();
                    updatePath();
                    return;
                }
            }                     
        }
        if (ExplorerManager.PROP_NODE_CHANGE.equals(evt.getPropertyName())) {
            debug("node change");
        }
    }
    
    /** Allow only simple selection.
     */
    public void vetoableChange(PropertyChangeEvent ev)
    throws PropertyVetoException {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(ev.getPropertyName())) {
            Node[] arr = (Node[])ev.getNewValue();
            if (arr.length > 1) {
                throw new PropertyVetoException("Only single selection allowed", ev); // NOI18N
            }
        }
    }
    
    
    private void updatePath(){
        absPath = getAbsPath(selectedNode);
        txtFolder.setText(absPath);
        putClientProperty(PROP_TARGET_LOCATION, absPath);        
   
    }
    
    public static String getTargetLocation(){
        return absPath;
    }
    
    private String getAbsPath(Node node){
        debug("getAbsPath:"+node.getName());
        if((selectedPath == null) || (selectedPath.length() == 0)){
            debug("node:"+node.getName()+" is root");
            return node.getName();
        }
        Node parent = node.getParentNode();
        if(parent.getName().equals("Filesystems"))
            return node.getName()+File.separator+selectedPath;
        else
            return getAbsPath(parent);
    }

    
    
    /** Sets the state of the createButton */
    private void enableCreateButton() {
        String folderPath = txtFolder.getText();
        File file = new File(folderPath);
        btnCreate.setEnabled(!file.exists());
        descriptor.fireChangeEvent();
    }
    
    
    private boolean DEBUG = false;
    private void debug(String msg){
        if(DEBUG)
            System.err.println("PrjSetupPanel: "+msg);
    }
    
      
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView beanTreeView1;
    private javax.swing.JButton btnCreate;
    private javax.swing.JLabel lblFolder;
    private javax.swing.JLabel lblLocation;
    private org.openide.explorer.ExplorerPanel packagesPanel;
    private javax.swing.JTextArea txtDesc;
    private javax.swing.JTextField txtFolder;
    // End of variables declaration//GEN-END:variables



}
