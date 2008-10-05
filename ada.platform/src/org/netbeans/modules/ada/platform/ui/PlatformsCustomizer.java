/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ada.platform.ui;

import java.io.File;
import java.awt.Dialog;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.ada.platform.AdaException;
import org.netbeans.api.ada.platform.AdaPlatform;
import org.netbeans.api.ada.platform.AdaPlatformManager;
import org.netbeans.modules.ada.platform.AdaPreferences;
import org.netbeans.modules.ada.platform.ui.PathListModel;
import org.netbeans.modules.ada.platform.ui.PlatformListModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * 
 * @author Andrea Lucarelli
 */
public class PlatformsCustomizer extends JPanel {

    private static final String LAST_PLATFORM_DIRECTORY = "lastPlatformDirectory"; // NOI18N

    /** Creates new form AdaPlatformCustomizer */
    public PlatformsCustomizer() {
        manager = AdaPlatformManager.getInstance();
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        platformsListScrollPanel = new javax.swing.JScrollPane();
        PlatformList = new javax.swing.JList();
        paltformsListTitleLabel = new javax.swing.JLabel();
        newButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        autoDetectButton = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();
        mainPanel = new javax.swing.JPanel();
        platfromNameLabel = new javax.swing.JLabel();
        platformName = new javax.swing.JTextField();
        compilerCommandLabel = new javax.swing.JLabel();
        compilerCommand = new javax.swing.JTextField();
        otherInfoLabel = new javax.swing.JLabel();
        platformInfoTextField = new javax.swing.JTextField();
        adaPathPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        adaPath = new javax.swing.JList();
        addPath = new javax.swing.JButton();
        removePath = new javax.swing.JButton();
        moveUpPath = new javax.swing.JButton();
        moveDownPath = new javax.swing.JButton();
        makeDefaultButton = new javax.swing.JButton();

        jLabel6.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.jLabel6.text")); // NOI18N

        PlatformList.setModel(platformListModel);
        PlatformList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        PlatformList.setCellRenderer(new PlatformListCellRenderer());
        PlatformList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PlatformListMouseClicked(evt);
            }
        });
        PlatformList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                PlatformListValueChanged(evt);
            }
        });
        platformsListScrollPanel.setViewportView(PlatformList);

        paltformsListTitleLabel.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.paltformsListTitleLabel.text")); // NOI18N

        newButton.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.newButton.text")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        removeButton.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.removeButton.text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        autoDetectButton.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.autoDetectButton.text")); // NOI18N
        autoDetectButton.setEnabled(false);

        platfromNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        platfromNameLabel.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.platfromNameLabel.text")); // NOI18N

        platformName.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.platformName.text_1")); // NOI18N

        compilerCommandLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        compilerCommandLabel.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.compilerCommandLabel.text")); // NOI18N

        compilerCommand.setEditable(false);
        compilerCommand.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.compilerCommand.text")); // NOI18N

        otherInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        otherInfoLabel.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.otherInfoLabel.text")); // NOI18N

        platformInfoTextField.setEditable(false);
        platformInfoTextField.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.platformInfoTextField.text")); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(mainPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .add(compilerCommandLabel))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .add(platfromNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(otherInfoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(compilerCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(platformName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                        .add(30, 30, 30))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(platformInfoTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(platfromNameLabel)
                    .add(platformName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(platformInfoTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(otherInfoLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(compilerCommandLabel)
                    .add(compilerCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(193, Short.MAX_VALUE))
        );

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.mainPanel.TabConstraints.tabTitle_1"), mainPanel); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.jLabel5.text_1")); // NOI18N

        adaPath.setModel(adaPathModel);
        adaPath.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(adaPath);

        addPath.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.addPath.text")); // NOI18N
        addPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPathActionPerformed(evt);
            }
        });

        removePath.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.removePath.text")); // NOI18N
        removePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePathActionPerformed(evt);
            }
        });

        moveUpPath.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.moveUpPath.text")); // NOI18N
        moveUpPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpPathActionPerformed(evt);
            }
        });

        moveDownPath.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.moveDownPath.text")); // NOI18N
        moveDownPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownPathActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout adaPathPanelLayout = new org.jdesktop.layout.GroupLayout(adaPathPanel);
        adaPathPanel.setLayout(adaPathPanelLayout);
        adaPathPanelLayout.setHorizontalGroup(
            adaPathPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adaPathPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(adaPathPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(adaPathPanelLayout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(adaPathPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(moveDownPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .add(moveUpPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .add(removePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .add(addPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)))
                    .add(jLabel5))
                .addContainerGap())
        );
        adaPathPanelLayout.setVerticalGroup(
            adaPathPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adaPathPanelLayout.createSequentialGroup()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(adaPathPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, adaPathPanelLayout.createSequentialGroup()
                        .add(addPath)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removePath)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(moveUpPath)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(moveDownPath))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.adaPathPanel.TabConstraints.tabTitle_1"), adaPathPanel); // NOI18N

        makeDefaultButton.setText(org.openide.util.NbBundle.getMessage(PlatformsCustomizer.class, "PlatformsCustomizer.makeDefaultButton.text")); // NOI18N
        makeDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeDefaultButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, makeDefaultButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, autoDetectButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(newButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, paltformsListTitleLabel)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, platformsListScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(paltformsListTitleLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(platformsListScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(removeButton)
                            .add(newButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(autoDetectButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(makeDefaultButton))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void loadPlatform() {
        platformName.setText(adaPlatform.getName());
        platformInfoTextField.setText(adaPlatform.getInfo());
        compilerCommand.setText(adaPlatform.getInterpreterCommand());
        adaPathModel.setModel(adaPlatform.getAdaCompilerPath());
    }

    private void clearPlatform() {
        platformName.setText("");
        platformInfoTextField.setText("");
        compilerCommand.setText("");
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        JFileChooser fc = new JFileChooser(AdaPreferences.getPreferences().get(LAST_PLATFORM_DIRECTORY, ""));
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileFilter(new FileFilter() {

            public boolean accept(File f) {
                return f.isDirectory(); // NOI18N
            }

            public String getDescription() {
                return getMessage("AdaPlatformCustomizer.adaPlatform");
            }
        });
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (file != null) {
                file = FileUtil.normalizeFile(file);
                FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(file));
                if (fo != null) {
                    try {
                        adaPlatform = manager.findPlatformProperties(fo);
                        loadPlatform();
                        platformListModel.refresh();
                        platformName.setEditable(true);
                    } catch (AdaException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }

}//GEN-LAST:event_newButtonActionPerformed

    private void PlatformListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_PlatformListValueChanged
        if (adaPlatform != null) {
            updatePlatform();
        }
        adaPlatform = manager.getPlatform(
                (String) platformListModel.getElementAt(
                PlatformList.getSelectedIndex()));
        loadPlatform();
        platformName.setEditable(false);
    }//GEN-LAST:event_PlatformListValueChanged

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (PlatformList.getSelectedIndex() != -1) {
            manager.removePlatform(
                    (String) platformListModel.getElementAt(
                    PlatformList.getSelectedIndex()));
            platformListModel.refresh();
            clearPlatform();
        }
}//GEN-LAST:event_removeButtonActionPerformed

    private void makeDefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeDefaultButtonActionPerformed
        if (PlatformList.getSelectedIndex() != -1) {
            manager.setDefaultPlatform((String) platformListModel.getElementAt(PlatformList.getSelectedIndex()));
            platformListModel.refresh();
        }
}//GEN-LAST:event_makeDefaultButtonActionPerformed

    private void moveDownPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownPathActionPerformed
        adaPathModel.moveDown(adaPath.getSelectedIndex());
}//GEN-LAST:event_moveDownPathActionPerformed

    private void moveUpPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpPathActionPerformed
        adaPathModel.moveUp(adaPath.getSelectedIndex());
}//GEN-LAST:event_moveUpPathActionPerformed

    private void removePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePathActionPerformed
        adaPathModel.remove(adaPath.getSelectedIndex());
}//GEN-LAST:event_removePathActionPerformed

    private void addPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPathActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.setFileHidingEnabled(false);
        fc.setDialogTitle("Select Ada Lib Directory");
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String cmd = fc.getSelectedFile().getAbsolutePath();
            adaPathModel.add(cmd);
        }
}//GEN-LAST:event_addPathActionPerformed

    private void PlatformListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PlatformListMouseClicked
        if (PlatformList.getSelectedIndex() != -1) {
            String platform = (String) platformListModel.getElementAt(PlatformList.getSelectedIndex());
            this.removeButton.setEnabled(isDefaultPLatform(platform));
            this.makeDefaultButton.setEnabled(isDefaultPLatform(platform));
        }
    }//GEN-LAST:event_PlatformListMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList PlatformList;
    private javax.swing.JList adaPath;
    private javax.swing.JPanel adaPathPanel;
    private javax.swing.JButton addPath;
    private javax.swing.JButton autoDetectButton;
    private javax.swing.JTextField compilerCommand;
    private javax.swing.JLabel compilerCommandLabel;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton makeDefaultButton;
    private javax.swing.JButton moveDownPath;
    private javax.swing.JButton moveUpPath;
    private javax.swing.JButton newButton;
    private javax.swing.JLabel otherInfoLabel;
    private javax.swing.JLabel paltformsListTitleLabel;
    private javax.swing.JTextField platformInfoTextField;
    private javax.swing.JTextField platformName;
    private javax.swing.JScrollPane platformsListScrollPanel;
    private javax.swing.JLabel platfromNameLabel;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton removePath;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
    private AdaPlatformManager manager;
    private AdaPlatform adaPlatform;
    private PathListModel adaPathModel = new PathListModel();
    private PlatformListModel platformListModel = new PlatformListModel();

    public static void showPlatformManager() {
        PlatformsCustomizer customizer = new PlatformsCustomizer();
        JButton closeButton = new JButton();
        closeButton.getAccessibleContext().setAccessibleDescription(getMessage("AdaPlatformCustomizer.closeButton.AccessibleContext.accessibleName"));
        Mnemonics.setLocalizedText(closeButton,
                NbBundle.getMessage(PlatformsCustomizer.class, "CTL_Close"));
        DialogDescriptor descriptor = new DialogDescriptor(
                customizer,
                getMessage("CTL_AdaPlatformCustomizer_Title"), // NOI18N
                true,
                new Object[]{closeButton},
                closeButton,
                DialogDescriptor.DEFAULT_ALIGN,
                new HelpCtx(PlatformsCustomizer.class),
                null);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(descriptor);
        dlg.setVisible(true);
        AdaPlatformManager.getInstance().save();
        dlg.dispose();
    }

    private static String getMessage(String key) {
        return NbBundle.getMessage(PlatformsCustomizer.class, key);
    }

    private void updatePlatform() {
        if (!adaPlatform.getName().equals(platformName.getText())) {
            manager.removePlatform(adaPlatform.getName());
            adaPlatform.setName(platformName.getText());
        }

        adaPlatform.setInfo(platformInfoTextField.getText());
        adaPlatform.setCompilerCommand(compilerCommand.getText());
        adaPlatform.setAdaCompilerPath(adaPathModel.getModel());
        manager.addPlatform(adaPlatform);
    }

    private boolean isDefaultPLatform (String platform) {
        String defaultPlatform = manager.getDefaultPlatform();
        return defaultPlatform!=null && !defaultPlatform.equals(platform);
    }

}
