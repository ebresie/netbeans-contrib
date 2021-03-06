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
package org.netbeans.modules.ada.editor.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.ada.editor.CodeUtils;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.ASTUtils;
import org.netbeans.modules.ada.editor.ast.nodes.Assignment;
import org.netbeans.modules.ada.editor.ast.nodes.BodyDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.FormalParameter;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.netbeans.modules.ada.editor.ast.nodes.Scalar;
import org.netbeans.modules.ada.editor.ast.nodes.Scalar.Type;
import org.netbeans.modules.ada.editor.ast.nodes.SingleFieldDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramBody;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.TypeName;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.ada.editor.ast.nodes.VariableBase;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultVisitor;
import org.netbeans.modules.ada.editor.indexer.AdaIndex;
import org.netbeans.modules.ada.editor.indexer.IndexedElement;
import org.netbeans.modules.ada.editor.indexer.IndexedFunction;
import org.netbeans.modules.ada.editor.indexer.IndexedPackageBody;
import org.netbeans.modules.ada.editor.indexer.IndexedPackageSpec;
import org.netbeans.modules.ada.editor.indexer.IndexedType;
import org.netbeans.modules.ada.editor.indexer.IndexedVariable;
import org.netbeans.modules.ada.editor.navigator.SemiAttribute.AttributedElement.Kind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Union2;

/**
 * Based on org.netbeans.modules.php.editor.nav.SemiAttribute (Jan Lahoda, Radek Matous)
 *
 * @author Andrea Lucarelli
 */
public class SemiAttribute extends DefaultVisitor {

    public DefinitionScope global;
    private Stack<DefinitionScope> scopes = new Stack<DefinitionScope>();
    private Map<ASTNode, AttributedElement> node2Element = new HashMap<ASTNode, AttributedElement>();
    private int offset;
    private ParserResult info;
    private Stack<ASTNode> nodes = new Stack<ASTNode>();

    public SemiAttribute(ParserResult info) {
        this(info, -1);
    }

    public SemiAttribute(ParserResult info, int o) {
        this.offset = o;
        this.info = info;
        scopes.push(global = new DefinitionScope());
    }

    @Override
    public void scan(ASTNode node) {
        if (node == null) {
            return;
        }

        if ((offset != (-1) && offset <= node.getStartOffset())) {
            throw new Stop();
        }

        nodes.push(node);

        super.scan(node);

        nodes.pop();

        if ((offset != (-1) && offset <= node.getEndOffset())) {
            throw new Stop();
        }
    }

    @Override
    public void visit(Program program) {
        //functions defined on top-level of the current file are visible before declared:
        performEnterPass(global, program.getStatements());
        //enterAllIndexedClasses();
        super.visit(program);
    }

    @Override
    public void visit(Assignment node) {
        final VariableBase vb = node.getLeftHandSide();

        if (vb instanceof Variable) {
            AttributedType at = null;

            String name = extractVariableName((Variable) vb);

            if (name != null) {
                node2Element.put(vb, scopes.peek().enterWrite(name, Kind.VARIABLE, vb, at));
            }
        }

        super.visit(node);
    }

    @Override
    public void visit(PackageSpecification node) {
        String name = node.getName().getName();
        PackageElement ce = (PackageElement) global.enterWrite(name, Kind.PACKAGE_SPEC, node);

        node2Element.put(node, ce);

        scopes.push(ce.enclosedElements);

        if (node.getBody() != null) {
            performEnterPass(ce.enclosedElements, node.getBody().getStatements());
        }

        super.visit(node);

        scopes.pop();
    }

    @Override
    public void visit(PackageBody node) {
        String name = node.getName().getName();
        PackageElement ce = (PackageElement) global.enterWrite(name, Kind.PACKAGE_BODY, node);

        node2Element.put(node, ce);

        scopes.push(ce.enclosedElements);

        if (node.getBody() != null) {
            performEnterPass(ce.enclosedElements, node.getBody().getStatements());
        }

        super.visit(node);

        scopes.pop();
    }

