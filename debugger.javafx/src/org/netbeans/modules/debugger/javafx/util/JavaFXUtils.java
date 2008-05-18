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

package org.netbeans.modules.debugger.javafx.util;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.request.EventRequestManager;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
* Utilities for debugger.
*
* @author Jan Jancura
*/
public class JavaFXUtils {


    // testing methods .........................................................................

    public static void printFeatures (Logger logger, VirtualMachine virtualMachine) {
        logger.fine ("canAddMethod " + virtualMachine.canAddMethod ());
        logger.fine ("canBeModified " + virtualMachine.canBeModified ());
        logger.fine ("canGetBytecodes " + virtualMachine.canGetBytecodes ());
        logger.fine ("canGetCurrentContendedMonitor " + virtualMachine.canGetCurrentContendedMonitor ());
        logger.fine ("canGetMonitorInfo " + virtualMachine.canGetMonitorInfo ());
        logger.fine ("canGetOwnedMonitorInfo " + virtualMachine.canGetOwnedMonitorInfo ());
        logger.fine ("canGetSourceDebugExtension " + virtualMachine.canGetSourceDebugExtension ());
        logger.fine ("canGetSyntheticAttribute " + virtualMachine.canGetSyntheticAttribute ());
        logger.fine ("canPopFrames " + virtualMachine.canPopFrames ());
        logger.fine ("canRedefineClasses " + virtualMachine.canRedefineClasses ());
        logger.fine ("canRequestVMDeathEvent " + virtualMachine.canRequestVMDeathEvent ());
        logger.fine ("canUnrestrictedlyRedefineClasses " + virtualMachine.canUnrestrictedlyRedefineClasses ());
        logger.fine ("canUseInstanceFilters " + virtualMachine.canUseInstanceFilters ());
        logger.fine ("canWatchFieldAccess " + virtualMachine.canWatchFieldAccess ());
        logger.fine ("canWatchFieldModification " + virtualMachine.canWatchFieldModification ());
    }
   
    public static void showMethods (ReferenceType rt) {
        System.out.println ("  ============================================"); // NOI18N
        System.out.println ("  Methods for " + rt.name ()); // NOI18N
        List l = rt.methods ();
        int i, k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println (((Method) l.get (i)).name () + " ; " + // NOI18N
                                ((Method) l.get (i)).signature ());

        System.out.println ("  ============================================"); // NOI18N
    }

    public static void showLinesForClass (ReferenceType rt) {
        System.out.println ("  ============================================"); // NOI18N
        System.out.println ("  Lines for " + rt.name ()); // NOI18N
        List l = null;
        try {
            l = rt.allLineLocations ();
        } catch (AbsentInformationException e) {
        }
        int i, k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("   " + ((Location) l.get (i)).lineNumber () + " : " + // NOI18N
                                ((Location) l.get (i)).codeIndex ()
                               );

        System.out.println ("  ============================================"); // NOI18N
    }

    public static void showRequests (EventRequestManager requestManager) {
        System.out.println ("  ============================================"); // NOI18N
        List l = requestManager.breakpointRequests ();
        System.out.println ("  Break request: " + l.size ()); // NOI18N
        int i, k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        l = requestManager.classPrepareRequests ();
        System.out.println ("  Class prepare request: " + l.size ()); // NOI18N
        k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        l = requestManager.accessWatchpointRequests ();
        System.out.println ("  Access watch request: " + l.size ()); // NOI18N
        k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        l = requestManager.classUnloadRequests ();
        System.out.println ("  Class unload request: " + l.size ()); // NOI18N
        k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        l = requestManager.exceptionRequests ();
        System.out.println ("  Exception request: " + l.size ()); // NOI18N
        k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        l = requestManager.methodEntryRequests ();
        System.out.println ("  Method entry request: " + l.size ()); // NOI18N
        k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        l = requestManager.methodExitRequests ();
        System.out.println ("  Method exit request: " + l.size ()); // NOI18N
        k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        l = requestManager.modificationWatchpointRequests ();
        System.out.println ("  Modif watch request: " + l.size ()); // NOI18N
        k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        l = requestManager.stepRequests ();
        System.out.println ("  Step request: " + l.size ()); // NOI18N
        k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        l = requestManager.threadDeathRequests ();
        System.out.println ("  Thread death entry request: " + l.size ()); // NOI18N
        k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        l = requestManager.threadStartRequests ();
        System.out.println ("  Thread start request: " + l.size ()); // NOI18N
        k = l.size ();
        for (i = 0; i < k; i++)
            System.out.println ("    " + l.get (i));
        System.out.println ("  ============================================"); // NOI18N
        
    }

