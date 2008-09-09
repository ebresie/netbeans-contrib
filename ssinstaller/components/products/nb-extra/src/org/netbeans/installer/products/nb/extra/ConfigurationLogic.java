/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.products.nb.extra;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.netbeans.installer.product.components.ProductConfigurationLogic;
import org.netbeans.installer.utils.FileProxy;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.RemovalMode;
import org.netbeans.installer.utils.helper.Status;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.Utils;
import org.netbeans.installer.wizard.Wizard;
import org.netbeans.installer.wizard.components.WizardComponent;

/**
 *
 * @author Leonid Mesnik
 */
public class ConfigurationLogic extends ProductConfigurationLogic {    
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    private List<WizardComponent> wizardComponents;
    
    public ConfigurationLogic() throws InitializationException {
        wizardComponents = Wizard.loadWizardComponents(
                WIZARD_COMPONENTS_URI,
                getClass().getClassLoader());
    }
    
    
    public List<WizardComponent> getWizardComponents() {
        // The location is gotten from Sun Studio location.
        return Collections.EMPTY_LIST;
        //return wizardComponents;
    }
    
    @Override
    public String getSystemDisplayName() {
        return getString("CL.system.display.name");
    }
    
    @Override
    public boolean allowModifyMode() {
        return false;
    }

    @Override
    public boolean registerInSystem() {
        return false;
    }

    @Override
    public RemovalMode getRemovalMode() {
        return RemovalMode.LIST;
    }
    
    
  
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String WIZARD_COMPONENTS_URI =
            FileProxy.RESOURCE_SCHEME_PREFIX + // NOI18N
            "org/netbeans/installer/products/nb/extra/wizard.xml"; // NOI18N
    
    private final static String SUNSTUDIO_NBHOME="default_netbeans_home="; // NOI18N

    // This methos is not used now.
    private void setNBLocation(File installationDirectory, File nbHome) throws IOException {        
        final File confFile = new File(installationDirectory.getAbsolutePath() + File.separator 
                + Utils.getMainDirectory() + File.separator + "prod" + File.separator
                + "etc" + File.separator + "sunstudio.conf");
        LogManager.log("Setting NB home in file " + confFile.getAbsolutePath());
        String contents = FileUtils.readFile(confFile);        
        String nbHomePath = StringUtils.escapeRegExp(nbHome.getAbsolutePath());        
        contents = contents.replaceAll(
                "#?" + "ext_class_path=" + "\".*?\"",
                SUNSTUDIO_NBHOME + "\"" + nbHomePath + "\"");        
        FileUtils.writeFile(confFile, contents);
    }
    
    @Override
    public void install(Progress progress) throws InstallationException {
        if (Utils.getSSBase() != null 
                && Utils.getSSBase().getStatus().equals(Status.INSTALLED)
                || Utils.getSSBase().getStatus().equals(Status.TO_BE_INSTALLED)) {
            this.getProduct().setParent(Utils.getSSBase());
            /*
             * The code to set up Sun Studio is not used
             *
            try {
                setNBLocation(Utils.getSSBase().getInstallationLocation(),
                        Utils.getNBBase().getInstallationLocation());
            } catch (IOException ex) {
                // the error not critical ???
                LogManager.log("Unable to set NB location" + ex.getMessage());                
            }*/
        }        
    }

    @Override
    public void uninstall(Progress progress) throws UninstallationException {
        
    }
}
