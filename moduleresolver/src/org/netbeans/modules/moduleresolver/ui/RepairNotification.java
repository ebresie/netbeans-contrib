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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.moduleresolver.ui;

import org.netbeans.modules.moduleresolver.*;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.api.autoupdate.UpdateElement;
import org.openide.util.NbBundle;

/**
 *
 * @author  Jirka Rechtacek
 */
public class RepairNotification extends javax.swing.JPanel {
    private static final String sModule = NbBundle.getMessage (RepairNotification.class, "RepairNotification_sModule");
    private static final String sModules = NbBundle.getMessage (RepairNotification.class, "RepairNotification_sModules");
    Collection<UpdateElement> candidates = null;
    
    /** Creates new form RepairNotification */
    public RepairNotification (Collection<UpdateElement> modules) {
        if (modules == null) {
            candidates = Collections.emptyList ();
        } else {
            candidates = modules;
        }
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spMessage = new javax.swing.JScrollPane();
        taMessage = new javax.swing.JTextArea();
        cbDontShowAgain = new javax.swing.JCheckBox();

        spMessage.setOpaque(false);

        taMessage.setColumns(20);
        taMessage.setEditable(false);
        taMessage.setLineWrap(true);
        taMessage.setRows(5);
        taMessage.setText(org.openide.util.NbBundle.getMessage(RepairNotification.class, "taMessage_RepairNotification", new Object[] {candidates.size (), candidates.size () == 1 ? sModule : sModules})); // NOI18N
        taMessage.setWrapStyleWord(true);
        taMessage.setOpaque(false);
        spMessage.setViewportView(taMessage);

        org.openide.awt.Mnemonics.setLocalizedText(cbDontShowAgain, org.openide.util.NbBundle.getMessage(RepairNotification.class, "RepairNotification.cbDontShowAgain.text")); // NOI18N
        cbDontShowAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDontShowAgainActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cbDontShowAgain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, spMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(spMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbDontShowAgain)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbDontShowAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDontShowAgainActionPerformed
        // TODO add your handling code here:
        FindBrokenModules.getPreferences ().putBoolean (FindBrokenModules.DO_CHECK, ! cbDontShowAgain.isSelected ());
    }//GEN-LAST:event_cbDontShowAgainActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbDontShowAgain;
    private javax.swing.JScrollPane spMessage;
    private javax.swing.JTextArea taMessage;
    // End of variables declaration//GEN-END:variables
    
}