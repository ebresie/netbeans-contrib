/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.annotation;

/**
 *
 * @author  mk104111
 */

import org.openide.explorer.*;
import org.openide.explorer.view.*;
import org.openide.explorer.propertysheet.PropertySheetView;
import org.openide.nodes.*;
import org.openide.util.NbBundle;
import java.util.*;
import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.util.*;



public class AnnotPatternCustomEditor extends javax.swing.JPanel implements ExplorerManager.Provider {

    
    private String[] patterns;
    private String[] displayNames;
    private ExplorerManager manager;
    private AnnotationPatternPropertyEditor editor;
    public AnnotPatternCustomEditor() {
        initComponents();
        org.openide.util.HelpCtx.setHelpIDString (this, AnnotPatternCustomEditor.class.getName ());
    }

    public void setCallingPropertyEditor(AnnotationPatternPropertyEditor editor) {
        this.editor = editor;
        AnnotPatternNode.VARIABLES_ARRAY = editor.getPatterns();
        AnnotPatternNode.VARIABLES_ARRAY_DISP_NAMES = editor.getPatternDisplaNames();
        HashMap map = new HashMap();
        for (int i = 0; i < AnnotPatternNode.VARIABLES_ARRAY.length; i++) {
            map.put(AnnotPatternNode.VARIABLES_ARRAY[i], AnnotPatternNode.VARIABLES_ARRAY_DISP_NAMES[i]);
        }
        AnnotPatternNode node = expand(map, editor.getAsText());
        ExplorerPanel panel = new ExplorerPanel();
//        this.add(panel, java.awt.BorderLayout.CENTER);
        manager = panel.getExplorerManager();
        
        manager.setRootContext(node);
        PropertySheetView propertySheetView = new PropertySheetView();
        try {
            propertySheetView.setSortingMode(org.openide.explorer.propertysheet.PropertySheet.UNSORTED);
        } catch (java.beans.PropertyVetoException exc) {
            // The change was vetoed
        }
        org.openide.awt.SplittedPanel split = new org.openide.awt.SplittedPanel();
        split.setSplitType(org.openide.awt.SplittedPanel.HORIZONTAL);
        split.add(new BeanTreeView(), org.openide.awt.SplittedPanel.ADD_LEFT);
        split.add(propertySheetView, org.openide.awt.SplittedPanel.ADD_RIGHT);
        //panel.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new RevisionTreeView(), propertySheetView));
        panel.add(split);
        ExplorerActions actions = new ExplorerActions();
        actions.attach(manager);
        this.add(panel, java.awt.BorderLayout.CENTER);
        setSize(300,300);
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        btnDefaultValue = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;

        btnDefaultValue.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcscore/annotation/Bundle").getString("AnnotPatterCustomEditor.btnDefaultValue"));
        btnDefaultValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultValueActionPerformed(evt);
            }
        });

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.insets = new java.awt.Insets(6, 12, 12, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(btnDefaultValue, gridBagConstraints1);

        btnApply.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcscore/annotation/Bundle").getString("AnnotPatternCustomEditor.bntApply"));
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.insets = new java.awt.Insets(8, 6, 12, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(btnApply, gridBagConstraints1);

        add(jPanel1, java.awt.BorderLayout.SOUTH);

    }//GEN-END:initComponents

    private void btnDefaultValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultValueActionPerformed
        // Add your handling code here:
        HashMap map = new HashMap();
        for (int i = 0; i < AnnotPatternNode.VARIABLES_ARRAY.length; i++) {
            map.put(AnnotPatternNode.VARIABLES_ARRAY[i], AnnotPatternNode.VARIABLES_ARRAY_DISP_NAMES[i]);
        }
        AnnotPatternNode node = expand(map, editor.getDefaultAnnotationPattern());
        manager.setRootContext(node);
    }//GEN-LAST:event_btnDefaultValueActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        // Add your handling code here:
        AnnotPatternNode node = (AnnotPatternNode)manager.getRootContext();
        editor.setAsText(node.getStringRepresentation());
    }//GEN-LAST:event_btnApplyActionPerformed

    public org.openide.explorer.ExplorerManager getExplorerManager() {
        return manager;
    }    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton btnDefaultValue;
    private javax.swing.JButton btnApply;
    // End of variables declaration//GEN-END:variables

    private static final long serialVersionUID = -7082330953630397064L;    

    

    public AnnotPatternNode expand(HashMap map, String cmd) {
        AnnotPatternNode node = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_PARENT);
        node.setName(NbBundle.getBundle(AnnotPatternCustomEditor.class).getString("ANNOT_NODE_NAME_ROOT"));
        node.setRoot(true);
        
        expand(node, map, cmd);
        return node;
    }
    
    public void expand(AnnotPatternNode node, HashMap map, String cmd) {
        boolean finished = false;
        String line = new String(cmd);
        int dollar = 0;
        int backslash = 0;
        int beginning = 0;
        while (!finished) {
//            System.out.println("line=" + line);
            dollar = line.indexOf('$', beginning);
            if (dollar > 0) {
                if (line.charAt(dollar - 1) == '\\') {
                    beginning = dollar + 1;
                    continue;
                }
            } else if (dollar != 0) {
                finished = true;
                continue;
            }
            if (dollar < line.length() - 3) {
                if (line.charAt(dollar + 1) == '{' ||
                    (line.charAt(dollar + 1) == '[' && line.charAt(dollar + 2) == '?' )) {
                        // it's ok.. we've found variable or condition..-> proceed..
                } else {
                    beginning = dollar + 1;
                }
                
            } else {
                // we'return prolly at the end.. now just add the remaining text..
                finished = true;
                continue;
            }
            // now decide what item it is..
            // 1. everything before the $ sign is a text item..
            String text = line.substring(0, dollar);
//            System.out.println("text=" + text);
//            System.out.println("dollar =" + dollar);
            line = line.substring(text.length());
//            System.out.println("line1=" + line);
            if (text.length() > 0) {
                AnnotPatternNode textNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_TEXT);
                textNode.setName(text);
                node.getChildren().add(new Node[] {textNode});
            }
            if (line.startsWith("${")) {
                // it's a variable..
                int trailingBracket = VcsUtilities.getPairIndex(line, 2, '{', '}');
//                System.out.println("trailing bracket=" + trailingBracket);
                if (trailingBracket > 0) {
                    String varName = line.substring(2, trailingBracket);
//                    System.out.println("varName=" + varName);
                    String displaName = (String)map.get(varName);
                    if (displaName != null) {
                        AnnotPatternNode varNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_VARIABLE);
                        varNode.setName(varName);
                        node.getChildren().add(new Node[] {varNode});
                    }
                    if (line.length() == trailingBracket + 1) {
                        line = "";
                    } else {
                        line = line.substring(trailingBracket + 1);
//                        System.out.println("cuttingline=" + line);
                    }
                } else {
                    finished = true;
                }
            } else if (line.startsWith("$[?")) {
                // it's a condition...
                int trailingBracket = VcsUtilities.getPairIndex(line, 2, '[', ']');
//                System.out.println("trailingBracket=" + trailingBracket);
                String cond = line.substring(3, trailingBracket).trim();
                AnnotPatternNode condNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_CONDITION);
                condNode.setName(cond);
                node.getChildren().add(new Node[] {condNode});

                AnnotPatternNode trueNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_PARENT);
                trueNode.setName(NbBundle.getBundle(AnnotPatternCustomEditor.class).getString("ANNOT_NODE_NAME_TRUE"));
                AnnotPatternNode falseNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_PARENT);
                falseNode.setName(NbBundle.getBundle(AnnotPatternCustomEditor.class).getString("ANNOT_NODE_NAME_FALSE"));
                condNode.getChildren().add(new Node[] {trueNode, falseNode}) ;
                
                line = line.substring(trailingBracket);
                // now get true result..
                int leadingBracket = line.indexOf('[');
                if (leadingBracket >= 0) {
                    trailingBracket = VcsUtilities.getPairIndex(line, leadingBracket + 1, '[', ']');
                    if (trailingBracket >= 0) {
                        String inside = line.substring(leadingBracket + 1, trailingBracket);
//                        System.out.println("trueinside=" + inside);
                        expand(trueNode, map, inside);
                        line = line.substring(trailingBracket + 1);
                    }
                }
//                System.out.println("line after true=" + line);
                // now get the false result..
                leadingBracket = line.indexOf('[');
                if (leadingBracket >= 0) {
                    trailingBracket = VcsUtilities.getPairIndex(line, leadingBracket + 1, '[', ']');
                    if (trailingBracket >= 0) {
                        String inside = line.substring(leadingBracket + 1, trailingBracket);
//                        System.out.println("falseinside=" + inside);
                        expand(falseNode, map, inside);
                        line = line.substring(trailingBracket + 1);
                    }
                }
//                System.out.println("line after false=" + line);
            }
            
        }
//        System.out.println("finished...");
        if (line.length() > 0) {
            AnnotPatternNode textNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_TEXT);
            textNode.setName(line);
            node.getChildren().add(new Node[] {textNode});
        }
    }

    
}
