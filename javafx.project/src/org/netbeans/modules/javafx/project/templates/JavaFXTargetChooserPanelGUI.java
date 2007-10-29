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

package org.netbeans.modules.javafx.project.templates;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.awt.Mnemonics;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * 
 * @author answer
 */
public class JavaFXTargetChooserPanelGUI extends javax.swing.JPanel implements ActionListener, DocumentListener {
  
    private static final String DEFAULT_NEW_PACKAGE_NAME = 
        NbBundle.getMessage( JavaFXTargetChooserPanelGUI.class, "LBL_JavaFXTargetChooserPanelGUI_DefaultNewPackageName"  ); // NOI18N
    private static final String NEW_CLASS_PREFIX = 
        NbBundle.getMessage( JavaFXTargetChooserPanelGUI.class, "LBL_JavaFXTargetChooserPanelGUI_NewJavaFXClassPrefix"  ); // NOI18N
    
    /** preferred dimension of the panel */
    private static final Dimension PREF_DIM = new Dimension(500, 340);
    
    private Project project;
    private String expectedExtension;
    private final List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    private int type;
    private SourceGroup groups[];
    private boolean ignoreRootCombo;
    
    /** Creates new form SimpleTargetChooserGUI */
    public JavaFXTargetChooserPanelGUI( Project p, SourceGroup[] groups, Component bottomPanel, int type ) {
        this.type = type;
        this.project = p;
        this.groups = groups;
        
        initComponents();        
                
        if ( type == NewJavaFXFileWizardIterator.TYPE_PACKAGE ) {
            packageComboBox.setVisible( false );
            packageLabel.setVisible( false );
            Mnemonics.setLocalizedText (fileLabel, NbBundle.getMessage (JavaFXTargetChooserPanelGUI.class, "LBL_JavaFXTargetChooserPanelGUI_CreatedFolder_Label")); // NOI18N
            Mnemonics.setLocalizedText (documentNameLabel, NbBundle.getMessage (JavaFXTargetChooserPanelGUI.class, "LBL_JavaFXTargetChooserPanelGUI_PackageName_Label")); // NOI18N
            documentNameTextField.getDocument().addDocumentListener( this );
        }
        else if ( type == NewJavaFXFileWizardIterator.TYPE_PKG_INFO ) {
            documentNameTextField.setEditable (false);
        }
        else {
            packageComboBox.getEditor().addActionListener( this );
            documentNameTextField.getDocument().addDocumentListener( this );
        }
        
                
        if ( bottomPanel != null ) {
            bottomPanelContainer.add( bottomPanel, java.awt.BorderLayout.CENTER );
        }
                
        //initValues( project, null, null );
        

        // Not very nice
        Component packageEditor = packageComboBox.getEditor().getEditorComponent();
        if ( packageEditor instanceof javax.swing.JTextField ) {
            ((javax.swing.JTextField)packageEditor).getDocument().addDocumentListener( this );
        }
        else {
            packageComboBox.addActionListener( this );
        }
        
        rootComboBox.setRenderer(new GroupListCellRenderer());        
        packageComboBox.setRenderer(PackageView.listRenderer());
        rootComboBox.addActionListener( this );
        
        setPreferredSize( PREF_DIM );
        setName( NbBundle.getBundle (JavaFXTargetChooserPanelGUI.class).getString ("LBL_JavaFXTargetChooserPanelGUI_Name")  ); // NOI18N
    }
            
    public void addNotify () {
        Dimension panel2Size = this.jPanel2.getPreferredSize();
        Dimension bottomPanelSize = this.bottomPanelContainer.getPreferredSize ();
        Dimension splitterSize = this.targetSeparator.getPreferredSize();        
        int vmax = panel2Size.height + bottomPanelSize.height + splitterSize.height + 12;   //Insets=12
        //Update only height, keep the wizard width
        if (vmax > PREF_DIM.height) {
            this.setPreferredSize (new Dimension (PREF_DIM.width,vmax));
        }
        super.addNotify();
    }
    
