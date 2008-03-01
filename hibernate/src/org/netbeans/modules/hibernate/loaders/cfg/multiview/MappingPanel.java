/*
 * MappingPanel.java
 *
 * Created on January 23, 2008, 4:18 PM
 */

package org.netbeans.modules.hibernate.loaders.cfg.multiview;

import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import org.netbeans.modules.hibernate.loaders.cfg.HibernateCfgDataObject;

/**
 *
 * @author  Dongmei Cao
 */
public class MappingPanel extends javax.swing.JPanel {
    
    /** Creates new form MappingPanel */
    public MappingPanel(HibernateCfgDataObject dObj) {
        initComponents();
        
        String[] mappingFiles = Util.getMappingFilesFromProject(dObj.getPrimaryFile());
        this.resourceComboBox.setModel( new DefaultComboBoxModel(mappingFiles) );
        
        // TODO: enable them later
        this.jarButton.setEnabled(false);
        this.fileButton.setEnabled(false);
    }
    
    public void initValues( String resourceName, String fileName, String jarName, String packageName, String className ) {
        this.resourceComboBox.setSelectedItem(resourceName);
        this.fileTextField.setText( fileName );
        this.jarTextField.setText( jarName );
        this.pacakgeTextField.setText( packageName );
        this.classTextField.setText( className );
    }
    
    public boolean isValid() {
        // At least one field should be filled to make it valid
        if( getResourceName().length() != 0 
                || getJarName().length() != 0 
                || getFileName().length() != 0 
                || getPackageName().length() != 0 
                || getClassName().length() != 0 ) {
            return true;
        } else
            return false;
            
    }
    
    public void addClassButtonActionListener( ActionListener listener ) {
        this.classButton.addActionListener(listener);
    }
    
    public void addFileButtonActionListener( ActionListener listener ) {
        this.fileButton.addActionListener(listener);
    }
    
    public void addJarButtonActionListener( ActionListener listener ) {
        this.jarButton.addActionListener(listener);
    }
    
    public void addPackageButtonListener( ActionListener listener ) {
        this.packageButton.addActionListener(listener);
    }
    
    public JTextField getResourceTextField() {
        return (JTextField)resourceComboBox.getEditor().getEditorComponent();
    }
    
    public JTextField getFileTextField() {
        return this.fileTextField;
    }
    
    public JTextField getClassTextField() {
        return classTextField;
    }

    public JTextField getJarTextField() {
        return jarTextField;
    }

    public JTextField getPacakgeTextField() {
        return pacakgeTextField;
    }
    
    public String getResourceName() {
        return getResourceTextField().getText().trim();
    }
    
    public String getFileName() {
        return this.fileTextField.getText().trim();
    }
    
    public String getJarName() {
        return this.jarTextField.getText().trim();
    }
    
    public String getPackageName() {
        return this.pacakgeTextField.getText().trim();
    }
    
    public String getClassName() {
        return this.classTextField.getText().trim();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        resourceLabel = new javax.swing.JLabel();
        fileLabel = new javax.swing.JLabel();
        fileTextField = new javax.swing.JTextField();
        jarLabel = new javax.swing.JLabel();
        jarTextField = new javax.swing.JTextField();
        packageLabel = new javax.swing.JLabel();
        pacakgeTextField = new javax.swing.JTextField();
        classLabel = new javax.swing.JLabel();
        classTextField = new javax.swing.JTextField();
        jarButton = new javax.swing.JButton();
        fileButton = new javax.swing.JButton();
        packageButton = new javax.swing.JButton();
        classButton = new javax.swing.JButton();
        resourceComboBox = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        resourceLabel.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.resourceLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(resourceLabel, gridBagConstraints);

        fileLabel.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.fileLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(fileLabel, gridBagConstraints);

        fileTextField.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.fileTextField.text")); // NOI18N
        fileTextField.setPreferredSize(new java.awt.Dimension(200, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(fileTextField, gridBagConstraints);

        jarLabel.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.jarLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(jarLabel, gridBagConstraints);

        jarTextField.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.jarTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(jarTextField, gridBagConstraints);

        packageLabel.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.packageLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(packageLabel, gridBagConstraints);

        pacakgeTextField.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.pacakgeTextField.text")); // NOI18N
        pacakgeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pacakgeTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(pacakgeTextField, gridBagConstraints);

        classLabel.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.classLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(classLabel, gridBagConstraints);

        classTextField.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.classTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(classTextField, gridBagConstraints);

        jarButton.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.jarButton.text")); // NOI18N
        jarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jarButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(jarButton, gridBagConstraints);

        fileButton.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.fileButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(fileButton, gridBagConstraints);

        packageButton.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.packageButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(packageButton, gridBagConstraints);

        classButton.setText(org.openide.util.NbBundle.getMessage(MappingPanel.class, "MappingPanel.classButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(classButton, gridBagConstraints);

        resourceComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(resourceComboBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void pacakgeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pacakgeTextFieldActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_pacakgeTextFieldActionPerformed

    private void jarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jarButtonActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jarButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton classButton;
    private javax.swing.JLabel classLabel;
    private javax.swing.JTextField classTextField;
    private javax.swing.JButton fileButton;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JButton jarButton;
    private javax.swing.JLabel jarLabel;
    private javax.swing.JTextField jarTextField;
    private javax.swing.JTextField pacakgeTextField;
    private javax.swing.JButton packageButton;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JComboBox resourceComboBox;
    private javax.swing.JLabel resourceLabel;
    // End of variables declaration//GEN-END:variables

    
}
