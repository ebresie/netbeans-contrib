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

package org.netbeans.modules.ada.project.ui;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.netbeans.modules.ada.project.AdaMimeResolver;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Andrea Lucarelli
 */
final class MainModuleChooser extends javax.swing.JPanel {
    
    private final FileObject[] roots;
    private final JButton okButton;
    private final static RequestProcessor RP = new RequestProcessor("MainModuleChooser");   //NOI18N
    
    /** Creates new form MainClassChooser */
    MainModuleChooser (final FileObject[] roots, final JButton okButton) {
        assert roots != null;
        assert okButton != null;
        this.roots = roots;        
        initComponents();
        this.okButton = okButton;
        this.okButton.setEnabled(false);
        ((DefaultListModel)this.mainModules.getModel()).addElement(NbBundle.getMessage(MainModuleChooser.class, "TXT_PleaseWait"));
        RP.post(new Runnable() {
            public void run() {
                initData();
            }
        });        
    }
    
    public String getMainModule () {
        return (String) mainModules.getSelectedValue();
    }
    
    
    private void initData () {
        final List<String> data = new LinkedList<String>();
        for (FileObject root : roots) {
            final Enumeration<? extends FileObject> fos = root.getChildren(true);
            while (fos.hasMoreElements()) {
                final FileObject fo = fos.nextElement();
                if (isAda (fo)) {
                    data.add(FileUtil.getRelativePath(root, fo));
                }
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DefaultListModel lm = (DefaultListModel)mainModules.getModel();
                lm.clear();
                for (String s : data) {
                    lm.addElement(s);
                }
                okButton.setEnabled(true);
            }
        });        
    }
    
    private static boolean isAda (final FileObject fo) {
        if (fo.isFolder() || !fo.isValid() || fo.isVirtual()) {
            return false;
        }
        return AdaMimeResolver.ADA_MIME_TYPE.equals(FileUtil.getMIMEType(fo));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainModules = new javax.swing.JList();

        setPreferredSize(new java.awt.Dimension(300, 300));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(mainModules);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MainModuleChooser.class, "MainModuleChooser.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(jLabel1, gridBagConstraints);

        mainModules.setModel(new DefaultListModel());
        jScrollPane1.setViewportView(mainModules);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList mainModules;
    // End of variables declaration//GEN-END:variables

}