    public void initValues( FileObject template, FileObject preselectedFolder ) {
        assert project != null : "Project must be specified."; // NOI18N
        // Show name of the project
        projectTextField.setText( ProjectUtils.getInformation(project).getDisplayName() );
        assert template != null;
        
        String displayName = null;
        try {
            DataObject templateDo = DataObject.find (template);
            displayName = templateDo.getNodeDelegate ().getDisplayName ();
        } catch (DataObjectNotFoundException ex) {
            displayName = template.getName ();
        }
        
        putClientProperty ("NewFileWizard_Title", displayName);// NOI18N        
        // Setup comboboxes 
        rootComboBox.setModel(new DefaultComboBoxModel(groups));
        SourceGroup preselectedGroup = getPreselectedGroup( preselectedFolder );
        ignoreRootCombo = true;
        rootComboBox.setSelectedItem( preselectedGroup );                       
        ignoreRootCombo = false;
        Object preselectedPackage = getPreselectedPackage(preselectedGroup, preselectedFolder, packageComboBox.getModel());
        if ( type == NewJavaFXFileWizardIterator.TYPE_PACKAGE ) {
            String docName = preselectedPackage == null || preselectedPackage.toString().length() == 0 ? 
                DEFAULT_NEW_PACKAGE_NAME : 
                preselectedPackage.toString() + "." + DEFAULT_NEW_PACKAGE_NAME;

            documentNameTextField.setText( docName );                    
            int docNameLen = docName.length();
            int defPackageNameLen = DEFAULT_NEW_PACKAGE_NAME.length();

            documentNameTextField.setSelectionEnd( docNameLen - 1 );
            documentNameTextField.setSelectionStart( docNameLen - defPackageNameLen );                
        } else {
            if (preselectedPackage != null) {
                // packageComboBox.setSelectedItem( preselectedPackage );
                packageComboBox.getEditor().setItem( preselectedPackage );
            }
            if (template != null) {
            	if ( documentNameTextField.getText().trim().length() == 0 ) { // To preserve the class name on back in the wiazard
                    if (this.type == NewJavaFXFileWizardIterator.TYPE_PKG_INFO) {
                        documentNameTextField.setText (template.getName ());
                    }
                    else {
                        //Ordinary file
                        String prefix = NEW_CLASS_PREFIX;
                        // See 91580
                        Object customPrefix = template.getAttribute("templateNamePrefix");
                        if (customPrefix != null) {
                            prefix = customPrefix.toString();
                        }
                        
                        documentNameTextField.setText (prefix + template.getName ());
                        documentNameTextField.selectAll ();
                    }
                }
            }
            updatePackages( false );
        }
        // Determine the extension
        String ext = template == null ? "" : template.getExt(); // NOI18N
        expectedExtension = ext.length() == 0 ? "" : "." + ext; // NOI18N
        
        updateText();
        
    }
        
    public FileObject getRootFolder() {
        return ((SourceGroup) rootComboBox.getSelectedItem()).getRootFolder();        
    }
    
    public String getPackageFileName() {
        
        if ( type == NewJavaFXFileWizardIterator.TYPE_PACKAGE ) {
            return ""; // NOI18N
        }
        
        String packageName = packageComboBox.getEditor().getItem().toString();        
        return  packageName.replace( '.', '/' ); // NOI18N        
    }
    
    /**
     * Name of selected package, or "" for default package.
     */
    String getPackageName() {
        if ( type == NewJavaFXFileWizardIterator.TYPE_PACKAGE ) {
            return ""; // NOI18N
        }
        return packageComboBox.getEditor().getItem().toString();
    }    
    
