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

package org.netbeans.modules.vcscore;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
//import java.util.StringTokenizer;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileSystemCapability;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileStatusListener;
import org.openide.util.NbBundle;
import org.openide.util.WeakListener;
import org.openide.util.actions.SystemAction;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.cache.CacheHandlerEvent;
import org.netbeans.modules.vcscore.cache.CacheHandlerListener;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.caching.VcsCacheDir;
import org.netbeans.modules.vcscore.commands.RegexOutputCommand;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.TextOutputCommand;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
//import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.versioning.RevisionChildren;
import org.netbeans.modules.vcscore.versioning.RevisionEvent;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.RevisionList;
import org.netbeans.modules.vcscore.versioning.RevisionListener;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
//import org.netbeans.modules.vcscore.versioning.VcsFileObject;
import org.netbeans.modules.vcscore.versioning.VcsFileStatusEvent;
//import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionChildren;
import org.netbeans.modules.vcscore.versioning.impl.VersioningDataLoader;
//import org.netbeans.modules.vcscore.versioning.impl.AbstractVersioningSystem;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.ErrorManager;

//import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionChildren;

/**
 * The VersioningSystem used by VcsFileSystem
 * @author  Martin Entlicher
 */
class VcsVersioningSystem extends VersioningFileSystem implements CacheHandlerListener {

    private VcsFileSystem fileSystem;
    //private VersioningFileSystem.Status status;
    private VersioningFileSystem.Versions versions;
    //private FileStatusListener fileStatus;
    private Hashtable revisionListsByName;
    /** Holds value of property showMessages. */
    private boolean showMessages = true;
    /** Holds value of property showUnimportantFiles. */
    private boolean showUnimportantFiles = false;
    /** Holds value of property showLocalFiles. */
    private boolean showLocalFiles = true;
    /** Holds value of property ignoredGarbageFiles -- regexp of ignorable children. */
    private String ignoredGarbageFiles = ""; // NOI18N
    /** regexp matcher for ignoredFiles, null if not needed */
    private transient RE ignoredGarbageRE = null;
    
    /** Holds value of property messageLength. */
    private int messageLength = 50;    
    
    private transient FSPropertyChangeListener propListener;
    
    public static final String PROP_SHOW_DEAD_FILES = "showDeadFiles"; //NOI18N
    public static final String PROP_SHOW_MESSAGES = "showMessages"; //NOI18N
    public static final String PROP_MESSAGE_LENGTH = "messageLength"; //NOI18N
    public static final String PROP_SHOW_UNIMPORTANT_FILES = "showUnimportantFiles"; //NOI18N
    public static final String PROP_SHOW_LOCAL_FILES = "showLocalFiles"; // NOI18N
    public static final String PROP_IGNORED_GARBAGE_FILES = "ignoredGarbageFiles"; // NOI18N
    
    private static final long serialVersionUID = 6349205836150345436L;

    /** Creates new VcsVersioningSystem */
    public VcsVersioningSystem(VcsFileSystem fileSystem) {
        super(fileSystem);
        this.fileSystem = fileSystem;
        try {
            setSystemName(fileSystem.getSystemName());
        } catch (java.beans.PropertyVetoException vExc) {}
        //this.status = new VersioningFileStatus();
        this.list = new VersioningList();//fileSystem.getVcsList();
        this.info = fileSystem.getVcsInfo();
        this.change = new VersioningFSChange();
        this.attr = new VcsVersioningAttrs();
        this.versions = new VersioningVersions();
        revisionListsByName = new Hashtable();
        initListeners();
        setCapability(null);//FileSystemCapability.DOC);
    }
    
    public void addNotify() {
        propagatePropertyChange(new String[] {PROP_IGNORED_GARBAGE_FILES, PROP_SHOW_MESSAGES, 
          PROP_MESSAGE_LENGTH, PROP_SHOW_LOCAL_FILES, PROP_SHOW_UNIMPORTANT_FILES, PROP_SHOW_DEAD_FILES});
    }
    
