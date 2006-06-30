/* The contents of this file are subject to the terms of the Common Development
/* and Distribution License (the License). You may not use this file except in
/* compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
/* or http://www.netbeans.org/cddl.txt.
/*
/* When distributing Covered Code, include this CDDL Header Notice in each file
/* and include the License file at http://www.netbeans.org/cddl.txt.
/* If applicable, add the following below the CDDL Header, with the fields
/* enclosed by brackets [] replaced by your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
 *
 * Contributor(s): Libor Martinek.
 */

package org.netbeans.modules.remotefs.ftpclient;

import org.netbeans.modules.remotefs.core.LogInfo;

/** FTPLogInfo stores login information
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPLogInfo implements LogInfo {
  static final long serialVersionUID = 4795532037339960289L;
  /** Host name */
  private String host = "localhost";
  /** Port number */
  private int port = FTPClient.DEFAULT_PORT;
  /** User name */
  private String user = "anonymous";
  /** Password */
  private String password = "forteuser@";
  
  /** Create empty LogInfo */
  public FTPLogInfo() {
  }
  
  /** Create LogInfo */
  public FTPLogInfo(String host, int port, String user, String password) {
    this.host = host;
    this.port = port;
    this.user = user;
    this.password = password;
  }
  
  /** Set host name */
  public void setHost(String host) {  this.host = host; }
  /** Set port number */
  public void setPort(int port) { this.port = port; }
  /** Set user name */
  public void setUser(String user) { this.user = user; }
  /** Set password */
  public void setPassword(String password) { this.password = password; }
  
  /** Get host name */
  public String getHost() { return host; }
  /** Get port number */
  public int getPort() { return port; }
  /** Get user name */
  public String getUser() { return user; }
  /** Get password */
  public String getPassword() { return password; }
  
  
  /** Return human redable description of this LogInfo */
  public String displayName() {
     return "ftp://"+((user!=null && user.equalsIgnoreCase("anonymous"))?"":user+"@")+
              host+((port==FTPClient.DEFAULT_PORT)?"":(":"+String.valueOf(port)));
  }
}