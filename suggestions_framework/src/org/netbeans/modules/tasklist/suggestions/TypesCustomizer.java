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

package org.netbeans.modules.tasklist.suggestions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.netbeans.api.tasklist.SuggestionManager;
import org.openide.util.NbBundle;
import org.openide.awt.Mnemonics;

/**
 * Panel used to customize which types are active, which are disabled,
 * and which of the active ones get confirmations.
 *
 * <p>
 * @todo Also consider adding a default priority assigned to suggestions
 *   of this type (if it's interesting to the user to see it, or edit it)
 * <p>
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
public class TypesCustomizer extends javax.swing.JPanel 
    implements ListSelectionListener {
    /** 
     * Creates new form TypesCustomizer 
     */
    public TypesCustomizer() {
        initComponents();
        initA11y();
        
        typesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        typesTable.setModel(new TypesCustomizer.SuggTypesTableModel());
        TableColumnModel cm = typesTable.getColumnModel();
        cm.getColumn(0).setPreferredWidth(50);
        cm.getColumn(1).setPreferredWidth(50);
        cm.getColumn(2).setPreferredWidth(300);
        typesTable.getSelectionModel().addListSelectionListener(this);
        typesTable.getSelectionModel().setSelectionInterval(0, 0);
    }

    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        activeLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descTextArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        typesTable = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        activeLabel.setLabelFor(typesTable);
        /*
        activeLabel.setText(NbBundle.getMessage(TypesCustomizer.class, "ActiveTypes")); // NOI18N();
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(activeLabel, gridBagConstraints);

        jLabel1.setLabelFor(descTextArea);
        /*
        jLabel1.setText(NbBundle.getMessage(TypesCustomizer.class, "TypeDesc")); // NOI18N();
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(jLabel1, gridBagConstraints);

        descTextArea.setEditable(false);
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);
        descTextArea.setPreferredSize(new java.awt.Dimension(400, 100));
        jScrollPane1.setViewportView(descTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        typesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(typesTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane3, gridBagConstraints);

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

        Mnemonics.setLocalizedText(activeLabel, NbBundle.getMessage(TypesCustomizer.class, "ActiveTypes")); // NOI18N
        Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(TypesCustomizer.class, "TypeDesc")); // NOI18N
        
        this.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(TypesCustomizer.class, "ACSD_TypesCustomizer")); // NOI18N
        typesTable.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(TypesCustomizer.class, "ACSD_Enabled")); // NOI18N
        descTextArea.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(TypesCustomizer.class, "ACSD_TypeDesc")); // NOI18N
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable typesTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JLabel activeLabel;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
    
    /** 
     * Apply changes in the dialog 
     */
    public void apply() {
        SuggestionManagerImpl manager = 
            (SuggestionManagerImpl)SuggestionManager.getDefault();

        SuggTypesTableModel model = (SuggTypesTableModel) typesTable.getModel();
        model.save();
    }
   
    public void valueChanged(javax.swing.event.ListSelectionEvent event) {
        int selected = typesTable.getSelectedRow();
        if (selected < 0) {
            descTextArea.setText("");
        } else {
            SuggTypesTableModel m = (SuggTypesTableModel) typesTable.getModel();
            descTextArea.setText(m.getType(selected).getDescription());
        }
    }
    
    /**
     * TableModel for SuggestionTypes
     */
    private static class SuggTypesTableModel extends AbstractTableModel {
        private static String[] columnNames;
        
        static {
            ResourceBundle rb = NbBundle.getBundle(SuggTypesTableModel.class);
            columnNames = new String[] {
                rb.getString("Active"), 
                rb.getString("Confirmation"), 
                rb.getString("Name")
            };
        }
        
        private SuggestionType[] types;
        private boolean[] enabled;
        private boolean[] confirm;
        
        /**
         * Constructor. Creates TableModel for all registered SuggestionTypes.
         */
        public SuggTypesTableModel() {
            Collection cl = SuggestionTypes.getTypes().getAllTypes();
            Iterator it = cl.iterator();
            SuggestionManagerImpl manager = 
                (SuggestionManagerImpl) SuggestionManager.getDefault();
            ArrayList types = new ArrayList();
            while (it.hasNext()) {
                types.add(it.next());
            }
            
            
            this.types = (SuggestionType[]) types.toArray(
                new SuggestionType[types.size()]);
            this.enabled = new boolean[types.size()];
            this.confirm = new boolean[types.size()];
            for (int i = 0; i < types.size(); i++) {
                SuggestionType type = (SuggestionType) types.get(i);
                enabled[i] = manager.isEnabled(type.getName());
                confirm[i] = manager.isConfirm(type);
            }
        }

        /**
         * Saves options.
         */
        public void save() {
            SuggestionManagerImpl manager = 
                (SuggestionManagerImpl) SuggestionManager.getDefault();
            for (int i = 0; i < types.length; i++) {
                manager.setEnabled(types[i].getName(), enabled[i], true);
                manager.setConfirm(types[i], confirm[i], 
                    i != types.length - 1);
            }
        }

        /**
         * Returns suggestion type for the specified row.
         * @param index row number
         * @return suggestion type
         */
        public SuggestionType getType(int index) {
            return types[index];
        }
        
        /**
         * Returns "confirm before fix" property for the specified row.
         * @param index row number
         * @return true = confirm
         */
        public boolean getConfirmation(int index) {
            return confirm[index];
        }

        /**
         * Sets "confirm before fix" property for the specified row.
         * @param index row number
         * @param c true = confirm
         */
        public void setConfirmation(int index, boolean c) {
            confirm[index] = c;
            fireTableCellUpdated(index, 1);
        }
        
        public int getRowCount() {
            return types.length;
        }

        public int getColumnCount() {
            return 3;
        }

        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        public Class getColumnClass(int columnIndex) {
            if (columnIndex == 2)
                return String.class;
            else
                return Boolean.class;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 2;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return enabled[rowIndex] ? Boolean.TRUE : Boolean.FALSE;
                case 1:
                    return confirm[rowIndex] ? Boolean.TRUE : Boolean.FALSE;
                case 2:
                    return types[rowIndex].getLocalizedName();
            }
            return null;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    enabled[rowIndex] = ((Boolean) aValue).booleanValue();
                    fireTableCellUpdated(rowIndex, columnIndex);
                    break;
                case 1:
                    confirm[rowIndex] = ((Boolean) aValue).booleanValue();
                    fireTableCellUpdated(rowIndex, columnIndex);
                    break;
            }
        }
    }
}
