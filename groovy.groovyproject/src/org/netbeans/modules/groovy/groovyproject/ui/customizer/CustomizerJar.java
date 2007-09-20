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

package org.netbeans.modules.groovy.groovyproject.ui.customizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.modules.groovy.groovyproject.GroovyProjectUtil;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** Customizer for general project attributes.
 *
 * @author  phrebejk
 */
public class CustomizerJar extends JPanel implements GroovyCustomizer.Panel, HelpCtx.Provider {
    
    private VisualPropertySupport vps;
        
    /** Creates new form CustomizerCompile */
    public CustomizerJar(  GroovyProjectProperties groovyProperties  ) {
        initComponents();        
        vps = new VisualPropertySupport( groovyProperties );
    }
    
    public void initValues() {
        
        vps.register( jTextFieldDistDir, GroovyProjectProperties.DIST_JAR );
        vps.register( jTextFieldExcludes, GroovyProjectProperties.BUILD_CLASSES_EXCLUDES );
        vps.register( jCheckBoxCommpress, GroovyProjectProperties.JAR_COMPRESS ); 
    } 
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx( CustomizerJar.class );
    }
        
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelDistDir = new javax.swing.JLabel();
        jTextFieldDistDir = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldExcludes = new javax.swing.JTextField();
        jCheckBoxCommpress = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EtchedBorder());
        org.openide.awt.Mnemonics.setLocalizedText(jLabelDistDir, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_CustomizeJar_DistDir_JTextField"));
        jLabelDistDir.setLabelFor(jTextFieldDistDir);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jLabelDistDir, gridBagConstraints);

        jTextFieldDistDir.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
        add(jTextFieldDistDir, gridBagConstraints);
        jTextFieldDistDir.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJar.class).getString("AD_jTextFieldDistDir"));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_CustomizeJar_Excludes_JTextField"));
        jLabel2.setLabelFor(jTextFieldExcludes);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jTextFieldExcludes, gridBagConstraints);
        jTextFieldExcludes.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJar.class).getString("AD_jTextFieldExcludes"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxCommpress, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_CustomizeJar_Commpres_JCheckBox"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        add(jCheckBoxCommpress, gridBagConstraints);
        jCheckBoxCommpress.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJar.class).getString("AD_jCheckBoxCompress"));

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxCommpress;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelDistDir;
    private javax.swing.JTextField jTextFieldDistDir;
    private javax.swing.JTextField jTextFieldExcludes;
    // End of variables declaration//GEN-END:variables
        
    // Storing methods ---------------------------------------------------------
    
    /** Stores the value according to the src component into the helper
     */
    private void store( JComponent src ) {
    
        /*
        if ( src == jTextFieldSrcDir ) {
            groovyProperties.put( J2SEProjectProperties.SRC_DIR, jTextFieldSrcDir.getText() );
        }
        else if ( src == jTextFieldBuildDir ) {
            groovyProperties.put( J2SEProjectProperties.BUILD_DIR, jTextFieldBuildDir.getText() );
        }
        else if ( src == jListClasspath ) {
            
            List elements = new ArrayList( classpathModel.size() );
            
            for ( Enumeration e = classpathModel.elements(); e.hasMoreElements(); ) {
                elements.add( e.nextElement() );
            }
            groovyProperties.put( J2SEProjectProperties.JAVAC_CLASSPATH, elements );
        }
        
        assert true : "CustomizerCompile - Unknown component : " + src; // NOI18N
        */
    } 
    
    
    
    // Private methods for classpath data manipulation -------------------------
        
}
