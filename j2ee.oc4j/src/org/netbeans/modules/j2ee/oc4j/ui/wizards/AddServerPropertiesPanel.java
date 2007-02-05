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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.oc4j.ui.wizards;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * @author pblaha
 */
public class AddServerPropertiesPanel implements WizardDescriptor.Panel, ChangeListener {
    
    private final static String PROP_ERROR_MESSAGE = "WizardPanel_errorMessage"; // NOI18N
    private WizardDescriptor wizard;
    private AddServerPropertiesVisualPanel component;
    private OC4JInstantiatingIterator instantiatingIterator;
    private transient Set <ChangeListener> listeners = new HashSet<ChangeListener>(1);
    
    /** Creates a new instance of AddServerPropertiesPanel */
    public AddServerPropertiesPanel(OC4JInstantiatingIterator instantiatingIterator) {
        this.instantiatingIterator = instantiatingIterator;
    }
    
    public boolean isValid() {
        AddServerPropertiesVisualPanel panel = (AddServerPropertiesVisualPanel)getComponent();
        
        if(!OC4JPluginUtils.isUserActivated(instantiatingIterator.getOC4JHomeLocation(), "oc4jadmin")) {
            panel.setInitialization(true);
            wizard.putProperty(PROP_ERROR_MESSAGE,NbBundle.getMessage(AddServerPropertiesPanel.class, "MSG_Initialization"));
            return false;
        }
        
        if(panel.getUser().length() == 0 ||
                panel.getPassword().length() == 0 ||
                panel.getWebSite().length() == 0 ) {
            wizard.putProperty(PROP_ERROR_MESSAGE,NbBundle.getMessage(AddServerPropertiesPanel.class, "MSG_MissingData"));
            return false;
        }
        
        try {
            new Integer(panel.getPort());
            new Integer(panel.getAdminPort());
            
            if(OC4JPluginProperties.isRunning("localhost", panel.getPort())
                    && !OC4JPluginProperties.isRunning("localhost", panel.getAdminPort())
                    || !OC4JPluginProperties.isRunning("localhost", panel.getPort())
                    && OC4JPluginProperties.isRunning("localhost", panel.getAdminPort()))
                throw new NumberFormatException();

        } catch(NumberFormatException ex) {
            wizard.putProperty(PROP_ERROR_MESSAGE,NbBundle.getMessage(AddServerPropertiesPanel.class, "MSG_WrongPort"));
            return false;
        }
        
        wizard.putProperty(PROP_ERROR_MESSAGE,null);
        instantiatingIterator.setPassword(panel.getPassword());
        instantiatingIterator.setUserName(panel.getUser());
        instantiatingIterator.setWebSite(panel.getWebSite());
        instantiatingIterator.setAdminPort(new Integer(panel.getAdminPort()));
        instantiatingIterator.setHttpPort(new Integer(panel.getPort()));
        return true;
    }
    
    public Component getComponent() {
        if (component == null) {
            component = new AddServerPropertiesVisualPanel(instantiatingIterator.getOC4JHomeLocation());
            component.addChangeListener(this);
        }
        return component;
    }
    
    public void stateChanged(ChangeEvent ev) {
        fireChangeEvent(ev);
    }
    
    private void fireChangeEvent(ChangeEvent ev) {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
    
    
    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    public void readSettings(Object settings) {
        if (wizard == null)
            wizard = (WizardDescriptor)settings;
    }
    
    public void storeSettings(Object settings) {
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx("j2eeplugins_registering_app_server_oc4j_properties"); //NOI18N
    }
}