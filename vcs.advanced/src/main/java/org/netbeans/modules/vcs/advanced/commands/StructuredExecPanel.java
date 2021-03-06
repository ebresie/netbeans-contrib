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

package org.netbeans.modules.vcs.advanced.commands;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Arrays;
import javax.swing.table.DefaultTableModel;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.explorer.propertysheet.DefaultPropertyModel;
import org.openide.explorer.propertysheet.PropertyPanel;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;

import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec.Argument;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.ChooseDirDialog;
import org.netbeans.modules.vcscore.util.ChooseFileDialog;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author  Martin Entlicher
 */
public class StructuredExecPanel extends javax.swing.JPanel implements EnhancedCustomPropertyEditor {
    
    private String execString;
    private StructuredExec execStructured;
    protected DefaultTableModel argTableModel;
    private VcsCommand cmd;
    private boolean writable;
    
    /** Creates new form StructuredExecPanel */
    public StructuredExecPanel() {
        this(null,true);
    }
    
    /** Creates new form StructuredExecPanel */
    public StructuredExecPanel(VcsCommand cmd) {
        this(cmd,true);
    }
    /** Creates new form StructuredExecPanel */
    public StructuredExecPanel(VcsCommand cmd, boolean writable) {        
        this.cmd = cmd;
        this.writable = writable;
        initComponents();
        enableComponents();
        execButtonGroup.add(stringRadioButton);
        execButtonGroup.add(structuredRadioButton);
        stringTextField.setColumns(50);
        attachFocusLostListener();
        postInitComponents();
        editButton.setVisible(isEditArgTableRowSupported());
        argTable.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent lsev) {
                removeButton.setEnabled(argTable.getSelectedRows().length > 0);
            }
        });
    }
    
    private void enableComponents(){
        if(writable)
            return;       
        stringRadioButton.setEnabled(false);
        stringTextField.setEditable(false);
        stringEditButton.setEnabled(false);
        structuredRadioButton.setEnabled(false);
        workLabel.setEnabled(false);
        workTextField.setEditable(false);
        workButton.setEnabled(false);
        execLabel.setEnabled(false);
        execTextField.setEditable(false);
        execButton.setEnabled(false);        
        argTable.setEnabled(false);      
        addButton.setEnabled(false);
        editButton.setEnabled(false);
        removeButton.setEnabled(false);      
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        execButtonGroup = new javax.swing.ButtonGroup();
        stringRadioButton = new javax.swing.JRadioButton();
        stringTextField = new javax.swing.JTextField();
        stringEditButton = new javax.swing.JButton();
        structuredRadioButton = new javax.swing.JRadioButton();
        structuredPanel = new javax.swing.JPanel();
        workLabel = new javax.swing.JLabel();
        workTextField = new javax.swing.JTextField();
        workButton = new javax.swing.JButton();
        execLabel = new javax.swing.JLabel();
        execTextField = new javax.swing.JTextField();
        execButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        argTable = new javax.swing.JTable();
        buttonsPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel"));
        stringRadioButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.stringRadioButton_mnc").charAt(0));
        stringRadioButton.setText(org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.ExecString"));
        stringRadioButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(stringRadioButton, gridBagConstraints);
        stringRadioButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.StringRadioButton"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 6);
        add(stringTextField, gridBagConstraints);
        stringTextField.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.stringField"));
        stringTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.stringField"));

        stringEditButton.setText(org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.ExecString.EditButton"));
        stringEditButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 11);
        add(stringEditButton, gridBagConstraints);
        stringEditButton.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.stringButton"));
        stringEditButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.stringButton"));

        structuredRadioButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.structuredRadioButton").charAt(0));
        structuredRadioButton.setText(org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.ExecStructured"));
        structuredRadioButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 11);
        add(structuredRadioButton, gridBagConstraints);
        structuredRadioButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructureExecPanel.structuredExecRadioButton"));

        structuredPanel.setLayout(new java.awt.GridBagLayout());

        workLabel.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.workLabel").charAt(0));
        workLabel.setLabelFor(workTextField);
        workLabel.setText(org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.ExecStructured.Working"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 6);
        structuredPanel.add(workLabel, gridBagConstraints);
        workLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.wrokLabel"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 12);
        structuredPanel.add(workTextField, gridBagConstraints);
        workTextField.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.workTextField"));
        workTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.workTextField"));

        workButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.workButton_mnc").charAt(0));
        workButton.setText(org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.Browse"));
        workButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        structuredPanel.add(workButton, gridBagConstraints);
        workButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.workButton"));

        execLabel.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.execLabel").charAt(0));
        execLabel.setLabelFor(execTextField);
        execLabel.setText(org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.ExecStructured.Executable"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        structuredPanel.add(execLabel, gridBagConstraints);
        execLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.execLabel"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        structuredPanel.add(execTextField, gridBagConstraints);
        execTextField.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.execTextField"));
        execTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.execTextField"));

        execButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.execButton_mnc").charAt(0));
        execButton.setText(org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.Browse"));
        execButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        structuredPanel.add(execButton, gridBagConstraints);
        execButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.workButton"));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(400, 200));
        jScrollPane1.setViewportView(argTable);
        argTable.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.argTable"));
        argTable.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.argTable"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 12);
        structuredPanel.add(jScrollPane1, gridBagConstraints);

        buttonsPanel.setLayout(new java.awt.GridBagLayout());

        addButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.addButton_mnc").charAt(0));
        addButton.setText(org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.AddArg"));
        addButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        buttonsPanel.add(addButton, gridBagConstraints);
        addButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.addButton"));

        editButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.editButton_mnc").charAt(0));
        editButton.setText(org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.EditArg"));
        editButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        buttonsPanel.add(editButton, gridBagConstraints);
        editButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.editButton"));

        removeButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACS_StructuredExecPanel.removeButton_nmc").charAt(0));
        removeButton.setText(org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.RemoveArg"));
        removeButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        buttonsPanel.add(removeButton, gridBagConstraints);
        removeButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/advanced/commands/Bundle").getString("ACSD_StructuredExecPanel.removeButton"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        structuredPanel.add(buttonsPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 11, 11);
        add(structuredPanel, gridBagConstraints);

    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == stringRadioButton) {
                StructuredExecPanel.this.stringRadioButtonActionPerformed(evt);
            }
            else if (evt.getSource() == stringEditButton) {
                StructuredExecPanel.this.stringEditButtonActionPerformed(evt);
            }
            else if (evt.getSource() == structuredRadioButton) {
                StructuredExecPanel.this.structuredRadioButtonActionPerformed(evt);
            }
            else if (evt.getSource() == workButton) {
                StructuredExecPanel.this.workButtonActionPerformed(evt);
            }
            else if (evt.getSource() == execButton) {
                StructuredExecPanel.this.execButtonActionPerformed(evt);
            }
            else if (evt.getSource() == addButton) {
                StructuredExecPanel.this.addButtonActionPerformed(evt);
            }
            else if (evt.getSource() == editButton) {
                StructuredExecPanel.this.editButtonActionPerformed(evt);
            }
            else if (evt.getSource() == removeButton) {
                StructuredExecPanel.this.removeButtonActionPerformed(evt);
            }
        }
    }//GEN-END:initComponents

    private void structuredRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_structuredRadioButtonActionPerformed
        // Add your handling code here:
        if (stringRadioButton.isSelected() && execStructured == null) {
            execStructured = new StructuredExec(new java.io.File(workTextField.getText()), execTextField.getText(), new Argument[0]);
        }
        enableString(stringRadioButton.isSelected());
        enableStructured(structuredRadioButton.isSelected());
        fieldsFocusLost();
    }//GEN-LAST:event_structuredRadioButtonActionPerformed

    private void stringRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stringRadioButtonActionPerformed
        // Add your handling code here:
        enableString(stringRadioButton.isSelected());
        enableStructured(structuredRadioButton.isSelected());
        fieldsFocusLost();
    }//GEN-LAST:event_stringRadioButtonActionPerformed

    private void stringEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stringEditButtonActionPerformed
        // Add your handling code here:
        PropertyEditor stringEditor = PropertyEditorManager.findEditor (String.class);
        if ((stringEditor == null)||(!writable)) {
            stringEditButton.setEnabled(false);
        }
        stringEditor.setValue(stringTextField.getText());
        java.awt.Component editorComponent = stringEditor.getCustomEditor();
        DialogDescriptor dd = new DialogDescriptor(editorComponent, org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.ExecString"));
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
            if (editorComponent instanceof EnhancedCustomPropertyEditor) {
                try {
                    stringTextField.setText((String) ((EnhancedCustomPropertyEditor) editorComponent).getPropertyValue());
                } catch (IllegalStateException isex) {
                    ErrorManager.getDefault().notify(isex);
                }
            } else {
                stringTextField.setText((String) stringEditor.getValue());
            }
            fieldsFocusLost();
        }
    }//GEN-LAST:event_stringEditButtonActionPerformed

    private void execButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_execButtonActionPerformed
        // Add your handling code here:
        java.awt.Frame frame = null;
        java.awt.Dialog dialog = null;
        java.awt.Container tparent = getTopLevelAncestor();
        if (tparent instanceof java.awt.Frame) {
            frame = (java.awt.Frame) tparent;
        } else if (tparent instanceof java.awt.Dialog) {
            dialog = (java.awt.Dialog) tparent;
        } else {
            frame = new javax.swing.JFrame();
        }
        ChooseFileDialog chooseFile;
        if (frame != null) {
            chooseFile = new ChooseFileDialog(frame, new java.io.File(execTextField.getText ()), false);
        } else {
            chooseFile = new ChooseFileDialog(dialog, new java.io.File(execTextField.getText ()), false);
        }
        VcsUtilities.centerWindow (chooseFile);
        chooseFile.show();
        String selected = chooseFile.getSelectedFile();
        if (selected == null) {
            //D.deb("no directory selected"); // NOI18N
            return ;
        }
        execTextField.setText(selected); 
        fieldsFocusLost();
    }//GEN-LAST:event_execButtonActionPerformed

    private void workButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workButtonActionPerformed
        // Add your handling code here:
        java.awt.Frame frame = null;
        java.awt.Dialog dialog = null;
        java.awt.Container tparent = getTopLevelAncestor();
        if (tparent instanceof java.awt.Frame) {
            frame = (java.awt.Frame) tparent;
        } else if (tparent instanceof java.awt.Dialog) {
            dialog = (java.awt.Dialog) tparent;
        } else {
            frame = new javax.swing.JFrame();
        }
        ChooseDirDialog chooseDir;
        if (frame != null) {
            chooseDir = new ChooseDirDialog(frame, new java.io.File(workTextField.getText ()));
        } else {
            chooseDir = new ChooseDirDialog(dialog, new java.io.File(workTextField.getText ()));
        }
        VcsUtilities.centerWindow (chooseDir);
        chooseDir.show();
        String selected = chooseDir.getSelectedDir();
        if (selected == null) {
            //D.deb("no directory selected"); // NOI18N
            return ;
        }
        workTextField.setText(selected);
        fieldsFocusLost();
    }//GEN-LAST:event_workButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // Add your handling code here:
        removeArgTableRow();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // Add your handling code here:
        editArgTableRow();
    }//GEN-LAST:event_editButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Add your handling code here:
        addArgTableRow();
        fieldsFocusLost();
    }//GEN-LAST:event_addButtonActionPerformed
    
    protected void postInitComponents() {
        argTableModel = new DefaultTableModel(new Object[0][0], new Object[] {
            org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.Arguments"),
            org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.ArgLine")
        }) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        };
        argTable.setModel(argTableModel);
        Object lineHeaderValue = argTable.getColumnModel().getColumn(1).getHeaderValue();
        javax.swing.table.TableCellRenderer tcr = argTable.getColumnModel().getColumn(1).getHeaderRenderer();
        if (tcr == null) {
            tcr = argTable.getTableHeader().getDefaultRenderer();
        }
        java.awt.Component lineHeaderComponent = tcr.getTableCellRendererComponent(argTable, lineHeaderValue, false, true, 0, 1);
        int width = lineHeaderComponent.getPreferredSize().width;
        argTable.getColumnModel().getColumn(1).setPreferredWidth(width + 24);
        argTable.getColumnModel().getColumn(1).setMaxWidth(width + 24);
    }
    
    private void enableString(boolean enable) {        
        if(writable){
            stringEditButton.setEnabled(enable);
            stringTextField.setEnabled(enable);
            stringTextField.setEditable(enable);
        }else{
            stringEditButton.setEnabled(false);
            stringTextField.setEnabled(false);
            stringTextField.setEditable(false);
        }
    }
    
    private void enableStructured(boolean enable) {
        if(writable){
            structuredPanel.setEnabled(enable);
            workLabel.setEnabled(enable);
            workTextField.setEnabled(enable);
            workTextField.setEditable(enable);
            workButton.setEnabled(enable);
            execLabel.setEnabled(enable);
            execTextField.setEnabled(enable);
            execTextField.setEditable(enable);
            execButton.setEnabled(enable);
            argTable.setEnabled(enable);
            addButton.setEnabled(enable);
            editButton.setEnabled(enable);
            removeButton.setEnabled(enable);
        }else{
            structuredPanel.setEnabled(false);
            workLabel.setEnabled(false);
            workTextField.setEnabled(false);
            workTextField.setEditable(false);
            workButton.setEnabled(false);
            execLabel.setEnabled(false);
            execTextField.setEnabled(false);
            execTextField.setEditable(false);
            execButton.setEnabled(false);
            argTable.setEnabled(false);
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            removeButton.setEnabled(false);
        }
    }
    
    protected void addArgTableRow() {
        argTableModel.addRow(new Object[] { "", Boolean.FALSE });
    }
    
    protected boolean isEditArgTableRowSupported() {
        return false;
    }
    
    protected void editArgTableRow() {
    }
    
    protected void removeArgTableRow() {
        int[] rows = argTable.getSelectedRows();
        Arrays.sort(rows);
        for (int i = rows.length - 1; i >= 0; i--) {
            argTableModel.removeRow(rows[i]);
        }
    }
    
    private void attachFocusLostListener() {       
        FocusListener fl = new FocusAdapter() {
            public void focusLost(FocusEvent fe) {
                fieldsFocusLost();
            }
        };
        stringTextField.addFocusListener(fl);        
        execTextField.addFocusListener(fl);
        workTextField.addFocusListener(fl);
        argTable.addFocusListener(fl);
        stringRadioButton.addFocusListener(fl);
        structuredRadioButton.addFocusListener(fl);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTable argTable;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton editButton;
    private javax.swing.JButton execButton;
    private javax.swing.ButtonGroup execButtonGroup;
    private javax.swing.JLabel execLabel;
    private javax.swing.JTextField execTextField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton stringEditButton;
    private javax.swing.JRadioButton stringRadioButton;
    private javax.swing.JTextField stringTextField;
    private javax.swing.JPanel structuredPanel;
    private javax.swing.JRadioButton structuredRadioButton;
    private javax.swing.JButton workButton;
    private javax.swing.JLabel workLabel;
    private javax.swing.JTextField workTextField;
    // End of variables declaration//GEN-END:variables
    
    protected javax.swing.JTable getArgTable() {
        return argTable;
    }
    
    /** This method is called when some field loose focus */
    protected void fieldsFocusLost() {              
    }
        
    public String getExecString() {
        execString = stringTextField.getText();
        return execString;
    }
    
    public void setExecString(String execString) {        
        this.execString = execString;
        if (execString != null) {
            stringTextField.setText(execString);
            stringRadioButton.setSelected(true);
            enableString(true);
            enableStructured(false);
        }
    }
    
    public void setExecStructured(StructuredExec execStructured) {
        this.execStructured = execStructured;
        if (execStructured != null) {
            setFromStructured();
            structuredRadioButton.setSelected(true);
            enableString(false);
            enableStructured(true);
        }
    }
    
    private void setFromStructured() {
        java.io.File wf = execStructured.getWorking();
        if (wf == null) {
            workTextField.setText("");
        } else {
            workTextField.setText(wf.getPath());
        }
        execTextField.setText(execStructured.getExecutable());
        Argument[] args = execStructured.getArguments();
        for (int i = argTableModel.getRowCount() - 1; i >= 0; i--) {
            argTableModel.removeRow(i);
        }
        for (int i = 0; i < args.length; i++) {
            argTableModel.addRow(new Object[] { args[i].getArgument(),
                                                args[i].isLine() ?
                                                    Boolean.TRUE :
                                                    Boolean.FALSE });
        }
    }
    
    public StructuredExec getExecStructured() {
        int n = argTableModel.getRowCount();
        StructuredExec.Argument[] args = new StructuredExec.Argument[n];
        for (int i = 0; i < n; i++) {
            String arg = (String) argTableModel.getValueAt(i, 0);
            boolean line = ((Boolean) argTableModel.getValueAt(i, 1)).booleanValue();
            args[i] = new StructuredExec.Argument(arg, line);
        }
        return new StructuredExec(new java.io.File(workTextField.getText()), execTextField.getText(), args);
    }
    
    public boolean isStringExecSelected() {
        return stringRadioButton.isSelected();
    }
    
    public Object getPropertyValue() throws IllegalStateException {
        if (isStringExecSelected()) {
            if (cmd != null) {
                cmd.setProperty(VcsCommand.PROPERTY_EXEC, getExecString());
            }
            return null;
        } else {
            return getExecStructured();
        }
    }
    
}
