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

package org.netbeans.modules.vcs.advanced;
import java.beans.*;
import java.util.ResourceBundle;

import org.openide.filesystems.*;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.netbeans.modules.vcscore.settings.RefreshModePropertyEditor;
import org.netbeans.modules.vcscore.VcsFileSystem;

/** BeanInfo for CommandLineVcsFileSystem.
 *
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class CommandLineVcsFileSystemBeanInfo extends SimpleBeanInfo {

    /* Descriptor of valid properties
    * @return array of properties
    */
    public PropertyDescriptor[] getPropertyDescriptors () {
        PropertyDescriptor[] desc;
        PropertyDescriptor rootDirectory=null;
        PropertyDescriptor debug=null;
        PropertyDescriptor variables=null;
        PropertyDescriptor commands=null;
        PropertyDescriptor cacheId=null;
        PropertyDescriptor config=null;
        //PropertyDescriptor lock=null;
        //PropertyDescriptor lockPrompt=null;
        PropertyDescriptor acceptUserParams = null;
        PropertyDescriptor runRefreshCommand = null;
        PropertyDescriptor processAllFiles = null;
        PropertyDescriptor annotationPattern = null;
        PropertyDescriptor autoRefresh = null;
        PropertyDescriptor notification = null;
        PropertyDescriptor hideShadowFiles = null;
        PropertyDescriptor createBackupFiles = null;
        PropertyDescriptor rememberPassword = null;
        PropertyDescriptor refreshTime = null;
        PropertyDescriptor hidden = null;
        PropertyDescriptor readOnly = null;

        try {
            rootDirectory=new PropertyDescriptor
                          (VcsFileSystem.PROP_ROOT, CommandLineVcsFileSystem.class, "getRootDirectory", null); // NOI18N
            debug=new PropertyDescriptor
                  (VcsFileSystem.PROP_DEBUG,CommandLineVcsFileSystem.class, "getDebug", "setDebug"); // NOI18N

            variables=new PropertyDescriptor
                      (VcsFileSystem.PROP_VARIABLES, CommandLineVcsFileSystem.class, "getVariables", "setVariables"); // NOI18N
            variables.setPropertyEditorClass (org.netbeans.modules.vcs.advanced.UserVariablesEditor.class);
            variables.setExpert(true);
            variables.setValue("canEditAsText", Boolean.FALSE);

            commands=new PropertyDescriptor
                     (VcsFileSystem.PROP_COMMANDS, CommandLineVcsFileSystem.class, "getCommands", "setCommands"); // NOI18N
            commands.setPropertyEditorClass (org.netbeans.modules.vcs.advanced.UserCommandsEditor.class);
            commands.setExpert(true);
            commands.setValue("canEditAsText", Boolean.FALSE);

            cacheId=new PropertyDescriptor
                    ("cacheId", CommandLineVcsFileSystem.class, "getCacheId", null); // NOI18N
            cacheId.setExpert(true);

            config=new PropertyDescriptor
                   ("config", CommandLineVcsFileSystem.class, "getConfig", null); // NOI18N
            /*
            lock=new PropertyDescriptor
                 (VcsFileSystem.PROP_CALL_LOCK, CommandLineVcsFileSystem.class, "isLockFilesOn", "setLockFilesOn"); // NOI18N
            lock.setExpert(true);

            lockPrompt=new PropertyDescriptor
                       (VcsFileSystem.PROP_CALL_LOCK_PROMPT, CommandLineVcsFileSystem.class, "isPromptForLockOn", "setPromptForLockOn"); // NOI18N
            lockPrompt.setExpert(true);
             */
            acceptUserParams = new PropertyDescriptor
                               (VcsFileSystem.PROP_EXPERT_MODE, CommandLineVcsFileSystem.class, "isExpertMode", "setExpertMode"); // NOI18N
            runRefreshCommand = new PropertyDescriptor
                               (GeneralVcsSettings.PROP_OFFLINE, CommandLineVcsFileSystem.class, "isOffLine", "setOffLine"); // NOI18N
            processAllFiles = new PropertyDescriptor
                               (VcsFileSystem.PROP_PROCESS_UNIMPORTANT_FILES, CommandLineVcsFileSystem.class, "isProcessUnimportantFiles", "setProcessUnimportantFiles"); // NOI18N
            processAllFiles.setExpert(true);
            annotationPattern = new PropertyDescriptor
                               (VcsFileSystem.PROP_ANNOTATION_PATTERN, CommandLineVcsFileSystem.class, "getAnnotationPattern", "setAnnotationPattern"); // NOI18N
            annotationPattern.setPropertyEditorClass(CommandLineAnnotPatternEditor.class);
            autoRefresh = new PropertyDescriptor
                               (GeneralVcsSettings.PROP_AUTO_REFRESH, CommandLineVcsFileSystem.class, "getAutoRefresh", "setAutoRefresh"); // NOI18N
            autoRefresh.setPropertyEditorClass(RefreshModePropertyEditor.class);
            notification = new PropertyDescriptor
                               (VcsFileSystem.PROP_COMMAND_NOTIFICATION, CommandLineVcsFileSystem.class, "isCommandNotification", "setCommandNotification"); // NOI18N
            hideShadowFiles = new PropertyDescriptor
                               (GeneralVcsSettings.PROP_HIDE_SHADOW_FILES, CommandLineVcsFileSystem.class, "isHideShadowFiles", "setHideShadowFiles"); // NOI18N
            hideShadowFiles.setExpert(true);
            createBackupFiles = new PropertyDescriptor
                                ("createBackupFiles", CommandLineVcsFileSystem.class, "isCreateBackupFiles", "setCreateBackupFiles"); // NOI18N
            createBackupFiles.setExpert(true);
            rememberPassword = new PropertyDescriptor
                                ("rememberPassword", CommandLineVcsFileSystem.class, "isRememberPassword", "setRememberPassword"); // NOI18N
            rememberPassword.setExpert(true);
            refreshTime = new PropertyDescriptor
                                ("refreshTime", CommandLineVcsFileSystem.class, "getCustomRefreshTime", "setCustomRefreshTime"); // NOI18N
            refreshTime.setExpert(true);
            hidden = new PropertyDescriptor
                                ("hidden", CommandLineVcsFileSystem.class, "isHidden", "setHidden"); // NOI18N
            readOnly = new PropertyDescriptor
                                ("readOnly", CommandLineVcsFileSystem.class, "isReadOnly", "setReadOnly"); // NOI18N


            desc = new PropertyDescriptor[] {
                       rootDirectory, debug, variables, commands, cacheId, config,
                       acceptUserParams, runRefreshCommand, processAllFiles,
                       annotationPattern, autoRefresh, notification, hideShadowFiles,
                       createBackupFiles,
                       rememberPassword, refreshTime, hidden, readOnly
                   };

            ResourceBundle bundle = NbBundle.getBundle (CommandLineVcsFileSystemBeanInfo.class);
            ResourceBundle bundleSettings = NbBundle.getBundle (GeneralVcsSettings.class);

            rootDirectory.setDisplayName      (bundle.getString("PROP_rootDirectory"));
            rootDirectory.setShortDescription (bundle.getString("HINT_rootDirectory"));
            debug.setDisplayName              (bundle.getString("PROP_debug"));
            debug.setShortDescription         (bundle.getString("HINT_debug"));
            variables.setDisplayName          (bundle.getString("PROP_variables"));
            variables.setShortDescription     (bundle.getString("HINT_variables"));
            commands.setDisplayName           (bundle.getString("PROP_commands"));
            commands.setShortDescription      (bundle.getString("HINT_commands"));
            cacheId.setDisplayName            (bundle.getString("PROP_cacheId"));
            cacheId.setShortDescription       (bundle.getString("HINT_cacheId"));
            config.setDisplayName             (bundle.getString("PROP_config"));
            config.setShortDescription        (bundle.getString("HINT_config"));
            //lock.setDisplayName               (bundle.getString("PROP_lock"));
            //lock.setShortDescription          (bundle.getString("HINT_lock"));
            //lockPrompt.setDisplayName         (bundle.getString("PROP_lockPrompt"));
            //lockPrompt.setShortDescription    (bundle.getString("HINT_lockPrompt"));
            acceptUserParams.setDisplayName   (bundle.getString("PROP_acceptUserParams"));
            acceptUserParams.setShortDescription(bundle.getString("HINT_acceptUserParams"));
            runRefreshCommand.setDisplayName  (bundleSettings.getString("PROP_offline"));
            runRefreshCommand.setShortDescription(bundleSettings.getString("HINT_offline"));
            processAllFiles.setDisplayName    (bundle.getString("PROP_processAllFiles"));
            processAllFiles.setShortDescription(bundle.getString("HINT_processAllFiles"));
            annotationPattern.setDisplayName  (bundle.getString("PROP_annotationPattern"));
            annotationPattern.setShortDescription(bundle.getString("HINT_annotationPattern"));
            autoRefresh.setDisplayName        (bundleSettings.getString("PROP_autoRefresh"));
            autoRefresh.setShortDescription   (bundleSettings.getString("HINT_autoRefresh"));
            notification.setDisplayName       (bundle.getString("PROP_commandNotification"));
            notification.setShortDescription  (bundle.getString("HINT_commandNotification"));
            hideShadowFiles.setDisplayName    (bundleSettings.getString("PROP_hideShadowFiles"));
            hideShadowFiles.setShortDescription(bundleSettings.getString("HINT_hideShadowFiles"));
            createBackupFiles.setDisplayName  (bundle.getString("PROP_createBackupFiles"));
            createBackupFiles.setShortDescription(bundle.getString("HINT_createBackupFiles"));
            rememberPassword.setDisplayName   (bundle.getString("PROP_rememberPassword"));
            rememberPassword.setShortDescription(bundle.getString("HINT_rememberPassword"));
            refreshTime.setDisplayName        (bundle.getString("PROP_refreshTime"));
            refreshTime.setShortDescription   (bundle.getString("HINT_refreshTime"));
            hidden.setDisplayName             (bundle.getString("PROP_hidden"));
            hidden.setShortDescription        (bundle.getString("HINT_hidden"));
            readOnly.setDisplayName           (bundle.getString("PROP_readOnly"));
            readOnly.setShortDescription      (bundle.getString("HINT_readOnly"));

        } catch (IntrospectionException ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
            desc = null;
        }
        return desc;
    }

    /* Provides the VCSFileSystem's icon */
    public java.awt.Image getIcon(int type) {
        switch (type) {
            case ICON_COLOR_16x16:
                return Utilities.loadImage("org/netbeans/modules/vcs/advanced/vcsGeneric.gif"); // NOI18N
        }
        return null;
    }

    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bd = new BeanDescriptor(CommandLineVcsFileSystem.class,
                                               org.netbeans.modules.vcs.advanced.VcsCustomizer.class);
        bd.setValue(VcsFileSystem.VCS_PROVIDER_ATTRIBUTE, Boolean.TRUE);
        bd.setValue(VcsFileSystem.VCS_FILESYSTEM_ICON_BASE, "org/netbeans/modules/vcs/advanced/vcsGeneric"); // NOI18N
        return bd;
    }

}

