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

package org.netbeans.modules.portalpack.servers.websynergy.ws;

import org.netbeans.modules.portalpack.servers.core.api.ConfigPanel;
import org.netbeans.modules.portalpack.servers.core.api.PSConfigPanelManager;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver.SunAppServerConfigPanel;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.ui.ClasspathConfigPanel;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.ui.DefaultServerConfigPanel;
import org.netbeans.modules.portalpack.servers.core.ui.InstallPanel;
import org.netbeans.modules.portalpack.servers.websynergy.ui.WSConfigPanel;

/**
 *
 * @author Satya
 */
public class WSConfigPanelManagerImpl implements PSConfigPanelManager, ServerConstants{
    
    
    /**
     * Creates a new instance of WSConfigPanelManagerImpl
     */
    public WSConfigPanelManagerImpl() {
    }

    public InstallPanel[] getInstallPanels(String psVersion) {
        
        return new InstallPanel[]{new InstallPanel(getServerConfigPanel()),
                                  new InstallPanel(new WSConfigPanel(psVersion),true)};
        
    }

    public ConfigPanel[] getConfigPanels(String psVersion) {
        
        
        return new ConfigPanel[]{new WSConfigPanel(psVersion),
                                    getServerConfigPanel(),new ClasspathConfigPanel()};
        
    }
    
    private ConfigPanel getServerConfigPanel()
    {
        DefaultServerConfigPanel serverConfigPanel = new DefaultServerConfigPanel();
        serverConfigPanel.registerServerConfigPanel(new SunAppServerConfigPanel(),SUN_APP_SERVER_9,org.openide.util.NbBundle.getMessage(WSConfigPanelManagerImpl.class, "Sun_Java_System_AppServer_9"));
        //serverConfigPanel.registerServerConfigPanel(new TomcatConfigPanel(),TOMCAT_5_X,org.openide.util.NbBundle.getMessage(WSConfigPanelManagerImpl.class, "Tomcat_5_x"));
        return serverConfigPanel;
    }
}
