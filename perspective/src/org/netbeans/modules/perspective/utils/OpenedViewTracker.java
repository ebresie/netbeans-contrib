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

/*
 * OpenedViewTracker.java
 *
 */

package org.netbeans.modules.perspective.utils;

import java.util.Set;
import org.netbeans.modules.perspective.views.Perspective;
import org.netbeans.modules.perspective.views.View;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Anuradha G
 */
public class OpenedViewTracker {

    WindowManager windowManager = WindowManager.getDefault();

    public OpenedViewTracker(Perspective perspective) {
        Set<TopComponent> opened = windowManager.getRegistry().getOpened();
        for (TopComponent topComponent : opened) {
            String tcId = windowManager.findTopComponentID(topComponent);
            View view = perspective.findView(tcId);
            if (windowManager.isEditorTopComponent(topComponent)) {
                continue;
            }
            if (view == null) {
                String modeName = windowManager.findMode(topComponent).getName();
                perspective.addComponent( tcId,modeName, true);
            } else {
                view.setOpen(true);
            }
        }
    }
}