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

package org.netbeans.modules.a11ychecker.output;

import java.io.Serializable;
import org.netbeans.modules.a11ychecker.FormBroker;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.Utilities;

/**
 * Top component for a11y result window
 * @author Max Sauer
 */
public final class ResultWindowTopComponent extends TopComponent {
    
    private static ResultWindowTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/modules/a11ychecker/output/a11yIcon.png";	// NOI18N
    private ResultPanel resultPanel;

    public ResultPanel getResultPanel() {
        return resultPanel;
    }
    private static final String PREFERRED_ID = "ResultWindowTopComponent";	// NOI18N
    
    private ResultWindowTopComponent() {
	initComponents2();
	setName(NbBundle.getMessage(ResultWindowTopComponent.class, "CTL_ResultWindowTopComponent"));	// NOI18N
	setToolTipText(NbBundle.getMessage(ResultWindowTopComponent.class, "HINT_ResultWindowTopComponent"));	// NOI18N
	setIcon(Utilities.loadImage(ICON_PATH, true));
    }
    
    private void initComponents2() {
	resultPanel = new org.netbeans.modules.a11ychecker.output.ResultPanel();

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(resultPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, resultPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
        );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 613, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 226, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ResultWindowTopComponent.class, "ResultWindowTopComponent.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ResultWindowTopComponent.class, "ResultWindowTopComponent.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized ResultWindowTopComponent getDefault() {
	if (instance == null) {
	    instance = new ResultWindowTopComponent();
	}
	return instance;
    }
    
    /**
     * Obtain the ResultWindowTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ResultWindowTopComponent findInstance() {
	TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
	if (win == null) {
	    ErrorManager.getDefault().log(ErrorManager.WARNING,
		    "Cannot find ResultWindowTopComponent. It will not be located properly in the window system.");	// NOI18N
	    return getDefault();
	}
	if (win instanceof ResultWindowTopComponent) {
	    return (ResultWindowTopComponent)win;
	}
	ErrorManager.getDefault().log(ErrorManager.WARNING,
		"There seem to be multiple components with the '" + PREFERRED_ID +	// NOI18N
		"' ID. That is a potential source of errors and unexpected behavior.");	// NOI18N
	return getDefault();
    }
    
    public int getPersistenceType() {
	return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentOpened() {
	FormBroker.getDefault().startBroker();
    }
    
    public void componentClosed() {
	FormBroker.getDefault().stopBroker();
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
	return new ResolvableHelper();
    }
    
    protected String preferredID() {
	return PREFERRED_ID;
    }
    
    final static class ResolvableHelper implements Serializable {
	private static final long serialVersionUID = 1L;
	public Object readResolve() {
	    return ResultWindowTopComponent.getDefault();
	}
    }
    
}
