/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.conditioned;

import javax.swing.table.DefaultTableModel;
import org.netbeans.modules.vcs.advanced.commands.StructuredExecPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author  Martin Entlicher
 */
public class ConditionedStructuredExecPanel extends StructuredExecPanel {
    
    private ConditionedString cexecString;
    private ConditionedObject cexecStructured;
    
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox conditionComboBox;
    private javax.swing.JLabel conditionLabel;
    private javax.swing.JButton editButton;
    private javax.swing.JButton removeButton;
    
    /** Creates a new instance of ConditionedStructuredExecPanel */
    public ConditionedStructuredExecPanel() {
    }
    
    protected void postInitComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], new Object[] {
            org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.Arguments"),
            org.openide.util.NbBundle.getMessage(ConditionedStructuredExecPanel.class, "ConditionedStructuredExecPanel.Conditions")
        });
        getArgTable().setModel(model);
        addConditionPanel();
    }
    
    private void addConditionPanel() {
        java.awt.GridBagConstraints gridBagConstraints;
        
        jPanel1 = new javax.swing.JPanel();
        conditionLabel = new javax.swing.JLabel();
        conditionComboBox = new javax.swing.JComboBox();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();
        
        jPanel1.setLayout(new java.awt.GridBagLayout());

        conditionLabel.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.Condition"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(conditionLabel, gridBagConstraints);

        conditionComboBox.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(conditionComboBox, gridBagConstraints);

        addButton.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.addButton"));
        addButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(addButton, gridBagConstraints);

        editButton.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.editButton"));
        editButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(editButton, gridBagConstraints);

        removeButton.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.removeButton"));
        removeButton.addActionListener(formListener);

        jPanel1.add(removeButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 11, 11);
        add(jPanel1, gridBagConstraints);
    }
    
    private class FormListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == conditionComboBox) {
                ConditionedStructuredExecPanel.this.conditionComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == addButton) {
                ConditionedStructuredExecPanel.this.addButtonActionPerformed(evt);
            }
            else if (evt.getSource() == editButton) {
                ConditionedStructuredExecPanel.this.editButtonActionPerformed(evt);
            }
            else if (evt.getSource() == removeButton) {
                ConditionedStructuredExecPanel.this.removeButtonActionPerformed(evt);
            }
        }
    }

    private void conditionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        //valueTextArea.setText(cs.getValue(iuc));
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        cexecString.removeValue(iuc);
        cexecStructured.removeValue(iuc);
        conditionComboBox.removeItem(iuc);
        removeButton.setEnabled(conditionComboBox.getItemCount() > 1);
    }

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        IfUnlessConditionPanel panel = new IfUnlessConditionPanel(iuc, new String[0]);
        DialogDescriptor dd = new DialogDescriptor(panel, org.openide.util.NbBundle.getMessage(IfUnlessConditionPanel.class, "IfUnlessConditionPanel.title"));
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
            iuc = panel.getCondition();
            //cs.setValue(iuc, cs.getValue((IfUnlessCondition) conditionComboBox.getSelectedItem()));  // Rather leave the last text
        }
    }

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        IfUnlessCondition iuc = new IfUnlessCondition(null);
        IfUnlessConditionPanel panel = new IfUnlessConditionPanel(iuc, new String[0]);
        DialogDescriptor dd = new DialogDescriptor(panel, org.openide.util.NbBundle.getMessage(IfUnlessConditionPanel.class, "IfUnlessConditionPanel.title"));
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
            iuc = panel.getCondition();
            conditionComboBox.addItem(iuc);
            //cs.setValue(iuc, cs.getValue((IfUnlessCondition) conditionComboBox.getSelectedItem()));  // Rather leave the last text
            conditionComboBox.setSelectedItem(iuc);
        }
        removeButton.setEnabled(conditionComboBox.getItemCount() > 1);
    }

    public ConditionedString getExecStringConditioned() {
        return cexecString;
    }
    
    public void setExecStringConditioned(ConditionedString cexecString) {
        this.cexecString = cexecString;
    }
    
    public void setExecStructuredConditioned(ConditionedObject cexecStructured) {
        this.cexecStructured = cexecStructured;
    }
    
    public ConditionedObject getExecStructuredConditioned() {
        return cexecStructured;
    }
}
