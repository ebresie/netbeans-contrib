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
package org.netbeans.modules.apisupport.metainfservices;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public final class ExportVisualPanel1 extends JPanel
implements TableModelListener{
    private List<String> files;

    ExportWizardPanel1 panel;
    /** Creates new form ExportVisualPanel1 */
    public ExportVisualPanel1(ExportWizardPanel1 panel) {
        this.panel = panel;
        initComponents();

        tableChanged(null);
    }
    
    public String getName() {
        return "Select Inteface to Export";
    }

    final void fillTable(Collection<String> interfaces) {
        DefaultTableModel t = (DefaultTableModel)table.getModel();
        if (t.getRowCount() > 0) {
            return;
        }

        for (String s : interfaces) {
            t.addRow(new Object[] { Boolean.FALSE, s });
        }

        t.addTableModelListener(this);
    }

    final List<String> generatedFiles() {
        return files;
    }


    public void tableChanged(TableModelEvent e) {
        StringBuffer modifiedFiles = new StringBuffer();
        List<String> f = new ArrayList<String>();

        boolean one = false;
        DefaultTableModel t = (DefaultTableModel)table.getModel();
        for (int i = 0; i < t.getRowCount(); i++) {
            Boolean b = (Boolean)t.getValueAt(i, 0);
            if (Boolean.TRUE.equals(b)) {
                one = true;
                modifiedFiles.append(panel.getTarget().getPath())
                .append(File.separatorChar)
                .append("META-INF")
                .append(File.separatorChar)
                .append("services")
                .append(File.separatorChar)
                .append(t.getValueAt(i, 1).toString())
                .append("\n");

                f.add("META-INF/services/" + t.getValueAt(i, 1).toString());
            }
        }
        panel.setValid(one);

        generated.setText(modifiedFiles.toString());

        files = f;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        description = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        generated = new javax.swing.JTextArea();
        generatedLabel = new javax.swing.JLabel();

        jScrollPane2.setFocusCycleRoot(true);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Export this interface?", "Class Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(table);

        description.setColumns(20);
        description.setEditable(false);
        description.setRows(5);
        description.setText("This wizard is about to generate a registration file that will allow other\nmodules to discover your class using Lookup.getDefault().lookup(SomeInterface.class).\nPlease select one or more interfaces that you which to export your class\nas.");
        description.setEnabled(false);

        generated.setColumns(20);
        generated.setEditable(false);
        generated.setRows(5);
        generated.setEnabled(false);
        jScrollPane1.setViewportView(generated);

        org.openide.awt.Mnemonics.setLocalizedText(generatedLabel, "Generated Files:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, description)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .add(generatedLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(description, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 202, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(generatedLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea description;
    private javax.swing.JTextArea generated;
    private javax.swing.JLabel generatedLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables




}

