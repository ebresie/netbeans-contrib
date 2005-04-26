/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.j2ee.blueprints.ui.projects;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;


public class FoldersListSettings extends SystemOption {

    static final long serialVersionUID = -5L;
    
    private static final String LAST_EXTERNAL_SOURCE_ROOT = "srcRoot";  //NOI18N

    private static final String NEW_PROJECT_COUNT = "newProjectCount"; //NOI18N
    
//    private static final String SHOW_AGAIN_BROKEN_REF_ALERT = "showAgainBrokenRefAlert"; //NOI18N

    public String displayName() {
        return ""; //NOI18N
    }

    public String getLastExternalSourceRoot () {
        return (String) getProperty(LAST_EXTERNAL_SOURCE_ROOT);
    }

    public void setLastExternalSourceRoot (String path) {
        putProperty (LAST_EXTERNAL_SOURCE_ROOT, path, true);
    }

    public int getNewProjectCount () {
        Integer value = (Integer) getProperty (NEW_PROJECT_COUNT);
        return value == null ? 0 : value.intValue();
    }

    public void setNewProjectCount (int count) {
        this.putProperty(NEW_PROJECT_COUNT, new Integer(count),true);
    }
    
//    public boolean isShowAgainBrokenRefAlert() {
//        Boolean b = (Boolean)getProperty(SHOW_AGAIN_BROKEN_REF_ALERT);
//        return b == null ? true : b.booleanValue();
//    }
//    
//    public void setShowAgainBrokenRefAlert(boolean again) {
//        this.putProperty(SHOW_AGAIN_BROKEN_REF_ALERT, Boolean.valueOf(again), true);
//    }

    public static FoldersListSettings getDefault () {
        return (FoldersListSettings) SystemOption.findObject (FoldersListSettings.class, true);
    }
}
