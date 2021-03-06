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

package org.netbeans.modules.corba.poasupport.nodes;

import java.util.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.SwingUtilities;

import org.openide.util.Utilities;
import org.openide.util.NbBundle;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.netbeans.modules.corba.settings.POASettings;
import org.netbeans.modules.corba.settings.POAPolicyDescriptor;
import org.netbeans.modules.corba.settings.POAPolicyValueDescriptor;
import org.netbeans.modules.corba.poasupport.*;
import org.netbeans.modules.corba.poasupport.tools.*;

/** Customizer for new POA
 *
 * @author Dusan Balek
 */
public class POACustomizer extends javax.swing.JPanel implements DocumentListener {
    
    /** Generated UID */
    //    static final long serialVersionUID =;
    
    private POAElement element;
    private POASettings settings;
    
    public POACustomizer(POAElement _poaElement) {
        element = _poaElement;
        String _tag = element.getRootPOA().getORBTag();
        if (_tag != null)
            settings = POASupport.getCORBASettings().getSettingByTag(_tag).getPOASettings();
        else
            settings = POASupport.getPOASettings();
        initComponents ();
        poaNameLabel.setDisplayedMnemonic(POASupport.getString("MNE_POACustomizer_POAName").charAt(0));
        varNameLabel.setDisplayedMnemonic(POASupport.getString("MNE_POACustomizer_VarName").charAt(0));
        managerLabel.setDisplayedMnemonic(POASupport.getString("MNE_POACustomizer_Mgr").charAt(0));
        managerComboBox.addItem(defaultManager());
        initDynamicComponents ();
        initContent();
        this.managerComboBox.getAccessibleContext().setAccessibleDescription (POASupport.getString("AD_POACustomizer_Mgr"));
        this.poaNameTextField.getAccessibleContext().setAccessibleDescription (POASupport.getString("AD_POACustomizer_POAName"));
        this.varNameTextField.getAccessibleContext().setAccessibleDescription (POASupport.getString("AD_POACustomizer_VarName"));
        this.getAccessibleContext().setAccessibleDescription (POASupport.getString ("AD_POACustomizer"));
        
        managerComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                managerComboBoxActionPerformed(evt);
            }
        }
        );
    }
    
    public void initContent() {
        setPOAName(""); // NOI18N
        setVarName(""); // NOI18N
        String manager = element.getManager();
        if (manager != null)
            managerComboBox.setSelectedItem(manager);
        else
            managerComboBox.setSelectedItem(defaultManager());
        Properties policies = element.getPolicies();
        if (policies != null)
            for (Enumeration e = policies.keys(); e.hasMoreElements(); ){
                String key = (String)e.nextElement();
                setPolicy(key, (String)policies.get(key));
            }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        poaPanel = new javax.swing.JPanel();
        poaNameLabel = new javax.swing.JLabel();
        poaNameTextField = new javax.swing.JTextField();
        varNameLabel = new javax.swing.JLabel();
        varNameTextField = new javax.swing.JTextField();
        managerLabel = new javax.swing.JLabel();
        managerComboBox = new javax.swing.JComboBox(element.getAvailablePOAManagers());
        propertiesPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        poaPanel.setLayout(new java.awt.GridBagLayout());

        poaPanel.setAlignmentY(0.0F);
        poaPanel.setAlignmentX(0.0F);
        poaNameLabel.setText(POASupport.getString("LBL_POACustomizer_POAName"));
        poaNameLabel.setForeground(java.awt.Color.black);
        poaNameLabel.setLabelFor(poaNameTextField);
        poaNameLabel.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 9, 0, 0);
        poaPanel.add(poaNameLabel, gridBagConstraints);

        poaNameTextField.setAlignmentY(0.0F);
        poaNameTextField.setAlignmentX(0.0F);
        poaNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                poaNameTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                poaNameTextFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 0, 9);
        poaPanel.add(poaNameTextField, gridBagConstraints);

        varNameLabel.setText(POASupport.getString("LBL_POACustomizer_VarName"));
        varNameLabel.setForeground(java.awt.Color.black);
        varNameLabel.setLabelFor(varNameTextField);
        varNameLabel.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 9, 0, 0);
        poaPanel.add(varNameLabel, gridBagConstraints);

        varNameTextField.setAlignmentY(0.0F);
        varNameTextField.setAlignmentX(0.0F);
        varNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                varNameTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                varNameTextFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 0, 9);
        poaPanel.add(varNameTextField, gridBagConstraints);

        managerLabel.setText(POASupport.getString("LBL_POACustomizer_Mgr"));
        managerLabel.setForeground(java.awt.Color.black);
        managerLabel.setLabelFor(managerComboBox);
        managerLabel.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 9, 10, 0);
        poaPanel.add(managerLabel, gridBagConstraints);

        managerComboBox.setAlignmentY(0.0F);
        managerComboBox.setAlignmentX(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 9, 9);
        poaPanel.add(managerComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 4);
        add(poaPanel, gridBagConstraints);

        propertiesPanel.setLayout(new java.awt.GridBagLayout());

        propertiesPanel.setAlignmentY(0.0F);
        propertiesPanel.setAlignmentX(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 4);
        add(propertiesPanel, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void poaNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_poaNameTextFieldFocusLost
        if (evt.isTemporary())
            return;
        if (POAChecker.checkPOAName(poaNameTextField.getText(), element, true)) {
            element.setPOAName(poaNameTextField.getText());
        }
        else {
            poaNameTextField.setText(element.getPOAName());
            poaNameTextField.requestFocus();
        }
        
    }//GEN-LAST:event_poaNameTextFieldFocusLost
    
    private void poaNameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_poaNameTextFieldFocusGained
        poaNameTextField.selectAll();
    }//GEN-LAST:event_poaNameTextFieldFocusGained
    
    private void varNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_varNameTextFieldFocusLost
        if (evt.isTemporary())
            return;
        if (POAChecker.checkPOAVarName(varNameTextField.getText(), element, true)) {
            element.setVarName(varNameTextField.getText());
        }
        else {
            varNameTextField.setText(element.getVarName());
            varNameTextField.requestFocus();
        }
    }//GEN-LAST:event_varNameTextFieldFocusLost
    
    private void varNameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_varNameTextFieldFocusGained
        varNameTextField.selectAll();
    }//GEN-LAST:event_varNameTextFieldFocusGained
    
    private void managerComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        String newName = (String)managerComboBox.getSelectedItem();
        if (newName.equals (defaultManager ())) {
            element.setManager(null);
        }
        else
            element.setManager(newName);
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel varNameLabel;
    private javax.swing.JLabel managerLabel;
    private javax.swing.JTextField poaNameTextField;
    private javax.swing.JPanel poaPanel;
    private javax.swing.JLabel poaNameLabel;
    private javax.swing.JComboBox managerComboBox;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JTextField varNameTextField;
    // End of variables declaration//GEN-END:variables
    
    private void initDynamicComponents() {
        poaPanel.setBorder(new javax.swing.border.TitledBorder(
        new javax.swing.border.EtchedBorder(java.awt.Color.white, new java.awt.Color (149, 142, 130)),
        POASupport.getString("LBL_POACustomizer_Details"), 0, 0,
        new java.awt.Font ("Dialog", 0, 11), java.awt.Color.black)); // NOI18N
        propertiesPanel.setBorder(new javax.swing.border.TitledBorder(
        new javax.swing.border.EtchedBorder(java.awt.Color.white, new java.awt.Color (149, 142, 130)),
        POASupport.getString("LBL_POACustomizer_Policies"), 0, 2,
        new java.awt.Font ("Dialog", 0, 11), java.awt.Color.black)); // NOI18N
        
        if (settings == null)
            return;
        ListIterator policies = settings.getPolicies().listIterator();
        boolean first = true;
        while (policies.hasNext()) {
            POAPolicyDescriptor policy = (POAPolicyDescriptor)policies.next();            
            javax.swing.JLabel label = new javax.swing.JLabel(policy.getName() + POASupport.getString("LBL_Colon"));
            ListIterator it = policy.getValues().listIterator();
            Vector vals = new Vector();
            while (it.hasNext())
                vals.add(((POAPolicyValueDescriptor)it.next()).getName());
            javax.swing.JComboBox combo = new javax.swing.JComboBox(vals);
            combo.getAccessibleContext().setAccessibleDescription (policy.getName());
            if (vals.size() == 0)
                combo.setEditable(true);
            label.setLabelFor(combo);
            String ch = policy.getMnemonicCharacter();
            if (ch != null)
                label.setDisplayedMnemonic(ch.charAt(0));
            label.setForeground(java.awt.Color.black);
            combo.addActionListener(propertyComboBoxesListener);
            java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints();
            if (first)
                constraints.insets = new java.awt.Insets(3, 9, 0, 0);
            else if (!policies.hasNext())
                constraints.insets = new java.awt.Insets(6, 9, 10, 0);
            else
                constraints.insets = new java.awt.Insets(6, 9, 0, 0);
            constraints.anchor = java.awt.GridBagConstraints.EAST;
            constraints.weighty = 1.0;
            
            propertiesPanel.add(label, constraints);
            policyLabels.add(label);
            
            constraints = new java.awt.GridBagConstraints();
            constraints.gridwidth = 0;
            constraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            if (first)
                constraints.insets = new java.awt.Insets(3, 6, 0, 9);
            else if (!policies.hasNext())
                constraints.insets = new java.awt.Insets(5, 6, 9, 9);
            else
                constraints.insets = new java.awt.Insets(5, 6, 0, 9);
            constraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            
            propertiesPanel.add(combo, constraints);
            policyComboBoxes.add(combo);
            first = false;
        };
    }
    
    private void propertyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        int index = policyComboBoxes.indexOf(evt.getSource());
        if (index == -1)
            return;
        String label = ((javax.swing.JLabel)(policyLabels.get(index))).getText();
        label = label.substring(0, label.length()-1);
        String value = (String)((javax.swing.JComboBox)policyComboBoxes.get(index)).getSelectedItem();
        Properties policies = element.getPolicies();
        String old_value = policies.getProperty(label);
        if (old_value == null) {
            List _values = settings.getPolicyByName(label).getValues();
            if (_values.size() > 0)
                old_value = ((POAPolicyValueDescriptor)_values.get(0)).getName();
            else
                old_value = "";
        }
        if (value.equals(old_value))
            return;
        if (POAChecker.checkPOAPoliciesChange(element, policies, label, value, true)) {
            element.setPolicies(policies);
            for (int i = 0; i < policyComboBoxes.size(); i++) {
                String l = ((javax.swing.JLabel)policyLabels.get(i)).getText();
                l = l.substring(0, l.length()-1);
                String v;
                ((javax.swing.JComboBox)policyComboBoxes.get(i)).removeActionListener(propertyComboBoxesListener);
                if ((v = policies.getProperty(l)) == null) {
                    if (((javax.swing.JComboBox)policyComboBoxes.get(i)).getItemCount() > 0)
                        ((javax.swing.JComboBox)policyComboBoxes.get(i)).setSelectedIndex(0);
                }
                else
                    ((javax.swing.JComboBox)policyComboBoxes.get(i)).setSelectedItem(v);
                ((javax.swing.JComboBox)policyComboBoxes.get(i)).addActionListener(propertyComboBoxesListener);
            }
        }
        else
            ((javax.swing.JComboBox)policyComboBoxes.get(index)).setSelectedItem(old_value);
    }
    
    private void setPolicy (String name, String value) {
        int idx;
        for (idx = 0; idx < policyLabels.size(); idx++)
            if ((name + POASupport.getString("LBL_Colon")).equals(((javax.swing.JLabel)policyLabels.get(idx)).getText()))
                break;
        if (idx < policyLabels.size())
            ((javax.swing.JComboBox)policyComboBoxes.get(idx)).setSelectedItem(value);
    }
    
    private Vector policyLabels = new Vector();
    private Vector policyComboBoxes = new Vector();
    private java.awt.event.ActionListener propertyComboBoxesListener = new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            propertyComboBoxActionPerformed(evt);
        }
    };
    
    private void setPOAName(String name) {
        String n = name;
        if (name == null || name.length () == 0)
            n =  element.getPOAName();
        
        poaNameTextField.getDocument().removeDocumentListener(this);
        poaNameTextField.setText(n);
        poaNameTextField.getDocument().addDocumentListener(this);
        
        if (name == null || name.length () == 0)
            poaNameTextField.selectAll ();
    }
    
    private void setVarName(String name) {
        String n = name;
        if (name == null || name.length () == 0)
            n =  element.getVarName();
        
        varNameTextField.getDocument().removeDocumentListener(this);
        varNameTextField.setText(n);
        varNameTextField.getDocument().addDocumentListener(this);
        
        if (name == null || name.length () == 0)
            varNameTextField.selectAll ();
    }
    
    private static String defaultManager () {
        return POASupport.getString("FMT_DefaultPOAManagerName");
    }
    
    public void changedUpdate(javax.swing.event.DocumentEvent p1) {
        if (p1.getDocument () == poaNameTextField.getDocument ()) {
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    if (poaNameTextField.getText().equals ("")) { // NOI18N
                        setPOAName (""); // NOI18N
                    }
                }
            });
            return;
        }
        if (p1.getDocument () == varNameTextField.getDocument ()) {
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    if (varNameTextField.getText().equals ("")) { // NOI18N
                        setVarName (""); // NOI18N
                    }
                }
            });
            return;
        }
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent p1) {
        changedUpdate(p1);
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent p1) {
        changedUpdate(p1);
    }
}
