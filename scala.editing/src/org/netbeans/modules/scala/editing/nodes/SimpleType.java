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
package org.netbeans.modules.scala.editing.nodes;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.HtmlFormatter;

/**
 *
 * @author dcaoyuan
 */
public class SimpleType extends TypeRef {

    private List<String> annotations;
    private List<List<TypeRef>> typeArgsList;

    public SimpleType(Token idToken, ElementKind kind) {
        super(idToken, kind);
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setTypeArgsList(List<List<TypeRef>> typeArgsList) {
        this.typeArgsList = typeArgsList;
    }

    public List<List<TypeRef>> getTypeArgsList() {
        return typeArgsList == null ? Collections.<List<TypeRef>>emptyList() : typeArgsList;
    }

    @Override
    public String getName() {
        return super.getName() + getTypeArgsName();
    }       
    
    protected String getTypeArgsName() {
        StringBuilder sb = new StringBuilder();
        for (List<TypeRef> typeArgs : getTypeArgsList()) {
            sb.append("[");
            if (typeArgs.size() == 0) {
                // wildcard
                sb.append("_");
            } else {
                for (Iterator<TypeRef> itr = typeArgs.iterator(); itr.hasNext();) {
                    sb.append(itr.next().getName());
                    if (itr.hasNext()) {
                        sb.append(", ");
                    }
                }
            }
            sb.append("]");
        }
        return sb.toString();
    }

    public void htmlFormatTypeArgs(HtmlFormatter formatter) {
        for (List<TypeRef> typeArgs : getTypeArgsList()) {
            formatter.appendText("[");
            if (typeArgs.size() == 0) {
                // wildcard
                formatter.appendText("_");
            } else {
                for (Iterator<TypeRef> itr = typeArgs.iterator(); itr.hasNext();) {
                    itr.next().htmlFormat(formatter);
                    if (itr.hasNext()) {
                        formatter.appendText(", ");
                    }
                }
            }
            formatter.appendText("]");
        }
    }
}