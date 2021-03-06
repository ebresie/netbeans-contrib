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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.node.actions.ui;

import javax.xml.namespace.QName;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.ddloaders.PortletXMLDataObject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/** 
 * Add Render Parameter input panel.
 * @author  Satyaranjan
 */
public class AddRenderParameterPanel extends javax.swing.JDialog {

    private PortletXMLDataObject dbObj;
    private String portletName;
    private EventObject prp;

    /** Creates new form AddRenderParameterPanel */
    public AddRenderParameterPanel(java.awt.Frame parent, PortletXMLDataObject dbObj) {
        this(parent, dbObj, null);
    }

    public AddRenderParameterPanel(java.awt.Frame parent, PortletXMLDataObject dbObj, String portletName) {
        super(parent, true);
        this.dbObj = dbObj;
        this.portletName = portletName;
        initComponents();
        setLocation(parent.getX() + (parent.getWidth() - getWidth()) / 2, parent.getY() + (parent.getHeight() - getHeight()) / 2);
        setVisible(true);
    }

    public EventObject getCoordinationObject() {
        return prp;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        nameSpaceTf = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        localPartTf = new javax.swing.JTextField();
        prefixTf = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        identifierTf = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        isQNameCB = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "LBL_ADD_PUBLIC_RENDER_PARAMETER")); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "LBL_NAMESPACE")); // NOI18N

        nameSpaceTf.setToolTipText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "TT_NAMESPACE")); // NOI18N
        nameSpaceTf.setEnabled(false);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "LBL_LOCAL_PART")); // NOI18N

        localPartTf.setToolTipText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "TT_LOCAL_PART")); // NOI18N

        prefixTf.setToolTipText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "TT_PREFIX")); // NOI18N
        prefixTf.setEnabled(false);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "LBL_PREFIX")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "LBL_ID")); // NOI18N

        identifierTf.setToolTipText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "TT_Add_Identifier")); // NOI18N

        okButton.setMnemonic('O');
        okButton.setText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "LBL_OK")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setMnemonic('C');
        cancelButton.setText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "LBL_CANCEL")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        isQNameCB.setText(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "LBL_ADD_AS_QNAME")); // NOI18N
        isQNameCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isQNameCBActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(12, 12, 12))
                    .add(layout.createSequentialGroup()
                        .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(31, 31, 31))
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(15, 15, 15)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(isQNameCB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(163, 163, 163))
                    .add(nameSpaceTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .add(identifierTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .add(localPartTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(prefixTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(150, 150, 150)))
                .add(24, 24, 24))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap(164, Short.MAX_VALUE)
                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(24, 24, 24))
        );

        layout.linkSize(new java.awt.Component[] {identifierTf, localPartTf, nameSpaceTf}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {cancelButton, okButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(isQNameCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(identifierTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(nameSpaceTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(localPartTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(prefixTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(7, 7, 7)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(okButton)
                    .add(cancelButton))
                .addContainerGap())
        );

        nameSpaceTf.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "ACC_NAMESPACE")); // NOI18N
        nameSpaceTf.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "TT_NAMESPACE")); // NOI18N
        localPartTf.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "ACC_LOCALPART")); // NOI18N
        localPartTf.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "TT_LOCAL_PART")); // NOI18N
        prefixTf.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "ACC_PREFIX")); // NOI18N
        prefixTf.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "TT_PREFIX")); // NOI18N
        identifierTf.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "ACC_Add_Identifier")); // NOI18N
        identifierTf.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "TT_Add_Identifier")); // NOI18N
        okButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "ACC_Ok")); // NOI18N
        okButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "LBL_OK")); // NOI18N
        cancelButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "ACC_Cancel")); // NOI18N
        cancelButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddRenderParameterPanel.class, "LBL_CANCEL")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    // TODO add your handling code here:
    this.setVisible(false);
    this.dispose();
}//GEN-LAST:event_cancelButtonActionPerformed

private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    // TODO add your handling code here:
    String identifier = identifierTf.getText();
    String prefix = prefixTf.getText();
    String namespace = nameSpaceTf.getText();
    String localPart = localPartTf.getText();

    if (identifier == null || identifier.length() == 0) {
        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(AddRenderParameterPanel.class, "NOT_A_VALID_IDENTIFIER"), NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
        return;
    }

    if (localPart == null || localPart.length() == 0) {
        NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(AddRenderParameterPanel.class, "NOT_A_VALID_LOCAL_PART"), NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
        return;
    }


    if (namespace == null || namespace.length() == 0) {
        namespace = null;
    }
    QName qName = null;
    if (portletName == null) {
        prp = new EventObject();
        if ((prefix == null || prefix.length() == 0) && namespace == null) {
 
            if (isQNameCB.isSelected()) {
                qName = new QName(localPart);
                prp.setQName(qName);
                dbObj.getPortletXmlHelper().addPublicRenderParameterAsQName(identifier, qName);
            } else {
                prp.setName(localPart);
                dbObj.getPortletXmlHelper().addPublicRenderParameterAsName(identifier, localPart);
            }
        } else {
            if (prefix == null || prefix.length() == 0) {
                qName = new QName(namespace, localPart);
            } else {
                qName = new QName(namespace, localPart, prefix);
            }
            prp.setQName(qName);
            dbObj.getPortletXmlHelper().addPublicRenderParameterAsQName(identifier, qName);
        }
        prp.setPublicRenderParamId(identifier);
        prp.setType(EventObject.PUBLIC_RENDER_PARAMETER_TYPE);
    } else { //also add Supported render parameter

        Object prpType = dbObj.getPortletXmlHelper().getPublicRenderParameterForId(identifier);
        if (prpType != null) {
            Object[] param = {identifier};
            NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(AddRenderParameterPanel.class, "MSG_RENDER_PARAM_WITH_SAME_ID_EXISTS", param),
                    NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        prp = new EventObject();
        if ((prefix == null || prefix.length() == 0) && namespace == null) {
            
            if (isQNameCB.isSelected()) {
                qName = new QName(localPart);
                prp.setQName(qName);
            } else {
                prp.setName(localPart);
            }
        } else {
            if (prefix == null || prefix.length() == 0) {
                qName = new QName(namespace, localPart);
            } else {
                qName = new QName(namespace, localPart, prefix);
            }
            prp.setQName(qName);
        }

        prp.setPublicRenderParamId(identifier);
        prp.setType(EventObject.PUBLIC_RENDER_PARAMETER_TYPE);

        prp = dbObj.getPortletXmlHelper().addSupportedPublicRenderParameter(portletName, prp);

    }
    this.setVisible(false);
    this.dispose();

}//GEN-LAST:event_okButtonActionPerformed

private void isQNameCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isQNameCBActionPerformed
// TODO add your handling code here:
    if (isQNameCB.isSelected()) {
        prefixTf.setEnabled(true);
        nameSpaceTf.setEnabled(true);
    } else {
        prefixTf.setEnabled(false);
        nameSpaceTf.setEnabled(false);
        prefixTf.setText("");
        nameSpaceTf.setText("");
    }
}//GEN-LAST:event_isQNameCBActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField identifierTf;
    private javax.swing.JCheckBox isQNameCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField localPartTf;
    private javax.swing.JTextField nameSpaceTf;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField prefixTf;
    // End of variables declaration//GEN-END:variables
}