    @Override
    public void visit(SubprogramSpecification node) {
        String name = node.getSubprogramName().getName();
        SubprogSpecElement fc = (SubprogSpecElement) global.enterWrite(name, Kind.SUBPROG_SPEC, node);

        DefinitionScope top = scopes.peek();

        if (!node2Element.containsKey(node)) {
            assert !top.packageScope;
            node2Element.put(node, fc);
        }

        scopes.push(fc.enclosedElements);

        if (top.packageScope) {
            assert top.thisVar != null;
            scopes.peek().enter(top.thisVar.name, top.thisVar.getKind(), top.thisVar);
        }

        super.visit(node);

        scopes.pop();
    }

    @Override
    public void visit(SubprogramBody node) {
        String name = node.getSubprogramSpecification().getSubprogramName().getName();
        SubprogBodyElement fc = (SubprogBodyElement) global.enterWrite(name, Kind.SUBPROG_BODY, node);

        DefinitionScope top = scopes.peek();

        if (!node2Element.containsKey(node)) {
            assert !top.packageScope;
            node2Element.put(node, fc);
        }

        scopes.push(fc.enclosedElements);

        if (top.packageScope) {
            assert top.thisVar != null;
            scopes.peek().enter(top.thisVar.name, top.thisVar.getKind(), top.thisVar);
        }

        super.visit(node);

        scopes.pop();
    }

    @Override
    public void visit(FormalParameter node) {
        Variable var = null;
        if (node.getParameterName() instanceof Variable) {
            var = (Variable) node.getParameterName();
        }
        if (var != null) {
            String name = extractVariableName(var);
            if (name != null) {
                scopes.peek().enterWrite(name, Kind.VARIABLE, var);
            }
        }
        TypeName parameterType = node.getParameterType();
        if (parameterType != null) {
            String name = parameterType.getTypeName().getName();
            if (name != null) {
                Collection<AttributedElement> namedGlobalElements = getNamedGlobalElements(Kind.PACKAGE_BODY, name);
                if (!namedGlobalElements.isEmpty()) {
                    node2Element.put(parameterType, lookup(name, Kind.PACKAGE_BODY));
                } else {
                    node2Element.put(parameterType, lookup(name, Kind.PACKAGE_SPEC));
                }
            }
        }
        super.visit(node);
    }

    @Override
    public void visit(TypeDeclaration node) {
        if (!node2Element.containsKey(node)) {
            String name = extractTypeName(node);
            if (name != null) {
                node2Element.put(node, lookup(name, Kind.TYPE));
            }
        }

        super.visit(node);
    }

    @Override
    public void visit(Variable node) {
        if (!node2Element.containsKey(node)) {
            String name = extractVariableName(node);
            if (name != null) {
                node2Element.put(node, lookup(name, Kind.VARIABLE));
            }
        }

        super.visit(node);
    }

    @Override
    public void visit(Scalar scalar) {
        if (scalar.getScalarType() == Type.STRING && !NavUtils.isQuoted(scalar.getStringValue())) {
            AttributedElement def = global.lookup(scalar.getStringValue(), Kind.CONST);

            node2Element.put(scalar, def);
        }

        super.visit(scalar);
    }

    private PackageElement getCurrentClassElement() {
        PackageElement c = null;
        for (int i = scopes.size() - 1; i >= 0; i--) {
            DefinitionScope scope = scopes.get(i);
            if (scope != null && scope.enclosingPackage != null) {
                c = scope.enclosingPackage;
                break;
            }
        }
        return c;
    }

    private AttributedElement enterGlobalVariable(String name) {
        AttributedElement g = global.lookup(name, Kind.VARIABLE);

        if (g == null) {
            //XXX: untested:
            g = global.enterWrite(name, Kind.VARIABLE, (ASTNode) null);
        }

        scopes.peek().enter(name, Kind.VARIABLE, g);

        return g;
    }

    private String getContextClassName() {
        String contextClassName = null;
        Enumeration<DefinitionScope> elements = scopes.elements();
        while (elements.hasMoreElements()) {
            DefinitionScope nextElement = elements.nextElement();
            if (nextElement.enclosingPackage != null) {
                contextClassName = nextElement.enclosingPackage.getName();
            }
        }
        return contextClassName;
    }

    private ParserResult getInfo() {
        return info;
    }

