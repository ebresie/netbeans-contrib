/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * Focus.java
 *
 * Created on February 3, 2003, 11:49 AM
 */

package org.netbeans.modules.uidiagnostics;
import org.openide.util.NbBundle;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.beans.*;
import java.awt.event.*;

/** A class that can optionally listen to events and report on those that
 *  pass its filters.
 *
 * @author  Tim Boudreau
 */
class Focus extends Object implements PropertyChangeListener {
    
    /** Creates a new instance of Focus */
    Focus() {
    }
    
    public boolean isListening() {
        return isListening;
    }
    
    public boolean toggleState() {
        if (isListening) 
            stopListening();
        else 
            startListening();
        return isListening;
    }
    
    boolean isListening=false;
    public void startListening() {
        if (isListening) return;
        System.out.println(NbBundle.getMessage (Focus.class, "MSG_LogStart") + new java.util.Date()); //NOI18N
        outFilters();
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addPropertyChangeListener (this);
        isListening = true;
    }
    
    public void outFilters() {
        EventFilter[] ef = getFilters();
        System.out.println(NbBundle.getMessage (Focus.class, "MSG_Filters"));
        for (int i=0; i < ef.length; i++) {
            System.out.println(" - " + ef[i].toString());
        }
        System.out.println("");
    }
    
    public void stopListening() {
        if (!isListening) return;
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.removePropertyChangeListener (this);
        isListening = false;
        System.out.println(NbBundle.getMessage (Focus.class, "MSG_LogStop") + new java.util.Date()); //NOI18N
    }
    
    public void configure() {
        JDialog d = new JDialog();
        d.setModal (true);
        d.getContentPane().setLayout (new java.awt.BorderLayout ());
        FilterConfigPanel jb = new FilterConfigPanel();
        jb.setFilters (getFilters());
        d.getContentPane().add (jb, java.awt.BorderLayout.CENTER);
        d.setSize(800, 400);
        d.setLocation (20,20);
        d.show();
        setFilters (jb.getFilters());
    }
    
    EventFilter[] filters=null;
    public void setFilters (EventFilter[] filters) {
        this.filters = filters;
    }
    
    public EventFilter[] getFilters () {
        if (filters != null) return filters;
        return new EventFilter[0];
    }
    
    long lastTime=System.currentTimeMillis();
    public void propertyChange(PropertyChangeEvent evt) {
        if (filters == null) return;
        boolean doOut = false;
        boolean stackTrace=false;
        for (int i=0; i < filters.length; i++) {
            if (filters[i].match (evt)) {
                doOut = true;
                stackTrace |= filters[i].isStackTrace();
            }
        }
        if (!doOut) return;
        
        long l = System.currentTimeMillis();
        //if there's been a lull, print a divider
        if (l - lastTime > 10000) {
            System.out.println("--------------------------------------------------\n");
        }
        
        StringBuffer out = new StringBuffer();
        out.append (l);
        out.append (NbBundle.getMessage(Focus.class, "MSG_Prop") + evt.getPropertyName()); //NOI18N
        Object o = evt.getOldValue();
        Object n = evt.getNewValue();
        out.append (NbBundle.getMessage(Focus.class, "MSG_From") + o2string(o)); //NOI18N
        out.append (NbBundle.getMessage(Focus.class, "MSG_To") + o2string(n)); //NOI18N
        System.out.println(out.toString());
        if (stackTrace) Thread.dumpStack();
        lastTime=l;
    }
    
    static String o2string (Object o) {
        if (o == null) return NbBundle.getMessage(Focus.class, "MSG_Null");
        if (o instanceof Component) {
            Component c = (Component) o;
            String s = c.getName();
            if (s == null) s = c.getClass().getName();
            return s;
        } else {
            return o.toString();
        }
    }

}
