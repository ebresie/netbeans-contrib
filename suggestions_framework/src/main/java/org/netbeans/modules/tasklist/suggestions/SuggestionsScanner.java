/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */


package org.netbeans.modules.tasklist.suggestions;

import org.openide.loaders.DataObject;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.Lookup;
import org.openide.util.Cancellable;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.WindowManager;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.text.CloneableEditorSupport;
import org.openide.nodes.Node;
import org.openide.ErrorManager;
import org.netbeans.modules.tasklist.providers.SuggestionContext;
import org.netbeans.modules.tasklist.providers.SuggestionProvider;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.apihole.tasklist.SPIHole;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.api.queries.VisibilityQuery;

import javax.swing.*;
import java.util.*;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.lang.reflect.InvocationTargetException;

/**
 * Scans for suggestions by delegating to
 * plugged-in providers.
 *
 * @todo Should I use FileObjects instead of DataObjects when passing
 *       file identity around? It seems weird that I don't allow
 *       scanning on secondary files (although it seems right in the
 *       cases I can think of - we don't want to scan .class files,
 *       .o files, .form files, ...). Pros: DataObject layer is a classification
 *       layer that defines EditorCookies etc. Cons: Too many dependencies
 *       on possibly slow and leaking code.
 *
 * @author Petr Kuzel
 */
public final class SuggestionsScanner implements Cancellable {

    /** Optional progress monitor */
    private ScanProgress progressMonitor;

    /**
     * Contains already scanned dataobjects.
     * It'd be very memory intensive so only preferred
     * are stored.
     * Other duplicities (unlikely) can cause
     * suggestion duplicities.
     */
    private final Set scanned = new HashSet();

    /** Target suggestion list. */
    private SuggestionList list;

    private ProviderAcceptor typeFilter;

    // target manager impl
    private final SuggestionManagerImpl manager;

    private final SuggestionProviders registry;

    // keep default instance (only if a client exists)
    private static Reference instance;

    private volatile boolean interrupted;
    private int suggestionsCounter;
    private int usabilityLimit = 503;
    private boolean workaround38476;

    // list that replaces direct manager regiltration
    private List cummulateInList;

    private SuggestionsScanner() {
        manager = (SuggestionManagerImpl) Lookup.getDefault().lookup(SuggestionManager.class);
        registry = SuggestionProviders.getDefault();
    }

    public static SuggestionsScanner getDefault() {
        if (instance == null) {
            return createDefault();
        }
        SuggestionsScanner scanner = (SuggestionsScanner) instance.get();
        if (scanner == null) {
            return createDefault();
        } else {
            return scanner;
        }
    }

    private static SuggestionsScanner createDefault() {
        SuggestionsScanner scanner = new SuggestionsScanner();
        instance = new WeakReference(scanner);
        return scanner;
    }

    /**
     * Scans recursively for suggestions notifing given progress monitor.
     * @param folders containers to be scanned. It must be DataObject subclasses!
     * @param list
     * @param monitor
     */
    public final synchronized void scan(DataObject.Container[] folders, SuggestionList list, ScanProgress monitor) {
        scan(folders, list, monitor, ProviderAcceptor.ALL);
    }

    /**
     * Scans recursively for suggestions notifing given progress monitor.
     * @param folders containers to be scanned. It must be DataObject subclasses!
     * @param list
     * @param monitor
     * @param filter suggestion provider filter
     */
    public final synchronized void scan(DataObject.Container[] folders, SuggestionList list, ScanProgress monitor, ProviderAcceptor filter) {
        try {
            typeFilter = filter;
            progressMonitor = monitor;
            scan(folders, list, true);
        } finally {
            typeFilter = null;
            progressMonitor = null;
            if (monitor != null) monitor.scanFinished();
        }
    }

    /**
     * Iterate over the folder recursively (optional) and scan all files.
     * We skip CVS and SCCS folders intentionally. Would be nice if
     * the filesystem hid these things from us.
     *
     * @param folders containers to be scanned. It must be DataObject subclasses!
     * @param list target suggestions list
     * @param recursive use descent policy
     */
    private final synchronized void scan(DataObject.Container[] folders, SuggestionList list,
                           boolean recursive) {

        lowMemoryWarning = false;
        lowMemoryWarningCount = 0;
        interrupted = false;
        assureMemory(REQUIRED_PER_ITERATION, true);   // guard low memory condition
        suggestionsCounter = 0;


        try {
            this.list = list;

            // scan opened files first these are most specifics
            // it should also improve perceived performance
            workaround38476 = true;
            scanPreferred(folders, recursive);
            workaround38476 = false;

            if (progressMonitor != null) {
                int estimate = -1;
                progressMonitor.estimate(estimate);
                for (int i = 0; i < folders.length; i++) {
                    // it's faster to check at FS level however we can miss some links (.shadow)
                    FileObject fo = ((DataObject)folders[i]).getPrimaryFile();
                    estimate += countFolders(fo);
                }
                progressMonitor.estimate(estimate);
                progressMonitor.scanStarted();
            }

            for (int i = 0; i < folders.length; i++) {
                if (shouldStop()) return;
                DataObject.Container folder = folders[i];
                scanFolder(folder, recursive);
            }
        } finally {
            scanned.clear();
        }
    }