    private AttributedElement lookup(String name, Kind k) {
        DefinitionScope ds = scopes.peek();

        AttributedElement e;

        switch (k) {
            case SUBPROG_BODY:
            case SUBPROG_SPEC:
            case PACKAGE_BODY:
            case PACKAGE_SPEC:
                e = global.lookup(name, k);
                break;
            default:
                e = ds.lookup(name, k);
                break;
        }

        if (e != null) {
            return e;
        }

        switch (k) {
            case SUBPROG_BODY:
            case SUBPROG_SPEC:
            case PACKAGE_BODY:
            case PACKAGE_SPEC:
                return global.enterWrite(name, k, (ASTNode) null);
            default:
                return ds.enterWrite(name, k, (ASTNode) null);
        }
    }

    public Collection<AttributedElement> getGlobalElements(Kind k) {
        return global.getElements(k);
    }

    public Collection<AttributedElement> getNamedGlobalElements(Kind k, String... filterNames) {
        Map<String, AttributedElement> name2El = global.name2Writes.get(k);

        List<AttributedElement> retval = new ArrayList<AttributedElement>();
        for (String fName : filterNames) {
            AttributedElement el = (name2El != null) ? name2El.get(fName) : null;
            if (el != null) {
                retval.add(el);
            } else {
                AdaIndex index = AdaIndex.get(info);
                for (IndexedPackageSpec m : index.getPackageSpec(null, fName, QuerySupport.Kind.PREFIX)) {
                    String idxName = m.getName();
                    el = global.enterWrite(idxName, Kind.PACKAGE_BODY, m);
                    retval.add(el);
                }
            }
        }
        return retval;
    }

    public AttributedElement getElement(ASTNode n) {
        return node2Element.get(n);
    }
    private Collection<IndexedElement> name2ElementCache;

    public void enterAllIndexedClasses() {
        if (name2ElementCache == null) {
            AdaIndex index = AdaIndex.get(info);
            name2ElementCache = new LinkedList<IndexedElement>();
            name2ElementCache.addAll(index.getPackageSpec(null, "", QuerySupport.Kind.PREFIX));
            name2ElementCache.addAll(index.getPackageBody(null, "", QuerySupport.Kind.PREFIX));
        }

        for (IndexedElement f : name2ElementCache) {
            if (f instanceof IndexedPackageSpec) {
                global.enterWrite(f.getName(), Kind.PACKAGE_SPEC, f);
            } else if (f instanceof IndexedPackageBody) {
                global.enterWrite(f.getName(), Kind.PACKAGE_BODY, f);
            }
        }
    }

    private void performEnterPass(DefinitionScope scope, Collection<? extends ASTNode> nodes) {
        for (ASTNode n : nodes) {

            if (n instanceof MethodDeclaration) {
                if (((MethodDeclaration) n).getSubprogramBody() != null) {
                    SubprogramBody sb = ((MethodDeclaration) n).getSubprogramBody();
                    String name = sb.getSubprogramSpecification().getSubprogramName().getName();
                    node2Element.put(n, scope.enterWrite(name, Kind.SUBPROG_BODY, n));
                    node2Element.put(sb, scope.enterWrite(name, Kind.SUBPROG_BODY, n));
                    continue;
                } else {
                    SubprogramSpecification ss = ((MethodDeclaration) n).getSubprogramSpecification();
                    String name = ss.getSubprogramName().getName();
                    node2Element.put(n, scope.enterWrite(name, Kind.SUBPROG_SPEC, n));
                    node2Element.put(ss, scope.enterWrite(name, Kind.SUBPROG_SPEC, n));
                }
            }

            if (n instanceof SubprogramSpecification) {
                String name = ((SubprogramSpecification) n).getSubprogramName().getName();
                node2Element.put(n, scope.enterWrite(name, Kind.SUBPROG_SPEC, n));
            }

            if (n instanceof SubprogramBody) {
                String name = ((SubprogramBody) n).getSubprogramSpecification().getSubprogramName().getName();
                node2Element.put(n, scope.enterWrite(name, Kind.SUBPROG_BODY, n));
            }

            if (n instanceof FieldsDeclaration) {
                for (SingleFieldDeclaration f : ((FieldsDeclaration) n).getFields()) {
                    String name = extractVariableName(f.getName());

                    if (name != null) {
                        node2Element.put(n, scope.enterWrite(name, Kind.VARIABLE, n));
                    }
                }
            }

            if (n instanceof TypeDeclaration) {
                String name = ((TypeDeclaration) n).getTypeName().getName();

                if (name != null) {
                    node2Element.put(n, scope.enterWrite(name, Kind.TYPE, n));
                }
            }

            if (n instanceof PackageSpecification) {
                PackageSpecification node = (PackageSpecification) n;
                String name = node.getName().getName();
                PackageElement ce = (PackageElement) global.enterWrite(name, Kind.PACKAGE_SPEC, node);
                node2Element.put(node, ce);
                if (node.getBody() != null) {
                    performEnterPass(ce.enclosedElements, node.getBody().getStatements());
                }
            }

            if (n instanceof PackageBody) {
                PackageBody node = (PackageBody) n;
                String name = node.getName().getName();
                PackageElement ce = (PackageElement) global.enterWrite(name, Kind.PACKAGE_BODY, node);
                node2Element.put(node, ce);
                if (node.getBody() != null) {
                    performEnterPass(ce.enclosedElements, node.getBody().getStatements());
                }
            }

        }
    }
    private static Map<ParserResult, SemiAttribute> info2Attr = new WeakHashMap<ParserResult, SemiAttribute>();

