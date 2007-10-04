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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.logmanagement.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.netbeans.modules.logmanagement.Logger;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class LevelChooserAction extends AbstractAction {

    private static final long serialVersionUID = 1l;
    private Logger logger;
    private JPopupMenu menu = new JPopupMenu();
    private ButtonGroup group = new ButtonGroup();
    private Map<Level, JRadioButtonMenuItem> map = new HashMap<Level, JRadioButtonMenuItem>();
    private boolean wormup;

    public LevelChooserAction(Logger logger) {
        putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(Logger.class, "level"));
        putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(Utilities.loadImage("org/netbeans/modules/logmanagement/resources/level.png", true)));
        this.logger = logger;
        JRadioButtonMenuItem all = new JRadioButtonMenuItem(new LevelAction(Level.ALL));
        menu.add(all);
        group.add(all);
        map.put(Level.ALL, all);

        JRadioButtonMenuItem config = new JRadioButtonMenuItem(new LevelAction(Level.CONFIG));
        menu.add(config);
        group.add(config);
        map.put(Level.CONFIG, config);

        JRadioButtonMenuItem fine = new JRadioButtonMenuItem(new LevelAction(Level.FINE));
        menu.add(fine);
        group.add(fine);
        map.put(Level.FINE, fine);
        JRadioButtonMenuItem finer = new JRadioButtonMenuItem(new LevelAction(Level.FINER));
        menu.add(finer);
        group.add(finer);
        map.put(Level.FINER, finer);
        JRadioButtonMenuItem finest = new JRadioButtonMenuItem(new LevelAction(Level.FINEST));
        menu.add(finest);
        group.add(finest);
        map.put(Level.FINEST, finest);
        JRadioButtonMenuItem info = new JRadioButtonMenuItem(new LevelAction(Level.INFO));
        menu.add(info);
        group.add(info);
        map.put(Level.INFO, info);
        JRadioButtonMenuItem off = new JRadioButtonMenuItem(new LevelAction(Level.OFF));
        menu.add(off);
        group.add(off);
        map.put(Level.OFF, off);
        JRadioButtonMenuItem server = new JRadioButtonMenuItem(new LevelAction(Level.SEVERE));
        menu.add(server);
        group.add(server);
        map.put(Level.SEVERE, server);
        JRadioButtonMenuItem warning = new JRadioButtonMenuItem(new LevelAction(Level.WARNING));
        menu.add(warning);
        group.add(warning);
        map.put(Level.WARNING, warning);
        JRadioButtonMenuItem selected = map.get(Level.parse(logger.getLevel()));
        if(selected!=null)selected.setSelected(true);
    }


    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            JButton button = (JButton) source;
            wormup(button);
            button.setSelected(true);
            Point point = button.getLocationOnScreen();

            menu.setInvoker(button);
            menu.setVisible(true);
            menu.setLocation(point.x+ (button.getWidth()), point.y);
        }
    }

    private synchronized void wormup(final JButton button) {
        if (!wormup) {
            button.setFocusPainted(false);
            menu.addPopupMenuListener(new PopupMenuListener() {

                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    if (button != null) {
                        button.setSelected(true);
                    }
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    if (button != null) {
                        button.setSelected(false);
                    }
                }

                public void popupMenuCanceled(PopupMenuEvent e) {
                    if (button != null) {
                        button.setSelected(false);
                    }
                }
            });
        }
    }

    private class LevelAction extends AbstractAction {

        private Level level;

        public LevelAction(Level level) {
            this.level = level;
             putValue(NAME, level.getLocalizedName());
        }

        public void actionPerformed(ActionEvent e) {
            logger.setLevel(level.getName());
            map.get(level).setSelected(true);
        }
    }
}