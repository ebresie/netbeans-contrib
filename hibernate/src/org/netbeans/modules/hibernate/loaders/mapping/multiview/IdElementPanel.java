/*
 * IdElementPanel.java
 *
 * Created on February 6, 2008, 11:12 AM
 */
package org.netbeans.modules.hibernate.loaders.mapping.multiview;

import javax.swing.JComponent;
import org.netbeans.modules.hibernate.loaders.mapping.HibernateMappingDataObject;
import org.netbeans.modules.hibernate.mapping.model.Id;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;

/**
 *
 * @author  Dongmei Cao
 */
public class IdElementPanel extends SectionInnerPanel {

    /** Creates new form IdElementPanel */
    public IdElementPanel(SectionView view, final HibernateMappingDataObject dObj, Id theId) {
        super(view);
        initComponents();
        userTypePanel.add(new TypeElementPanel());
        generatorPanel.add(new GeneratorElementPanel());
    }

    @Override
    public void setValue(JComponent source, Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void linkButtonPressed(Object ddBean, String ddProperty) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JComponent getErrorComponent(String errorId) {
       return null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        filler = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nodeLabel = new javax.swing.JLabel();
        accessLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        columnLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        unsavedValueLabel = new javax.swing.JLabel();
        typeElemLabel = new javax.swing.JLabel();
        generatorLabel = new javax.swing.JLabel();
        metaLabel = new javax.swing.JLabel();
        nodeTextField = new javax.swing.JTextField();
        accessTextField = new javax.swing.JTextField();
        columnTextField = new javax.swing.JTextField();
        typeTextField = new javax.swing.JTextField();
        unsavedValueTextField = new javax.swing.JTextField();
        userTypePanel = new javax.swing.JPanel();
        generatorPanel = new javax.swing.JPanel();
        metaDataPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        org.jdesktop.layout.GroupLayout fillerLayout = new org.jdesktop.layout.GroupLayout(filler);
        filler.setLayout(fillerLayout);
        fillerLayout.setHorizontalGroup(
            fillerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 88, Short.MAX_VALUE)
        );
        fillerLayout.setVerticalGroup(
            fillerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 24, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        add(filler, gridBagConstraints);

        nameLabel.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.nameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(nameLabel, gridBagConstraints);

        nodeLabel.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.nodeLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(nodeLabel, gridBagConstraints);

        accessLabel.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.accessLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(accessLabel, gridBagConstraints);

        typeLabel.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.typeLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(typeLabel, gridBagConstraints);

        columnLabel.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.columnLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(columnLabel, gridBagConstraints);

        nameTextField.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.nameTextField.text")); // NOI18N
        nameTextField.setPreferredSize(new java.awt.Dimension(200, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(nameTextField, gridBagConstraints);

        unsavedValueLabel.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.unsavedValueLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(unsavedValueLabel, gridBagConstraints);

        typeElemLabel.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.typeElemLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(typeElemLabel, gridBagConstraints);

        generatorLabel.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.generatorLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(generatorLabel, gridBagConstraints);

        metaLabel.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.metaLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(metaLabel, gridBagConstraints);

        nodeTextField.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.nodeTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(nodeTextField, gridBagConstraints);

        accessTextField.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.accessTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(accessTextField, gridBagConstraints);

        columnTextField.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.columnTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(columnTextField, gridBagConstraints);

        typeTextField.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.typeTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(typeTextField, gridBagConstraints);

        unsavedValueTextField.setText(org.openide.util.NbBundle.getMessage(IdElementPanel.class, "IdElementPanel.unsavedValueTextField.text")); // NOI18N
        unsavedValueTextField.setPreferredSize(new java.awt.Dimension(200, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(unsavedValueTextField, gridBagConstraints);

        userTypePanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(userTypePanel, gridBagConstraints);

        generatorPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(generatorPanel, gridBagConstraints);

        metaDataPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(metaDataPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accessLabel;
    private javax.swing.JTextField accessTextField;
    private javax.swing.JLabel columnLabel;
    private javax.swing.JTextField columnTextField;
    private javax.swing.JPanel filler;
    private javax.swing.JLabel generatorLabel;
    private javax.swing.JPanel generatorPanel;
    private javax.swing.JPanel metaDataPanel;
    private javax.swing.JLabel metaLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel nodeLabel;
    private javax.swing.JTextField nodeTextField;
    private javax.swing.JLabel typeElemLabel;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JTextField typeTextField;
    private javax.swing.JLabel unsavedValueLabel;
    private javax.swing.JTextField unsavedValueTextField;
    private javax.swing.JPanel userTypePanel;
    // End of variables declaration//GEN-END:variables
}