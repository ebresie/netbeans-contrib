/*
 * PreferencesUI.java
 *
 * Created on February 1, 2009, 1:25 PM
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.ui;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author  satyaranjan
 */
public class PreferencesUI extends javax.swing.JDialog {

    private static String SERVICE_JAR_NAME = "service.jar.name";
    private static String COPY_JAR_TO_SERVERCLASSPATH = "copy.jar.to.serverclasspath";
    private Properties props;
    private File serviceProps;
    
    /** Creates new form PreferencesUI */
    public PreferencesUI(FileObject serviceXml) {
        super(WindowManager.getDefault().getMainWindow(), true);
        Frame parent = WindowManager.getDefault().getMainWindow();
        initComponents();
        setLocation(parent.getX() +
                (parent.getWidth() - getWidth()) / 2, parent.getY() + 
                (parent.getHeight() - getHeight()) / 2);
        
        getRootPane().setDefaultButton(cancelButton);
        initData(serviceXml);
        
        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        serviceJarLabel = new javax.swing.JLabel();
        serviceJarTf = new javax.swing.JTextField();
        copyJarCB = new javax.swing.JCheckBox();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(PreferencesUI.class, "LBL_Preferences")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PreferencesUI.class, "LBL_TTL_PREFERENCES"))); // NOI18N

        serviceJarLabel.setText(org.openide.util.NbBundle.getMessage(PreferencesUI.class, "PreferencesUI.serviceJarLabel.text")); // NOI18N

        copyJarCB.setText(org.openide.util.NbBundle.getMessage(PreferencesUI.class, "PreferencesUI.copyJarCB.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(serviceJarLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(serviceJarTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(copyJarCB, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(136, 136, 136)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(serviceJarTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(serviceJarLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(copyJarCB)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        saveButton.setText(org.openide.util.NbBundle.getMessage(PreferencesUI.class, "PreferencesUI.saveButton.text")); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(PreferencesUI.class, "PreferencesUI.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(233, 233, 233)
                        .add(saveButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(saveButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initData(FileObject serviceXml) {

        Project proj = FileOwnerQuery.getOwner(serviceXml);
        serviceProps = new File(FileUtil.toFile(serviceXml).getParent(),
                "." + serviceXml.getName() + ".properties");

        if (serviceProps.exists()) {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(serviceProps);

                props = new Properties();
                props.load(fin);

                String sjn = props.getProperty(SERVICE_JAR_NAME);
                if (sjn == null || sjn.trim().length() == 0) {
                    serviceJarTf.setText(proj.getProjectDirectory().getName() + "-service.jar");
                } else {
                    serviceJarTf.setText(sjn);
                }
                
                boolean copyJarToServerCp = 
                        Boolean.parseBoolean(props.getProperty(COPY_JAR_TO_SERVERCLASSPATH));
                
                copyJarCB.setSelected(copyJarToServerCp);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    fin.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            serviceJarTf.setText(proj.getProjectDirectory().getName() + "-service.jar");
        }
    }
    
private boolean validateFileName() {
    String sjn = serviceJarTf.getText();
    
    if(sjn == null || sjn.trim().length() == 0) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                NbBundle.getMessage(PreferencesUI.class, "MSG_NOT_A_VALID_FILE_NAME"),
                    NotifyDescriptor.ERROR_MESSAGE));
        return false;
    }
    
    if(!sjn.endsWith(".jar") && !sjn.endsWith(".JAR")) {
        
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                NbBundle.getMessage(PreferencesUI.class, "MSG_FILENAME_NOT_END_WITH_JAR"),
                    NotifyDescriptor.ERROR_MESSAGE));
        return false;
    }
    
    sjn = sjn.substring(0, sjn.lastIndexOf("."));
    if(!validateString(sjn,false)) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                NbBundle.getMessage(PreferencesUI.class, "MSG_NOT_A_VALID_FILE_NAME") + ": " + sjn + ".jar",
                    NotifyDescriptor.ERROR_MESSAGE));
        return false;
    }
    
    return true;
}

private boolean validateString(String value,boolean allowSpace) {
        if(value == null || value.trim().length() == 0){
            return false;
        }
        
        for(int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            
            if(!allowSpace && c == ' ')
                return false;
            if(!Character.isLetterOrDigit(c) && !(c == '_') && !(c == '-')){
                return false;
            }
        }
        return true;
    }

private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
// TODO add your handling code here
   
    if(!validateFileName())
        return;
    if(props != null) {

            FileOutputStream fout = null;
            try {
                String sjn = serviceJarTf.getText();
                props.setProperty(SERVICE_JAR_NAME, sjn);
                
                boolean isCopyToServerCp = copyJarCB.isSelected();
                if(isCopyToServerCp)
                    props.setProperty(COPY_JAR_TO_SERVERCLASSPATH,Boolean.toString(isCopyToServerCp));
                else
                    props.remove(COPY_JAR_TO_SERVERCLASSPATH);
                
                fout = new FileOutputStream(serviceProps);
                props.store(fout, sjn);
                fout.flush();
                
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    fout.close();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
    } else {

            FileOutputStream fout = null;
            try {
                String sjn = serviceJarTf.getText();
                props = new Properties();
                props.setProperty(SERVICE_JAR_NAME, sjn);
                fout = new FileOutputStream(serviceProps);
                props.store(fout, sjn);
                fout.flush();
                
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    fout.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
    }
    
    dispose();
}//GEN-LAST:event_saveButtonActionPerformed

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
// TODO add your handling code here:
    dispose();
}//GEN-LAST:event_cancelButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
      
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox copyJarCB;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel serviceJarLabel;
    private javax.swing.JTextField serviceJarTf;
    // End of variables declaration//GEN-END:variables

}