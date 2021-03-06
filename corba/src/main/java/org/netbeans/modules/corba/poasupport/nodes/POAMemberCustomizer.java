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

import java.util.Vector;
import java.awt.Component;
import java.text.MessageFormat;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.TitledBorder;
import javax.swing.SwingUtilities;

import org.openide.util.Utilities;
import org.openide.src.*;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.netbeans.modules.corba.settings.POASettings;
import org.netbeans.modules.corba.poasupport.*;
import org.netbeans.modules.corba.poasupport.tools.*;

/** Customizer for new POA
 *
 * @author Dusan Balek
 */
public class POAMemberCustomizer extends javax.swing.JPanel implements DocumentListener {
    
    /** Generated UID */
    //    static final long serialVersionUID =;
    
    private POAMemberElement element;
    private Vector availableImpls;
    private ClassElement ce = null;
    private ImplFinder finder;
    
    /** Initializes the Form */
    public POAMemberCustomizer(POAMemberElement _element) {
        element = _element;
        
        FileObject fo = null;
        try {
            fo = ((DataObject)element.getDeclaringClass().getSource().getCookie(DataObject.class)).getPrimaryFile().getParent();
        }
        catch (Exception ex) {
        }
        finder = ImplFinder.getDefault();
        if (element instanceof ServantManagerElement)
            availableImpls = finder.getAvailableImpls(fo, ImplFinder.SERVANT_MANAGER);
        else if (element instanceof POAActivatorElement)
            availableImpls = finder.getAvailableImpls(fo, ImplFinder.ADAPTER_ACTIVATOR);
        else
            availableImpls = finder.getAvailableImpls(fo, ImplFinder.SERVANT);
        initComponents();
        postInitComponents();
        initDynamicComponents();
        varNameLabel.setDisplayedMnemonic(POASupport.getString("MNE_POAMemberCustomizer_VarName").charAt(0));
        genCheckBox.setMnemonic(POASupport.getString("MNE_POAMemberCustomizer_Gen").charAt(0));
        typeNameLabel.setDisplayedMnemonic(POASupport.getString("MNE_POAMemberCustomizer_TypeName").charAt(0));
        constructorLabel.setDisplayedMnemonic(POASupport.getString("MNE_POAMemberCustomizer_Ctor").charAt(0));
        initContent();
        constructorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                constructorComboBoxActionPerformed(evt);
            }
        }
        );
    }
    
    public void initContent() {
        setVarName(""); // NOI18N
        if (element instanceof ServantElement)
            setID(""); // NOI18N
        typeNameComboBox.setSelectedItem(element.getTypeName());
        updateConstructors();
        String ctor = element.getConstructor();
        if (ctor != null) {
            int idx = ctor.lastIndexOf(POASettings.DOT, ctor.indexOf(POASettings.LBR));
            if (idx != -1)
                ctor = ctor.substring(idx+1);
        }
        constructorComboBox.setSelectedItem(ctor);
        if ((element.getTypeName() != null) && (element.getConstructor() != null) && (!genCheckBox.isSelected()))
            genCheckBox.doClick();
    }
    
    
    private void postInitComponents () {
        this.varNameTextField.getAccessibleContext().setAccessibleDescription (POASupport.getString("AD_POAMemberCustomizer_VarName"));
        this.typeNameComboBox.getAccessibleContext().setAccessibleDescription (POASupport.getString("AD_POAMemberCustomizer_TypeName"));
        this.constructorComboBox.getAccessibleContext().setAccessibleDescription (POASupport.getString("AD_POAMemberCustomizer_Ctor"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        varNameLabel = new javax.swing.JLabel();
        varNameTextField = new javax.swing.JTextField();
        genCheckBox = new javax.swing.JCheckBox();
        typeNameLabel = new javax.swing.JLabel();
        typeNameComboBox = new javax.swing.JComboBox(availableImpls);
        constructorLabel = new javax.swing.JLabel();
        constructorComboBox = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        varNameLabel.setText(POASupport.getString("LBL_POAMemberCustomizer_VarName"));
        varNameLabel.setForeground(java.awt.Color.black);
        varNameLabel.setLabelFor(varNameTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(varNameLabel, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 11);
        add(varNameTextField, gridBagConstraints);

        genCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(genCheckBox, gridBagConstraints);

        typeNameLabel.setText(POASupport.getString("LBL_POAMemberCustomizer_TypeName"));
        typeNameLabel.setForeground(java.awt.Color.black);
        typeNameLabel.setLabelFor(typeNameComboBox);
        typeNameLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 24, 0, 0);
        add(typeNameLabel, gridBagConstraints);

        typeNameComboBox.setEditable(true);
        typeNameComboBox.setPreferredSize(new java.awt.Dimension(256, 20));
        typeNameComboBox.setMinimumSize(new java.awt.Dimension(128, 20));
        typeNameComboBox.setMaximumSize(new java.awt.Dimension(32767, 20));
        typeNameComboBox.setEnabled(false);
        typeNameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeNameComboBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 0, 11);
        add(typeNameComboBox, gridBagConstraints);

        constructorLabel.setText(POASupport.getString("LBL_POAMemberCustomizer_Ctor"));
        constructorLabel.setForeground(java.awt.Color.black);
        constructorLabel.setLabelFor(constructorComboBox);
        constructorLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 24, 12, 0);
        add(constructorLabel, gridBagConstraints);

        constructorComboBox.setEditable(true);
        constructorComboBox.setPreferredSize(new java.awt.Dimension(130, 20));
        constructorComboBox.setMinimumSize(new java.awt.Dimension(126, 20));
        constructorComboBox.setMaximumSize(new java.awt.Dimension(32767, 20));
        constructorComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 11, 11);
        add(constructorComboBox, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void typeNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeNameComboBoxActionPerformed
        String newName = (String)typeNameComboBox.getSelectedItem();
        String oldName = element.getTypeName();
        if ((newName == null) || (newName.equals(""))) { // NOI18N
            ce = null;
            element.setTypeName(null);
            if ((oldName != null) && (!oldName.equals(""))) // NOI18N
                updateConstructors();
            return;
        }
        if (POAChecker.checkTypeName(newName, true)) {
            ce = ClassElement.forName(newName);
            element.setTypeName(newName);
            typeNameComboBox.getEditor().setItem(newName); // dirty hack
            if (!newName.equals(oldName))
                updateConstructors();
            return;
        }
        typeNameComboBox.setSelectedItem(oldName);
        typeNameComboBox.requestFocus();
    }//GEN-LAST:event_typeNameComboBoxActionPerformed
    
    private void varNameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_varNameTextFieldFocusGained
        varNameTextField.selectAll();
    }//GEN-LAST:event_varNameTextFieldFocusGained
    
    private void varNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_varNameTextFieldFocusLost
        if (evt.isTemporary())
            return;
        String newName = varNameTextField.getText();
        if (POAChecker.checkPOAMemberVarName(newName, element, genCheckBox.isSelected(), true)) {
            element.setVarName(newName);
        }
        else {
            varNameTextField.setText(element.getVarName());
            varNameTextField.requestFocus();
        }
    }//GEN-LAST:event_varNameTextFieldFocusLost
    
    private void idTextFieldFocusGained(java.awt.event.FocusEvent evt) {
        idTextField.selectAll();
    }
    
    private void idTextFieldFocusLost(java.awt.event.FocusEvent evt) {
        if (evt.isTemporary())
            return;
        String newID = idTextField.getText();
        if (((ServantElement)element).getIDAssignmentMode() == POASettings.SERVANT_WITH_SYSTEM_ID) {
            if (POAChecker.checkServantIDVarName(newID, (ServantElement)element, true))
                ((ServantElement)element).setIDVarName(newID);
            else {
                idTextField.setText(((ServantElement)element).getIDVarName());
                idTextField.requestFocus();
            }
        }
        else {
            if (POAChecker.checkServantID(newID, (ServantElement)element, true))
                ((ServantElement)element).setObjID(newID);
            else {
                idTextField.setText(((ServantElement)element).getObjID());
                idTextField.requestFocus();
            }
        }
    }
    
    private void genCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genCheckBoxActionPerformed
        if (genCheckBox.isSelected()) {
            if (!POAChecker.checkPOAMemberVarName(varNameTextField.getText(), element, true, true)) {
                genCheckBox.setSelected(false);
                varNameTextField.requestFocus();
                return;
            }
            typeNameLabel.setEnabled(true);
            typeNameComboBox.setEnabled(true);
            constructorLabel.setEnabled(true);
            constructorComboBox.setEnabled(true);
            String newName = (String)typeNameComboBox.getSelectedItem();
            if ((newName == null) || (newName.equals(""))) { // NOI18N
                element.setTypeName(null);
            }
            else
                element.setTypeName(newName);
            String newCtor = (String)constructorComboBox.getSelectedItem();
            if ((newCtor == null) || (newCtor.equals(""))) { // NOI18N
                element.setConstructor(null);
            }
            else {
                if (newName != null) {
                    int idx = newName.lastIndexOf(POASettings.DOT);
                    if (idx != -1) {
                        element.setConstructor(newName.substring(0, idx+1) + newCtor);
                        return;
                    }
                }
                element.setConstructor(newCtor);
            }
        }
        else {
            typeNameLabel.setEnabled(false);
            typeNameComboBox.setEnabled(false);
            constructorLabel.setEnabled(false);
            constructorComboBox.setEnabled(false);
            element.setTypeName(null);
            element.setConstructor(null);
        }
    }//GEN-LAST:event_genCheckBoxActionPerformed
    
    private void constructorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        String newCtor = (String)constructorComboBox.getSelectedItem();
        if ((newCtor == null) || (newCtor.equals(""))) { // NOI18N
            element.setConstructor(null);
            return;
        }
