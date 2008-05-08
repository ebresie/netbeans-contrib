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

import org.netbeans.api.lexer.Token;
import org.netbeans.modules.gsf.api.ElementKind;

/**
 *
 * @author Caoyuan Deng
 */
public class FieldRef extends AstRef {

    /** base may be AstExpr, FunRef, FieldRef, IdRef etc */
    private AstElement base;
    private Id field;
    private String retType;

    public FieldRef(Token idToken) {
        super(null, idToken, ElementKind.FIELD);
    }

    public void setBase(AstElement base) {
        this.base = base;
    }

    public AstElement getBase() {
        return base;
    }

    public void setField(Id field) {
        this.field = field;
    }
    
    public Id getField() {
        return field;
    }

    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        if (base != null) {
            TypeRef baseType = base.getType();
            if (baseType != null) {
                sb.append(baseType.getName());
            }
        }
        sb.append(field.getName());
        return sb.toString();
    }

    public void setRetType(String retType) {
        this.retType = retType;
    }

    public String getRetType() {
        return retType;
    }
    
}
