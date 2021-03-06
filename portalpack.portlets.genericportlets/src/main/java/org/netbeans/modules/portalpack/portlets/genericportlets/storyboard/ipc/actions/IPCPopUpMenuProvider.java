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
package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.actions;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.ipc.IPCGraphScene;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Satyaranjan
 */
public class IPCPopUpMenuProvider implements PopupMenuProvider, ActionListener {

    private JPopupMenu menu;
    private static final String ACTION_RESET = "Reset"; //NOI18N

    private IPCGraphScene scene;

    public IPCPopUpMenuProvider(IPCGraphScene scene) {
        this.scene = scene;
        menu = new JPopupMenu(NbBundle.getMessage(IPCPopUpMenuProvider.class, "MENU_POP_UP"));
        JMenuItem item;

        item = new JMenuItem(NbBundle.getMessage(IPCPopUpMenuProvider.class, "MENU_RESET_STORYBOARD"));
        item.setActionCommand(ACTION_RESET);
        item.addActionListener(this);
        item.setBackground(Color.WHITE);
        menu.add(item);
    }

    public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ACTION_RESET)) {
            scene.resetScene();
        }
    }
}
