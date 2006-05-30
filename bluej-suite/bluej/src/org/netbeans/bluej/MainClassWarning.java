/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.bluej;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import org.openide.filesystems.FileObject;

/** Shows a warning that no main class is set and allows choose a main class.
 *
 * @author  Jiri Rechtacek
 * @author Milos Kleint copied from j2se project type to bluej one
 */
public class MainClassWarning extends JPanel {
   
    private String message;
    private FileObject[] sourcesRoots;

    /** Creates new form LibrariesChooser */
    public MainClassWarning (String message, FileObject[] sourcesRoots) {
        this.sourcesRoots = sourcesRoots;
        this.message = message;
        initComponents();
        // add MainClassChooser
    }
    
    /** Returns the selected main class.
     *
     * @return name of class or null if no class with the main method is selected
     */ 
    public String getSelectedMainClass () {
        return ((MainClassChooser)jPanel1).getSelectedMainClass ();
    }
    
    public void setSelectedMainClass(String clazz) {
        ((MainClassChooser)jPanel1).setSelectedMainClass(clazz);
    }
    
    public String getArguments() {
        return ((MainClassChooser)jPanel1).getArguments();
    }
    
    public void setArguments(String args) {
        ((MainClassChooser)jPanel1).setArguments(args);
    }

    public void addChangeListener (ChangeListener l) {
        ((MainClassChooser)jPanel1).addChangeListener (l);
    }
    
    public void removeChangeListener (ChangeListener l) {
        ((MainClassChooser)jPanel1).removeChangeListener (l);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new MainClassChooser (sourcesRoots, org.openide.util.NbBundle.getBundle(MainClassWarning.class).getString("CTL_SelectAvaialableMainClasses"));

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleDescription(null);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, this.message);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 12);
        add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 12);
        add(jPanel1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables


}
