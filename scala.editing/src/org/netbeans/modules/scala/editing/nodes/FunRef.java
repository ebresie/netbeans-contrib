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

import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.ElementKind;

/**
 *
 * @author Caoyuan Deng
 */
public class FunRef extends AstRef {

    /** base may be AstExpr, FunRef, FieldRef, IdRef etc */
    private AstElement base;
    private Id call;
    private List<AstExpr> params;
    private boolean local;
    private String retType;

    public FunRef(Token idToken, ElementKind kind) {
        super(null, idToken, kind);
    }

    public void setBase(AstElement base) {
        this.base = base;
    }

    public AstElement getBase() {
        return base;
    }

    public void setCall(Id call) {
        this.call = call;
    }

    public Id getCall() {
        return call;
    }

    public void setParams(List<AstExpr> params) {
        this.params = params;
    }

    public List<AstExpr> getParams() {
        return params;
    }

    public void setLocal() {
        this.local = true;
    }

    public boolean isLocal() {
        return local;
    }

    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        if (base != null) {
            TypeRef baseType = base.getType();
            if (baseType != null) {
                sb.append(" :").append(baseType.getName());
            }
        }
        sb.append('.').append(call.getName());
        return sb.toString();
    }

    public void setRetType(String retType) {
        this.retType = retType;
    }

    public String getRetType() {
        return retType;
    }

    // ----- Special FunRef
    public static class ApplyRef extends FunRef {

        public ApplyRef() {
            super(null, ElementKind.METHOD);
        }

        @Override
        public int getPickOffset(TokenHierarchy th) {
            return getBase().getPickOffset(th);
        }

        @Override
        public int getPickEndOffset(TokenHierarchy th) {
            return getBase().getPickEndOffset(th);
        }
                
        @Override
        public String getName() {
            return "apply";
        }
    }
}
