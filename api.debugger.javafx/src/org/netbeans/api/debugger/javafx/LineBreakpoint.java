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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.api.debugger.javafx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerManager;
import org.openide.ErrorManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.URLMapper;
import org.openide.util.WeakListeners;


/**
 * Notifies about line breakpoint events.
 *
 * <br><br>
 * <b>How to use it:</b>
 * <pre style="background-color: rgb(255, 255, 153);">
 *    DebuggerManager.addBreakpoint (LineBreakpoint.create (
 *        "examples.texteditor.Ted",
 *        27
 *    ));</pre>
 * This breakpoint stops in Ted class on 27 line number.
 *
 * @author Jan Jancura
 */
public class LineBreakpoint extends JavaFXBreakpoint {

    /** Property name constant */
    public static final String          PROP_LINE_NUMBER = "lineNumber"; // NOI18N
    /** Property name constant */
    public static final String          PROP_URL = "url"; // NOI18N
    /** Property name constant. */
    public static final String          PROP_CONDITION = "condition"; // NOI18N
    /** Property name constant. */
    public static final String          PROP_SOURCE_NAME = "sourceName"; // NOI18N
    /** Property name constant. */
    public static final String          PROP_SOURCE_PATH = "sourcePath"; // NOI18N
    /** Property name constant. */
    public static final String          PROP_STRATUM = "stratum"; // NOI18N
    /** Property name constant. */
    public static final String          PROP_PREFERRED_CLASS_NAME = "classNamePreferred"; // NOI18N
    /** Property name constant */
    public static final String          PROP_INSTANCE_FILTERS = "instanceFilters"; // NOI18N
    /** Property name constant */
    public static final String          PROP_THREAD_FILTERS = "threadFilters"; // NOI18N
    
    private String                      url = ""; // NOI18N
    private int                         lineNumber;
    private String                      condition = ""; // NOI18N
    private String                      sourceName = null;
    private String                      sourcePath = null;
    private String                      stratum = "JavaFX"; // NOI18N
    private String                      className = null;
    private Map<JavaFXDebugger,ObjectVariable[]> instanceFilters;
    private Map<JavaFXDebugger,JavaFXThread[]> threadFilters;

    
    private LineBreakpoint (String url) {
        this.url = url;
    }
    
    /**
     * Creates a new breakpoint for given parameters.
     *
     * @param url a string representation of URL of the source file
     * @param lineNumber a line number
     * @return a new breakpoint for given parameters
     */
    public static LineBreakpoint create (
        String url,
        int lineNumber
    ) {
        LineBreakpoint b = new LineBreakpointImpl (url);
        b.setLineNumber (lineNumber);
        return b;
    }

    /**
     * Gets the string representation of URL of the source file,
     * which contains the class to stop on.
     *
     * @return name of class to stop on
     */
    public String getURL () {
        return url;
    }
    
    /**
     * Sets the string representation of URL of the source file,
     * which contains the class to stop on.
     *
     * @param url the URL of class to stop on
     */
    public void setURL (String url) {
        String old;
        synchronized (this) {
            if (url == null) url = "";
            if ( (url == this.url) ||
                 ((url != null) && (this.url != null) && url.equals (this.url))
            ) return;
            old = this.url;
            this.url = url;
        }
        firePropertyChange (PROP_URL, old, url);
    }
    
    /**
     * Gets number of line to stop on.
     *
     * @return line number to stop on
     */
    public int getLineNumber () {
        return lineNumber;
    }
    
    /**
     * Sets number of line to stop on.
     *
     * @param ln a line number to stop on
     */
    public void setLineNumber (int ln) {
        int old;
        synchronized (this) {
            if (ln == lineNumber) return;
            old = lineNumber;
            lineNumber = ln;
        }
        firePropertyChange (
            PROP_LINE_NUMBER,
            new Integer (old),
            new Integer (ln)
        );
    }
    
    /**
     * Get the instance filter for a specific debugger session.
     * @return The instances or <code>null</code> when there is no instance restriction.
     */
    public ObjectVariable[] getInstanceFilters(JavaFXDebugger session) {
        if (instanceFilters != null) {
            return instanceFilters.get(session);
        } else {
            return null;
        }
    }
    
    /**
     * Set the instance filter for a specific debugger session. This restricts
     * the breakpoint to specific instances in that session.
     * @param session the debugger session
     * @param instances the object instances or <code>null</code> to unset the filter.
     */
    public void setInstanceFilters(JavaFXDebugger session, ObjectVariable[] instances) {
        if (instanceFilters == null) {
            instanceFilters = new WeakHashMap<JavaFXDebugger, ObjectVariable[]>();
        }
        if (instances != null) {
            instanceFilters.put(session, instances);
        } else {
            instanceFilters.remove(session);
        }
        firePropertyChange(PROP_INSTANCE_FILTERS, null,
                instances != null ?
                    new Object[] { session, instances } : null);
    }
    
    /**
     * Get the thread filter for a specific debugger session.
     * @return The thread or <code>null</code> when there is no thread restriction.
     */
    public JavaFXThread[] getThreadFilters(JavaFXDebugger session) {
        if (threadFilters != null) {
            return threadFilters.get(session);
        } else {
            return null;
        }
    }
    
    /**
     * Set the thread filter for a specific debugger session. This restricts
     * the breakpoint to specific threads in that session.
     * @param session the debugger session
     * @param threads the threads or <code>null</code> to unset the filter.
     */
    public void setThreadFilters(JavaFXDebugger session, JavaFXThread[] threads) {
        if (threadFilters == null) {
            threadFilters = new WeakHashMap<JavaFXDebugger, JavaFXThread[]>();
        }
        if (threads != null) {
            threadFilters.put(session, threads);
        } else {
            threadFilters.remove(session);
        }
        firePropertyChange(PROP_THREAD_FILTERS, null,
                threads != null ?
                    new Object[] { session, threads } : null);
    }

