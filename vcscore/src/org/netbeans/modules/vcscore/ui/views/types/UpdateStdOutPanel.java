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

package org.netbeans.modules.vcscore.ui.views.types;

/**
 *
 * @author  mkleint
 * @version
 */

import org.netbeans.modules.vcscore.ui.views.*;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.loaders.*;
import javax.swing.text.*;
import javax.swing.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.File;
import java.util.*;
import org.openide.util.NbBundle;
import javax.accessibility.*;
import org.openide.text.CloneableEditorSupport;

public class UpdateStdOutPanel extends SingleNodeView  {

    public static final String REPOSITORY_FILENAME = "REPOSITORY_FILENAME"; //NOI18N
    public static final String REPOSITORY_REVISION = "REPOSITORY_REVISION"; //NOI18N
    public static final String PIPED_FILE = "PIPED_FILE"; //NOI18N
    
    public static final String TYPE = "PIPED_UPDATE"; //NOI18N

    private FileVcsInfo currentInfo;
    
    private FileVcsInfo clearInfo;
    
    
    /** Creates new form UpdateStdOutPanel */
    public UpdateStdOutPanel() {
        initComponents ();
        initAccessibility();
        epFile.setEditable(false);
        initClearInfo();
    }
    
    private void initClearInfo() {
        clearInfo = FileVcsInfoFactory.createBlankFileVcsInfo(UpdateStdOutPanel.TYPE, new File(""));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        pnlHeader = new javax.swing.JPanel();
        lblFileName = new javax.swing.JLabel();
        txFileName = new javax.swing.JLabel();
        lblRevNumber = new javax.swing.JLabel();
        txRevNumber = new javax.swing.JLabel();
        lblRevFile = new javax.swing.JLabel();
        txRevFile = new javax.swing.JTextField();
        spFile = new javax.swing.JScrollPane();
        epFile = new javax.swing.JEditorPane();

        setLayout(new java.awt.GridBagLayout());

        pnlHeader.setLayout(new java.awt.GridBagLayout());

        lblFileName.setText(org.openide.util.NbBundle.getBundle(UpdateStdOutPanel.class).getString("UpdateStdOutPanel.lblFileName.text"));
        lblFileName.setLabelFor(txFileName);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        pnlHeader.add(lblFileName, gridBagConstraints);

        txFileName.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        pnlHeader.add(txFileName, gridBagConstraints);

        lblRevNumber.setText(org.openide.util.NbBundle.getBundle(UpdateStdOutPanel.class).getString("UpdateStdOutPanel.lblRevNumber.text"));
        lblRevNumber.setLabelFor(txRevNumber);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlHeader.add(lblRevNumber, gridBagConstraints);

        txRevNumber.setText("jLabel4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHeader.add(txRevNumber, gridBagConstraints);

        lblRevFile.setText(org.openide.util.NbBundle.getBundle(UpdateStdOutPanel.class).getString("UpdateStdOutPanel.lblRevFile.text"));
        lblRevFile.setLabelFor(txRevFile);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlHeader.add(lblRevFile, gridBagConstraints);

        txRevFile.setEditable(false);
        txRevFile.setText("jTextField1");
        txRevFile.setMinimumSize(new java.awt.Dimension(60, 20));
        txRevFile.setPreferredSize(new java.awt.Dimension(400, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHeader.add(txRevFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 11);
        add(pnlHeader, gridBagConstraints);

        spFile.setViewportView(epFile);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(spFile, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblFileName;
    private javax.swing.JScrollPane spFile;
    private javax.swing.JLabel lblRevNumber;
    private javax.swing.JLabel lblRevFile;
    private javax.swing.JLabel txRevNumber;
    private javax.swing.JTextField txRevFile;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JLabel txFileName;
    private javax.swing.JEditorPane epFile;
    // End of variables declaration//GEN-END:variables

    private void initAccessibility() {
        
        AccessibleContext context = this.getAccessibleContext();
        context.setAccessibleName(NbBundle.getMessage(UpdateStdOutPanel.class, "ACSD_UpdateStdOutPanel")); //NOI18N
        
        context = epFile.getAccessibleContext();
        context.setAccessibleName(NbBundle.getMessage(UpdateStdOutPanel.class, "ACSD_UpdateStdOutPanel.epFile")); //NOI18N
        
    }       
    
    public void setData(FileVcsInfo info, FileObject fo) {
        currentInfo = info;
        txFileName.setText(info.getFile().getAbsolutePath());
        txRevNumber.setText(info.getAttributeNonNull(REPOSITORY_REVISION));
        txRevFile.setText(info.getAttributeNonNull(REPOSITORY_FILENAME));
        //setting up editor kit
        File tempFile = (File)info.getAttribute(PIPED_FILE);
        if (tempFile != null && tempFile.exists()) {
            String mime = getMIMEType(fo);
            EditorKit kit = CloneableEditorSupport.getEditorKit(mime);
//            D.deb("MIME = "+mime+": I have kit = "+kit); // NOI18N
            epFile.setEditorKit(kit);
            Document doc = kit.createDefaultDocument();
            try {
                URL flURL = tempFile.toURL();
                try {
                    kit.read(flURL.openStream(), doc, 0);
                    kit.install(epFile);
                    epFile.setDocument(doc);
                } catch (javax.swing.text.BadLocationException e) {
//                    E.err("BadLocationException"); // NOI18N
                    return;
                } catch (IOException ex) {
//                    E.err("IOException"); // NOI18N
                    return;
                }
                
            } catch (MalformedURLException exc) {
//                E.err("malformed URL"); // NOI18N
            }
            epFile.setDocument(doc);
        }
        
    }
    
    private String getMIMEType(FileObject info) {
/*        File infoFile = info.getFile();
        if (infoFile == null) return "text/plain"; // NOI18N
        String tempStr = infoFile.getName();
        String extension = null;
        D.deb("tempStr=" + tempStr); // NOI18N
        int ind = tempStr.indexOf('.');
        if (ind > 0) {
            extension = tempStr.substring(ind + 1);
        }
        D.deb("extension=" + extension); // NOI18N
        if (extension == null) {
            extension = "text/plain"; // NOI18N
        } 
 */
        if (info == null) {
            return "text/plain"; // NOI18N
        }
        String type = FileUtil.getMIMEType(info);
        if (type == null) {
            type = "text/plain"; // NOI18N
        }
        return type;
    }
    
  /**
   * Overriding the SingleNodeView method, to refresh the display
   */
  public void setContextNode(Node node) {
      super.setContextNode(node);
      Node infoNode = getContextNode();
      if (infoNode != null) {
          FileVcsInfo info = (FileVcsInfo)infoNode.getCookie(FileVcsInfo.class);
          if (info != null && info.getType().equals(TYPE)) {
              DataObject dob = (DataObject)infoNode.getCookie(DataObject.class);
              FileObject fo = null;
              if (dob != null) {
                  Iterator it = dob.files().iterator();
                  while (it.hasNext()) {
                      FileObject fobject = (FileObject)it.next();
                      if (fobject.getNameExt().equals(info.getFile().getName())) {
                          fo = fobject;
                          break;
                      }
                  }
              }
              setData(info, fo);
          } else {
              setData(clearInfo, null);
          }
      } else {
          setData(clearInfo, null);
      }
  }
    
}
