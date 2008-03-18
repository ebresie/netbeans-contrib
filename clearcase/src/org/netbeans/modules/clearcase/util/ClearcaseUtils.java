/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.clearcase.util;

import org.netbeans.modules.clearcase.*;
import org.netbeans.modules.versioning.spi.VCSContext;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import org.netbeans.modules.clearcase.client.AfterCommandRefreshListener;
import org.netbeans.modules.clearcase.client.CheckoutCommand;
import org.netbeans.modules.clearcase.client.ClearcaseClient;
import org.netbeans.modules.clearcase.client.OutputWindowNotificationListener;
import org.netbeans.modules.clearcase.client.status.FileEntry;
import org.netbeans.modules.clearcase.client.status.ListStatus;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Clearase specific utility methods.
 * 
 * @author Maros Sandor
 */
public class ClearcaseUtils {

    private ClearcaseUtils() {
    }

    /**
     * Determines whether the supplied context contains something managed by Clearcase.s
     * 
     * @param context context to examine
     * @return true if the context contains some files that are managed by Clearcase, false otherwise
     */
    public static boolean containsVersionedFiles(VCSContext context) {
        FileStatusCache cache = Clearcase.getInstance().getFileStatusCache();
        Set<File> roots = context.getRootFiles();
        for (File file : roots) {
            if ((cache.getInfo(file).getStatus() & FileInformation.STATUS_VERSIONED) != 0 ) {
                return true;
            }                
        }
        return false;
    }
    
    /**
     * Returns path of the file in VOB.
     * 
     * @param file a versioned file 
     * @return path of the file in VOB or null if the file is not under clearcase control
     */
    public static String getLocation(File file) {
        File parent = Clearcase.getInstance().getTopmostManagedParent(file);
        if (parent != null) {
            // TODO what is vob root?
            return file.getAbsolutePath().substring(parent.getAbsolutePath().length());
        } else {
            return null;
        }
    }
    
    public static String getExtendedName(File file, String revision) {
        return file.getAbsolutePath() + Clearcase.getInstance().getExtendedNamingSymbol() + revision;
    }
    
    /**
     * Scans given file set recursively and puts all files and directories found in the result array. 
     * 
     * @return File[] all files and folders found in the 
     */
    public static File[] expandRecursively(VCSContext ctx, FileFilter filter) {
        Set<File> fileSet = ctx.computeFiles(filter);
        Set<File> expandedFileSet = new HashSet<File>(fileSet.size() * 2);
        for (File file : fileSet) {
            addFilesRecursively(file, expandedFileSet);
        }
        return (File[]) expandedFileSet.toArray(new File[expandedFileSet.size()]);
    }

