/*
 * NewEventPanel.java
 *
 * Created on January 24, 2008, 2:52 PM
 */
package org.netbeans.modules.hibernate.loaders.cfg.multiview;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;

/**
 *
 * @author  dc151887
 */
public class NewEventPanel extends javax.swing.JPanel {

    // TODO: hard code here for now
    private static final String[] types = new String[]{
        "auto-flush",
        "merge",
        "create",
        "create-onflush",
        "delete",
        "dirty-check",
        "evict",
        "flush",
        "flush-entity",
        "load",
        "load-collection",
        "lock",
        "refresh",
        "replicate",
        "save-update",
        "save",
        "update",
        "pre-load",
        "pre-update",
        "pre-insert",
        "pre-delete",
        "post-load",
        "post-update",
        "post-insert",
        "post-delete",
        "post-commit-update",
        "post-commit-insert",
        "post-commit-delete"
    };

    /** Creates new form NewEventPanel */
    public NewEventPanel() {
        initComponents();
        eventTypeComboBox.setModel( new DefaultComboBoxModel(types));
    }
   
    public String getEventType() {
        return (String)eventTypeComboBox.getSelectedItem();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        eventTypeLabel = new javax.swing.JLabel();
        eventTypeComboBox = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        eventTypeLabel.setText(org.openide.util.NbBundle.getMessage(NewEventPanel.class, "NewEventPanel.eventTypeLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(eventTypeLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(eventTypeComboBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox eventTypeComboBox;
    private javax.swing.JLabel eventTypeLabel;
    // End of variables declaration//GEN-END:variables
}