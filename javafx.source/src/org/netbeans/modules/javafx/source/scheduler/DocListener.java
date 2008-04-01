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

package org.netbeans.modules.javafx.source.scheduler;

import org.netbeans.api.javafx.source.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenHierarchyEvent;
import org.netbeans.api.lexer.TokenHierarchyListener;
import org.openide.cookies.EditorCookie;
import org.openide.util.WeakListeners;

/**
 * 
 * @author David Strupl (initially copied from Java Source module JavaSource.java)
 */
public class DocListener implements PropertyChangeListener, ChangeListener, TokenHierarchyListener {

    private EditorCookie.Observable ec;
    private TokenHierarchyListener lexListener;
    private volatile Document document;
    JavaFXSource source;

    public DocListener(EditorCookie.Observable ec, JavaFXSource source) {
        super();
        this.source = source;
        assert ec != null;
        this.ec = ec;
        this.ec.addPropertyChangeListener(WeakListeners.propertyChange(this, this.ec));
        Document doc = ec.getDocument();
        if (doc != null) {
            TokenHierarchy th = TokenHierarchy.get(doc);
            th.addTokenHierarchyListener(lexListener = WeakListeners.create(TokenHierarchyListener.class, this, th));
            document = doc;
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (EditorCookie.Observable.PROP_DOCUMENT.equals(evt.getPropertyName())) {
            Object old = evt.getOldValue();
            if (old instanceof Document && lexListener != null) {
                TokenHierarchy th = TokenHierarchy.get((Document) old);
                th.removeTokenHierarchyListener(lexListener);
                lexListener = null;
            }
            Document doc = ec.getDocument();
            if (doc != null) {
                TokenHierarchy th = TokenHierarchy.get(doc);
                th.addTokenHierarchyListener(lexListener = WeakListeners.create(TokenHierarchyListener.class, this, th));
                this.document = doc;
                source.resetState(true, false);
            } else {
                //reset document
                this.document = doc;
            }
        }
    }

    public void stateChanged(ChangeEvent e) {
        source.resetState(true, false);
    }

    public void tokenHierarchyChanged(TokenHierarchyEvent evt) {
        source.resetState(true, true);
    }
    
    public Document getDocument() {
        return document;
    }
}