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
package org.netbeans.modules.scala.editing.visitors;

import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Element;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.Modifier;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.scala.editing.ScalaMimeResolver;
import org.openide.filesystems.FileObject;


/**
 *
 * @author Caoyuan Deng
 */
public class Signature implements ElementHandle {

    private Element element;
    private javax.lang.model.element.ElementKind kind;
    private Set<Modifier> mods;
    private OffsetRange nameRange;
    private Scope enclosingScope;
    private String name;
    private Element packageElement;

    public Signature(Element element, OffsetRange nameRange) {
        this.element = element;
        this.name = element.getSimpleName().toString();

        /**
         * @Note: nameNode always includes preceding whitespace, but not includes
         * following whitespace (endOffset is correct), but nameString has strip 
         * the whitespace, so we should adjust nameRange according to the endOffset.
         * 
         * Strip whitespaces and adjust nameRange */
        int adjust = nameRange.getLength() - name.length();
        this.nameRange = adjust > 0 ? new OffsetRange(nameRange.getStart() + adjust, nameRange.getEnd())
                : nameRange;
    }
    
    public Element getElement() {
        return element;
    }

    public String getName() {
        return name;
    }

    public OffsetRange getNameRange() {
        return nameRange;
    }

    public ElementKind getKind() {
        switch (element.getKind()) {
            case CLASS:
                return ElementKind.CLASS;
            case METHOD:
                return ElementKind.METHOD;
            case CONSTRUCTOR:
                return ElementKind.CONSTRUCTOR;
            default:
                return ElementKind.OTHER;
        }
    }
    
    public void setPackageElement(Element packageElement) {
        this.packageElement = packageElement;
    }
    
    public Element getPackageElement() {
        return packageElement;
    }

    /**
     * @Note: enclosingScope will be set when call
     *   {@link Scope#addDefinition(Definition)} or {@link Scope#addUsage(Usage)}
     */
    protected void setEnclosingScope(Scope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    /**
     * @return the scope that encloses this item 
     */
    public Scope getEnclosingScope() {
        assert enclosingScope != null : "Each signature should set enclosing scope!";
        return enclosingScope;
    }

    public String getMimeType() {
        return ScalaMimeResolver.MIME_TYPE;
    }

    public boolean signatureEquals(ElementHandle handle) {
        // XXX TODO
        return false;
    }

    public FileObject getFileObject() {
        return null;
    }

    public Set<Modifier> getModifiers() {
        if (mods == null) {
            mods = new HashSet<Modifier>();
        }

//        List<com.sun.fortress.nodes.Modifier> fortressMods = Collections.emptyList();
//        if (node instanceof TraitObjectAbsDeclOrDecl) {
//            fortressMods = ((TraitObjectAbsDeclOrDecl) node).getMods();
//        } else if (node instanceof FnAbsDeclOrDecl) {
//            fortressMods = ((FnAbsDeclOrDecl) node).getMods();
//        }
//
//        for (com.sun.fortress.nodes.Modifier mod : fortressMods) {
//            if (mod instanceof ModifierPrivate) {
//                mods.add(Modifier.PRIVATE);
//            }
//        }

        return mods;
    }

    public String getIn() {
        return null;
    }
    

}