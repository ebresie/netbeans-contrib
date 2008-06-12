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

package org.netbeans.modules.scala.editing.nodes.types;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.type.TypeKind;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.scala.editing.nodes.AstScope;

/**
 *
 * @author dcaoyuan
 */
public class SimpleTupleType extends TypeRef {
    private List<TypeRef> types;
    
    public SimpleTupleType(Token pickToken) {
        super(null, pickToken, TypeKind.DECLARED);
    }
    
    public void setTypes(List<TypeRef> types) {
        this.types = types;
    }
    
    public List<TypeRef> getTypes() {
        return types == null ? Collections.<TypeRef>emptyList() : types;
    }

    @Override
    public AstScope getEnclosingScope() {
        return types.get(0).getEnclosingScope();
    }    
    
    @Override
    public int getPickOffset(TokenHierarchy th) {
        return -1;
    }        

    /** @Todo how to define tuple type's pick offsets?
     */
    @Override
    public int getPickEndOffset(TokenHierarchy th) {
        return -1;
    }        

    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("(");
        for (Iterator<TypeRef> itr = getTypes().iterator(); itr.hasNext();) {
            sb.append(itr.next().getName());
            if (itr.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        
        return sb.toString();
    }
    
    
    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        formatter.appendText("(");
        for (Iterator<TypeRef> itr = getTypes().iterator(); itr.hasNext();) {
            itr.next().htmlFormat(formatter);
            if (itr.hasNext()) {
                formatter.appendText(", ");
            }
        }
        formatter.appendText(")");
        htmlFormatTypeArgs(formatter);
    }        
}
