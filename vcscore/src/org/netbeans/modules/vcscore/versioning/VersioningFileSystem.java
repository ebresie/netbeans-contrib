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

package org.netbeans.modules.vcscore.versioning;

import java.beans.*;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.io.IOException;
import java.util.*;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.actions.VersioningExplorerAction;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.turbo.Turbo;

/**
 * Adds support for versioned input streams to VcsFileSystems.
 * <p>
 * Subclasses note: this object stores it's setting directly
 * into peer filesystem. Properties must be explicitly loaded/stored
 * using special purpose load/store methods.
 *
 * @author  Martin Entlicher
 * @author  Petr Kuzel (removed extends FileSystem)
 */
public abstract class VersioningFileSystem {

    private static final SystemAction[] NO_ACTIONS = new SystemAction[0];

    /** wrapped filesystem */
    private final AbstractFileSystem fileSystem;

    /** Keeps trace of supported filesystems. */
    private static final Map fs2versiong = new WeakHashMap(10);

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public VersioningFileSystem(AbstractFileSystem underlyingFs) {
        fileSystem = underlyingFs;
        fs2versiong.put(fileSystem, this);
    }

    /** Finds existing versioning support for file system or null. */
    public static VersioningFileSystem findFor(FileSystem fileSystem) {
        return (VersioningFileSystem) fs2versiong.get(fileSystem);
    }

    /** Support for versioned access to file streams. */
    public abstract VersioningFileSystem.Versions getVersions();

    public static interface Versions extends Serializable {

        public RevisionList getRevisions(String name, boolean refresh);

        public java.io.InputStream inputStream(String name, String revision) throws java.io.FileNotFoundException;

    }

    /** @deprecated for identity purposes use VersionFileSytem directly. */
    public String getSystemName() {
        return fileSystem.getSystemName();
    }

    public FileObject getRoot() {
        return fileSystem.getRoot();
    }

    /** Callback called on adding to VersioningRepository. */
    protected void addNotify() {
    }

    /** Callback called after removal from VersioningRepository. */
    protected void removeNotify() {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    protected final boolean storeProperty(String name, Serializable value) {
        try {
            getRoot().setAttribute("Versioning-Property-" + name, value);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    protected final Serializable loadProperty(String name, Serializable defaultValue) {
        Serializable ret = (Serializable) getRoot().getAttribute("Versioning-Property-" + name);
        return ret != null ? ret : defaultValue;
    }

    protected final void firePropertyChange(PropertyChangeEvent e) {
        pcs.firePropertyChange(e);
    }

    protected final void firePropertyChange(String name, Object oldVal, Object newVal) {
        PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldVal, newVal);
        firePropertyChange(e);
    }

    public String getDisplayName() {
        return fileSystem.getDisplayName();
    }

    // unused or called by reflection?
    public SystemAction[] getActions(Set vfoSet) {
        SystemAction[] actions = fileSystem.getActions(vfoSet);
        SystemAction myAction = SystemAction.get(VersioningExplorerAction.class);
        int index = 0;
        for (; index < actions.length; index++) {
            if (myAction.equals(actions[index])) break;
        }
        if (index < actions.length) {
            SystemAction[] actions1 = new SystemAction[actions.length - 1];
            if (index > 0) {
                System.arraycopy(actions, 0, actions1, 0, index);
            }
            if (index < actions1.length) {
                System.arraycopy(actions, index + 1, actions1, index, actions1.length - index);
            }
            actions = actions1;
        }
        return actions;
    }
    
    public SystemAction[] getRevisionActions(FileObject fo, Set revisionItems) {
        return NO_ACTIONS;
    }

    protected final FileObject findResource(String name) {
        return fileSystem.findResource(name);
    }

    protected void refreshExistingFolders() {
        // TODO require extended contract of FS passed in constructor
        // anyway I'm uncertain if it;s necessary at all
        if (fileSystem instanceof VcsFileSystem) {
            VcsFileSystem fs = (VcsFileSystem) fileSystem;
            fs.refreshExistingFolders();
        }
    }
    
    /**
     * The filter of file names that should not be presented in GUI, redefining visibilityquery.
     */
    public abstract FilenameFilter getFileFilter();
    
}