    public static SemiAttribute semiAttribute(ParserResult info) {
        SemiAttribute a = info2Attr.get(info);

        if (a == null) {
            long startTime = System.currentTimeMillis();

            a = new SemiAttribute(info);
            a.scan(ASTUtils.getRoot(info));

            a.info = null;

            info2Attr.put(info, a);

            long endTime = System.currentTimeMillis();

            FileObject fo = info.getSnapshot().getSource().getFileObject();

            Logger.getLogger("TIMER").log(Level.FINE, "SemiAttribute global instance", new Object[]{fo, a});
            Logger.getLogger("TIMER").log(Level.FINE, "SemiAttribute global time", new Object[]{fo, (endTime - startTime)});
        }

        return a;
    }

    public static SemiAttribute semiAttribute(ParserResult info, int stopOffset) {
        SemiAttribute a = new SemiAttribute(info, stopOffset);

        try {
            a.scan(ASTUtils.getRoot(info));
        } catch (Stop s) {
        }

        return a;
    }

    private static String name(ASTNode n) {
        if (n instanceof Identifier) {
            return ((Identifier) n).getName();
        }

        return null;
    }

    @CheckForNull
    //TODO converge this method with CodeUtils.extractTypeName()
    public static String extractTypeName(TypeDeclaration var) {
        String typeName = CodeUtils.extractTypeName(var);

        return typeName;
    }

    @CheckForNull
    //TODO converge this method with CodeUtils.extractVariableName()
    public static String extractVariableName(Variable var) {
        String varName = CodeUtils.extractVariableName(var);

        return varName;
    }

    public Collection<PackageElement> getPackagesSpec() {
        Collection<PackageElement> retval = null;
        if (global != null) {
            retval = global.getPackagesSpec();
        } else {
            retval = Collections.emptyList();
        }
        return retval;
    }

    public Collection<AttributedElement> getFunctions() {
        Collection<AttributedElement> retval = null;
        if (global != null) {
            retval = global.getFunctions();
        } else {
            retval = Collections.emptyList();
        }
        return retval;
    }

    public boolean hasGlobalVisibility(AttributedElement elem) {
        if (elem.isClassMember()) {
            PackageMemberElement cme = (PackageMemberElement) elem;
            boolean isGlobal = (cme.getModifier() == -1 || !cme.isPrivate()) && hasGlobalVisibility(cme.getPackageElement());
            return isGlobal;
        }
        return (global != null) ? global.getElements(elem.getKind()).contains(elem) : false;
    }

    public static class AttributedElement {

        private List<Union2<ASTNode, IndexedElement>> writes; //aka declarations
        private List<AttributedType> writesTypes;
        private String name;
        private Kind k;

        public AttributedElement(Union2<ASTNode, IndexedElement> n, String name, Kind k) {
            this(n, name, k, null);
        }