    private void initListeners() {
        /*
        fileStatus = new FileStatusListener() {
            public void annotationChanged(FileStatusEvent ev) {
                fireFileStatusChanged(ev);
            }
        };
        fileSystem.addFileStatusListener(WeakListener.fileStatus(fileStatus, fileSystem));
         */
        propListener = new FSPropertyChangeListener();
        fileSystem.addPropertyChangeListener(WeakListener.propertyChange(propListener, fileSystem));
        addPropertyChangeListener(propListener);
    }

    /** Creates Reference. In FileSystem, which subclasses AbstractFileSystem, you can overload method
     * createReference(FileObject fo) to achieve another type of Reference (weak, strong etc.)
     * This method does a similar stuff to VcsFileSystem.createReference()
     * @param fo is FileObject. It`s reference you require to get.
     * @return Reference to FileObject
     */
    protected java.lang.ref.Reference createReference(final FileObject fo) {
        java.lang.ref.Reference ref;
        FileCacheProvider cache = fileSystem.getCacheProvider();
	if (cache != null) {
            ref = cache.createReference(fo);
	} else {
            ref = super.createReference(fo);
        }
	return ref;
    }
    
    /**
     * Get the delegated file system.
     * Just to be able to access this method in this package.
     */
    protected FileSystem getFileSystem() {
        return fileSystem;
    }
    
    /*
    public AbstractFileSystem.List getList() {
        return fileSystem.getVcsList();
    }
    
    public AbstractFileSystem.Info getInfo() {
        return fileSystem.getVcsInfo();
    }
     */
    
    
    public VersioningFileSystem.Versions getVersions() {
        return versions;
    }
    
    public FileSystem.Status getStatus() {
        return fileSystem.getStatus();
    }        
    
    public FileStatusProvider getFileStatusProvider() {
        return fileSystem.getStatusProvider();
    }
    
    public String[] getStates(org.openide.loaders.DataObject dobj) {
        return fileSystem.getStates(dobj);
    }
    
    public boolean isShowDeadFiles() {
        return fileSystem.isShowDeadFiles();
    }

