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

package org.netbeans.modules.corba.wizard.panels;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.openide.*;
import org.openide.util.*;
import org.netbeans.modules.corba.wizard.CorbaWizardData;

/**
 *
 * @author  tzezula
 * @version 
 */
public abstract class AbstractWizardPanel extends javax.swing.JPanel implements WizardDescriptor.Panel {

    protected CorbaWizardData data;
    private ArrayList list = new ArrayList();

    /** Creates new form AbstractWizardPanel */
    public AbstractWizardPanel() {
        initComponents ();
    }

    /** Get the current component
     *  @return the component
     */
    public java.awt.Component getComponent(){
        return this;
    }

    /** Return the Help Context for this panel
     */
    public HelpCtx getHelp () {
        return HelpCtx.DEFAULT_HELP;
    }

    public void readSettings (Object settings) {
        if (settings instanceof CorbaWizardData)
            readCorbaSettings((CorbaWizardData)settings);
    }

    public abstract void readCorbaSettings (CorbaWizardData settings);


    public void storeSettings (Object settings) {
        if (settings instanceof CorbaWizardData)
            storeCorbaSettings((CorbaWizardData)settings);
    }

    public abstract void storeCorbaSettings (CorbaWizardData settings);

    public boolean isValid(){
        return true;
    }

    public void firePropertyChange () {
        ArrayList lst;
        synchronized (this){
            lst = (ArrayList) this.list.clone();
        }
        ChangeEvent event = new ChangeEvent(this);
        for (int i=0; i< lst.size(); i++){
            ChangeListener listener = (ChangeListener) lst.get(i);
            listener.stateChanged(event);
        }
    }

    public synchronized void addChangeListener (ChangeListener listener) {
        this.list.add(listener);
    }

    public synchronized void removeChangeListener (ChangeListener listener) {
        this.list.remove(listener);
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        setLayout (new java.awt.BorderLayout ());

    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}