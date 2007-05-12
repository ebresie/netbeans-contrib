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

package org.netbeans.modules.scala.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.AssertionFailedError;
import static junit.framework.Assert.*;

/** Check the behaviour of <public-packages> in project.xml modules.
 *
 * @author Jaroslav Tulach
 */
public class AntSupport extends Object {
    private AntSupport () {
    }
    
    final static String readFile (java.io.File f) throws java.io.IOException {
        int s = (int)f.length ();
        byte[] data = new byte[s];
        assertEquals ("Read all data", s, new java.io.FileInputStream (f).read (data));
        
        return new String (data);
    }
    
    final static File extractString (String res) throws Exception {
        File f = File.createTempFile("res", ".xml");
        f.deleteOnExit ();
        
        FileOutputStream os = new FileOutputStream(f);
        InputStream is = new ByteArrayInputStream(res.getBytes("UTF-8"));
        for (;;) {
            int ch = is.read ();
            if (ch == -1) break;
            os.write (ch);
        }
        os.close ();
            
        return f;
    }
    
    final static File extractResource(String res) throws Exception {
        URL u = AntSupport.class.getResource(res);
        assertNotNull ("Resource should be found " + res, u);
        
        File f = File.createTempFile("res", ".xml");
        f.deleteOnExit ();
        
        FileOutputStream os = new FileOutputStream(f);
        InputStream is = u.openStream();
        for (;;) {
            int ch = is.read ();
            if (ch == -1) break;
            os.write (ch);
        }
        os.close ();
            
        return f;
    }
    
    final static void execute (String res, String[] args) throws Exception {
        execute (extractResource (res), args);
    }
    
    private static ByteArrayOutputStream out;
    private static ByteArrayOutputStream err;
    
    final static String getStdOut() {
        return out.toString();
    }
    final static String getStdErr() {
        return err.toString();
    }
    
    final static void execute(File f, String... args) throws Exception {
        // we need security manager to prevent System.exit
        if (! (System.getSecurityManager () instanceof MySecMan)) {
            out = new java.io.ByteArrayOutputStream ();
            err = new java.io.ByteArrayOutputStream ();
            System.setOut (new java.io.PrintStream (out));
            System.setErr (new java.io.PrintStream (err));
            
            System.setSecurityManager (new MySecMan ());
        }
        
        MySecMan sec = (MySecMan)System.getSecurityManager();
        
        // Jesse claims that this is not the right way how the execution
        // of an ant script should be invoked:
        //
        // better IMHO to just run the task directly
        // (setProject() and similar, configure its bean properties, and call
        // execute()), or just make a new Project and initialize it.
        // ant.Main.main is not intended for embedded use. Then you could get rid
        // of the SecurityManager stuff, would be cleaner I think.
        //
        // If I had to write this once again, I would try to follow the
        // "just make a new Project and initialize it", but as this works
        // for me now, I leave it for the time when somebody really 
        // needs that...
        
        List arr = new ArrayList();
        arr.add ("-f");
        arr.add (f.toString ());
        arr.addAll(Arrays.asList(args));
        
        
        out.reset ();
        err.reset ();
        
        try {
            sec.setActive(true);
            
            Class launcher = Class.forName("org.apache.tools.ant.Main");
            Method main = launcher.getDeclaredMethod("main", String[].class);
            
            try {
                main.invoke(null, (Object)arr.toArray (new String[0]));
            } catch (InvocationTargetException ex) {
                if (ex.getTargetException() instanceof Exception) {
                    throw (Exception)ex.getTargetException();
                } else {
                    throw ex;
                }
            }
        } catch (MySecExc ex) {
            assertNotNull ("The only one to throw security exception is MySecMan and should set exitCode", sec.exitCode);
            ExecutionError.assertExitCode (
                "Execution has to finish without problems", 
                sec.exitCode.intValue ()
            );
        } finally {
            sec.setActive(false);
        }
    }
    
    static class ExecutionError extends AssertionFailedError {
        public final int exitCode;
        
        public ExecutionError (String msg, int e) {
            super (msg);
            this.exitCode = e;
        }
        
        public static void assertExitCode (String msg, int e) {
            if (e != 0) {
                throw new ExecutionError (
                    msg + " was: " + e + "\nOutput: " + out.toString () +
                    "\nError: " + err.toString (),  
                    e
                );
            }
        }
    }
    
    private static class MySecExc extends SecurityException {
        public void printStackTrace() {
        }
        public void printStackTrace(PrintStream ps) {
        }
        public void printStackTrace(PrintWriter ps) {
        }
    }
    
    private static class MySecMan extends SecurityManager {
        public Integer exitCode;
        
        private boolean active;
        
        public void checkExit (int status) {
            if (active) {
                exitCode = new Integer (status);
                throw new MySecExc ();
            }
        }

        public void checkPermission(Permission perm, Object context) {
        }

        public void checkPermission(Permission perm) {
        /*
            if (perm instanceof RuntimePermission) {
                if (perm.getName ().equals ("setIO")) {
                    throw new MySecExc ();
                }
            }
         */
        }

        public void checkMulticast(InetAddress maddr) {
        }

        public void checkAccess (ThreadGroup g) {
        }

        public void checkWrite (String file) {
        }

        public void checkLink (String lib) {
        }

        public void checkExec (String cmd) {
        }

        public void checkDelete (String file) {
        }

        public void checkPackageAccess (String pkg) {
        }

        public void checkPackageDefinition (String pkg) {
        }

        public void checkPropertyAccess (String key) {
        }

        public void checkRead (String file) {
        }

        public void checkSecurityAccess (String target) {
        }

        public void checkWrite(FileDescriptor fd) {
        }

        public void checkListen (int port) {
        }

        public void checkRead(FileDescriptor fd) {
        }

        public void checkMulticast(InetAddress maddr, byte ttl) {
        }

        public void checkAccess (Thread t) {
        }

        public void checkConnect (String host, int port, Object context) {
        }

        public void checkRead (String file, Object context) {
        }

        public void checkConnect (String host, int port) {
        }

        public void checkAccept (String host, int port) {
        }

        public void checkMemberAccess (Class clazz, int which) {
        }

        public void checkSystemClipboardAccess () {
        }

        public void checkSetFactory () {
        }

        public void checkCreateClassLoader () {
        }

        public void checkAwtEventQueueAccess () {
        }

        public void checkPrintJobAccess () {
        }

        public void checkPropertiesAccess () {
        }

        void setActive(boolean b) {
            active = b;
        }
    } // end of MySecMan
}