    private static void addFilesRecursively(File file, Set<File> fileSet) {
        fileSet.add(file);
        File [] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                addFilesRecursively(child, fileSet);
            }
        }
    }

    public static String getMimeType(File file) {
        // TODO: implement
        return "text/plain";
    }

    /**
     * @return true if the buffer is almost certainly binary.
     * Note: Non-ASCII based encoding encoded text is binary,
     * newlines cannot be reliably detected.
     */
    public static boolean isBinary(byte[] buffer) {
        for (int i = 0; i<buffer.length; i++) {
            int ch = buffer[i];
            if (ch < 32 && ch != '\t' && ch != '\n' && ch != '\r') {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Compares two {@link FileInformation} objects by importance of statuses they represent.
     */
    public static class ByImportanceComparator implements Comparator<FileInformation> {
        public int compare(FileInformation i1, FileInformation i2) {
            return getComparableStatus(i1.getStatus()) - getComparableStatus(i2.getStatus());
        }
    }
    
    /**
     * Gets integer status that can be used in comparators. The more important the status is for the user,
     * the lower value it has. Conflict is 0, unknown status is 100.
     *
     * @return status constant suitable for 'by importance' comparators
     */
    public static int getComparableStatus(int status) {
        return 0;
        // XXX
//        if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY)) {
//            return 12;
//        } else if (0 != (status & FileInformation.STATUS_VERSIONED_CHECKEDOUT)) {
//            return 16;
//        } else if (0 != (status & FileInformation.STATUS_VERSIONED_HIJACKED)) {
//            return 22;    
//        } else if (0 != (status & FileInformation.STATUS_VERSIONED_MODIFIEDINREPOSITORY)) {
//            return 32;
//        } else if (0 != (status & FileInformation.STATUS_VERSIONED_UPTODATE)) {
//            return 50;
//        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_EXCLUDED)) {
//            return 100;
//        } else if (0 != (status & FileInformation.STATUS_NOTVERSIONED_NOTMANAGED)) {
//            return 101;
//        } else if (status == FileInformation.STATUS_UNKNOWN) {
//            return 102;
//        } else {
//            throw new IllegalArgumentException("Uncomparable status: " + status); // NOI18N
//        }
    }
    
    /**
     * Returns the {@link FileEntry} for the given file or 
     * null if the file does not exist.
     * 
     * This method synchronously accesses disk and may block for a longer period of time.
     * 
     * @param file the file to get the {@link FileEntry} for
     * @return the {@link FileEntry}
     */
    public static FileEntry readEntry(ClearcaseClient client, File file) {
        List<FileEntry> entries = readEntries(client, file, true);
        if(entries == null || entries.size() == 0) {
            return null;
        }
        return entries.get(0);
    }
    
    /**
     * Returns FileEntries for the given file.
     * 
     * This method synchronously accesses disk and may block for a longer period of time.
     * 
     * @param file the file
     * @return {@link FileEntry}-s describing the files actuall status
     * @see {@link FileEntry}
     */   
    public static List<FileEntry> readEntries(ClearcaseClient client, File file, boolean directory) {
        if(file == null) {
            return null;
        }       
        // 1. list files ...
        ListStatus ls = new ListStatus(file, directory);    
        client.exec(ls, false);

        return new ArrayList<FileEntry>(ls.getOutput());
    }

    /**
     * The file given in {@link #ensureMutable(org.netbeans.modules.clearcase.client.ClearcaseClient, java.io.File)} or
     * {@link #ensureMutable(org.netbeans.modules.clearcase.client.ClearcaseClient, java.io.File, org.netbeans.modules.clearcase.client.status.FileEntry)}     
     * is mutable.
     */
    public static int IS_MUTABLE        = 1;
    
    /**
     * The file given in {@link #ensureMutable(org.netbeans.modules.clearcase.client.ClearcaseClient, java.io.File)} or
     * {@link #ensureMutable(org.netbeans.modules.clearcase.client.ClearcaseClient, java.io.File, org.netbeans.modules.clearcase.client.status.FileEntry)}
     * is mutable and was checkedout by the method.
     */
    public static int WAS_CHECKEDOUT    = 2;
    
    /**
     * Checks out the file or directory depending on the user-selected strategy in Options.
     * In case the file is already writable or the directory is checked out, the method does nothing.
     * Interceptor entry point.
     * 
     * @param client ClearcaseClient
     * @param file file to checkout
     * @return <ul> 
     *            <li>0 isn't mutable
     *            <li>{@link #IS_MUTABLE} is mutable  
     *            <li>{@link #WAS_CHECKEDOUT} is mutable and was checkout by the method
     *          </ul> 
     * @see org.netbeans.modules.clearcase.ClearcaseModuleConfig#getOnDemandCheckout()
     */
    public static int ensureMutable(ClearcaseClient client, File file) {
        return ensureMutable(client, file, null);
    }   
        
    /**
     * Checks out the file or directory depending on the user-selected strategy in Options.
     * In case the file is already writable or the directory is checked out, the method does nothing.
     * Interceptor entry point.
     * 
     * @param client ClearcaseClient
     * @param file file to checkout
     * @param entry the given files {@link FileEntry}
     * @return <ul> 
     *            <li>0 isn't mutable
     *            <li>{@link #IS_MUTABLE} is mutable  
     *            <li>{@link #WAS_CHECKEDOUT} is mutable and was checkout by the method
     *          </ul> 
     * @see org.netbeans.modules.clearcase.ClearcaseModuleConfig#getOnDemandCheckout()
     */
    public static int ensureMutable(ClearcaseClient client, File file, FileEntry entry) {
        if (file.isDirectory()) {
            if(entry == null) {
                entry = ClearcaseUtils.readEntry(client, file);                
            }
            if (entry == null || entry.isCheckedout() || entry.isViewPrivate()) {
                return IS_MUTABLE;
            }
        } else {
            if (file.canWrite()) return IS_MUTABLE;
        }

        ClearcaseModuleConfig.OnDemandCheckout odc = ClearcaseModuleConfig.getOnDemandCheckout();

        CheckoutCommand command;
        switch (odc) {
        case Disabled:
            // XXX let the user decide if he want's to checkout the file
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message(NbBundle.getMessage(ClearcaseInterceptor.class, "Interceptor_NoOnDemandCheckouts_Warning"))); //NOI18N
            return 0;
        case Reserved:
        case ReservedWithFallback:
            command = new CheckoutCommand(new File [] { file }, null, CheckoutCommand.Reserved.Reserved, true, new AfterCommandRefreshListener(file));
            break;
        case Unreserved:
            command = new CheckoutCommand(new File [] { file }, null, CheckoutCommand.Reserved.Unreserved, true, new AfterCommandRefreshListener(file));
            break;
        default:
            throw new IllegalStateException("Illegal Checkout type: " + odc);
        }
        
        Clearcase.getInstance().getClient().exec(command, odc != ClearcaseModuleConfig.OnDemandCheckout.ReservedWithFallback);
        if(!command.hasFailed()) {
            return WAS_CHECKEDOUT;
        } else if(odc == ClearcaseModuleConfig.OnDemandCheckout.ReservedWithFallback) {
            command = new CheckoutCommand(new File [] { file }, null, CheckoutCommand.Reserved.Unreserved, true, 
                                          new OutputWindowNotificationListener(), new AfterCommandRefreshListener(file));
            Clearcase.getInstance().getClient().exec(command, true);
            if (command.hasFailed()) {    
                return 0;
            } else {
                return WAS_CHECKEDOUT;
            }   
        } else {
            return 0;
        }
    }        
}
