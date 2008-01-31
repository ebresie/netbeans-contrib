/*
 * PropertyPanel.java
 *
 * Created on January 24, 2008, 12:18 PM
 */
package org.netbeans.modules.hibernate.loaders.cfg.multiview;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.netbeans.modules.hibernate.HibernateCfgProperties;
import org.netbeans.modules.hibernate.cfg.model.SessionFactory;

/**
 * Panel for adding new Hibernate property
 * 
 * @author  Dongmei Cao
 */
public class PropertyPanel extends javax.swing.JPanel implements ActionListener {

    JTextField valueTextField = null;
    JComboBox valueComboBox = null;
    ValueComboBoxEditor editor = null;
    
    /** Creates new form PropertyPanel */
    public PropertyPanel(String propCat, boolean add, SessionFactory sessionFactory, String propName, String propValue) {
        initComponents();

        // The comb box only contains the property names that are not defined yet when adding
        if (add) {
            nameComboBox.setModel(new DefaultComboBoxModel(Util.getAvailPropNames(propCat, sessionFactory)));
        } else {
            nameComboBox.setModel(new DefaultComboBoxModel(Util.getAllPropNames(propCat)));
            nameComboBox.setSelectedItem(propName);
        }

        valueTextField = new JTextField();
        valueTextField.setPreferredSize(new java.awt.Dimension(200, 19));
        
        valueComboBox = new JComboBox();
        editor = new ValueComboBoxEditor(valueTextField);
        valueComboBox.setPreferredSize(new java.awt.Dimension(200, 19));

        // Add the appropriate component for the value 
        String selectedPropName = (String) nameComboBox.getSelectedItem();
        addValueComponent(selectedPropName, propValue);

        nameComboBox.addActionListener((ActionListener) this);

        // Disable the name combo box for editing
        nameComboBox.setEnabled(add);
    }

    public void addValueComponent(String propName, String propValue) {
        valuePanel.removeAll();
        Object possibleValue = HibernateCfgProperties.getPossiblePropertyValue(propName);
        if (possibleValue == null) {
         
            valuePanel.add(valueTextField, java.awt.BorderLayout.CENTER);
            valueTextField.setText(propValue);
            
        } else if (possibleValue instanceof String[]) {
            
            //valueComboBox = new JComboBox();
            valueComboBox.setModel( new DefaultComboBoxModel((String[]) possibleValue));
            valueComboBox.setEditable(true);
            valueComboBox.setEditor( editor );

            valuePanel.add(valueComboBox, java.awt.BorderLayout.CENTER);
            
            if (propValue != null) {
                valueComboBox.setSelectedItem(propValue);
            } else
                valueComboBox.setSelectedIndex(0);
        }

        this.revalidate();
        this.repaint();
    }

    public JTextField getValueTextField() {
        /*if (valuePanel.getComponents()[0] instanceof JTextField) {
            return (JTextField) valuePanel.getComponents()[0];
        } else {
            return (JTextField) ((JComboBox) valuePanel.getComponents()[0]).getEditor().getEditorComponent();
        }*/
        return this.valueTextField;
    }

    public String getPropertyName() {
        return (String) this.nameComboBox.getSelectedItem();
    }

    public String getPropertyValue() {
        return getValueTextField().getText().trim();
    }

    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        String propName = (String) cb.getSelectedItem();
        addValueComponent(propName, null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        nameLabel = new javax.swing.JLabel();
        valueLabel = new javax.swing.JLabel();
        nameComboBox = new javax.swing.JComboBox();
        valuePanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        nameLabel.setText(org.openide.util.NbBundle.getMessage(PropertyPanel.class, "PropertyPanel.nameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(nameLabel, gridBagConstraints);

        valueLabel.setText(org.openide.util.NbBundle.getMessage(PropertyPanel.class, "PropertyPanel.valueLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(valueLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(nameComboBox, gridBagConstraints);

        valuePanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(valuePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox nameComboBox;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JPanel valuePanel;
    // End of variables declaration//GEN-END:variables

private class ValueComboBoxEditor implements ComboBoxEditor {
        JTextField textField = null;
        
        ValueComboBoxEditor( JTextField textField ) {
            this.textField = textField;
        }

        public Component getEditorComponent() {
            return this.textField;
        }

        public void setItem(Object anObject) {
            this.textField.setText( (String)anObject );
        }

        public Object getItem() {
            return this.textField.getText().trim();
        }

        public void selectAll() {
            this.textField.selectAll();
        }

        public void addActionListener(ActionListener l) {
            this.textField.addActionListener(l);
        }

        public void removeActionListener(ActionListener l) {
            this.textField.removeActionListener(l);
        }
    }
}
