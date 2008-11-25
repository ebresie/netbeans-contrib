/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.erlang.platform.options;

import org.netbeans.api.editor.settings.SimpleValueNames;
import static org.netbeans.modules.erlang.platform.options.FmtOptions.*;
import static org.netbeans.modules.erlang.platform.options.FmtOptions.CategorySupport.OPTION_ID;
import org.netbeans.modules.erlang.platform.options.FmtOptions.CategorySupport;
import org.openide.util.NbBundle;

/**
 *
 * @author  phrebejk
 */
public class FmtTabsIndents extends javax.swing.JPanel {
   
    /** Creates new form FmtTabsIndents */
    public FmtTabsIndents() {
        initComponents();
        // Not yet implemented
        //indentCasesFromSwitchCheckBox.setVisible(false);
        
        indentSizeField.putClientProperty(OPTION_ID, SimpleValueNames.INDENT_SHIFT_WIDTH);
        continuationIndentSizeField.putClientProperty(OPTION_ID, continuationIndentSize);
        reformatCommentsCheckBox.putClientProperty(OPTION_ID, reformatComments);
        indentHtmlCheckBox.putClientProperty(OPTION_ID, indentHtml);
    }
    
    public static FormatingOptionsPanel.Category getController() {
        return new CategorySupport(
                "LBL_Indents", 
                new FmtTabsIndents(),    // NOI18N   
                NbBundle.getMessage(FmtTabsIndents.class, "SAMPLE_Indents"), // NOI18N
                new String[] { SimpleValueNames.TEXT_LIMIT_WIDTH, "30" }
                ); // NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField3 = new javax.swing.JTextField();
        jCheckBox3 = new javax.swing.JCheckBox();
        indentSizeLabel = new javax.swing.JLabel();
        indentSizeField = new javax.swing.JTextField();
        continuationIndentSizeLabel = new javax.swing.JLabel();
        continuationIndentSizeField = new javax.swing.JTextField();
        reformatCommentsCheckBox = new javax.swing.JCheckBox();
        indentHtmlCheckBox = new javax.swing.JCheckBox();

        jTextField3.setText("jTextField3");

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox3, "jCheckBox3");
        jCheckBox3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox3.setMargin(new java.awt.Insets(0, 0, 0, 0));

        setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(indentSizeLabel, org.openide.util.NbBundle.getMessage(FmtTabsIndents.class, "LBL_IndentSize")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(continuationIndentSizeLabel, org.openide.util.NbBundle.getMessage(FmtTabsIndents.class, "LBL_ContinuationIndentSize")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(reformatCommentsCheckBox, org.openide.util.NbBundle.getMessage(FmtTabsIndents.class, "LBL_ReformatComments")); // NOI18N
        reformatCommentsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(indentHtmlCheckBox, org.openide.util.NbBundle.getMessage(FmtTabsIndents.class, "LBL_IndentHTML")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(indentSizeLabel)
                            .add(continuationIndentSizeLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(indentSizeField)
                            .add(continuationIndentSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)))
                    .add(reformatCommentsCheckBox)
                    .add(indentHtmlCheckBox))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(indentSizeLabel)
                    .add(indentSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(continuationIndentSizeLabel)
                    .add(continuationIndentSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(reformatCommentsCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(indentHtmlCheckBox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField continuationIndentSizeField;
    private javax.swing.JLabel continuationIndentSizeLabel;
    private javax.swing.JCheckBox indentHtmlCheckBox;
    private javax.swing.JTextField indentSizeField;
    private javax.swing.JLabel indentSizeLabel;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JCheckBox reformatCommentsCheckBox;
    // End of variables declaration//GEN-END:variables
    
}
