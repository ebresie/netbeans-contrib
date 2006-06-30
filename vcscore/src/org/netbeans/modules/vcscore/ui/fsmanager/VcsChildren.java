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

package org.netbeans.modules.vcscore.ui.fsmanager;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.registry.FSRegistryEvent;
import org.netbeans.modules.vcscore.registry.FSRegistryListener;

import org.openide.nodes.Children;
import org.openide.nodes.*;
import org.openide.cookies.FilterCookie;
import org.openide.filesystems.*;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.WeakSet;


/** Implements children. Nodes representing registered filesystems.
 * 
 * @author Richard Gregor
 */
public class VcsChildren extends Children.Keys implements FSRegistryListener, Runnable, PropertyChangeListener {

    private FSRegistry registry;
    private Set infoWithAttachedListeners = new WeakSet();
    
    /** Create pattern children. The children are initilay unfiltered.
     * @param elemrent the atteached class. For this class we recognize the patterns 
     */ 

    public VcsChildren() {
        super();
        debug("VcsChildren Init");
        registry = FSRegistry.getDefault();
        // Add registry listener
        registry.addFSRegistryListener((FSRegistryListener) WeakListeners.create(FSRegistryListener.class, this, registry));      
    }

    /** Called when the preparation of nodes is needed
     */
    protected void addNotify() {        
        setKeys (getVcsFileSystems());        
    }

    /** Called when all children are garbage collected */
    protected void removeNotify() {
        setKeys( java.util.Collections.EMPTY_SET );
    }
    
    /** Creates nodes for given key.
     */
    protected Node[] createNodes( final Object key ) {
        debug("create nodes");
        
        FSInfo info = (FSInfo)key; 
        FSInfoBeanNode node = null;
        try{
            node = new FSInfoBeanNode(info);            
        }catch(Exception e){
        }
        return new Node[] { node };
    }
    
    private Object[] getVcsFileSystems() {           
        debug("getVcsFileSystems");
        //filter those which are isControl
        ArrayList list = new ArrayList();        
        FSInfo info[] = registry.getRegistered();
        for (int i=0; i< info.length; i++) {
            if (info[i].isControl()) {
                list.add(info[i]);
            }
            if (!infoWithAttachedListeners.contains(info[i])) {
                infoWithAttachedListeners.add(info[i]);
                info[i].addPropertyChangeListener(WeakListeners.propertyChange(this, info[i]));
            }
        }
        
        info = (FSInfo[]) list.toArray(new FSInfo[0]);
        return info;
    }

    /** Called when a new filesystem information is added.
     *
     */
    public void fsAdded(FSRegistryEvent ev) {
        resetKeys();
    }
    
    /** Called when a filesystem information is removed.
     *
     */
    public void fsRemoved(FSRegistryEvent ev) {
        resetKeys();
    }
    
    /**
     * Dispatch the refresh of children keys into AWT thread to avoid deadlock.
     */
    private final void resetKeys() {
        javax.swing.SwingUtilities.invokeLater(this);
    }
    

    /**
     * Refresh the children keys. This is implemented as runnable, for
     * convenient dispatch into AWT.
     */
    public void run() {
        setKeys(getVcsFileSystems());
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (FSInfo.PROP_CONTROL.equals(evt.getPropertyName())) {
            VcsChildren.this.resetKeys();
        }
    }

    
    class FSInfoBeanNode extends AbstractNode implements PropertyChangeListener {
        private FSInfo info;
        
        public FSInfoBeanNode(FSInfo info) throws IntrospectionException{        
            super(Children.LEAF);
            this.info = info;            
            setName(info.getFSRoot().toString());
            setDisplayName(info.getFSRoot().toString());
            info.addPropertyChangeListener(WeakListeners.propertyChange(this, info));
        }
        
        /** Finds an icon for this node. The filesystem's icon is returned.
         * @see java.bean.BeanInfo
         * @see org.openide.filesystems.FileSystem#getIcon
         * @param type constants from <CODE>java.bean.BeanInfo</CODE>
         * @return icon to use to represent the bean
         */
        public Image getIcon(int type) {
            Image icon = info.getIcon();
            return icon==null ? super.getIcon(type) : icon;
            /*
            FileSystem fileSystem = info.getFileSystem();
            Class klass = fileSystem.getClass();
            BeanInfo bi = null;
            try {
                bi = org.openide.util.Utilities.getBeanInfo(fileSystem.getClass());
            } catch (IntrospectionException e) {
                return super.getIcon(type);
            }
            Image icon =  bi.getIcon(type);
            return icon==null ? super.getIcon(type) : icon;
             */
        }

        /** Creates properties.
        */
        protected Sheet createSheet () {
            Sheet s = Sheet.createDefault ();
            Sheet.Set ss = s.get (Sheet.PROPERTIES);                    
            Node.Property p = null;           
            p = new PropertySupport.ReadOnly(
            "displayType", // NOI18N
            String.class,
            NbBundle.getMessage(VcsManager.class, "LBL_VcsNodeType"),// NOI18N
            NbBundle.getMessage(VcsManager.class, "HINT_VcsNodeType")// NOI18N
            ) {
                public Object getValue() {                    
                   // return null;
                    return info.getDisplayType();
                } 
            };
            p.setValue("suppressCustomEditor", Boolean.TRUE);
            ss.put(p);
            return s;
        }
        
        
        
        public FSInfo getInfo(){
            return info;
        }
        
        public Action getPreferredAction() {
            return VcsManager.getInstance().getCustomizeAction();
        }
    
        /** This method gets called when an info property is changed.
         * @param evt A PropertyChangeEvent object describing the event source
         *   	and the property that has changed.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (FSInfo.PROP_ROOT.equals(evt.getPropertyName())) {
                setName(info.getFSRoot().toString());
                setDisplayName(info.getFSRoot().toString());
            }
        }        
        
        
    }
        
            
    private boolean debug = false;
    private void debug(String msg){
        if(debug)
            System.err.println("VcsChildren: "+msg);
    }
    
}
