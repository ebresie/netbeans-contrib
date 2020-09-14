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

package org.netbeans.modules.corba.wizard.nodes.gui;

import javax.swing.event.DocumentListener;
import org.netbeans.modules.corba.wizard.nodes.utils.IdlUtilities;

/**
 *
 * @author  Tomas Zezula
 * @version
 */
public class UnionPanel extends ExPanel implements DocumentListener {

    /** Creates new form UnionPanel */
    public UnionPanel() {
        super ();
        initComponents ();
        postInitComponents ();
    }


    private void postInitComponents () {
        this.name.getDocument().addDocumentListener (this);
        this.type.getDocument().addDocumentListener (this);
        this.jLabel1.setDisplayedMnemonic (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_ModuleName_MNE").charAt(0));
        this.jLabel2.setDisplayedMnemonic (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_DiscriminatorType_MNE").charAt(0));
        this.getAccessibleContext().setAccessibleDescription (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("AD_UnionPanel"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {
        jLabel1 = new javax.swing.JLabel ();
        jLabel2 = new javax.swing.JLabel ();
        name = new javax.swing.JTextField ();
        name.setPreferredSize (new java.awt.Dimension (100,16));
        name.setToolTipText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_UnionName"));
        type = new javax.swing.JTextField ();
        type.setPreferredSize (new java.awt.Dimension (100,16));
        type.setToolTipText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_UnionDiscriminator"));
        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;

        jLabel1.setText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_ModuleName"));
        jLabel1.setLabelFor (this.name);

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.insets = new java.awt.Insets (8, 8, 4, 4);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add (jLabel1, gridBagConstraints1);

        jLabel2.setText (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_DiscriminatorType"));
        jLabel2.setLabelFor (this.type);

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets (4, 8, 8, 4);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add (jLabel2, gridBagConstraints1);



        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets (8, 4, 4, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add (name, gridBagConstraints1);



        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets (4, 4, 8, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add (type, gridBagConstraints1);
        this.setPreferredSize (new java.awt.Dimension (250,88));
    }

    public String getName () {
        return this.name.getText().trim();
    }
    
    public void setName (String name) {
	this.name.setText (name);
    }
  
    public String getType () {
        return this.type.getText().trim();
    }
    
    public void setType (String type) {
	this.type.setText (type);
    }
  


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField name;
    private javax.swing.JTextField type;
    // End of variables declaration//GEN-END:variables


    private void checkState () {
        if (IdlUtilities.isValidIDLIdentifier(name.getText()) && 
	    IdlUtilities.isValidIDLIdentifier(type.getText())) {
            enableOk();
        }
        else {
            disableOk();
        }
    }

    public void removeUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState();
    }

    public void changedUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState();
    }

    public void insertUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState();
    }

}