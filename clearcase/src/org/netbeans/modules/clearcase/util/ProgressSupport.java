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

package org.netbeans.modules.clearcase.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.clearcase.Clearcase;
import org.openide.util.Cancellable;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tomas Stupka
 */
public abstract class ProgressSupport implements Runnable, Cancellable {

    private RequestProcessor.Task task;
    private String displayName;
    private ProgressHandle progressHandle;
    private boolean canceled = false;
    private RequestProcessor rp;
    
    public ProgressSupport(RequestProcessor rp, String displayName) {
        this(rp, displayName, null);
    }
    
    public ProgressSupport(RequestProcessor rp, String displayName, JButton cancel) {
        this.displayName = displayName;
        this.rp = rp;
        if(cancel != null) {
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancel();
                }
            });
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void schedule(int delay) {
        if(task == null) {
            task = rp.create(this);
        }      
        task.schedule(delay);
    }
    
    public void start() {                        
        task = rp.post(this);        
    }

    public JComponent getProgressComponent() {
        return ProgressHandleFactory.createProgressComponent(getProgressHandle());                                            
    }
        
    private void startProgress() {    
        getProgressHandle().start();
        Clearcase.LOG.fine("Progress started: " + displayName);
    }
    
    private ProgressHandle getProgressHandle() {
        if(progressHandle == null) {
            progressHandle = ProgressHandleFactory.createHandle(displayName, this);
        }
        return progressHandle;
    }
    
    public void run() {                
        try {            
            startProgress();
            perform();               
        } finally {            
            finnishProgress();                 
        }
    }

    protected abstract void perform();

    protected void finnishProgress() {
        getProgressHandle().finish();
        progressHandle = null;
        if(isCanceled()) {
            Clearcase.LOG.fine("Progress canceled: " + displayName);
        } else {
            Clearcase.LOG.fine("Progress finnished: " + displayName);   
        }        
    }
    
    public boolean cancel() {
        canceled = true;
        return task.cancel();
    }
}
