/*
 * TypedefPanel.java
 *
 * Created on February 4, 2008, 9:31 PM
 */

package org.netbeans.modules.hibernate.loaders.mapping.multiview;

import javax.swing.JComponent;
import org.netbeans.modules.hibernate.loaders.mapping.HibernateMappingDataObject;
import org.netbeans.modules.hibernate.mapping.model.Typedef;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;

/**
 *
 * @author  dc151887
 */
public class TypedefPanel extends SectionInnerPanel {
    private HibernateMappingDataObject mappingDataObject;
    private Typedef typedef;
    
    
    /** Creates new form TypedefPanel */
    public TypedefPanel(SectionView view, final HibernateMappingDataObject dObj, final Typedef typedef) {
        super(view);
        this.mappingDataObject = dObj;
        this.typedef = typedef;
        
        initComponents();
        
        TypedefParamsTableModel model = new TypedefParamsTableModel(typedef);
        TypedefParamsTablePanel panel = new TypedefParamsTablePanel(dObj, model);
        paramPanel.add( panel );
        
        this.classTextField.setText( typedef.getAttributeValue("Class") );
        addModifier(classTextField);
        
        this.nameTextField.setText(typedef.getAttributeValue("Name"));
        addModifier(nameTextField);
    }
    
    public void setValue(javax.swing.JComponent source, Object value) {
        String text = (String)value;
        if (source==classTextField) {
            typedef.setAttributeValue("Class", text);
        } else if (source==nameTextField) {
            typedef.setAttributeValue("Name", text);
        } 
        mappingDataObject.modelUpdatedFromUI();
    }
    
    public void linkButtonPressed(Object ddBean, String ddProperty) {
        
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

        fill = new javax.swing.JPanel();
        classLabel = new javax.swing.JLabel();
        classTextField = new javax.swing.JTextField();
        paramPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        parametersLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        org.jdesktop.layout.GroupLayout fillLayout = new org.jdesktop.layout.GroupLayout(fill);
        fill.setLayout(fillLayout);
        fillLayout.setHorizontalGroup(
            fillLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        fillLayout.setVerticalGroup(
            fillLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(fill, gridBagConstraints);

        classLabel.setText(org.openide.util.NbBundle.getMessage(TypedefPanel.class, "TypedefPanel.classLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(classLabel, gridBagConstraints);

        classTextField.setText(org.openide.util.NbBundle.getMessage(TypedefPanel.class, "TypedefPanel.classTextField.text")); // NOI18N
        classTextField.setPreferredSize(new java.awt.Dimension(200, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(classTextField, gridBagConstraints);

        paramPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(paramPanel, gridBagConstraints);

        nameLabel.setText(org.openide.util.NbBundle.getMessage(TypedefPanel.class, "TypedefPanel.nameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(nameLabel, gridBagConstraints);

        nameTextField.setText(org.openide.util.NbBundle.getMessage(TypedefPanel.class, "TypedefPanel.nameTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(nameTextField, gridBagConstraints);

        parametersLabel.setText(org.openide.util.NbBundle.getMessage(TypedefPanel.class, "TypedefPanel.parametersLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(parametersLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel classLabel;
    private javax.swing.JTextField classTextField;
    private javax.swing.JPanel fill;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JPanel paramPanel;
    private javax.swing.JLabel parametersLabel;
    // End of variables declaration//GEN-END:variables

}
