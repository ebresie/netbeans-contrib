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

package org.netbeans.modules.ada.editor.ast.nodes;

import org.netbeans.modules.ada.editor.ast.nodes.visitors.Visitor;
import org.netbeans.modules.ada.editor.ast.ASTNode;

/**
 * Based on org.netbeans.modules.php.editor.parser.astnodes.Comment
 *
 * Represents an ADA comment
 * <pre>e.g.<pre> -- this is a comment,
 * --
 * -- Decription to Foo
 * --
 *
 * @author Andrea Lucarelli
 */
public class Comment extends ASTNode {

    public enum Type {

        // TODO: add TYPE_ADA_DOC
        // TODO: add TYPE_SPARK_DOC
        TYPE_SINGLE_LINE ("singleLine");
        
        private final String text;
        
        Type(String textRepresentation) {
            text = textRepresentation;
        }
        
        @Override
        public String toString() {
            return text;
        }
    };
    
    private Type commentType;

    public Comment(int start, int end) {
        super(start, end);
        // only single line comments are allowed in Ada language
        commentType = Comment.Type.TYPE_SINGLE_LINE;
    }

    public Type getCommentType() {
        return commentType;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