    /**
     * Return all opened top components in editor mode.
     * @return never null
     */
    static TopComponent[] openedTopComponents() {
        final Object[] wsResult = new Object[1];
        try {

            if (SwingUtilities.isEventDispatchThread()) {
                Mode editorMode = WindowManager.getDefault().findMode(CloneableEditorSupport.EDITOR_MODE);
                if (editorMode == null) {
                    return new TopComponent[0];
                } else {
                    return editorMode.getTopComponents();
                }
            } else {
                // I just hope that we are not called from non-AWT thread
                // still holding AWTTreeLock otherwise deadlock
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        Mode editorMode = WindowManager.getDefault().findMode(CloneableEditorSupport.EDITOR_MODE);
                        if (editorMode == null) {
                            wsResult[0] = new TopComponent[0];
                        } else {
                            wsResult[0] = editorMode.getTopComponents();
                        }
                    }
                });
                return (TopComponent[]) wsResult[0];
            }
        } catch (InterruptedException e) {
            return new TopComponent[0];
        } catch (InvocationTargetException e) {
            return new TopComponent[0];
        }
    }

    /**
     * Determines if given dataobject lies in scan context
     * and scans it.
     *
     * @param folders scan context
     * @param recursive iff true scan context is scanned recusively
     */
    private void scanPreferred(DataObject.Container[] folders, boolean recursive) {

        TopComponent[] views = openedTopComponents();
        DataObject[] roots = null;
        for (int i = 0; i<views.length; i++) {
            Node[] nodes = views[i].getActivatedNodes();
            if (nodes == null) continue;  // XXX issue #38383
            for (int n = 0; n<nodes.length; n++) {
                DataObject dobj = (DataObject) nodes[n].getCookie(DataObject.class);
                if (dobj != null) {
                    if (roots == null) {
                        Set allRoots = new HashSet();
                        for (int r = 0; r<folders.length; r++) {
                            DataObject[] droots = folders[r].getChildren();
                            for (int d = 0; d<droots.length; d++) {
                                allRoots.add(droots[d]);
                            }
                        }
                        roots = (DataObject[]) allRoots.toArray(new DataObject[allRoots.size()]);
                    }
                    scanPreferred(dobj, roots, recursive);
                    break;  // one DataObject per TC is enough :-)
                }
            }
        }
    }

    private void scanPreferred(DataObject dobj, DataObject[] roots, boolean recursive) {
        FileObject fo = dobj.getPrimaryFile();
        for (int i=0; i<roots.length; i++) {
            FileObject root = roots[i].getPrimaryFile();
            if (root.equals(fo) || (recursive ? FileUtil.isParentOf(root,fo) : fo.getParent().equals(root))) {
                scanLeaf(dobj);
                scanned.add(dobj);
                break; // certainly it could be under more roots
                       // but it would create duplicates and slow down the test
            }
        }
    }

    private void scanFolder(DataObject.Container folder, boolean recursive) {
        DataObject[] children = folder.getChildren();
        for (int i = 0; i < children.length; i++) {

            if (shouldStop()) return;

            DataObject f = children[i];
            if (f instanceof DataObject.Container) {
                if (!recursive) {
                    continue;
                }

                //XXX Skip CVS and SCCS folders
                String name = f.getPrimaryFile().getNameExt();
                if ("CVS".equals(name) || "SCCS".equals(name) || ".svn".equals(name)) { // NOI18N
                    continue;
                }

                if (progressMonitor == null) {
                    // XXX stil strange that possibly backgournd process writes directly to UI
                    StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(SuggestionsScanner.class,
                                    "ScanningFolder", // NOI18N
                                    f.getPrimaryFile().getNameExt()));
                } else {
                    progressMonitor.folderEntered(f.getPrimaryFile());
                }

                scanFolder((DataObject.Container) f, true); // recurse!
                if (progressMonitor != null) {
                    progressMonitor.folderScanned(f.getPrimaryFile());
                }

            } else {
                scanLeaf(f);
            }
        }
    }


    /**
     * Scan content of selected top component and return results
     * as list instead of direct registering with manager.
     *
     * @return list (possibly empty)
     */
    final synchronized List scanTopComponent(TopComponent topComponent, ProviderAcceptor acceptor) {
        List ret = Collections.EMPTY_LIST;
        try {

            // init

            cummulateInList = new LinkedList();
            progressMonitor = null;
            scanned.clear();
            workaround38476 = topComponent.isOpened();
            suggestionsCounter = 0;
            interrupted = false;
            typeFilter = acceptor;

            // perform

            Node[] nodes = topComponent.getActivatedNodes();
            if (nodes == null) return ret;
            for (int n = 0; n<nodes.length; n++) {
                DataObject dobj = (DataObject) nodes[n].getCookie(DataObject.class);
                if (dobj == null) return ret;
                scanLeaf(dobj);
                break;  // one node is enough
            }
            ret = cummulateInList;
        } finally {
            cummulateInList = null;
            typeFilter = null;
        }
        return ret;
    }

    /**
     * Scans given data object. Converts it to scanning context.
     */
    private void scanLeaf(DataObject dobj) {
        // Get document, and I do mean now!

        if (!dobj.isValid()) return;

        if (scanned.contains(dobj)) return;

        final EditorCookie edit =
                (EditorCookie) dobj.getCookie(EditorCookie.class);
        if (edit == null) return;

        FileObject fo = dobj.getPrimaryFile();
        if (VisibilityQuery.getDefault().isVisible(fo) == false) return; // ignore backups etc
        String extension = fo.getExt();
        boolean directAccess = "java".equals(extension) || "properties".equals(extension);  // #38476
        final boolean isPrimed = edit.getDocument() == null && directAccess == false;

        SuggestionContext env = SPIHole.createSuggestionContext(dobj);

        scanLeaf(env);

        if (false) {
            try {
                Thread.sleep(1000);  // simulate long document processing
                                     // to see what timeout based tasks are triggered
                                     // (e.g. background java.parser.ParsingSupport.parse)
            } catch (InterruptedException e) {
                // ignore
            }
        }


        // XXX default editor cookie implementation (CloneableEditorSupport)
        // does not release documents on unless one explicitly
        // call close() that as side effect closes all components.
        // So call close() is we are likely only document users
        Runnable r = new Runnable() {
                public void run() {
                    if (isPrimed && edit.getOpenedPanes() == null && workaround38476 == false) {
                        edit.close();
                    }
                }
            };
        if( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait( r );
            } catch( InvocationTargetException itE ) {
                //ignore
            } catch( InterruptedException iE ) {
                //ignore
            }
        }

        if (progressMonitor != null) {
            progressMonitor.fileScanned(dobj.getPrimaryFile());
        }

    }

    private void scanLeaf(SuggestionContext env) {
        List providers = registry.getProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            if (interrupted) return;
            interrupted = Thread.interrupted();
            SuggestionProvider provider = (SuggestionProvider) it.next();

            assert typeFilter != null;
            if (typeFilter.accept(provider) == false) continue;

            // FIXME no initialization events possibly fired
            // I guess that reponsibility for recovering from missing
            // lifecycle events should be moved to providers
            if (provider instanceof DocumentSuggestionProvider) {
                List l = null;
                String type = null;
                try {
                    type = provider.getType();
                    l = ((DocumentSuggestionProvider) provider).scan(env);
                } catch (RuntimeException e) {
                    ErrorManager.getDefault().annotate(e, "Skipping faulty provider (" + provider + ").");  // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                } catch (ThreadDeath e) {
                    throw e;
                } catch (Error e) {
                    ErrorManager.getDefault().annotate(e, "Skipping faulty provider (" + provider + ").");  // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
                if (l != null && l.size() > 0) {
                    // XXX ensure that scan returns a homogeneous list of tasks
                    suggestionsCounter += l.size();
                    if (cummulateInList == null) {
                        manager.register(type, l, null, list, true);
                    } else {
                        cummulateInList.addAll(l);
                    }
                }
            }
        }
    }

    // low memory condition detection logic ~~~~~~~~

    private static boolean lowMemoryWarning = false;
    private static int lowMemoryWarningCount = 0;
    private static int MB = 1024 * 1024;
    private static int REQUIRED_PER_ITERATION = 2 * MB;
    private static int REQUIRED_PER_FULL_GC = 7 * MB;

    /**
     * throws RuntimeException if low memory condition happens
     * @param estimate etimated memory requirements before next check
     * @param tryGC on true use potentionally very slow test that is more accurate (cooperates with GC)
     */
    private void assureMemory(int estimate, boolean tryGC) {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory();
        long max = rt.maxMemory();  // XXX on some 1.4.1 returns heap&native instead of -Xmx
        long required = Math.max(total/13, estimate + REQUIRED_PER_FULL_GC);
        if (total ==  max && rt.freeMemory() < required) {
            // System.err.println("MEM " + max + " " +  total + " " + rt.freeMemory());
            if (tryGC) {
                try {
                    byte[] gcProvocation = new byte[(int)required];
                    gcProvocation[0] = 75;
                    gcProvocation = null;
                    return;
                } catch (OutOfMemoryError e) {
                    handleNoMemory();
                }
            } else {
                lowMemoryWarning = true;
            }
        } else if (lowMemoryWarning) {
            lowMemoryWarning = false;
            lowMemoryWarningCount ++;
        }
        // gc is getting into corner
        if (lowMemoryWarningCount > 7 || (total == max && rt.freeMemory() < REQUIRED_PER_FULL_GC)) {
            handleNoMemory();
        }

    }

    private void handleNoMemory() {
        interrupted = true;
        if (progressMonitor != null) {
            progressMonitor.scanTerminated(-1);
        }
    }

    /** Test stop condition (thread interrupted or low memory) */
    private boolean shouldStop() {
        if (interrupted) return true;
        interrupted = Thread.interrupted();
        if (interrupted) return true;
        assureMemory(REQUIRED_PER_ITERATION, false);
        if (interrupted) return true;
        if (suggestionsCounter > getCountLimit()) {
            interrupted = true;
            if (progressMonitor != null) {
                progressMonitor.scanTerminated(-3);
            }
        }
        return interrupted;

    }

