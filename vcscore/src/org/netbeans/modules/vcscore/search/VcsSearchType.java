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


package org.netbeans.modules.vcscore.search;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.WeakListener;
import org.openidex.search.*;


/**
 * SearchType which searches statuses of files in cvs filesystems.
 *
 * @author  Martin Entlicher, Peter Zavadsky
 * @see org.openidex.search.SearchType
 */
public class VcsSearchType extends SearchType {

    public static final long serialVersionUID = 812466793021976245L;    
    
    private String matchStatus = null;
    private boolean matchExcept = false;

    private static Vector statuses = new Vector();
    private int[] indexes;
    private Vector matchStatuses = null;

    /** Property change listener. */
    private PropertyChangeListener propListener;
    
    
    /** Creates new VcsSearchType */
    public VcsSearchType() {
    }


    /** Prepares search object for search. Listens on the underlying 
     * object and fires SearchType.PROP_OBJECT_CHANGED property change
     * in cases object has changed. */
    protected void prepareSearchObject(Object searchObject) {
        DataObject dataObject = extractDataObject(searchObject);

        if(dataObject == null) 
            return;
        
        dataObject.addPropertyChangeListener(
            WeakListener.propertyChange(getDataObjectListener(), dataObject)
        );
        
    }

    /** Gets property change listener which listens on changes on searched data object. */
    private synchronized PropertyChangeListener getDataObjectListener() {
        if(propListener == null) {
            propListener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if(DataObject.PROP_COOKIE.equals(evt.getPropertyName()))
                        firePropertyChange(PROP_OBJECT_CHANGED, null, evt.getSource());
                }
            };
        }
        
        return propListener;
    }
    
    /** Tests object. Implements superclass abstract method. */
    public boolean testObject(Object object) {
        DataObject dataObject = extractDataObject(object);
            
        if(dataObject == null)
            return false;
        
        return testDataObject(dataObject);
    }

    /** Gets data object from search object. */
    private static DataObject extractDataObject(Object object) {
        DataObject dataObject = null;
        
        if(object instanceof DataObject) {
            dataObject = (DataObject)object;
        } else if(object instanceof FileObject) {
            try{
                dataObject = DataObject.find((FileObject)object);
            } catch(DataObjectNotFoundException dnfe) {
                dnfe.printStackTrace();
            }
        }

        return dataObject;
    }

    /** Creates array of search type classes.
     * @return array containing one element - <code>DataObject</code> class */
    protected Class[] createSearchTypeClasses() {
        return new Class[] {DataObject.class};
    }
    
    /** Adds available cvs statuses. */
    private void addStatuses(String[] possibleStatuses) {
        if (possibleStatuses == null)
            return;
        
        for(int i = 0; i < possibleStatuses.length; i++) {
            if (!statuses.contains(possibleStatuses[i])) statuses.add(possibleStatuses[i]);
        }
        String[] sorted = (String[]) statuses.toArray(new String[0]);
        Arrays.sort(sorted);
        statuses = new Vector(Arrays.asList(sorted));
    }

    public String[] getStatuses() {
        //System.out.println("getStatuses(this = "+this+"): return = "+statuses);
        return (String[]) statuses.toArray(new String[0]);
    }

    /** Overrides superclass method. */
    public Node[] acceptSearchRootNodes(Node[] roots) {
        if(roots == null || roots.length == 0) 
            return roots;

        List acceptedRoots = new ArrayList(roots.length);
        //statuses = new Vector();
        for(int i = 0; i < roots.length; i++) {
            Node root = roots[i];
            
            DataFolder dataFolder = (DataFolder)root.getCookie(DataFolder.class);
            if(dataFolder != null) {
                FileObject fo = dataFolder.getPrimaryFile();
                FileSystem fs = null;
                try {
                    fs = fo.getFileSystem();
                } catch(FileStateInvalidException fsie) {
                    if(Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                        fsie.printStackTrace();
                    }
                }
                
                VcsSearchTypeFileSystem searchFS;
                if (fs instanceof VcsSearchTypeFileSystem) {
                    searchFS = (VcsSearchTypeFileSystem) fs;
                } else {
                    searchFS = (VcsSearchTypeFileSystem) fo.getAttribute(VcsSearchTypeFileSystem.VCS_SEARCH_TYPE_ATTRIBUTE);
                }
                if (searchFS != null) {
                    acceptedRoots.add(root);
                    continue;
                }
            }

            InstanceCookie.Of ic = (InstanceCookie.Of)root.getCookie(InstanceCookie.Of.class);
            if(ic != null && ic.instanceOf(Repository.class)) {
                acceptedRoots.add(root);
            }

        }

        return (Node[])acceptedRoots.toArray(new Node[acceptedRoots.size()]);
    }

    /** Implements superclass abstract method. */
    public boolean enabled(Node[] nodes) {
        if(nodes == null || nodes.length == 0)
            return false;

        boolean statusesAdded = false;
        //statuses = new Vector();
        for(int i = 0; i < nodes.length; i++) {
            DataFolder dataFolder = (DataFolder)nodes[i].getCookie(DataFolder.class);
            if(dataFolder != null) {
                FileObject fo = dataFolder.getPrimaryFile();
                FileSystem fs = null;
                try {
                    fs = fo.getFileSystem();
                } catch(FileStateInvalidException fsie) {
                    if(Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                        fsie.printStackTrace();
                    }
                }
                VcsSearchTypeFileSystem searchFS;
                if (fs instanceof VcsSearchTypeFileSystem) {
                    searchFS = (VcsSearchTypeFileSystem) fs;
                } else {
                    searchFS = (VcsSearchTypeFileSystem) fo.getAttribute(VcsSearchTypeFileSystem.VCS_SEARCH_TYPE_ATTRIBUTE);
                }
                if (searchFS != null) {
                    String[] possibleStatuses = searchFS.getPossibleFileStatuses();
                    if(!statusesAdded) {
                        statuses = new Vector();
                        statusesAdded = true;
                    }
                    addStatuses(possibleStatuses);
                }
            } else {
                // DataSystem does not have a DataObject cookie => skip all nodes with DataObject cookies
                if (nodes[i].getCookie(DataObject.class) != null) continue;
                InstanceCookie.Of ic = (InstanceCookie.Of)nodes[i].getCookie(InstanceCookie.Of.class);
                if(ic != null && ic.instanceOf(Repository.class)) {
                    FileSystem[] fileSystems = org.openide.filesystems.Repository.getDefault().toArray();
                    for(int j = 0; j < fileSystems.length; j++) {
                        VcsSearchTypeFileSystem searchFS;
                        if (fileSystems[j] instanceof VcsSearchTypeFileSystem) {
                            searchFS = (VcsSearchTypeFileSystem) fileSystems[j];
                        } else {
                            searchFS = (VcsSearchTypeFileSystem) fileSystems[j].getRoot().getAttribute(VcsSearchTypeFileSystem.VCS_SEARCH_TYPE_ATTRIBUTE);
                        }
                        if (searchFS != null) {
                            String[] possibleStatuses = searchFS.getPossibleFileStatuses();
                            if(!statusesAdded) {
                                statuses = new Vector();
                                statusesAdded = true;
                            }
                            addStatuses(possibleStatuses);
                        }
                    }
                }
            }
        }

        return statusesAdded;
    }

    public String getTabText() {
        return NbBundle.getBundle(VcsSearchType.class).getString ("CTL_Status");
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public Vector getMatchStatuses() {
        Vector matchStatuses = new Vector(indexes.length);
        for(int i = 0; i < indexes.length; i++) {
            matchStatuses.add(statuses.get(indexes[i]));
        }
        return matchStatuses;
    }

    public void setMatchStatus(String status) {
        if (status == null) {
            setValid(false);
            throw new IllegalArgumentException();
        }
        String old = matchStatus;
        this.matchStatus = status;
        setValid(true);
        firePropertyChange("Status", old, status);
    }

    public void setStatusIndexes(int[] indexes) {
        this.indexes = indexes;
        setValid(indexes.length > 0);
        firePropertyChange("Status", null, null);
        matchStatuses = getMatchStatuses();
    }
    
    public int[] getStatusIndexes() {
        return indexes;
    }

    public boolean getMatchExcept() {
        return matchExcept;
    }

    public void setMatchExcept(boolean matchExcept) {
        this.matchExcept = matchExcept;
    }


    private boolean testDataObject(DataObject dobj) {
        if (matchStatuses == null) return true;
        FileObject fo = dobj.getPrimaryFile();
        FileSystem fs = null;
        try {
            fs = fo.getFileSystem();
        } catch(FileStateInvalidException exc) {
            fs = null;
        }
        VcsSearchTypeFileSystem searchFS;
        if (fs instanceof VcsSearchTypeFileSystem) {
            searchFS = (VcsSearchTypeFileSystem) fs;
        } else {
            searchFS = (VcsSearchTypeFileSystem) fo.getAttribute(VcsSearchTypeFileSystem.VCS_SEARCH_TYPE_ATTRIBUTE);
        }
        if (searchFS == null) return false;
        String[] states = searchFS.getStates(dobj);
        if (matchExcept) {
            List statesList = Arrays.asList(states);
            return !matchStatuses.containsAll(statesList);
        } else {
            boolean contains = false;
            for (int i = 0; i < states.length; i++) {
                contains = contains || matchStatuses.contains(states[i]);
            }
            return contains;
        }
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(this.getClass());
    }

}
