/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard.nodes.gui;

import javax.swing.event.DocumentListener;
import java.util.StringTokenizer;
/** 
 *
 * @author  root
 * @version 
 */
public class OperationPanel extends ExPanel implements DocumentListener {

    /** Creates new form OperationPanel */
    public OperationPanel() {
        initComponents ();
        postInitComponents ();
    }
  
    public String getName () {
        return this.name.getText ();
    }
    
    public void setName (String name) {
	this.name.setText (name);
    }
  
    public String getReturnType () {
        return this.ret.getText ();
    }
    
    public void setReturnType (String ret) {
	this.ret.setText (ret);
    }
  
    public String getParameters () {
        return this.params.getText ();
    }
    
    public void setParameters (String params) {
	this.params.setText (params);
    }
  
    public String getExceptions () {
        return this.except.getText ();
    }
    
    public void setExceptions (String except) {
	this.except.setText (except);
    }
  
    public String getContext () {
        return this.ctx.getText ();
    }
    
    public void setContext (String context) {
	this.ctx.setText(context);
    }
  
    public boolean isOneway () {
        return this.oneway.isSelected ();
    }
    
    public void setOneway (boolean oneway) {
	this.oneway.setSelected (oneway);
    }

    private void postInitComponents () {
        this.name.getDocument().addDocumentListener (this);
        this.ret.getDocument().addDocumentListener(this);
        this.params.getDocument().addDocumentListener (this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        ret = new javax.swing.JTextField();
        params = new javax.swing.JTextField();
        except = new javax.swing.JTextField();
        ctx = new javax.swing.JTextField();
        oneway = new javax.swing.JCheckBox();
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        setPreferredSize(new java.awt.Dimension(250, 160));
        
        jLabel1.setLabelFor(name);
        jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_ModuleName"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.insets = new java.awt.Insets(8, 8, 4, 4);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints1);
        
        
        jLabel2.setLabelFor(ret);
        jLabel2.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_Return"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets(4, 8, 4, 4);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel2, gridBagConstraints1);
        
        
        jLabel3.setLabelFor(params);
        jLabel3.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_Params"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.insets = new java.awt.Insets(4, 8, 4, 4);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel3, gridBagConstraints1);
        
        
        jLabel4.setLabelFor(except);
        jLabel4.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_Except"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.insets = new java.awt.Insets(4, 8, 4, 4);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel4, gridBagConstraints1);
        
        
        jLabel5.setLabelFor(ctx);
        jLabel5.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_Ctx"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.insets = new java.awt.Insets(4, 8, 4, 4);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel5, gridBagConstraints1);
        
        
        name.setPreferredSize(new java.awt.Dimension(100, 16));
        name.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_OperationName"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(8, 4, 4, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(name, gridBagConstraints1);
        
        
        ret.setPreferredSize(new java.awt.Dimension(100, 16));
        ret.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_OperationRetType"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(ret, gridBagConstraints1);
        
        
        params.setPreferredSize(new java.awt.Dimension(100, 16));
        params.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_OperationParams"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(params, gridBagConstraints1);
        
        
        except.setPreferredSize(new java.awt.Dimension(100, 16));
        except.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_OperationExceptions"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(except, gridBagConstraints1);
        
        
        ctx.setPreferredSize(new java.awt.Dimension(100, 16));
        ctx.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TIP_OperationCtx"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(ctx, gridBagConstraints1);
        
        
        oneway.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("TXT_OpMode"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 5;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(4, 8, 8, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(oneway, gridBagConstraints1);
        
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField name;
    private javax.swing.JTextField ret;
    private javax.swing.JTextField params;
    private javax.swing.JTextField except;
    private javax.swing.JTextField ctx;
    private javax.swing.JCheckBox oneway;
    // End of variables declaration//GEN-END:variables


    
    public void removeUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState ();
    }

    public void changedUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState ();
    }

    public void insertUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState ();
    }
    
    private boolean acceptableArguments (String params) {
        if (params.length()==0)
            return true;
        if (params.endsWith(","))
            return false;
        StringTokenizer tk = new StringTokenizer (params,",");  // No I18N
        while (tk.hasMoreTokens()) {
            String param = tk.nextToken().trim();
            String modifier = "";
            String type = "";
            String name = "";
            int state = 0;
            int start = 0;
            for (int i=0; i< param.length();i++) {
                if (state == 7 && param.charAt(i)!=' ' && param.charAt(i)!='\t') {
                    state = 8;  // Error
                }
                if (state == 8) {  // Error state
                    break;      // We found error 
                }
                if (state == 0 && (param.charAt(i)==' ' || param.charAt(i)=='\t')) {
                    modifier = param.substring(start,i);
                    state = 5;
                }
                if (state == 5 && param.charAt(i)!=' ' && param.charAt(i)!='\t') {
                    state = 1;
                    start = i;
                }
                if (state == 1 && (param.charAt(i)==' ' || param.charAt(i)=='\t')) {
                    type = param.substring(start,i);
                    state = 6;
                }
                if (state == 6 && param.charAt(i)!=' ' && param.charAt(i)!='\t') {
                    state = 2;
                    start = i;
                }
                if (state == 2 && (param.charAt(i)==' ' || param.charAt(i)=='\t' || (i == param.length()-1))) {
                    name = param.substring(start,i+1);
                    state = 7;
                }
            }
            if (state != 7)
                return false;
            if (!modifier.equals ("in") && !modifier.equals("out") && !modifier.equals("inout"))
                return false;
        }
        return true;
    }

    private void checkState () {
        if (this.name.getText().length() >0 && this.ret.getText().length() >0 && acceptableArguments (this.params.getText())) {
            enableOk();
        }
        else {
            disableOk();
        }
    }
}