/*
            String tie = finder.findTIEForServant(ce.getSource().getPackage()+ POASettings.DOT + ce.getName());
            if (tie != null)
                element.setConstructor(tie + POASettings.LBR + POASettings.NEW + ce.getSource().getPackage() + POASettings.DOT + newCtor+ POASettings().RBR);
            else
 */
        constructorComboBox.getEditor().setItem(newCtor); // dirty hack
        String packageName = (String)typeNameComboBox.getSelectedItem();
        if (packageName != null) {
            int idx = packageName.lastIndexOf(POASettings.DOT);
            if (idx != -1) {
                element.setConstructor(packageName.substring(0, idx+1) + newCtor);
                return;
            }
        }
        element.setConstructor(newCtor);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel varNameLabel;
    private javax.swing.JComboBox typeNameComboBox;
    private javax.swing.JCheckBox genCheckBox;
    private javax.swing.JLabel constructorLabel;
    private javax.swing.JLabel typeNameLabel;
    private javax.swing.JComboBox constructorComboBox;
    private javax.swing.JTextField varNameTextField;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField idTextField;
    
    private void initDynamicComponents() {
        if (element instanceof ServantElement) {
            this.getAccessibleContext().setAccessibleDescription (POASupport.getString("AD_POAMemeberCustomizerServantElement"));
            genCheckBox.setText(POASupport.getString("LBL_POAMemberCustomizer_GenServant"));
            genCheckBox.getAccessibleContext().setAccessibleDescription(POASupport.getString("AD_POAMemberCustomizer_GenServant"));
            idTextField = new javax.swing.JTextField();
            if (((ServantElement)element).getIDAssignmentMode() == POASettings.SERVANT_WITH_SYSTEM_ID) {
                idLabel = new javax.swing.JLabel(POASupport.getString("LBL_POAMemberCustomizer_IDVar"));
                idLabel.setDisplayedMnemonic(POASupport.getString("MNE_POAMemberCustomizer_IDVar").charAt(0));
                this.idTextField.getAccessibleContext().setAccessibleDescription ("AD_POAMemberCustomizer_IDVar");
            }
            else {
                idLabel = new javax.swing.JLabel(POASupport.getString("LBL_POAMemberCustomizer_ObjID"));
                idLabel.setDisplayedMnemonic(POASupport.getString("MNE_POAMemberCustomizer_ObjID").charAt(0));
                this.idTextField.getAccessibleContext().setAccessibleDescription ("AD_POAMemberCustomizer_ObjID");
            }
            java.awt.GridBagConstraints gridBagConstraints1;
            
            idLabel.setForeground(java.awt.Color.black);
            idLabel.setLabelFor(idTextField);
            
            gridBagConstraints1 = new java.awt.GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(6, 12, 0, 0);
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.weighty = 1.0;
            add(idLabel, gridBagConstraints1, 2);
            
            idTextField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    idTextFieldFocusLost(evt);
                }
                public void focusGained(java.awt.event.FocusEvent evt) {
                    idTextFieldFocusGained(evt);
                }
            }
            );
            
            gridBagConstraints1 = new java.awt.GridBagConstraints();
            gridBagConstraints1.gridwidth = 0;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new java.awt.Insets(5, 6, 0, 11);
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.weighty = 1.0;
            add(idTextField, gridBagConstraints1, 3);
        }
        else if (element instanceof ServantManagerElement) {
            this.getAccessibleContext().setAccessibleDescription (POASupport.getString("AD_POAMemberCustomizerServantManager"));
            genCheckBox.setText(POASupport.getString("LBL_POAMemberCustomizer_GenServantManager"));
            this.genCheckBox.getAccessibleContext().setAccessibleDescription(POASupport.getString ("AD_POAMemberCustomizer_GenServantManager"));
        }
        else if (element instanceof DefaultServantElement) {
            this.getAccessibleContext().setAccessibleDescription (POASupport.getString("AD_POAMemberCustomizerDefaultServant"));
            genCheckBox.setText(POASupport.getString("LBL_POAMemberCustomizer_GenDefaultServant"));
            this.genCheckBox.getAccessibleContext().setAccessibleDescription(POASupport.getString("AD_POAMemberCustomizer_GenDefaultServant"));
        }
        else if (element instanceof POAActivatorElement) {
            this.getAccessibleContext().setAccessibleDescription (POASupport.getString("AD_POAMemberCustomizerPOAActivator"));
            genCheckBox.setText(POASupport.getString("LBL_POAMemberCustomizer_GenPOAActivator"));
            this.genCheckBox.getAccessibleContext().setAccessibleDescription(POASupport.getString("AD_POAMemberCustomizer_GenPOAActivator"));
        }
    }
    
    private void setVarName(String name) {
        String n = name;
        if (name == null || name.length() == 0)
            n =  element.getVarName();
        
        varNameTextField.getDocument().removeDocumentListener(this);
        varNameTextField.setText(n);
        varNameTextField.getDocument().addDocumentListener(this);
        
        if (name == null || name.length() == 0)
            varNameTextField.selectAll();
    }
    
    private void setID(String name) {
        String n = name;
        if (name == null || name.length() == 0) {
            if (((ServantElement)element).getIDAssignmentMode() == POASettings.SERVANT_WITH_SYSTEM_ID)
                n = ((ServantElement)element).getIDVarName();
            else
                n = ((ServantElement)element).getObjID();
        }
        
        idTextField.getDocument().removeDocumentListener(this);
        idTextField.setText(n);
        idTextField.getDocument().addDocumentListener(this);
        
        if (name == null || name.length() == 0)
            idTextField.selectAll();
    }
    
    private void updateConstructors() {
        constructorComboBox.removeAllItems();
        constructorComboBox.setSelectedItem(null);
        if (ce != null) {
            ConstructorElement[] ctors = ce.getConstructors();
            if (ctors.length == 0) {
                constructorComboBox.addItem(ce.getName() + POASettings.LBR + POASettings.RBR);
                return;
            }
            for (int i = 0; i < ctors.length; i++) {
                StringBuffer buf = new StringBuffer();
                buf.append(ctors[i].getName() + POASettings.LBR);
                MethodParameter[] params = ctors[i].getParameters();
                if (params.length > 0)
                    buf.append(params[0].getType().toString());
                for (int j = 1; j < params.length; j++) {
                    buf.append(POASettings.COMMA_SEPARATOR + params[j].getType().toString());
                }
                buf.append(POASettings.RBR);
                constructorComboBox.addItem(buf.toString());
            }
        }
        else {
            String type = (String)typeNameComboBox.getSelectedItem();
            if (type != null) {
                int idx = type.lastIndexOf(POASettings.DOT);
                if (idx != -1)
                    type = type.substring(idx+1);
                if (!type.equals("")) // NOI18N
                    constructorComboBox.addItem(type + POASettings.LBR + POASettings.RBR);
            }
        }
    }
    
    public void changedUpdate(javax.swing.event.DocumentEvent p1) {
        if (p1.getDocument() == varNameTextField.getDocument()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (varNameTextField.getText().equals("")) { // NOI18N
                        setVarName(""); // NOI18N
                    }
                }
            });
            return;
        }
        if ((idTextField != null) && (p1.getDocument() == idTextField.getDocument())) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (idTextField.getText().equals("")) { // NOI18N
                        setID(""); // NOI18N
                    }
                }
            });
            return;
        }
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent p1) {
        changedUpdate(p1);
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent p1) {
        changedUpdate(p1);
    }
}
