/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

/*
 * OneLibraryPanel.java
 *
 * Created on May 28, 2011, 11:29:19 AM
 */
package org.netbeans.modules.nodejs.libraries;

import java.util.Arrays;

/**
 *
 * @author Tim Boudreau
 */
final class OneLibraryPanel extends javax.swing.JPanel {

    public OneLibraryPanel(String name, String description, String author) {
        initComponents();
        //GTK UI delegate is screwy as usual
        if (descriptionLabel.getUI().getClass().getName().contains("synth")) {
            descriptionLabel.setUI(new javax.swing.plaf.basic.BasicTextAreaUI());
        }
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setBackground(getBackground());
        setProperties(name, description, author);
    }
    
    boolean isSelected() {
        return selectBox.isSelected();
    }
    
    public String getModuleName() {
        return nameLabel.getText().trim();
    }

    private static final int PAD_TO_CHARS = 35;
    void setProperties(String name, String description, String author) {
        if (name.length() < PAD_TO_CHARS) {
            char[] c = new char[PAD_TO_CHARS - name.length()];
            Arrays.fill(c, ' ');
            name = name + new String(c);
        }
        nameLabel.setText(name);
        String desc = description;
        descriptionLabel.setText(desc);
        descriptionLabel.setToolTipText(author);
        invalidate();
        revalidate();
        repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        selectBox = new javax.swing.JCheckBox();
        nameLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JTextArea();

        setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("controlShadow")), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setLayout(new java.awt.GridBagLayout());

        selectBox.setText(org.openide.util.NbBundle.getMessage(OneLibraryPanel.class, "OneLibraryPanel.selectBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(selectBox, gridBagConstraints);

        nameLabel.setBackground(javax.swing.UIManager.getDefaults().getColor("control"));
        nameLabel.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        nameLabel.setLabelFor(selectBox);
        nameLabel.setText(org.openide.util.NbBundle.getMessage(OneLibraryPanel.class, "OneLibraryPanel.nameLabel.text")); // NOI18N
        nameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nameClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 27;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(nameLabel, gridBagConstraints);

        descriptionLabel.setBackground(getBackground());
        descriptionLabel.setColumns(20);
        descriptionLabel.setEditable(false);
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setText(org.openide.util.NbBundle.getMessage(OneLibraryPanel.class, "OneLibraryPanel.descriptionLabel.text")); // NOI18N
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setAutoscrolls(false);
        descriptionLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(descriptionLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void nameClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nameClicked
        selectBox.setSelected(!selectBox.isSelected());
    }//GEN-LAST:event_nameClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descriptionLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JCheckBox selectBox;
    // End of variables declaration//GEN-END:variables
}