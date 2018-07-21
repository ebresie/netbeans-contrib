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
/*
 * FilterConfigPanel.java
 *
 * Created on February 3, 2003, 2:26 PM
 */

package org.netbeans.modules.uidiagnostics;
import java.awt.Font;
import org.openide.windows.TopComponent;
import javax.swing.*;
import java.beans.*;
import javax.swing.event.*;
/**
 *
 * @author  Tim Boudreau
 */
public class FilterConfigPanel extends TopComponent implements PropertyChangeListener, ListSelectionListener {
    Model model = new Model();
    /** Creates new form FilterConfigPanel */
    public FilterConfigPanel() {
        initComponents();
        list.addListSelectionListener(this);
        // Cannot edit the form anymore, so do this
        Font f = UIManager.getFont("Label.font");
        if (f == null) {
            Integer sz = UIManager.getInt("customFontSize");
            f = new Font("Dialog", Font.PLAIN, sz == null ? 14 : sz.intValue());
        }
        jTextArea1.setFont(f);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        list.setModel(model);
        filterpanel = new org.netbeans.modules.uidiagnostics.FilterPanel();
        jTextArea1 = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        addButton.setText("Add new filter");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jPanel1.add(addButton);

        removeButton.setText("Remove selected filter");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        jPanel1.add(removeButton);

        add(jPanel1, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setDividerLocation(210);
        jSplitPane1.setDividerSize(9);
        list.setMinimumSize(new java.awt.Dimension(50, 50));
        list.setPreferredSize(new java.awt.Dimension(200, 200));
        jScrollPane1.setViewportView(list);

        jSplitPane1.setLeftComponent(jScrollPane1);

        filterpanel.setMaximumSize(new java.awt.Dimension(2000, 2000));
        filterpanel.setMinimumSize(new java.awt.Dimension(100, 200));
        filterpanel.setPreferredSize(new java.awt.Dimension(400, 400));
        jSplitPane1.setRightComponent(filterpanel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("control"));
        jTextArea1.setEditable(false);
        jTextArea1.setFont(new java.awt.Font("Dialog", 0, 12));
        jTextArea1.setLineWrap(true);
        jTextArea1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/uidiagnostics/Bundle").getString("LBL_ConfigDescription"));
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 7, 12)));
        add(jTextArea1, java.awt.BorderLayout.NORTH);

    }//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // Add your handling code here:
        int i = list.getSelectedIndex();
        if (i == -1) return;
        model.remove(i);
        if (i < model.size()) {
            list.setSelectedIndex (i);
        } else {
            if (model.size() > 0) {
                list.setSelectedIndex (model.size()-1);
            }
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        EventFilter fltr = new EventFilter();
        model.addElement (fltr);
        filterpanel.setFilter (fltr);
        fltr.addPropertyChangeListener (this);
        list.setSelectedIndex(model.size() -1);
    }//GEN-LAST:event_addButtonActionPerformed

    public void propertyChange(PropertyChangeEvent evt) {
        //update the tree on changes here
        int i = model.indexOf(evt.getSource());
        model.fireContentsChanged(this, i, i);
    }
    
    public EventFilter[] getFilters() {
        EventFilter[] result = new EventFilter[model.size()];
        for (int i=0; i < result.length; i++) {
            result[i] = (EventFilter) model.get(i);
        }
        return result;
    }
    
    public void setFilters(EventFilter[] f) {
        int oldIndex = list.getSelectedIndex();
        model.clear();
        for (int i=0; i < f.length; i++) {
            model.addElement(f[i]);
        }
        if ((oldIndex < f.length) && (oldIndex >= 0)){
            list.setSelectedIndex(oldIndex);
        } else {
            list.setSelectedIndex (0);
        }
    }
    
    public void valueChanged(ListSelectionEvent e) {
        int i = list.getSelectedIndex();
        if (i == -1) return;
        EventFilter ef = (EventFilter) model.getElementAt(i);
        filterpanel.setFilter (ef);
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private org.netbeans.modules.uidiagnostics.FilterPanel filterpanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JList list;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables

    public static void main (String[] args) {
        JDialog d = new JDialog();
        d.getContentPane().setLayout (new java.awt.BorderLayout ());
        FilterConfigPanel jb = new FilterConfigPanel();
        d.getContentPane().add (jb, java.awt.BorderLayout.CENTER);
        d.setSize(640, 600);
        d.setLocation (20,20);
        d.show();
    }
    
    private class Model extends DefaultListModel {
        public void fireContentsChanged (Object src, int start, int end) {
            super.fireContentsChanged (src,start,end);
        }
    }
}
