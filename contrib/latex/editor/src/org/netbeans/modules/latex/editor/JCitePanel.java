/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the Viewer module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import java.util.Iterator;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.ListDataListener;
import org.netbeans.modules.latex.editor.AnalyseBib;
import org.netbeans.modules.latex.editor.AnalyseBib.BibRecord;
import org.netbeans.modules.latex.model.command.LaTeXSource;


/**
 *
 * @author Jan Lahoda
 */
public class JCitePanel extends JPanel {
    
    private List values;
    private String label;
    
    /** Creates new form JCitePanel */
    public JCitePanel(List values, String label) {
        this.values = values;
        this.label = label;
        initComponents();
    }
    
    private String getLabel() {
        return label;
    }
    
    /** This method is called from within the constructor to
     * initialize the form
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        referencesList = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jLabel1.setLabelFor(referencesList);
        jLabel1.setText(getLabel());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(jLabel1, gridBagConstraints);

        referencesList.setModel(new DefaultComboBoxModel(values.toArray()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(referencesList, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox referencesList;
    // End of variables declaration//GEN-END:variables

//    private List/*<BibRecord>*/ references = null;
//    
//    public void setUp(List values, String name) {
//        references = Utilities.getAllBibReferences(source);
//        
//        Iterator            referencesIter = references.iterator();
//        String[]            result = new String[references.size()];
//        int                 index = 0;
//        
//        while (referencesIter.hasNext()) {
//            BibRecord record = (BibRecord) referencesIter.next();
//            
//            result[index++] = record.getRef() + ":" + record.getTitle();
//        }
//        
//        referencesList.setModel(new DefaultComboBoxModel(result));
//    }
//    
//    public BibRecord getSelectedRecord() {
//        int selected = referencesList.getSelectedIndex();
//        
//        if (selected == (-1))
//            return null;
//        
//        return (BibRecord) references.get(selected);
//    }
    
    public int getSelected() {
        return referencesList.getSelectedIndex();
    }
    
}
