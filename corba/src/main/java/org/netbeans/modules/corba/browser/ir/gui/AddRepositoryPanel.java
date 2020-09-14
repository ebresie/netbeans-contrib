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

package org.netbeans.modules.corba.browser.ir.gui;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JFileChooser;
import org.netbeans.modules.corba.utils.IORFileFilter;


public class AddRepositoryPanel extends javax.swing.JPanel implements ItemListener {


    private static final String FILE_PROTOCOL = "file:///"; //NOI18N

    /** Creates new form BindingContextPanel */



    public AddRepositoryPanel() {

        initComponents ();
        this.jRadioButton1.addItemListener (this);
        this.jRadioButton2.addItemListener (this);
        this.jRadioButton1.setSelected (true);
        java.util.ResourceBundle b = org.openide.util.NbBundle.getBundle("org/netbeans/modules/corba/browser/ir/Bundle"); //NOI18N
        this.getAccessibleContext().setAccessibleDescription (b.getString("AD_AddRepositoryPanel"));
        nameLabel.setDisplayedMnemonic(b.getString("CTL_NameLabel_MNE").charAt(0)); //NOI18N
        urlLabel.setDisplayedMnemonic(b.getString("CTL_URLLabel_MNE").charAt(0));   //NOI18N
        iorLabel.setDisplayedMnemonic(b.getString("CTL_IORLabel_MNE").charAt(0));   //NOI18N
        this.jRadioButton1.setMnemonic (b.getString ("TXT_FromURL_MNE").charAt (0));
        this.jRadioButton2.setMnemonic (b.getString ("TXT_FromIOR_MNE").charAt (0));
        this.jButton1.setMnemonic (b.getString ("TXT_Browse_MNE").charAt (0));
        nameField.getAccessibleContext().setAccessibleDescription (b.getString("AD_NameLabel"));
        iorField.getAccessibleContext().setAccessibleDescription (b.getString("AD_URLLabel"));
        urlField.getAccessibleContext().setAccessibleDescription(b.getString("AD_IORLabel"));
        this.jRadioButton1.getAccessibleContext ().setAccessibleDescription (b.getString ("AD_FromURL"));
        this.jRadioButton2.getAccessibleContext ().setAccessibleDescription (b.getString ("AD_FromIOR"));
        this.jButton1.getAccessibleContext ().setAccessibleDescription (b.getString ("AD_Browse"));
    }


    /** This method is called from within the constructor to

     * initialize the form.

     * WARNING: Do NOT modify this code. The content of this method is

     * always regenerated by the FormEditor.

     */



    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        urlLabel = new javax.swing.JLabel();
        urlField = new javax.swing.JTextField();
        iorLabel = new javax.swing.JLabel();
        iorField = new javax.swing.JTextField();
        fillPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(400, 200));
        nameLabel.setLabelFor(nameField);
        nameLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/browser/ir/Bundle").getString("CTL_NameLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(nameLabel, gridBagConstraints);

        nameField.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 10, 0, 11);
        add(nameField, gridBagConstraints);

        urlLabel.setLabelFor(urlField);
        urlLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/browser/ir/Bundle").getString("CTL_URLLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(urlLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 11);
        gridBagConstraints.weightx = 1.0;
        add(urlField, gridBagConstraints);

        iorLabel.setLabelFor(iorField);
        iorLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/browser/ir/Bundle").getString("CTL_IORLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(iorLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 11);
        gridBagConstraints.weightx = 1.0;
        add(iorField, gridBagConstraints);

        fillPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        fillPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(fillPanel, gridBagConstraints);

        jButton1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/browser/ir/Bundle").getString("TXT_Browse"));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browse(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        add(jButton1, gridBagConstraints);

        jRadioButton1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/browser/ir/Bundle").getString("TXT_FromURL"));
        buttonGroup1.add(jRadioButton1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jRadioButton1, gridBagConstraints);

        jRadioButton2.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/browser/ir/Bundle").getString("TXT_FromIOR"));
        buttonGroup1.add(jRadioButton2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jRadioButton2, gridBagConstraints);

    }//GEN-END:initComponents

    private void browse(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browse
        // Add your handling code here:
        JFileChooser fs = new JFileChooser ();
        fs.setFileFilter ( new IORFileFilter ());
        if (fs.showOpenDialog (null) == JFileChooser.APPROVE_OPTION) {
            String fileName = fs.getSelectedFile ().getAbsolutePath ();
            this.urlField.setText (FILE_PROTOCOL + fileName);
        }
    }//GEN-LAST:event_browse





    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JTextField iorField;
    private javax.swing.JTextField urlField;
    private javax.swing.JLabel iorLabel;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JPanel fillPanel;
    // End of variables declaration//GEN-END:variables





    public String getName () {

        return nameField.getText ();

    }





    public String getUrl () {

        return urlField.getText ();

    }





    public String getIOR () {

        return iorField.getText ();

    }

    /** Invoked when an item has been selected or deselected by the user.
     * The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     */
    public void itemStateChanged(ItemEvent e) {
        boolean flag = this.jRadioButton1.isSelected ();
        this.urlLabel.setEnabled (flag);
        this.urlField.setEnabled (flag);
        this.jButton1.setEnabled (flag);
        this.iorLabel.setEnabled (!flag);
        this.iorField.setEnabled (!flag);
    }    


}


