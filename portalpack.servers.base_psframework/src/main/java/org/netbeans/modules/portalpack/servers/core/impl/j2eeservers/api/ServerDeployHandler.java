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

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.api;

import java.io.File;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;

/**
 *
 * @author Satya
 */
public interface ServerDeployHandler {
    
    public boolean deploy(String warFile) throws Exception;
    public boolean undeploy(String appName) throws Exception;
    public boolean install() throws Exception;
    public boolean deploy(String dir,String contextName) throws Exception;
    public void restart(String contextRoot) throws Exception;
    public boolean isDeployOnSaveSupported();
    public TargetModuleID[] getAvailableModules(Target[] targets);
    public boolean isServerRunning();
    public File  getModuleDirectory(TargetModuleID module);
}
