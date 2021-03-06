/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dew4nb.services.debugger;

import com.sun.jdi.AbsentInformationException;
import java.beans.Customizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.DebuggerStartException;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAThread;
import org.netbeans.modules.debugger.jpda.JPDADebuggerImpl;
import org.netbeans.modules.dew4nb.endpoint.EndPoint;
import org.netbeans.modules.dew4nb.endpoint.Status;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.netbeans.spi.debugger.jpda.SourcePathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 *
 * @author Tomas Zezula
 */
final class ActiveSessions {

     private static final Logger LOG = Logger.getLogger(ActiveSessions.class.getName());

    //@GuardedBy("ActiveSessions.class")
    private static ActiveSessions instance;
    private static final ThreadLocal<Boolean> inEval = new ThreadLocal<Boolean>() {
         @Override
         protected Boolean initialValue() {
             return Boolean.FALSE;
         }
    };

    private final ConcurrentMap<Integer,Data> active;
    private final AtomicInteger sequencer;

    private ActiveSessions() {
        this.active = new ConcurrentHashMap<>();
        this.sequencer = new AtomicInteger();
    }


    int createSession(
            @NonNull final WorkspaceResolver.Context context,
            @NonNull final EndPoint.Env env) {
        Parameters.notNull("context", context); //NOI18N
        Parameters.notNull("env", env); //NOI18N        
        Session session = null;
        final DebugInterceptor di = DebugInterceptor.getInstance();
        try {
            for (session = findSession(); session == null && di.isDebuggerStarting(); session = findSession()) {
                LOG.info("Wating for debugger session....");    //NOI18N
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            //Pass
        }
        if (session == null) {
            LOG.warning("No debugger session");
            return -1;
        }
        final int id = sequencer.incrementAndGet();
        if (active.putIfAbsent(id, new Data(id, context, env, session)) != null) {
            throw new IllegalStateException("Trying to reuse active session");  //NOI18N
        }
        final JPDADebugger jpda = session.lookupFirst(null, JPDADebugger.class);
        if (!(jpda instanceof JPDADebuggerImpl)) {
            throw new IllegalStateException("Wrong debugger service.");    //NOI18N
        }
        try {
            jpda.waitRunning();
            //Hack:
            //Now comes the fun, JPDADebuggerImpl is full of races
            //so after wait we need to busy wait. Inverted spin-park :-)
            while (jpda.getState() < 2) {
                Thread.sleep(500);
            }
        } catch (DebuggerStartException | InterruptedException ex) {
            LOG.log(Level.WARNING, "Debugger start Exception: {0}", ex);
            return -1;
        }
        final int state = jpda.getState();
        if (state == JPDADebugger.STATE_RUNNING || state == JPDADebugger.STATE_STOPPED) {
            return id;
        } else {
            LOG.log(Level.WARNING, "Wrong debugger state: {0}", state);
            return -1;
        }        
    }

    @CheckForNull
    WorkspaceResolver.Context getContext(final int sessionId) {
        final Data data = active.get(sessionId);
        return data == null ? null : data.ctx;
    }

    @CheckForNull
    EndPoint.Env getEnv(final int sessionId) {
        final Data data = active.get(sessionId);
        return data == null ? null : data.env;
    }

    @CheckForNull
    Session getDebugSession(final int sessionId) {
        final Data data = active.get(sessionId);
        return data == null ? null : data.session;
    }

    void setEval(boolean inEval) {
        ActiveSessions.inEval.set(inEval);
    }

    @NonNull
    static synchronized ActiveSessions getInstance() {
        if (instance == null) {
            instance = new ActiveSessions();
        }
        return instance;
    }

    /**
     * Finds debuggers session.
     * Todo: debugger API does not pair sessions with projects,
     * no way to find out correct session.
     * @return
     */
    @CheckForNull
    private Session findSession () {
        final DebuggerManager dm = DebuggerManager.getDebuggerManager();
        final DML dml = new DML(dm);
        dm.addDebuggerListener(dml);
        try {
            return dml.getCurrentSession();
        }finally {
            dm.removeDebuggerListener(dml);
        }
    }

    private static final class Data implements PropertyChangeListener {
        final int id;
        final WorkspaceResolver.Context ctx;
        final EndPoint.Env env;
        final Session session;
        final SourcePathProvider sourcePath;
        final JPDADebugger jpda;
        volatile JPDAThread currentThread;



        private Data(
            final int id,
            @NonNull final WorkspaceResolver.Context ctx,
            @NonNull final EndPoint.Env env,
            @NonNull final Session session) {
            Parameters.notNull("ctx", ctx); //NOI18N
            Parameters.notNull("env", env); //NOI18N
            Parameters.notNull("session", session); //NOI18N
            this.id = id;
            this.ctx = ctx;
            this.env = env;
            this.session = session;
            sourcePath = getContext(session);
            this.jpda = this.session.lookupFirst(null, JPDADebugger.class);
            if (!(jpda instanceof JPDADebuggerImpl)) {
                throw new IllegalStateException("Wrong debugger service.");    //NOI18N
            }
            jpda.addPropertyChangeListener(this);            
        }


        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            final String propName = evt.getPropertyName();            
            if (JPDADebugger.PROP_CURRENT_THREAD.equals(propName)) {
                if (currentThread != null) {
                    ((Customizer)currentThread).removePropertyChangeListener(this);
                }
                currentThread = jpda.getCurrentThread();
                if (currentThread != null) {
                    ((Customizer)currentThread).addPropertyChangeListener(this);
                    if (currentThread.isSuspended()) {
                        sendSuspend(currentThread);
                    }
                }
            } else if (JPDADebugger.PROP_STATE.equals(propName)) {
                if (jpda.getState() == JPDADebugger.STATE_DISCONNECTED) {
                    sendDisconnected();
                }
            } else if (JPDAThread.PROP_SUSPENDED.equals(propName)) {
                if (inEval.get() == Boolean.TRUE) {
                    return;
                }
                assert  evt.getSource() == currentThread;
                if (currentThread.isSuspended()) {
                    sendSuspend(currentThread);
                }
            }
        }


        private void sendSuspend(JPDAThread t) {
            CallStackFrame[] callStack = null;
            try {
                 callStack = t.getCallStack();
            } catch (AbsentInformationException aie) {/*pass, no -g*/}
            env.sendObject(createSuspendResult(callStack));
        }

        private void sendDisconnected() {
            env.sendObject(createSuspendResult(DebugMessageType.disconnected));
        }

        @NonNull
        private SuspendResult createSuspendResult(@NullAllowed CallStackFrame[] callStack) {
            final SuspendResult res = createSuspendResult(DebugMessageType.suspended);
            if (callStack != null) {
                final WorkspaceResolver wr = WorkspaceResolver.getDefault();
                if (wr == null) {
                    throw new IllegalStateException("No workspace resolver.");  //NOI18N
                }
                final FileObject root = wr.resolveFile(ctx);
                FileObject cannonRoot = null;
                File rootF = FileUtil.toFile(root);
                if (rootF != null) {
                    try {
                        cannonRoot = FileUtil.toFileObject(rootF.getCanonicalFile());
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                for (CallStackFrame csf : callStack) {
                    String relativePath;
                    try {
                        relativePath = csf.getSourcePath(null).replace (File.separatorChar, '/');
                    } catch (AbsentInformationException e) {
                        relativePath = "<unknown>";
                    }
                    String surl = sourcePath.getURL (relativePath, true);
                    if (surl == null) {
                        try {
                            String sn = csf.getSourceName(null);
                            if (sn.startsWith("file:/")) {
                                surl = sn;
                            }
                        } catch (AbsentInformationException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                    if (surl != null) {
                        try {
                           final FileObject fo = URLMapper.findFileObject(new java.net.URL(surl));
                           if (root != null && fo != null && FileUtil.isParentOf(root, fo)) {
                               relativePath = FileUtil.getRelativePath(root, fo);
                           } else if (cannonRoot != null && fo != null && FileUtil.isParentOf(cannonRoot, fo)) {
                               relativePath = FileUtil.getRelativePath(cannonRoot, fo);
                           }
                        } catch (java.net.MalformedURLException muex) {
                            LOG.log(
                                Level.WARNING,
                                "Malformed URL {0}",    //NOI18N
                                surl);
                        }
                    }
                    res.getStack().add(String.format(
                        "%s:%d",
                        relativePath,
                        csf.getLineNumber(null)));
                }
            }
            return res;
         }

        @NonNull
        private SuspendResult createSuspendResult(@NonNull final DebugMessageType type) {
            final SuspendResult res = new SuspendResult();
            res.setId(id);
            res.setStatus(Status.done);
            res.setType(type);
            return res;
        }

        private SourcePathProvider getContext (Session session) {
           List<? extends SourcePathProvider> l = session.lookup(null, SourcePathProvider.class);
           SourcePathProvider sourcePathProvider = l.get(0);
           int i, k = l.size ();
           for (i = 1; i < k; i++) {
               sourcePathProvider = new CompoundContextProvider (
                   (SourcePathProvider) l.get (i),
                   sourcePathProvider
               );
           }
           return sourcePathProvider;
        }
    }

    private static final class DML extends DebuggerManagerAdapter {

        private final DebuggerManager dm;
        private final Lock lock;
        private final Condition cond;
        //GuardedBy("lock")
        private Session session;

        DML(@NonNull final DebuggerManager dm) {
            Parameters.notNull("dm", dm);   //NOI18N
            this.dm = dm;
            this.lock = new ReentrantLock();
            this.cond = lock.newCondition();
        }

        @Override
        public void sessionAdded(@NonNull Session session) {
            lock.lock();
            try {
                this.session = session;
                cond.signalAll();
            } finally {
                lock.unlock();
            }
        }

        Session getCurrentSession() {
            Session res = dm.getCurrentSession();
            if (res != null) {
                return res;
            }
            lock.lock();
            try {
                while (this.session == null) {
                    try {
                        if (!cond.await(20, TimeUnit.SECONDS)) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        return null;
                    }
                }
                return this.session;
            } finally {
                lock.unlock();
            }
        }
    }
}