    /**
     * Returns condition.
     *
     * @return cond a condition
     */
    public String getCondition () {
        return condition;
    }
    
    /**
     * Sets condition.
     *
     * @param c a new condition
     */
    public void setCondition (String c) {
        String old;
        synchronized (this) {
            if (c == null) c = "";
            c = c.trim ();
            if ( (c == condition) ||
                 ((c != null) && (condition != null) && condition.equals (c))
            ) return;
            old = condition;
            condition = c;
        }
        firePropertyChange (PROP_CONDITION, old, c);
    }
    
    /**
     * Returns stratum.
     *
     * @return a stratum
     */
    public String getStratum () {
        return stratum;
    }
    
    /**
     * Sets stratum.
     *
     * @param s a new stratum
     */
    public void setStratum (String s) {
        String old;
        synchronized (this) {
            if (s == null) s = "";
            s = s.trim ();
            if ( (s == stratum) ||
                 ((s != null) && (stratum != null) && stratum.equals (s))
            ) return;
            old = stratum;
            stratum = s;
        }
        firePropertyChange (PROP_CONDITION, old, s);
    }
    
    /**
     * Returns the name of the source file.
     *
     * @return a source name or <code>null</code> when no source name is defined.
     */
    public String getSourceName () {
        return sourceName;
    }
    
    /**
     * Sets the name of the source file.
     *
     * @param sn a new source name or <code>null</code>.
     */
    public void setSourceName (String sn) {
        String old;
        synchronized (this) {
            if (sn != null) sn = sn.trim ();
            if ( (sn == sourceName) ||
                 ((sn != null) && (sourceName != null) && sourceName.equals (sn))
            ) return;
            old = sourceName;
            sourceName = sn;
        }
        firePropertyChange (PROP_SOURCE_NAME, old, sn);
    }

    /**
     * Returns source path, relative to the source root.
     *
     * @return a source path or <code>null</code> when no source path is defined.
     *
     * @since 1.3
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * Sets source path, relative to the source root.
     *
     * @param sp a new source path or <code>null</code>
     *
     * @since 1.3
     */
    public void setSourcePath (String sp) {
        String old;
        synchronized (this) {
            if (sp != null) sp = sp.trim();
            if (sp == sourcePath || (sp != null && sp.equals(sourcePath))) {
                return ;
            }
            old = sourcePath;
            sourcePath = sp;
        }
        firePropertyChange (PROP_SOURCE_PATH, old, sp);
    }
    
    /**
     * Sets the binary class name that is used to submit the breakpoint.
     * @param className The binary class name, or <code>null</code> if the class
     * name should be retrieved automatically from the URL and line number.
     * @since 2.8
     */
    public void setPreferredClassName(String className) {
        String old;
        synchronized (this) {
            if (this.className == className || (className != null && className.equals(this.className))) {
                return ;
            }
            old = className;
            this.className = className;
        }
        firePropertyChange (PROP_PREFERRED_CLASS_NAME, old, className);
    }
    
    /**
     * Gets the binary class name that is used to submit the breakpoint.
     * @return The binary class name, if previously set by {@link setPreferedClassName}
     * method, or <code>null</code> if the class name should be retrieved
     * automatically from the URL and line number.
     * @since 2.8
     */
    public String getPreferredClassName() {
        return className;
    }
    
    /**
     * Returns a string representation of this object.
     *
     * @return  a string representation of the object
     */
    public String toString () {
        String fileName = null;
        try {
            FileObject fo = URLMapper.findFileObject(new URL(url));
            if (fo != null) {
                fileName = fo.getNameExt();
            }
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        if (fileName == null) fileName = url;
        return "LineBreakpoint " + fileName + " : " + lineNumber;
    }
    
    private static class LineBreakpointImpl extends LineBreakpoint 
                                            implements Comparable, FileChangeListener, ChangeListener {
        
       // We need to hold our FileObject so that it's not GC'ed, because we'd loose our listener.
       private FileObject fo;
       
       public LineBreakpointImpl(String url) {
            super(url);
            try {
                fo = URLMapper.findFileObject(new URL(url));
                if (fo != null) {
                    fo.addFileChangeListener(WeakListeners.create(FileChangeListener.class, this, fo));
                }
            } catch (MalformedURLException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        
        public int compareTo(Object o) {
            if (o instanceof LineBreakpointImpl) {
                LineBreakpoint lbthis = this;
                LineBreakpoint lb = (LineBreakpoint) o;
                int uc = lbthis.url.compareTo(lb.url);
                if (uc != 0) {
                    return uc;
                } else {
                    return lbthis.lineNumber - lb.lineNumber;
                }
            } else {
                return -1;
            }
        }

        public void fileFolderCreated(FileEvent fe) {
        }

        public void fileDataCreated(FileEvent fe) {
        }

        public void fileChanged(FileEvent fe) {
        }

        public void fileDeleted(FileEvent fe) {
            DebuggerManager.getDebuggerManager().removeBreakpoint(this);
            fo = null;
        }

        public void fileRenamed(FileRenameEvent fe) {
            try {
                this.setURL(((FileObject) fe.getSource()).getURL().toString());
            } catch (FileStateInvalidException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }

        public void fileAttributeChanged(FileAttributeEvent fe) {
        }
    
        public void stateChanged(ChangeEvent chev) {
            Object source = chev.getSource();
            if (source instanceof Breakpoint.VALIDITY) {
                setValidity((Breakpoint.VALIDITY) source, chev.toString());
            } else {
                throw new UnsupportedOperationException(chev.toString());
            }
        }

    }
}