    public String getTargetName() {
        String text = documentNameTextField.getText().trim();
        
        if ( text.length() == 0 ) {
            return null;
        }
        else {
            return text;
        }

    }
    
    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    private void fireChange() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : listeners) {
            l.stateChanged(e);
        }
    }
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        targetSeparator = new javax.swing.JSeparator();
        bottomPanelContainer = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        documentNameLabel = new javax.swing.JLabel();
        documentNameTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        rootComboBox = new javax.swing.JComboBox();
        packageLabel = new javax.swing.JLabel();
        packageComboBox = new javax.swing.JComboBox();
        fileLabel = new javax.swing.JLabel();
        fileTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(targetSeparator, gridBagConstraints);

        bottomPanelContainer.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(bottomPanelContainer, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        documentNameLabel.setLabelFor(documentNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(documentNameLabel, org.openide.util.NbBundle.getMessage(JavaFXTargetChooserPanelGUI.class, "LBL_JavaFXTargetChooserPanelGUI_ClassName_Label")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(documentNameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(documentNameTextField, gridBagConstraints);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/javafx/project/templates/Bundle"); // NOI18N
        documentNameTextField.getAccessibleContext().setAccessibleDescription(bundle.getString("AD_documentNameTextField")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 24, 0);
        jPanel2.add(jPanel1, gridBagConstraints);

        jLabel5.setLabelFor(projectTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(JavaFXTargetChooserPanelGUI.class, "LBL_JavaFXTargetChooserPanelGUI_jLabel5")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        jPanel2.add(jLabel5, gridBagConstraints);

        projectTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        jPanel2.add(projectTextField, gridBagConstraints);
        projectTextField.getAccessibleContext().setAccessibleDescription(bundle.getString("AD_projectTextField")); // NOI18N

        jLabel1.setLabelFor(rootComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(JavaFXTargetChooserPanelGUI.class, "LBL_JavaFXTargetChooserPanelGUI_jLabel1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        jPanel2.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        jPanel2.add(rootComboBox, gridBagConstraints);
        rootComboBox.getAccessibleContext().setAccessibleDescription(bundle.getString("AD_rootComboBox")); // NOI18N

        packageLabel.setLabelFor(packageComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(JavaFXTargetChooserPanelGUI.class, "LBL_JavaFXTargetChooserPanelGUI_jLabel2")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        jPanel2.add(packageLabel, gridBagConstraints);

        packageComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        jPanel2.add(packageComboBox, gridBagConstraints);
        packageComboBox.getAccessibleContext().setAccessibleDescription(bundle.getString("AD_packageComboBox")); // NOI18N

        fileLabel.setLabelFor(fileTextField);
        org.openide.awt.Mnemonics.setLocalizedText(fileLabel, org.openide.util.NbBundle.getMessage(JavaFXTargetChooserPanelGUI.class, "LBL_JavaFXTargetChooserPanelGUI_CreatedFile_Label")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 12, 0);
        jPanel2.add(fileLabel, gridBagConstraints);

        fileTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 0);
        jPanel2.add(fileTextField, gridBagConstraints);
        fileTextField.getAccessibleContext().setAccessibleDescription(bundle.getString("AD_fileTextField")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(jPanel2, gridBagConstraints);

        getAccessibleContext().setAccessibleDescription(bundle.getString("AD_JavaFXTargetChooserPanelGUI")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanelContainer;
    private javax.swing.JLabel documentNameLabel;
    private javax.swing.JTextField documentNameTextField;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JComboBox packageComboBox;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JComboBox rootComboBox;
    private javax.swing.JSeparator targetSeparator;
    // End of variables declaration//GEN-END:variables

    // ActionListener implementation -------------------------------------------
        
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if ( rootComboBox == e.getSource() ) {            
//            if ( !ignoreRootCombo && type != NewJavaFXFileWizardIterator.TYPE_PACKAGE ) {
//                updatePackages( true );
//            }
            updateText();
        }
        else if ( packageComboBox == e.getSource() ) {
            updateText();
            fireChange();
        }
        else if ( packageComboBox.getEditor()  == e.getSource() ) {
            updateText();
            fireChange();
        }
    }    
    
    // DocumentListener implementation -----------------------------------------
    
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        updateText();
        fireChange();        
    }    
    
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        changedUpdate( e );
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        changedUpdate( e );
    }
    
    // Private methods ---------------------------------------------------------
        
    private RequestProcessor.Task updatePackagesTask = null;
    
    private static final ComboBoxModel WAIT_MODEL = new DefaultComboBoxModel( 
        new String[] {
            NbBundle.getMessage( JavaFXTargetChooserPanelGUI.class, "LBL_JavaFXTargetChooserPanelGUI_PackageName_PleaseWait"  ) // NOI18N
         // NOI18N
        } 
     
    ); 
    
    private void updatePackages( final boolean clean ) {
        WAIT_MODEL.setSelectedItem( packageComboBox.getEditor().getItem() );
        packageComboBox.setModel( WAIT_MODEL );
        
        if ( updatePackagesTask != null ) {
            updatePackagesTask.cancel();
        }
        
        updatePackagesTask = new RequestProcessor( "ComboUpdatePackages" ).post(
            new Runnable() {
            
                private ComboBoxModel model;
            
                public void run() {
                    if ( !SwingUtilities.isEventDispatchThread() ) {
                        model = PackageView.createListView((SourceGroup) rootComboBox.getSelectedItem());                        
                        SwingUtilities.invokeLater( this );
                    }
                    else {
                        if ( !clean ) {
                            model.setSelectedItem( packageComboBox.getEditor().getItem() );
                        }
                        packageComboBox.setModel( model );
                    }
                }
            }
        );
                
    }
        
    private void updateText() {
        
        SourceGroup g = (SourceGroup) rootComboBox.getSelectedItem();
        FileObject rootFolder = g.getRootFolder();
        String packageName = getPackageFileName();
        String documentName = documentNameTextField.getText().trim();
        if ( type == NewJavaFXFileWizardIterator.TYPE_PACKAGE ) {
            documentName = documentName.replace( '.', '/' ); // NOI18N
        }
        else if ( documentName.length() > 0 ) {
            documentName = documentName + expectedExtension;
        }
        String createdFileName = FileUtil.getFileDisplayName( rootFolder ) + 
            ( packageName.startsWith("/") || packageName.startsWith( File.separator ) ? "" : "/" ) + // NOI18N
            packageName + 
            ( packageName.endsWith("/") || packageName.endsWith( File.separator ) || packageName.length() == 0 ? "" : "/" ) + // NOI18N
            documentName;
        
        fileTextField.setText( createdFileName.replace( '/', File.separatorChar ) ); // NOI18N        
    }
    
    private SourceGroup getPreselectedGroup(FileObject folder) {
        for(int i = 0; folder != null && i < groups.length; i++) {
            FileObject root = groups[i].getRootFolder();
            if (root.equals(folder) || FileUtil.isParentOf(root, folder)) {
                return groups[i];
            }
        }
        return groups[0];
    }
    
    /**
     * Get a package combo model item for the package the user selected before opening the wizard.
     * May return null if it cannot find it; or a String instance if there is a well-defined
     * package but it is not listed among the packages shown in the list model.
     */
    private Object getPreselectedPackage(SourceGroup group, FileObject folder, ListModel model) {
        if ( folder == null ) {
            return null;
        }
        FileObject root = group.getRootFolder();
        
        String relPath = FileUtil.getRelativePath( root, folder );
        
        if ( relPath == null ) {
            // Group Root folder is no a parent of the preselected folder
            // No package should be selected
            return null; 
        }        
        else {
            // Find the right item.            
            String name = relPath.replace('/', '.');
            /*
            int max = model.getSize();
            for (int i = 0; i < max; i++) {
                Object item = model.getElementAt(i);
                if (item.toString().equals(name)) {
                    return item;
                }
            }
             */
            // Didn't find it.
            // #49954: should nonetheless show something in the combo box.
            return name;
        }        
    }
    
    // Private innerclasses ----------------------------------------------------

    /**
     * Displays a {@link SourceGroup} in {@link #rootComboBox}.
     */
    private static final class GroupListCellRenderer extends DefaultListCellRenderer/*<SourceGroup>*/ {
        
        public GroupListCellRenderer() {}
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            SourceGroup g = (SourceGroup) value;
            super.getListCellRendererComponent(list, g.getDisplayName(), index, isSelected, cellHasFocus);
            setIcon(g.getIcon(false));
            return this;
        }
        
    }
    
}
