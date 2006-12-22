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
package org.netbeans.modules.java.tools.navigation;

import org.openide.windows.WindowManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;


/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
class ResizablePopup {
    private static JDialog javaStructureDialog;

    static JDialog getJavaFileStructureDialog() {
        if (javaStructureDialog == null) {
            javaStructureDialog = new JDialog(WindowManager.getDefault()
                                                           .getMainWindow(),
                    "", false) {
                        public void setVisible(boolean visible) {
                            super.setVisible(visible);

                            if (!visible) {
                                getContentPane().removeAll();
                            }
                        }
                    };
            javaStructureDialog.setUndecorated(true);
            javaStructureDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            javaStructureDialog.getContentPane().setLayout(new BorderLayout());

            Dimension dimensions = Toolkit.getDefaultToolkit().getScreenSize();
            javaStructureDialog.setBounds(((dimensions.width / 2) - 410),
                ((dimensions.height / 2) - 300), 820, 600);
        }

        return javaStructureDialog;
    }
}
