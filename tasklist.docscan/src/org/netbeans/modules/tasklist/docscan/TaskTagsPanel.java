/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import java.util.Arrays;
import java.util.EventObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.CellEditorListener;
import javax.swing.table.*;

import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.PriorityListCellRenderer;

/**
 * Customizer panel for the set of tags scanned from source.
 * <p>
 * Please read comment at the beginning of initA11y before editing
 * this file using the form builder.
 * <p>
 *
 * @todo Set single list selection?
 *
 * @author  Tor Norbye
 */
public class TaskTagsPanel extends javax.swing.JPanel
        implements EnhancedCustomPropertyEditor, ActionListener {

    private DefaultTableModel model = null;

    /** Creates new form TaskTagsPanel */
    public TaskTagsPanel(TaskTags tags) {
        initComponents();
        initA11y();
        setPreferredSize(new Dimension(400, 200));
        this.tags = tags;

        TaskTag[] tagy = tags.getTags();
        model = new DefaultTableModel(new Object[0][0], new String[] {"Pattern", "Priority"}) {
            Class[] types = new Class [] {
                String.class, SuggestionPriority.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 1;
            }
        };

        for (int i = 0; i < tagy.length; i++) {
            model.addRow(new Object[]{
                tagy[i].getToken(),
                tagy[i].getPriority()
            });
        }
        patternsTable.setModel(model);

        TableColumn sportColumn = patternsTable.getColumnModel().getColumn(1);
        JComboBox combo = new JComboBox();
        combo.addItem(SuggestionPriority.HIGH);
        combo.addItem(SuggestionPriority.MEDIUM_HIGH);
        combo.addItem(SuggestionPriority.MEDIUM);
        combo.addItem(SuggestionPriority.MEDIUM_LOW);
        combo.addItem(SuggestionPriority.LOW);
        sportColumn.setCellEditor(new DefaultCellEditor(combo));

        addButton.addActionListener(this);
        changeButton.addActionListener(this);
        deleteButton.addActionListener(this);


/*
        ListCellRenderer priorityRenderer = new PriorityListCellRenderer();
        ComboBoxModel prioritiesModel =
        new DefaultComboBoxModel(Task.getPriorityNames());

        prioCombo.setModel(prioritiesModel);
        prioCombo.setRenderer(priorityRenderer);

        tokenList.setCellRenderer(new TaskTagRenderer());
        TaskTag[] t = tags.getTags();
        model = new DefaultListModel();
        for (int i = 0; i < t.length; i++) {
            model.addElement(t[i]);
        }
        tokenList.setModel(model);


        tokenList.addListSelectionListener(this);
        tokenList.setSelectionInterval(0, 0);

        updateSensitivity();
        nameField.getDocument().addDocumentListener(this);
        prioCombo.addActionListener(this);
*/
    }

    private TaskTags tags = null;

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        tagLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        patternsTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        changeButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        /*
        tagLabel.setText("Tag List:");
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(tagLabel, gridBagConstraints);

        jScrollPane1.setViewportView(patternsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        /*
        addButton.setText("Add");
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(addButton, gridBagConstraints);

        /*
        changeButton.setText("Change");
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(changeButton, gridBagConstraints);

        /*
        deleteButton.setText("Delete");
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(deleteButton, gridBagConstraints);

    }//GEN-END:initComponents

    /** Initialize accessibility settings on the panel */
    private void initA11y() {
        /*
          I couldn't figure out how to use Mnemonics.setLocalizedText
          to set labels and checkboxes with a mnemonic using the
          form builder, so the closest I got was to use "/*" and "* /
          as code pre-init/post-init blocks, such that I don't actually
          execute the bundle lookup code - and then call it explicitly
          below. (I wanted to keep the text on the components so that
          I can see them when visually editing the GUI.
        */

        Mnemonics.setLocalizedText(addButton, NbBundle.getMessage(
                TaskTagsPanel.class, "AddTag")); // NOI18N
        addButton.setToolTipText (NbBundle.getMessage (
            TaskTagsPanel.class, 
            "HINT_AddTag"
        ));
        Mnemonics.setLocalizedText(changeButton, NbBundle.getMessage(
                TaskTagsPanel.class, "ChangeTag")); // NOI18N
        changeButton.setToolTipText (NbBundle.getMessage (
            TaskTagsPanel.class, 
            "HINT_ChangeTag"
        ));
        Mnemonics.setLocalizedText(deleteButton, NbBundle.getMessage(
                TaskTagsPanel.class, "DeleteTag")); // NOI18N
//        Mnemonics.setLocalizedText(nameLabel, NbBundle.getMessage(
//                 TaskTagsPanel.class, "TagName")); // NOI18N
//        Mnemonics.setLocalizedText(prioLabel, NbBundle.getMessage(
//                 TaskTagsPanel.class, "TagPrio")); // NOI18N
        deleteButton.setToolTipText (NbBundle.getMessage (
            TaskTagsPanel.class, 
            "HINT_DeleteTag"
        ));
        Mnemonics.setLocalizedText(tagLabel, NbBundle.getMessage(
                TaskTagsPanel.class, "TagList")); // NOI18N
        tagLabel.setToolTipText (NbBundle.getMessage (
            TaskTagsPanel.class, 
            "HINT_TagList"
        ));
        patternsTable.setToolTipText (NbBundle.getMessage (
            TaskTagsPanel.class, 
            "HINT_TagList"
        ));
        

//        prioLabel.setLabelFor(prioCombo);
//        tagLabel.setLabelFor(tokenList);
//        nameLabel.setLabelFor(nameField);

        this.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(TaskTagsPanel.class, "ACSD_Tags")); // NOI18N
//        prioCombo.getAccessibleContext().setAccessibleDescription(
//                NbBundle.getMessage(TaskTagsPanel.class, "ACSD_Prio")); // NOI18N
//        tokenList.getAccessibleContext().setAccessibleDescription(
//                NbBundle.getMessage(TaskTagsPanel.class, "ACSD_List")); // NOI18N
//        nameField.getAccessibleContext().setAccessibleDescription(
//                NbBundle.getMessage(TaskTagsPanel.class, "ACSD_Name")); // NOI18N
        // Buttons too?
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton changeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable patternsTable;
    private javax.swing.JLabel tagLabel;
    // End of variables declaration//GEN-END:variables


    // When used as a property customizer
    public Object getPropertyValue() throws IllegalStateException {
        return getEditedTags();
    }

    private TaskTags getEditedTags() {
        TaskTag[] ts = new TaskTag[model.getRowCount()];
        for (int i = 0; i < model.getRowCount(); i++) {
            String token = (String) model.getValueAt(i, 0);
            SuggestionPriority prio =  (SuggestionPriority) model.getValueAt(i,1);
            TaskTag tag = new TaskTag(token, prio);
            ts[i] = tag;
        }
        tags = new TaskTags();
        tags.setTags(ts);
        return tags;
    }

    private void updateSensitivity() {
        int[] selected = patternsTable.getSelectedRows();
        int count = (selected != null) ? selected.length : 0;
        deleteButton.setEnabled(count == 1);
        changeButton.setEnabled (count == 1);
        addButton.setEnabled(true);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == addButton) {
            NotifyDescriptor.InputLine d = new NotifyDescriptor.InputLine("Pattern", "New Task Pattern");
            DialogDisplayer.getDefault().notify(d);
            String text = d.getInputText();
            model.addRow(new Object[] {text, SuggestionPriority.MEDIUM});
        } else if (source == changeButton) {
            int row = patternsTable.getSelectedRow();
            String pattern = (String) model.getValueAt(row, 0);
            NotifyDescriptor.InputLine d = new NotifyDescriptor.InputLine("Pattern", "Edit Task Pattern");
            d.setInputText(pattern);
            DialogDisplayer.getDefault().notify(d);
            String text = d.getInputText();
            model.setValueAt(text, row, 0);
        } else if (source == deleteButton) {
            int row = patternsTable.getSelectedRow();
            model.removeRow(row);
        }
        updateSensitivity();
    }

}