    public static void showConnectors (List l) {
        int i, k = l.size ();
        for (i = 0; i < k; i++) showConnector ((Connector) l.get (i));
    }

    public static void showConnector (Connector connector) {
        System.out.println ("  ============================================"); // NOI18N
        System.out.println ("  Connector: " + connector); // NOI18N
        System.out.println ("    name: " + connector.name ()); // NOI18N
        System.out.println ("    description: " + connector.description ()); // NOI18N
        System.out.println ("    transport: " + (connector.transport () != null ? connector.transport ().name () : "null")); // NOI18N
        showProperties (connector.defaultArguments ());
        System.out.println ("  ============================================"); // NOI18N
    }

    public static void showThread (ThreadReference tr) {
        System.out.println ("  ============================================"); // NOI18N
        try {
            System.out.println ("  Thread: " + tr.name ()); // NOI18N
        } catch (Exception e) {
            System.out.println ("  Thread: " + e); // NOI18N
        }
        
        try {
            System.out.println ("    status: " + tr.status ()); // NOI18N
        } catch (Exception e) {
            System.out.println ("    status: " + e); // NOI18N
        }
        
        try {
            System.out.println ("    isSuspended: " + tr.isSuspended ()); // NOI18N
        } catch (Exception e) {
            System.out.println ("    isSuspended: " + e); // NOI18N
        }
        
        try {
            System.out.println ("    frameCount: " + tr.frameCount ()); // NOI18N
        } catch (Exception e) {
            System.out.println ("    frameCount: " + e); // NOI18N
        }
        
        try {
            System.out.println ("    location: " + tr.frame (0)); // NOI18N
        } catch (Exception e) {
            System.out.println ("    location: " + e); // NOI18N
        }
        System.out.println ("  ============================================"); // NOI18N
    }


    private static void showProperties (Map properties) {
        Iterator i = properties.keySet ().iterator ();
        while (i.hasNext ()) {
            Object k = i.next ();
            Connector.Argument a = (Connector.Argument) properties.get (k);
            System.out.println ("    property: " + k + " > " + a.name ()); // NOI18N
            System.out.println ("      desc: " + a.description ()); // NOI18N
            System.out.println ("      mustSpecify: " + a.mustSpecify ()); // NOI18N
            System.out.println ("      value: " + a.value ()); // NOI18N
        }
    }

    public static void listGroup (String s, ThreadGroupReference g) {
        List l = g.threadGroups ();
        int i, k = l.size ();
        for (i = 0; i < k; i++) {
            System.out.println (s + "Thread Group: " + l.get (i) + " : " + // NOI18N
                                ((ThreadGroupReference)l.get (i)).name ()
                               );
            listGroup (s + "  ", (ThreadGroupReference)l.get (i)); // NOI18N
        }
        l = g.threads ();
        k = l.size ();
        for (i = 0; i < k; i++) {
            System.out.println (s + "Thread: " + l.get (i) + " : " + // NOI18N
                                ((ThreadReference)l.get (i)).name ()
                               );
        }
    }

    private static void listGroups (List g) {
        System.out.println ("  ============================================"); // NOI18N
        int i, k = g.size ();
        for (i = 0; i < k; i++) {
            System.out.println ("Thread Group: " + g.get (i) + " : " + // NOI18N
                                ((ThreadGroupReference)g.get (i)).name ()
                               );
            listGroup ("  ", (ThreadGroupReference)g.get (i)); // NOI18N
        }
        System.out.println ("  ============================================"); // NOI18N
    }
}