        public AttributedElement(Union2<ASTNode, IndexedElement> n, String name, Kind k, AttributedType type) {
            this.writes = new LinkedList<Union2<ASTNode, IndexedElement>>();
            this.writesTypes = new LinkedList<AttributedType>();
            this.writes.add(n);

            this.writesTypes.add(type);
            this.name = name;
            this.k = k;
        }

        public boolean isClassMember() {
            return false;
        }

        public List<Union2<ASTNode, IndexedElement>> getWrites() {
            return writes;
        }

        public Kind getKind() {
            return k;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AttributedElement)) {
                return false;
            }
            AttributedElement element = (AttributedElement) obj;
            return this.name.equals(element.name) && this.k.equals(element.k);
        }

        void addWrite(Union2<ASTNode, IndexedElement> node, AttributedType type) {
            writes.add(node);
            writesTypes.add(type);
        }

        Types getTypes() {
            return new Types(this);
        }

        public String getScopeName() {
            String retval = ""; //NOI18N
            Types types = getTypes();
            for (int i = 0; i < types.size(); i++) {
                AttributedType type = types.getType(i);
                if (type != null) {
                    retval = type.getTypeName();
                    break;
                }
            }
            return retval;
        }

        public enum Kind {

            TYPE, VARIABLE, SUBPROG_SPEC, SUBPROG_BODY, PACKAGE_SPEC, PACKAGE_BODY, CONST;
        }
    }

    private static class Types {

        private AttributedElement el;

        Types(AttributedElement el) {
            this.el = el;
        }

        int size() {
            return el.writesTypes.size();
        }

        AttributedType getType(int idx) {
            return el.writesTypes.get(idx);
        }
    }

    public static class PackageMemberElement extends AttributedElement {

        private PackageElement packageElement;
        int modifier = -1;

        public PackageMemberElement(Union2<ASTNode, IndexedElement> n, PackageElement packageElement, String name, Kind k) {
            super(n, name, k);
            this.packageElement = packageElement;
            setModifiers(n, name);
            assert packageElement != null;
        }

        public String getPackageName() {
            return getPackageElement().getName();
        }

        @Override
        public String getScopeName() {
            return getPackageName();
        }

        public int getModifier() {
            return modifier;
        }

        public boolean isPublic() {
            return BodyDeclaration.Modifier.isPublic(getModifier());
        }

        public boolean isPrivate() {
            return BodyDeclaration.Modifier.isPrivate(getModifier());
        }

        public PackageElement getPackageElement() {
            return packageElement;
        }

        @Override
        public boolean isClassMember() {
            return true;
        }

        public PackageMemberKind getPackageMemberKind() {
            PackageMemberKind retval = null;
            switch (getKind()) {
                case TYPE:
                    retval = PackageMemberKind.TYPE;
                    break;
                case CONST:
                    retval = PackageMemberKind.CONST;
                    break;
                case SUBPROG_BODY:
                case SUBPROG_SPEC:
                    retval = PackageMemberKind.METHOD;
                    break;
                case VARIABLE:
                    retval = PackageMemberKind.FIELD;
                    break;
                default:
                    assert false;

            }
            assert retval != null;
            return retval;
        }

        private void setModifiers(Union2<ASTNode, IndexedElement> n, String name) {
            if (n.hasFirst()) {
                ASTNode node = n.first();
                if (node instanceof BodyDeclaration) {
                    modifier = ((BodyDeclaration) node).getModifier();
//                } else if (node instanceof ClassConstantDeclaration) {
//                    modifier |= BodyDeclaration.Modifier.PUBLIC;
                } else {
                    assert false : name;
                }
            } else if (n.hasSecond()) {
                IndexedElement index = n.second();
                if (index != null) {
                    Set<Modifier> modifiers = index.getModifiers();
                    for (Modifier mod : modifiers) {
                        switch (mod) {
                            case PRIVATE:
                                modifier |= BodyDeclaration.Modifier.PRIVATE;
                                break;
                            case PUBLIC:
                                modifier |= BodyDeclaration.Modifier.PUBLIC;
                                break;
                        }
                    }
                }
            }
        }

        public enum PackageMemberKind {

            TYPE, FIELD, METHOD, CONST;
        }
    }

    public class PackageElement extends AttributedElement {

        private final DefinitionScope enclosedElements;
        private boolean initialized;

        public PackageElement(Union2<ASTNode, IndexedElement> n, String name, Kind k) {
            super(n, name, k);
            enclosedElements = new DefinitionScope(this);
        }

        public AttributedElement lookup(String name, Kind k) {
            AttributedElement el = enclosedElements.lookup(name, k);
            if (el != null) {
                return el;
            }
            AdaIndex index = AdaIndex.get(info);
            int attrs = AdaIndex.ANY_ATTR;

            switch (k) {
				case SUBPROG_BODY:
                    for (IndexedFunction m : index.getAllMethods(null, getName(), name, QuerySupport.Kind.PREFIX, attrs)) {
                        enclosedElements.enterWrite(m.getName(), Kind.SUBPROG_BODY, m);
                    }
                    break;

				case SUBPROG_SPEC:
                    for (IndexedFunction m : index.getAllMethods(null, getName(), name, QuerySupport.Kind.PREFIX, attrs)) {
                        enclosedElements.enterWrite(m.getName(), Kind.SUBPROG_SPEC, m);
                    }
                    break;

                case TYPE:
                    for (IndexedType m : index.getAllTypes(null, getName(), name, QuerySupport.Kind.PREFIX, attrs)) {
                        String idxName = m.getName();
                        enclosedElements.enterWrite(idxName, Kind.TYPE, m);
                    }
                    break;

                case VARIABLE:
                    for (IndexedVariable m : index.getAllFields(null, getName(), name, QuerySupport.Kind.PREFIX, attrs)) {
                        String idxName = m.getName();
                        enclosedElements.enterWrite(idxName, Kind.VARIABLE, m);
                    }
                    break;

            }
            return enclosedElements.lookup(name, k);
        }

        public Collection<AttributedElement> getElements(Kind k) {
            List<AttributedElement> elements = new ArrayList<AttributedElement>();

            getElements0(elements, k);

            return Collections.unmodifiableList(elements);
        }

        public Collection<AttributedElement> getNamedElements(Kind k, String... filterNames) {
            Collection<AttributedElement> elements = getElements(k);
            List<AttributedElement> retval = new ArrayList<AttributedElement>();
            for (String fName : filterNames) {
                for (AttributedElement el : elements) {
                    if (el.getName().equals(fName)) {
                        retval.add(el);
                    }
                }
            }
            return retval;
        }

        public Collection<AttributedElement> getMethods() {
            Collection<AttributedElement> elems = getElements(Kind.SUBPROG_BODY);
            elems.addAll(getElements(Kind.SUBPROG_SPEC));
            return elems;
        }

        public Collection<AttributedElement> getFields() {
            Collection<AttributedElement> elems = getElements(Kind.VARIABLE);
            List<AttributedElement> retval = new ArrayList<AttributedElement>();
            for (AttributedElement elm : elems) {
                retval.add(elm);
            }
            return retval;
        }

        private void getElements0(List<AttributedElement> elements, Kind k) {
            elements.addAll(enclosedElements.getElements(k));
        }

        boolean isInitialized() {
            return initialized;
        }

        void initialized() {
            initialized = true;
        }
    }

    public class SubprogSpecElement extends AttributedElement {

        private final DefinitionScope enclosedElements;
        private boolean initialized;

        public SubprogSpecElement(Union2<ASTNode, IndexedElement> n, String name, Kind k) {
            super(n, name, k);
            enclosedElements = new DefinitionScope(this);
        }

        public AttributedElement lookup(String name, Kind k) {
            return enclosedElements.lookup(name, k);
        }

        public Collection<AttributedElement> getElements(Kind k) {
            List<AttributedElement> elements = new ArrayList<AttributedElement>();

            getElements0(elements, k);

            return Collections.unmodifiableList(elements);
        }

        public Collection<AttributedElement> getNamedElements(Kind k, String... filterNames) {
            Collection<AttributedElement> elements = getElements(k);
            List<AttributedElement> retval = new ArrayList<AttributedElement>();
            for (String fName : filterNames) {
                for (AttributedElement el : elements) {
                    if (el.getName().equals(fName)) {
                        retval.add(el);
                    }
                }
            }
            return retval;
        }

        public Collection<AttributedElement> getVariables() {
            return getElements(Kind.VARIABLE);
        }

        private void getElements0(List<AttributedElement> elements, Kind k) {
            elements.addAll(enclosedElements.getElements(k));
        }

        boolean isInitialized() {
            return initialized;
        }

        void initialized() {
            initialized = true;
        }
    }

    public class SubprogBodyElement extends AttributedElement {

        private final DefinitionScope enclosedElements;
        private boolean initialized;

        public SubprogBodyElement(Union2<ASTNode, IndexedElement> n, String name, Kind k) {
            super(n, name, k);
            enclosedElements = new DefinitionScope(this);
        }

        public AttributedElement lookup(String name, Kind k) {
            return enclosedElements.lookup(name, k);
        }

        public Collection<AttributedElement> getElements(Kind k) {
            List<AttributedElement> elements = new ArrayList<AttributedElement>();

            getElements0(elements, k);

            return Collections.unmodifiableList(elements);
        }

        public Collection<AttributedElement> getNamedElements(Kind k, String... filterNames) {
            Collection<AttributedElement> elements = getElements(k);
            List<AttributedElement> retval = new ArrayList<AttributedElement>();
            for (String fName : filterNames) {
                for (AttributedElement el : elements) {
                    if (el.getName().equals(fName)) {
                        retval.add(el);
                    }
                }
            }
            return retval;
        }

        public Collection<AttributedElement> getVariables() {
            return getElements(Kind.VARIABLE);
        }

        private void getElements0(List<AttributedElement> elements, Kind k) {
            elements.addAll(enclosedElements.getElements(k));
        }

        boolean isInitialized() {
            return initialized;
        }

        void initialized() {
            initialized = true;
        }
    }

    public class DefinitionScope {

        private final Map<Kind, Map<String, AttributedElement>> name2Writes = new HashMap<Kind, Map<String, AttributedElement>>();
//        private final Map<AttributedElement, ASTNode> reads = new HashMap<AttributedElement, ASTNode>();
        private boolean packageScope;
        private boolean subprogSpecScope;
        private boolean subprogBodyScope;
        private AttributedElement thisVar;
        private PackageElement enclosingPackage;
        private SubprogSpecElement enclosingSubprogSpec;
        private SubprogBodyElement enclosingSubprogBody;

        public DefinitionScope() {
        }

        public DefinitionScope(PackageElement enclosingPackage) {
            this.enclosingPackage = enclosingPackage;
            this.packageScope = enclosingPackage != null;

            if (packageScope) {
                thisVar = enterWrite("", Kind.VARIABLE, (ASTNode) null, new PackageType(enclosingPackage));
            }
        }

        public DefinitionScope(SubprogSpecElement enclosingSubprogSpec) {
            this.enclosingSubprogSpec = enclosingSubprogSpec;
            this.subprogSpecScope = enclosingSubprogSpec != null;
        }

        public DefinitionScope(SubprogBodyElement enclosingSubprogBody) {
            this.enclosingSubprogBody = enclosingSubprogBody;
            this.subprogBodyScope = enclosingSubprogBody != null;
        }

        public AttributedElement enterWrite(String name, Kind k, ASTNode node) {
            return enterWrite(name, k, node, null);
        }

        public AttributedElement enterWrite(String name, Kind k, ASTNode node, AttributedType type) {
            return enterWrite(name, k, Union2.<ASTNode, IndexedElement>createFirst(node), type);
        }

        public AttributedElement enterWrite(String name, Kind k, IndexedElement el) {
            return enterWrite(name, k, Union2.<ASTNode, IndexedElement>createSecond(el), null);
        }

        private AttributedElement enterWrite(String name, Kind k, Union2<ASTNode, IndexedElement> node, AttributedType type) {
            Map<String, AttributedElement> name2El = name2Writes.get(k);

            if (name2El == null) {
                name2Writes.put(k, name2El = new HashMap<String, AttributedElement>());
            }

            AttributedElement el = name2El.get(name);

            if (el == null) {
                if (k == Kind.PACKAGE_SPEC || k == Kind.PACKAGE_BODY) {
                    el = new PackageElement(node, name, k);
                } else {
                    if (packageScope) { // && !Arrays.asList(new String[]{"this"}).contains(name)) {
                        switch (k) {
                            case CONST:
                            case SUBPROG_BODY:
                            case SUBPROG_SPEC:
                            case VARIABLE:
                            case TYPE:
                                el = new PackageMemberElement(node, enclosingPackage, name, k);
                                break;
                            default:
                                assert false;
                        }
                    } else {
                        if (k == Kind.SUBPROG_SPEC) {
                            el = new SubprogSpecElement(node, name, k);
                        } else if (k == Kind.SUBPROG_BODY) {
                            el = new SubprogBodyElement(node, name, k);
                        } else if (k == Kind.VARIABLE) {
                            if (type == null && subprogSpecScope && enclosingSubprogSpec != null) {
                                type = new SubprogSpecType(enclosingSubprogSpec);
                            } else if (type == null && subprogBodyScope && enclosingSubprogBody != null) {
                                type = new SubprogBodyType(enclosingSubprogBody);
                            }
                            el = new AttributedElement(node, name, k, type);
                        } else {
                            el = new AttributedElement(node, name, k, type);
                        }
                    }
                }

                name2El.put(name, el);
            } else {
                el.addWrite(node, type);
            }

            return el;
        }

        public AttributedElement enter(String name, Kind k, AttributedElement el) {
            Map<String, AttributedElement> name2El = name2Writes.get(k);
            if (name2El == null) {
                name2Writes.put(k, name2El = new HashMap<String, AttributedElement>());
            }
            name2El.put(name, el);
            return el;
        }

        public AttributedElement lookup(String name, Kind k) {
            AttributedElement el = null;
            Map<String, AttributedElement> name2El = name2Writes.get(k);
            if (name2El != null) {
                el = name2El.get(name);
            }
            if (el == null) {
                AdaIndex index = AdaIndex.get(info);
                switch (k) {
                    case CONST:
                        //for (IndexedConstant m : index.getConstants(null, name, NameKind.PREFIX)) {
                        //    String idxName = m.getName();
                        //    el = enterWrite(idxName, Kind.CONST, m);
                        //}
                        break;
                }
            }
            return el;
        }

        public Collection<AttributedElement> getElements(Kind k) {
            Map<String, AttributedElement> name2El = name2Writes.get(k);
            if (name2El != null) {
                return Collections.unmodifiableCollection(name2El.values());
            }
            return Collections.emptyList();
        }

        public Collection<AttributedElement> getFunctions() {
            return getElements(Kind.SUBPROG_BODY);
        }

        public Collection<AttributedElement> getVariables() {
            return getElements(Kind.VARIABLE);
        }

        private Collection<AttributedElement> getConstants() {
            return getElements(Kind.CONST);
        }

        public Collection<PackageElement> getPackagesSpec() {
            Collection<PackageElement> retval = new LinkedHashSet<PackageElement>();
            Collection<AttributedElement> elements = getElements(Kind.PACKAGE_SPEC);
            for (AttributedElement el : elements) {
                assert el instanceof PackageElement;
                retval.add((PackageElement) el);
            }
            return retval;
        }
    }

    private static final class Stop extends Error {
    }

    public static abstract class AttributedType {

        public abstract String getTypeName();
    }

    public static class PackageType extends AttributedType {

        private PackageElement element;

        public PackageType(PackageElement element) {
            this.element = element;
        }

        public PackageElement getElement() {
            return element;
        }

        @Override
        public String getTypeName() {
            return getElement().getName();
        }
    }

    public static class SubprogSpecType extends AttributedType {

        private SubprogSpecElement element;

        public SubprogSpecType(SubprogSpecElement element) {
            this.element = element;
        }

        public SubprogSpecElement getElement() {
            return element;
        }

        @Override
        public String getTypeName() {
            return getElement().getName();
        }
    }

    public static class SubprogBodyType extends AttributedType {

        private SubprogBodyElement element;

        public SubprogBodyType(SubprogBodyElement element) {
            this.element = element;
        }

        public SubprogBodyElement getElement() {
            return element;
        }

        @Override
        public String getTypeName() {
            return getElement().getName();
        }
    }
}
