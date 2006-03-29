/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70CommandType.java
 */

package org.netbeans.modules.j2ee.sun.ws7.j2ee;
import javax.enterprise.deploy.shared.CommandType;

/**
 * CommandType for the Webserver 70 commands like starting and stopping the
 * Instances other than Admin Server
 *
 * @author Mukesh Garg
 */
public class WS70CommandType extends CommandType {
    /**
      * The DeploymentManger action operation being processed is distribute.
      */
     public static final WS70CommandType DISTRIBUTE = new WS70CommandType(0);
     /**
      * The DeploymentManger action operation being processed is start.
      */
     public static final WS70CommandType START = new WS70CommandType(1);
     /**
      * The DeploymentManger action operation being processed is stop.
      */
     public static final WS70CommandType STOP = new WS70CommandType(2);
     /**
      * The DeploymentManger action operation being processed is undeploy.
      */
     public static final WS70CommandType UNDEPLOY = new WS70CommandType(3);
     /**
      * he DeploymentManger action operation being processed is redeploy.
      */
     public static final WS70CommandType REDEPLOY = new WS70CommandType(4);
     
     public static final WS70CommandType STARTTARGET = new WS70CommandType(11);
     public static final WS70CommandType STOPTARGET = new WS70CommandType(12);
     public static final WS70CommandType STARTTARGETDEBUG = new WS70CommandType(13);
    
     private static final CommandType[] enumValueTable = new CommandType[]{
         DISTRIBUTE,
         START,
         STOP,
         UNDEPLOY,
         REDEPLOY,
         STARTTARGET,
         STOPTARGET,
         STARTTARGETDEBUG
     };

     private static final String[] stringTable = new String[]{
         "distribute",
         "start",
         "stop",
         "undeploy",
         "redeploy",
         "starttarget",
         "stoptarget",
         "starttargetdebug"
     };
     private int value;
     protected WS70CommandType(int value){
         super(value);
         this.value = value;
     }
}