//  *** Low memory condition listener
//    // heuristically detect overload
//    private static Reference memoryReference;
//
//    // Allocate some extra memory and keep soft reference to it
//    // once it gets collected JVM tries to eliminate unnecesary
//    // memory alocation from system resources
//    private class MemoryReference extends SoftReference implements Runnable {
//
//        MemoryReference(Object ref) {
//            super(ref, Utilities.activeReferenceQueue());
//        }
//
//        // gets called by Utilities.activeReferenceQueue
//        public void run() {
//            memoryReleased();
//        }
//    }
//
//    private void memoryReleased() {
//        long total = Runtime.getRuntime().totalMemory();
//        long free = Runtime.getRuntime().freeMemory();
//        allocateMemory();
//        if (total == Runtime.getRuntime().totalMemory()) {
//            // no new system memory allocated
//            // there were many soft references or we are getting to maxMemory limit
//            if (!interrupted) {
//                // XXX on most implementations it's maxMemory limit
//                if (Runtime.getRuntime().freeMemory() < 10000 ) {
//                    interrupted = true;
//                    if (progressMonitor != null) {
//                        progressMonitor.scanTerminated(-1);
//                    }
//                }
//            }
//        }
//    }
//
//    private void allocateMemory() {
//        if (interrupted) return;
//        if (memoryReference != null && memoryReference.get() != null) return;
//        try {
//            byte[] memory = new byte[3*1024*1024];
//            memoryReference = new MemoryReference(memory);
//        } catch (OutOfMemoryError err) {
//            interrupted = true;
//        }
//    }
//  *** Low memory condition listener

    /** Stop scannig after discovering limit suggestions */
    private int getCountLimit() {
        return usabilityLimit;
    }

    private static int countFolders(FileObject projectFolder) {
        int count = 0;
        if (Thread.currentThread().isInterrupted()) return count;
        Enumeration en = projectFolder.getFolders(false);
        while (en.hasMoreElements()) {
            FileObject next = (FileObject) en.nextElement();
            String name = next.getNameExt();
            if ("CVS".equals(name) || "SCCS".equals(name) || ".svn".equals(name)) { // NOI18N
                continue;
            }
            count++;
            count += countFolders(next);  // recursion
        }
        return count;
    }

    public boolean cancel() {
        interrupted = true;
        if (progressMonitor != null) {
            progressMonitor.scanTerminated(-2);
        }
        return true;
    }

    /** Set treshold meaning to stop the scanner. */
    public void setUsabilityLimit(int usabilityLimit) {
        this.usabilityLimit = usabilityLimit;
    }

    /**
     * Handles scan method emmited progress callbacks.
     * Implementation can interrupt scanning thread
     * (current thread) by standard thread interruption
     * methods.
     */
    public interface ScanProgress {
        /**
         * Predics how many folders will be scanned.
         * @param estimatedFolders estimate (-1 for not yet know).
         */
        void estimate(int estimatedFolders);

        void scanStarted();

        void folderEntered(FileObject folder);

        void fileScanned(FileObject file);

        void folderScanned(FileObject folder);

        void scanFinished();

        /**
         * Scan was terminated unfinished
         * @param reason -1 out of memory, -2 user interrupt, -3 count limit
         */
        void scanTerminated(int reason);
    }


}
