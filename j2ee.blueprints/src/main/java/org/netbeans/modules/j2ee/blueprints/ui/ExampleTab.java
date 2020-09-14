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

package org.netbeans.modules.j2ee.blueprints.ui;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

import org.netbeans.modules.j2ee.blueprints.catalog.bpcatalogxmlparser.Nbcategory;
import org.openide.filesystems.FileUtil;

/**
 * Tab Panel containing information about sample code for a solutions
 * catalog entry.
 *
 * @author Mark Roth
 */
public class ExampleTab 
    extends BluePrintsTabPanel 
{
    /** Creates new form ExampleTabPanel */
    public ExampleTab(BluePrintsPanel bluePrintsPanel) {
        super(bluePrintsPanel);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        launchExampleText = new javax.swing.JTextPane();
        installBtn = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBackground(java.awt.Color.white);
        launchExampleText.setEditable(false);
        launchExampleText.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/blueprints/ui/Bundle").getString("launchExampleText"));
        launchExampleText.setMargin(new java.awt.Insets(12, 12, 12, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        add(launchExampleText, gridBagConstraints);

        installBtn.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/blueprints/ui/Bundle").getString("launchBtn"));
        installBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installBtnActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(installBtn, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void installBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_installBtnActionPerformed
        installExample();
    }//GEN-LAST:event_installBtnActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton installBtn;
    private javax.swing.JTextPane launchExampleText;
    // End of variables declaration//GEN-END:variables
   
    // Special constants that allow us to preselect a template in the new 
    // project wizard
    private static final String OPEN_SAMPLE_ACTION = 
        "org-netbeans-modules-project-ui-WelcomeScreenHack/" + // NOI18N
        "org-netbeans-modules-project-ui-NewSample.instance";  // NOI18N
    private static final String BUNDLE_PROPERTY_PREFIX = 
        "Samples/Blueprints"; // NOI18N
    private static final String PRESELECT_CATEGORY = 
        "PRESELECT_CATEGORY"; // NOI18N
    private static final String PRESELECT_TEMPLATE =
        "PRESELECT_TEMPLATE"; // NOI18N

    
    public void setScrollPosition(int scrollPosition) {
        // Ignore - no scroll position for this tab.
    }

    public int getScrollPosition() {
        // No scroll position for this tab.
        return 0;
    }
    
    public void updateTab() {
        // Nothing to update.
        // The contents of this panel remain the same regardless of what
        // article is slected.
    }
    
    private void installExample() {
        performAction(OPEN_SAMPLE_ACTION, "");
    }

    private Action findAction (String key) {
        FileObject fo = FileUtil.getConfigFile(key);
        
        if (fo != null && fo.isValid()) {
            try {
                DataObject dob = DataObject.find (fo);
                InstanceCookie ic = 
                    (InstanceCookie) dob.getCookie(InstanceCookie.class);
                
                if (ic != null) {
                    Object instance = ic.instanceCreate();
                    if (instance instanceof Action) {
                        return (Action) instance;
                    }
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
                return null;
            }
        }
        return null;
    }
    
    private boolean performAction(String key, String command) {
        Action a = findAction (key);
        if (a == null) {
            return false;
        }
        Nbcategory category = bluePrintsPanel.getSelectedCategory();
        String categoryId = category.getId();
        if(category.getShowSpec().equals("true")){ //NOI18N
            categoryId = category.getSpec() + "/" + categoryId; // NOI18N
        }     
        a.putValue(PRESELECT_CATEGORY, BUNDLE_PROPERTY_PREFIX + "/" // NOI18N
            + categoryId);
        a.putValue(PRESELECT_TEMPLATE, bluePrintsPanel.getExampleId());
        
        ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
            command);
        try {
            a.actionPerformed(ae);
            return true;
        } catch (Exception e) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            return false;
        }
    }
}