    public void setShowDeadFiles(boolean showDeadFiles) {
        fileSystem.setShowDeadFiles(showDeadFiles);
        firePropertyChange(PROP_SHOW_DEAD_FILES, !showDeadFiles ? Boolean.TRUE : Boolean.FALSE, showDeadFiles ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public boolean isShowUnimportantFiles() {
        return showUnimportantFiles;
    }
    
    public void setShowUnimportantFiles(boolean showUnimportantFiles) {
        if (this.showUnimportantFiles != showUnimportantFiles) {
            this.showUnimportantFiles = showUnimportantFiles;
            firePropertyChange(PROP_SHOW_UNIMPORTANT_FILES, !showUnimportantFiles ? Boolean.TRUE : Boolean.FALSE, showUnimportantFiles ? Boolean.TRUE : Boolean.FALSE);
            refreshExistingFolders();
        }
    }

    public boolean isShowLocalFiles() {
        return showLocalFiles;
    }
    
    public void setShowLocalFiles(boolean showLocalFiles) {
        if (this.showLocalFiles != showLocalFiles) {
            this.showLocalFiles = showLocalFiles;
            firePropertyChange(PROP_SHOW_LOCAL_FILES, !showLocalFiles ? Boolean.TRUE : Boolean.FALSE, showLocalFiles ? Boolean.TRUE : Boolean.FALSE);
            refreshExistingFolders();
        }
    }

    public String getIgnoredGarbageFiles () {
        return ignoredGarbageFiles;
    }
    
    public synchronized void setIgnoredGarbageFiles (String nue) throws IllegalArgumentException {
        if (! nue.equals (ignoredGarbageFiles)) {
            if (nue.length () > 0) {
                try {
                    ignoredGarbageRE = new RE (nue);
                } catch (RESyntaxException rese) {
                    IllegalArgumentException iae = new IllegalArgumentException ();
                    ErrorManager.getDefault().annotate (iae, rese);
                    throw iae;
                }
            } else {
                ignoredGarbageRE = null;
            }
            ignoredGarbageFiles = nue;
            firePropertyChange (PROP_IGNORED_GARBAGE_FILES, null, nue); // NOI18N
            refreshExistingFolders();
        }
    }
    
    /** Getter for property showMessages.
     * @return Value of property showMessages.
     */
    public boolean isShowMessages() {
        return this.showMessages;
    }
    
    /** Setter for property showMessages.
     * @param showMessages New value of property showMessages.
     */
    public void setShowMessages(boolean showMessages) {
        if (this.showMessages != showMessages) {
            this.showMessages = showMessages;
            firePropertyChange(PROP_SHOW_MESSAGES, !showMessages ? Boolean.TRUE : Boolean.FALSE, showMessages ? Boolean.TRUE : Boolean.FALSE);
            redisplayRevisions();
        }
    }
    
    private void redisplayRevisions() {
        Iterator it = this.revisionListsByName.values().iterator();
        while (it.hasNext()) {
            RevisionList list = (RevisionList)it.next();
            displayRevisions(list);
        }
    }
    
    private void displayRevisions(RevisionList list) {
        Iterator it2 = list.iterator();
        while (it2.hasNext()) {
            RevisionItem item = (RevisionItem)it2.next();
            if (isShowMessages()) {
                String messageString = item.getMessage();
                if (messageString != null) {
                    item.setDisplayName(item.getRevisionVCS() + "  " + cutMessageString(messageString)); //NOI18N
                }
            } else {
                if (item.getMessage() != null) {
                    item.setDisplayName(item.getRevisionVCS());
                }
            }
        }
        
    }
    
    
    private String cutMessageString(String message) {
        String toReturn = message;
        if (message != null && message.length() > (getMessageLength() + 3)) {
            toReturn = message.substring(0, getMessageLength()) + "..."; //NOI18N
        }
        if (toReturn != null) {
            toReturn = toReturn.replace('\n', ' ');
        }
        return toReturn;
    }
        
    /** Getter for property messageLength.
     * @return Value of property messageLength.
     */
    public int getMessageLength() {
        return this.messageLength;
    }
    
    /** Setter for property messageLength.
     * @param messageLength New value of property messageLength.
     */
    public void setMessageLength(int messageLength) {
        int oldLength = this.messageLength;
        this.messageLength = messageLength;
        if (messageLength < 0) {
            this.messageLength = 0;
        }
        firePropertyChange(PROP_MESSAGE_LENGTH, new Integer(oldLength), new Integer(messageLength));
        redisplayRevisions();
        
    }    
    
    /*
    public RevisionChildren createRevisionChildren(RevisionList list) {
        return new NumDotRevisionChildren(list);
    }
     */
    
    public boolean isReadOnly() {
        return false;
    }
    
    private static Object vsActionAccessLock = new Object();

    public SystemAction[] getRevisionActions(FileObject fo, Set revisionItems) {
        VcsRevisionAction action = (VcsRevisionAction) SystemAction.get(VcsRevisionAction.class);
        synchronized (vsActionAccessLock) {
            action.setFileSystem(fileSystem);
            action.setFileObject(fo);
            action.setSelectedRevisionItems(revisionItems);
        }
        return new SystemAction[] { action };
    }

    /*
    public void fireRevisionChange(String name) {
        fireRevisionChange(name, null);
    }
     */
    
    private void vcsStatusChanged (String path, boolean recursively) {
        FileObject fo = findExistingResource(path);
        if (fo == null) return ;
        Enumeration enum = existingFileObjects(fo);
        //D.deb("I have root = "+fo.getName()); // NOI18N
        HashSet hs = new HashSet();
        if (enum.hasMoreElements()) {
            // First add the root FileObject
            hs.add(enum.nextElement());
        }
        while(enum.hasMoreElements()) {
            //fo = (FileObject) enum.nextElement();
            //hs.add(fo);
            FileObject chfo = (FileObject) enum.nextElement();
            if (!recursively && !fo.equals(chfo.getParent())) break;
            hs.add(chfo);
            //D.deb("Added "+fo.getName()+" fileObject to update status"+fo.getName()); // NOI18N
        }
        Set s = Collections.synchronizedSet(hs);
        fireVcsFileStatusChanged(new VcsFileStatusEvent(this, s));
    }

    private void vcsStatusChanged (String name) {
        FileObject fo = findExistingResource(name);
        if (fo == null) return;
        fireVcsFileStatusChanged (new VcsFileStatusEvent(this, Collections.singleton(fo)));
    }
    
    /**
     * is called each time the status of a file changes in cache.
     * The filesystem has to decide wheater it affects him (only in case when
     * there's not the 1-to-1 relationship between cache and fs.
     */
    public void statusChanged(final CacheHandlerEvent event) {
        final String root = fileSystem.getRootDirectory().getAbsolutePath();
        final String absPath = event.getCacheFile().getAbsolutePath();
        if (absPath.startsWith(root)) { // it belongs to this FS -> do something
            //D.deb("-------- it is in this filesystem");
            VcsFileSystem.getStatusChangeRequestProcessor().post(new Runnable() {
                public void run() {
                    String path;
                    if (root.length() == absPath.length()) {
                        path = "";
                    } else {
                        path = absPath.substring(root.length() + 1, absPath.length());
                    }
                    path = path.replace(java.io.File.separatorChar, '/');
                    if (event.getCacheFile() instanceof org.netbeans.modules.vcscore.cache.CacheDir) {
                        vcsStatusChanged(path, event.isRecursive());
                    } else {
                        vcsStatusChanged(path);
                    }
                }
            });
        }
    }
    
    /**
     * is Called when a file/dir is removed from cache.
     */
    public void cacheRemoved(CacheHandlerEvent event) {
    }
    
    /**
     * is called when a file/dir is added to the cache. The filesystem should
     * generally perform findResource() on the dir the added files is in
     * and do refresh of that directory.
     * Note:
     */
    public void cacheAdded(CacheHandlerEvent event) {
    }
    
    /*
    private class VersioningFileStatus extends Object implements VersioningFileSystem.Status {

        public java.lang.String annotateName(java.lang.String displayName, java.util.Set files) {
            return fileSystem.annotateName(displayName, files);
        }
        
        public java.awt.Image annotateIcon(java.awt.Image icon, int iconType, java.util.Set files) {
            return fileSystem.annotateIcon(icon, iconType, files);
        }
        
    }
     */

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException, NotActiveException {
        in.defaultReadObject();
        if (ignoredGarbageFiles == null) {
            ignoredGarbageFiles = "";
        } else if (ignoredGarbageFiles.length () > 0) {
            try {
                ignoredGarbageRE = new RE (ignoredGarbageFiles);
            } catch (RESyntaxException rese) {
                ErrorManager.getDefault ().notify(rese);
            }
        }
    }
    
    private class VersioningList extends Object implements AbstractFileSystem.List {
        
        private static final long serialVersionUID = 107435350712853937L;
        
        public String[] children(String name) {
            String[] vcsFiles = null;
            String[] files = null;
            FileCacheProvider cache = fileSystem.getCacheProvider();
            HashMap removedFilesScheduledForRemove = new HashMap();
            if (cache != null) {// && cache.isDir(name)) {
                cache.readDirFromDiskCache(name);
                vcsFiles = cache.getFilesAndSubdirs(name);
                if (!fileSystem.isShowDeadFiles()) {
                    vcsFiles = fileSystem.filterDeadFilesOut(name, vcsFiles);
                }
            }
            if (vcsFiles == null) files = fileSystem.getLocalFiles(name);
            else files = fileSystem.addLocalFiles(name, vcsFiles, removedFilesScheduledForRemove);
            if (cache != null) {
                VcsCacheDir cacheDir = (VcsCacheDir) cache.getDir(name);
                if (files.length == 0 && (cacheDir == null || (!cacheDir.isLoaded() && !cacheDir.isLocal())) ||
                    (cacheDir == null || (!cacheDir.isLoaded() && !cacheDir.isLocal())) && fileSystem.areOnlyHiddenFiles(files)) cache.readDir(name/*, false*/); // DO refresh when the local directory is empty !
            }
            //FileStatusProvider status = fileSystem.getStatusProvider();
            for (int i = 0; i < files.length; i++) {
                if (fileSystem.isFilterBackupFiles() && files[i].endsWith(fileSystem.getBackupExtension()) ||
                    !isShowUnimportantFiles() &&
                        !fileSystem.isImportant((name.length() == 0) ? files[i] : name + "/" + files[i]) ||
                    //!isShowLocalFiles() && cache != null && status != null &&  -- makes problems, since every file is initially local
                    //    status.getLocalFileStatus().equals(status.getFileStatus((name.length() == 0) ? files[i] : name + "/" + files[i])) ||
                    ignoredGarbageRE != null && ignoredGarbageRE.match (files[i])) {
                
                    files[i] = null;
                }
            }
            return files;
        }

    }


    private class VcsVersioningAttrs extends VersioningAttrs {
        
        public VcsVersioningAttrs() {
            super(VcsVersioningSystem.this.info);
        }
        
        public Object readAttribute(String name, String attrName) {
            Object value = super.readAttribute(name, attrName);
            if (value == null) {
                value = fileSystem.getVcsAttributes().readAttribute(name, attrName);
            }
            return value;
        }
    }
    
    private class VersioningVersions extends Object implements VersioningFileSystem.Versions {
        
        private static final long serialVersionUID = -8842749866809190554L;
        
        public VersioningVersions() {
            fileSystem.addRevisionListener(new RevisionListener() {
                public void stateChanged(javax.swing.event.ChangeEvent ev) {
                    //System.out.println("revision state changed:"+ev);
                    if (!(ev instanceof RevisionEvent)) return ;
                    RevisionEvent event = (RevisionEvent) ev;
                    String name = event.getFilePath();
                    //System.out.println("  name = "+name);
                    //public void revisionsChanged(int whatChanged, FileObject fo, Object info) {
                    RevisionList oldList = (RevisionList) revisionListsByName.get(name);
                    //System.out.println("old List = "+oldList);
                    if (oldList != null) {
                        RevisionList newList = createRevisionList(name);
                        ArrayList workNew = new ArrayList(newList);
                        synchronized (oldList) {
                            ArrayList workOld = new ArrayList(oldList);
                            workNew.removeAll(oldList);
                            //System.out.println("ADDING new revisions: "+workNew);
                            oldList.addAll(workNew); // add all new revisions
                            workOld.removeAll(newList);
                            //System.out.println("ADDING new revisions: "+workNew);
                            oldList.removeAll(workOld); // remove all old revisions (some VCS may perhaps allow removing revisions)
                            FileStatusProvider status = getFileStatusProvider();
                            if (status != null) {
                                String revision = status.getFileRevision(name);
                                if (revision != null) {
                                    for (Iterator it = oldList.iterator(); it.hasNext(); ) {
                                        RevisionItem item = (RevisionItem) it.next();
                                        item.setCurrent(revision.equals(item.getRevision()));
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
        
        public RevisionList getRevisions(String name, boolean refresh) {
            RevisionList list = (RevisionList) revisionListsByName.get(name);//new org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionList();
            if (list == null || refresh) {
                //org.openide.util.RequestProcessor.postRequest(new Runnable() {
                //    public void run() {
                list = createRevisionList(name);
                if (list != null) revisionListsByName.put(name, list);
                        //versioningSystem.fireRevisionChange(name);
                //    }
                //});
                //System.out.println("createRevisionList("+name+") = "+list);
            }
            //list.add(new org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem("1.1"));
            //list.add(new org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem("1.2"));
            return list;
        }
        
        private RevisionList createRevisionList(final String name) {
            //System.out.println("createRevisionList("+name+")");
            CommandSupport cmdSupport = fileSystem.getCommandSupport(VcsCommand.NAME_REVISION_LIST);
            if (cmdSupport == null) return null;
            Command cmd = cmdSupport.createCommand();
            if (cmd == null || !(cmd instanceof RegexOutputCommand)) return null;
            FileObject fo = fileSystem.findFileObject(name);
            if (fo == null) fo = VcsVersioningSystem.this.findResource(name);
            if (fo != null) {
                FileObject[] files = new FileObject[] { fo };
                cmd.setFiles(files);
            } else if (cmd instanceof VcsDescribedCommand) {
                ((VcsDescribedCommand) cmd).setDiskFiles(new java.io.File[] {
                    fileSystem.getFile(name)
                });
            }
            final StringBuffer dataBuffer = new StringBuffer();
            RegexOutputListener dataListener = new RegexOutputListener() {
                public void outputMatchedGroups(String[] data) {
                    if (data != null && data.length > 0) {
                        if (data[0] != null) dataBuffer.append(data[0]);
                    }
                }
            };
            ((RegexOutputCommand) cmd).addRegexOutputListener(dataListener);
            //VcsCommandExecutor[] vces = VcsAction.doCommand(files, cmd, null, fileSystem, null, null, dataListener, null);
            cmd.execute().waitFinished();
            RevisionList list = getEncodedRevisionList(name, dataBuffer.toString());
            if (list != null) displayRevisions(list);
            /*
            if (vces.length > 0) {
                final VcsCommandExecutor vce = vces[0];
                try {
                    fileSystem.getCommandsPool().waitToFinish(vce);
                } catch (InterruptedException iexc) {
                    return null;
                }
                list = getEncodedRevisionList(name, dataBuffer.toString());
                if (list != null) displayRevisions(list);
            }
             */
            return list;//(RevisionList) revisionListsByName.get(name);
        }
        
        private RevisionList getEncodedRevisionList(final String name, String encodedRL) {
            //System.out.println("addEncodedRevisionList("+name+", "+encodedRL.length()+")");
            if (encodedRL.length() == 0) return null;
            RevisionList list = null;
            try {
                list = (RevisionList) VcsUtilities.decodeValue(encodedRL);
            } catch (java.io.IOException ioExc) {
                //ioExc.printStackTrace();
                list = null;
            }
            return list;
            /*
            if (list != null) {
                /*
                addRevisionListener(new RevisionListener() {
                    public void revisionsChanged(int whatChanged, FileObject fo, Object info) {
                        RevisionList newList = createRevisionList(name);
                        RevisionList oldList = (RevisionList) revisionListsByName.get(name);
                        if (oldList != null) {
                            oldList.clear();
                            oldList.addAll(newList);
                        }
                    }
                });
                 *
                revisionListsByName.put(name, list);
            }
             */
            //versioningSystem.fireRevisionChange(name, new RevisionEvent());
        }
        
        public java.io.InputStream inputStream(String name, String revision) throws java.io.FileNotFoundException {
            CommandSupport cmdSupport = fileSystem.getCommandSupport(VcsCommand.NAME_REVISION_OPEN);
            if (cmdSupport == null) return null;
            Command command = cmdSupport.createCommand();
            if (command == null || !(command instanceof VcsDescribedCommand)) return null;
            VcsDescribedCommand cmd = (VcsDescribedCommand) command;
            Hashtable additionalVars = new Hashtable();
            additionalVars.put("REVISION", revision);
            cmd.setAdditionalVariables(additionalVars);
            FileObject[] files = new FileObject[] { fileSystem.findFileObject(name) };
            cmd.setFiles(files);
            final StringBuffer fileBuffer = new StringBuffer();
            TextOutputListener fileListener = new TextOutputListener() {
                public void outputLine(String line) {
                    if (line != null) {
                        fileBuffer.append(line + "\n");
                    }
                }
            };
            cmd.addTextOutputListener(fileListener);
            //VcsCommandExecutor[] vces = VcsAction.doCommand(files, cmd, additionalVars, fileSystem, fileListener, null, null, null);
            CommandTask task = cmd.execute();
            task.waitFinished();
            boolean success = task.getExitStatus() == task.STATUS_SUCCEEDED;
            /*
            for (int i = 0; i < vces.length; i++) {
                try {
                    fileSystem.getCommandsPool().waitToFinish(vces[i]);
                } catch (InterruptedException iexc) {
                    throw (java.io.FileNotFoundException)
                        TopManager.getDefault().getErrorManager().annotate(new java.io.FileNotFoundException(), iexc);
                }
                success = success && (vces[i].getExitStatus() == VcsCommandExecutor.SUCCEEDED);
            }
             */
            if (VcsCommandIO.getBooleanProperty(cmd.getVcsCommand(), VcsCommand.PROPERTY_IGNORE_FAIL)) success = true;
            if (!success) {
                throw (java.io.FileNotFoundException) ErrorManager.getDefault().annotate(
                    new java.io.FileNotFoundException(),
                    NbBundle.getMessage(VcsVersioningSystem.class, "MSG_RevisionOpenCommandFailed", name, revision));
            }
            if (fileBuffer.length() == 0) {
                throw (java.io.FileNotFoundException) ErrorManager.getDefault().annotate(
                    new java.io.FileNotFoundException(),
                    NbBundle.getMessage(VcsVersioningSystem.class, "MSG_FileRevisionIsEmpty", name, revision));
            }
            return new ByteArrayInputStream(fileBuffer.toString().getBytes());
        }
    }

    
    private class FSPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent event) {
            String propName = event.getPropertyName();
            Object oldValue = event.getOldValue();
            Object newValue = event.getNewValue();
            if (VcsFileSystem.PROP_ANNOTATION_PATTERN.equals(propName)) {
                FileObject root = findResource("");
                Set foSet = new HashSet();
                Enumeration enum = existingFileObjects(root);
                while (enum.hasMoreElements()) {
                    foSet.add(enum.nextElement());
                }
                fireFileStatusChanged(new FileStatusEvent(VcsVersioningSystem.this, foSet, false, true));
            }
            if (PROP_SHOW_DEAD_FILES.equals(propName)) {
                FileObject root = findResource("");
                heyDoRefreshFolderRecursive(root);
            }
            if (VcsFileSystem.PROP_ROOT.equals(propName) && (!event.getSource().equals(VcsVersioningSystem.this))) {
//                rootFile = fileSystem.getRootDirectory();
                try {
                    String oldSystName = getSystemName();
                    setSystemName(fileSystem.getSystemName());
                    VcsVersioningSystem.this.firePropertyChange(VcsVersioningSystem.this.PROP_SYSTEM_NAME, oldSystName, getSystemName());
                } catch (java.beans.PropertyVetoException vExc) {
                    ErrorManager.getDefault().notify(org.openide.ErrorManager.WARNING, vExc);
                }
                FileObject fo = refreshRoot();
                VcsVersioningSystem.this.firePropertyChange(VcsVersioningSystem.this.PROP_ROOT, null, fo);
                return;
            }
            if (VcsFileSystem.PROP_DISPLAY_NAME.equals(propName)) {
                if (newValue == null) return ; // Filter out events from myself, since I listen on this also!
                VcsVersioningSystem.this.firePropertyChange(VcsVersioningSystem.this.PROP_DISPLAY_NAME, null, null);
            }
        }

        private void heyDoRefreshFolderRecursive(FileObject fo) {
            Enumeration enum = existingFileObjects(fo);
            while(enum.hasMoreElements()) {
                ((FileObject) enum.nextElement()).refresh();
            }
        }
    }
    
    /*
    private class FileStatusEventAdapter extends FileStatusEvent {
        
        private FileStatusEvent eventOrig;
        
        public FileStatusEventAdapter(FileStatusEvent event) {
            eventOrig = event;
        }
        
        public FileSystem getFileSystem() {
            return VcsVersioningSystem.this;
        }
        
        public boolean hasChanged(FileObject file) {
            FileObject fileOrig = fileSystem.findFileObject(file.getPackageNameExt('/', '.'));
            if (fileOrig == null) return false;
            return eventOrig.hasChanged(fileOrig);
        }
        
        public boolean isNameChange() {
            return eventOrig.isNameChange();
        }
        
        public boolean isIconChange() {
            return eventOrig.isIconChange();
        }
        
    }
     */
    
}
