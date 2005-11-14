/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Registry of recognized filesystem types.
 *
 * @author  Martin Entlicher
 */
public class FSRegistry {
    
    private static FSRegistry registry;
    
    private List registryListeners = new LinkedList();
    private List fsInfos = new LinkedList();
    private boolean initialized = false; // whether fsInfos are initialized
    private Comparator fsInfoComparator = new FSInfoComparator();
    
    /** Creates a new instance of FSRegistry */
    private FSRegistry() {
        // fsInfos.addAll(RecognizedFS.getDefault().getManuallyRecognized());
        // initialized lazily due to the possibility of deadlocks.
    }
    
    /**
     * Get the default filesystem registry.
     */
    public static synchronized FSRegistry getDefault() {
        if (registry == null) {
            registry = new FSRegistry();
        }
        return registry;
    }
    
    /**
     * Register a filesystem information.
     */
    public void register(FSInfo fsInfo) {
        register(fsInfo, null, false);
    }
    
    /**
     * Register a filesystem information.
     */
    void register(FSInfo fsInfo, Object propagationId, boolean autoRecognized) {
        Set manuallyRecognized = null;
        if (!initialized) {
            manuallyRecognized = RecognizedFS.getDefault().getManuallyRecognized();
        }
        synchronized (fsInfos) {
            if (!initialized) {
                fsInfos.addAll(manuallyRecognized);
                initialized = true;
            }
            if (fsInfos.contains(fsInfo)) {
                // Already registered
                return ;
            }
            fsInfos.add(fsInfo);
        }
        if (!autoRecognized) {
            RecognizedFS.getDefault().addManuallyRecognized(fsInfo);
        }
        fireFSInfoChanged(fsInfo, true, propagationId);
    }
    
    /**
     * Unregister a filesystem information.
     */
    public void unregister(FSInfo fsInfo) {
        unregister(fsInfo, null);
    }
    
    /**
     * Unregister a filesystem information.
     */
    void unregister(FSInfo fsInfo, Object propagationId) {
        Set manuallyRecognized = null;
        if (!initialized) {
            manuallyRecognized = RecognizedFS.getDefault().getManuallyRecognized();
        }
        synchronized (fsInfos) {
            if (!initialized) {
                fsInfos.addAll(manuallyRecognized);
                initialized = true;
            }
            fsInfos.remove(fsInfo);
        }
        RecognizedFS.getDefault().removeRecognized(fsInfo);
        fireFSInfoChanged(fsInfo, false, propagationId);
        fsInfo.destroy();
    }
    
    /**
     * Get all registered filesystem infos.
     */
    public FSInfo[] getRegistered() {
        FSInfo[] info;
        Set manuallyRecognized = null;
        if (!initialized) {
            manuallyRecognized = RecognizedFS.getDefault().getManuallyRecognized();
        }
        synchronized (fsInfos) {
            if (!initialized) {
                fsInfos.addAll(manuallyRecognized);
                initialized = true;
            }
            info = (FSInfo[]) fsInfos.toArray(new FSInfo[fsInfos.size()]);
        }
        Arrays.sort(info, fsInfoComparator);
        return info;
    }
    
    /**
     * Find out whether a given filesystem info is already registered or not.
     * @param info The filesystem info to test
     * @return true When the filesystem info is already registered, false otherwise.
     */
    public boolean isRegistered(FSInfo info) {
        Set manuallyRecognized = null;
        if (!initialized) {
            manuallyRecognized = RecognizedFS.getDefault().getManuallyRecognized();
        }
        synchronized (fsInfos) {
            if (!initialized) {
                fsInfos.addAll(manuallyRecognized);
                initialized = true;
            }
            return fsInfos.contains(info);
        }
    }
    
    /**
     * Add a filesystem registry listener.
     */
    public void addFSRegistryListener(FSRegistryListener fsrl) {
        synchronized (registryListeners) {
            registryListeners.add(fsrl);
        }
    }
    
    /**
     * Remove a filesystem registry listener.
     */
    public void removeFSRegistryListener(FSRegistryListener fsrl) {
        synchronized (registryListeners) {
            registryListeners.remove(fsrl);
        }
    }
    
    protected void fireFSInfoChanged(FSInfo fsInfo, boolean added, Object propagationId) {
        FSRegistryEvent evt = new FSRegistryEvent(this, fsInfo, added);
        evt.setPropagationId(propagationId);
        List listeners;
        synchronized (registryListeners) {
            listeners = new ArrayList(registryListeners);
        }
        for (Iterator it = listeners.iterator(); it.hasNext(); ) {
            FSRegistryListener l = (FSRegistryListener) it.next();
            if (added) l.fsAdded(evt);
            else l.fsRemoved(evt);
        }
    }
    
    /**
     * Compare the FSInfo objects by their FS root directories.
     */
    private static final class FSInfoComparator extends Object implements Comparator {
        
        public int compare(Object o1, Object o2) {
            FSInfo fi1 = (FSInfo) o1;
            FSInfo fi2 = (FSInfo) o2;
            return fi1.getFSRoot().compareTo(fi2.getFSRoot());
        }
        
    }
}
