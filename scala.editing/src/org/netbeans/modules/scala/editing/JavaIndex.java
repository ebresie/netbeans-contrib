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
package org.netbeans.modules.scala.editing;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.NameKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.modules.java.source.JavaSourceAccessor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Exceptions;

/**
 *
 * @author Caoyuan Deng
 */
public class JavaIndex {

    public static final Set<SearchScope> JAVA_ALL_SCOPE = EnumSet.allOf(SearchScope.class);
    public static final Set<SearchScope> JAVA_SOURCE_SCOPE = EnumSet.of(SearchScope.SOURCE);
    private static Map<FileObject, Reference<JavaSource>> fileToJavaSource =
            new WeakHashMap<FileObject, Reference<JavaSource>>();
    private static Map<FileObject, Reference<CompilationController>> fileToJavaController =
            new WeakHashMap<FileObject, Reference<CompilationController>>();
    private final ClassIndex index;
    private final JavaSource source;
    private final CompilationController controller;
    private final ScalaIndex scalaIndex;

    private JavaIndex(ClassIndex index, JavaSource source, CompilationController controller, ScalaIndex scalaIndex) {
        this.index = index;
        this.source = source;
        this.controller = controller;
        this.scalaIndex = scalaIndex;
    }

    public static JavaIndex get(org.netbeans.modules.gsf.api.CompilationInfo info, ScalaIndex scalaIndex) {
        FileObject fo = info.getFileObject();
        /** 
         * @Note: We cannot create js via JavaSource.forFileObject(fo) here, which
         * does not support virtual source yet (only ".java" and ".class" files 
         * are supported), but we can create js via JavaSource.create(cpInfo);
         */
        Reference<JavaSource> sourceRef = fileToJavaSource.get(fo);
        JavaSource source = sourceRef != null ? sourceRef.get() : null;
        if (source == null) {
            ClasspathInfo javaCpInfo = ClasspathInfo.create(fo);
            source = JavaSource.create(javaCpInfo);
            fileToJavaSource.put(fo, new WeakReference<JavaSource>(source));

        }

        Reference<CompilationController> controllerRef = fileToJavaController.get(fo);
        CompilationController controller = controllerRef != null ? controllerRef.get() : null;
        if (controller == null) {
            final org.netbeans.api.java.source.CompilationController[] javaControllers =
                    new org.netbeans.api.java.source.CompilationController[1];
            try {
                source.runUserActionTask(new Task<CompilationController>() {

                    public void run(CompilationController controller) throws Exception {
                        controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                        javaControllers[0] = controller;
                    }
                }, true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            controller = javaControllers[0];
            fileToJavaController.put(fo, new WeakReference<CompilationController>(controller));
        }

        ClassIndex index = source.getClasspathInfo().getClassIndex();

        return new JavaIndex(index, source, controller, scalaIndex);
    }

    public Set<IndexedElement> getPackages(String fqnPrefix) {
        Set<String> pkgNames = index.getPackageNames(fqnPrefix, true, JAVA_ALL_SCOPE);
        Set<IndexedElement> idxElements = new HashSet<IndexedElement>();
        for (String pkgName : pkgNames) {
            if (pkgName.length() > 0) {
                IndexedElement idxElement = IndexedElement.create("", "", pkgName, pkgName, "", 0, scalaIndex, true);
                idxElements.add(idxElement);
            }
        }
        return idxElements;
    }

    public Set<IndexedElement> getPackageContent(String pkgFqn, String prefix) {
        Elements theElements = controller.getElements();
        PackageElement pe = theElements.getPackageElement(pkgFqn);
        if (pe != null) {
            Set<IndexedElement> idxElements = new HashSet<IndexedElement>();
            for (Element e : pe.getEnclosedElements()) {
                if (e.getKind().isClass() || e.getKind().isInterface()) {
                    String simpleName = e.getSimpleName().toString();
                    if (JavaUtilities.startsWith(simpleName, prefix)) {
                        String in = "";
                        StringBuilder base = new StringBuilder();
                        base.append(simpleName.toLowerCase());
                        base.append(';');
                        if (in != null) {
                            base.append(in);
                        }
                        base.append(';');
                        base.append(simpleName);
                        base.append(';');
                        base.append(IndexedElement.computeAttributes(e));

                        IndexedElement idxElement = IndexedElement.create(simpleName, base.toString(), "", scalaIndex, false);
                        idxElements.add(idxElement);
                    }
                }
            }
            return idxElements;
        }
        return Collections.<IndexedElement>emptySet();
    }

    public Set<IndexedElement> getByFqn(String name, String type, NameKind kind,
            Set<SearchScope> scope, boolean onlyConstructors, ScalaParserResult context,
            boolean includeMethods, boolean includeProperties, boolean includeDuplicates) {

        final Set<IndexedElement> idxElements = includeDuplicates ? new DuplicateElementSet() : new HashSet<IndexedElement>();        

        JavaSourceAccessor.getINSTANCE().lockJavaCompiler();

        NameKind originalKind = kind;
        if (kind == NameKind.SIMPLE_NAME) {
            // I can't do exact searches on methods because the method
            // entries include signatures etc. So turn this into a prefix
            // search and then compare chopped off signatures with the name
            kind = NameKind.PREFIX;
        }

        if (kind == NameKind.CASE_INSENSITIVE_PREFIX || kind == NameKind.CASE_INSENSITIVE_REGEXP) {
            // TODO - can I do anything about this????
            //field = ScalaIndexer.FIELD_BASE_LOWER;
            //terms = FQN_BASE_LOWER;
        }

        String searchUrl = null;
        if (context != null) {
            try {
                searchUrl = context.getFile().getFileObject().getURL().toExternalForm();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        Set<String> seenTypes = new HashSet<String>();
        seenTypes.add(type);
        boolean haveRedirected = false;
        boolean inheriting = type == null;

        if (type == null || type.length() == 0) {
            type = "Object";
        }

        String fqn;
        if (type != null && type.length() > 0) {
            fqn = type + "." + name;
        } else {
            fqn = name;
        }

        String lcfqn = fqn.toLowerCase();

        /** always use NameKind.SIMPLE_NAME search index.getDeclaredTypes */
        kind = NameKind.SIMPLE_NAME;

        Elements theElements = controller.getElements();
        Types theTypes = controller.getTypes();

        Set<ElementHandle<TypeElement>> dclTypes = index.getDeclaredTypes(type, kind, scope);

        for (ElementHandle<TypeElement> teHandle : dclTypes) {
            IndexedElement idxElement = null;

            TypeElement te = teHandle.resolve(controller);

            PackageElement pe = theElements.getPackageOf(te);
            if (pe != null) {
            }

            TypeMirror typeMirror = te.asType();
            TypeElement typeElem = typeMirror.getKind() == TypeKind.DECLARED ? (TypeElement) ((DeclaredType) typeMirror).asElement() : null;

            if (te != null) {
                for (Element e : theElements.getAllMembers(te)) {
                    
                    if (e.getModifiers().contains(Modifier.PRIVATE)) {
                        continue;
                    }
                    
                    String simpleMame = e.getSimpleName().toString();
                    
                    switch (e.getKind()) {
                        case ENUM_CONSTANT:
                        case EXCEPTION_PARAMETER:
                        case FIELD:
                        case LOCAL_VARIABLE:
                        case PARAMETER:
                            if ("this".equals(simpleMame) || "class".equals(simpleMame) || "super".equals(simpleMame)) {
                                //results.add(JavaCompletionItem.createKeywordItem(ename, null, anchorOffset, false));
                                } else {
                                TypeMirror tm = typeMirror.getKind() == TypeKind.DECLARED ? theTypes.asMemberOf((DeclaredType) typeMirror, e) : e.asType();
                            //results.add(JavaCompletionItem.createVariableItem((VariableElement) e, tm, anchorOffset, typeElem != e.getEnclosingElement(), elements.isDeprecated(e), isOfSmartType(env, tm, smartTypes)));
                            }
                            break;
                        case CONSTRUCTOR:
                            ExecutableType et = (ExecutableType) (typeMirror.getKind() == TypeKind.DECLARED ? theTypes.asMemberOf((DeclaredType) typeMirror, e) : e.asType());
                            //results.add(JavaCompletionItem.createExecutableItem((ExecutableElement) e, et, anchorOffset, typeElem != e.getEnclosingElement(), elements.isDeprecated(e), inImport, isOfSmartType(env, type, smartTypes)));
                            break;
                        case METHOD:
                            et = (ExecutableType) (typeMirror.getKind() == TypeKind.DECLARED ? theTypes.asMemberOf((DeclaredType) typeMirror, e) : e.asType());
                            String in = e.getEnclosingElement().getSimpleName().toString();
                            StringBuilder base = new StringBuilder();
                            base.append(simpleMame.toLowerCase());
                            base.append(';');
                            if (in != null) {
                                base.append(in);
                            }
                            base.append(';');
                            base.append(simpleMame);
                            base.append(';');
                            base.append(IndexedElement.computeAttributes(e));

                            idxElement = IndexedElement.create(simpleMame, base.toString(), "", scalaIndex, false);
                            break;
                        case CLASS:
                        case ENUM:
                        case INTERFACE:
                        case ANNOTATION_TYPE:
                            DeclaredType dt = (DeclaredType) (typeMirror.getKind() == TypeKind.DECLARED ? theTypes.asMemberOf((DeclaredType) typeMirror, e) : e.asType());
                            //results.add(JavaCompletionItem.createTypeItem((TypeElement) e, dt, anchorOffset, false, elements.isDeprecated(e), insideNew, false));
                            break;
                    }

                    if (idxElement == null) {
                        continue;
                    }
                    boolean isFunction = idxElement instanceof IndexedFunction;
                    if (isFunction && !includeMethods) {
                        continue;
                    } else if (!isFunction && !includeProperties) {
                        continue;
                    }
                    if (onlyConstructors && !idxElement.getKind().name().equals(ElementKind.CONSTRUCTOR.name())) {
                        continue;
                    }
                    if (!haveRedirected) {
                        idxElement.setSmart(true);
                    }

                    inheriting = typeElem != e.getEnclosingElement();
                    if (!inheriting) {
                        idxElement.setInherited(false);
                    }
                    idxElements.add(idxElement);
                }

            //addMembers(env, te.asType(), te, kinds, baseType, inImport, insideNew);
            }

//                String[] signatures = map.getValues(field);
//
//                if (signatures != null) {
//                    // Check if this file even applies
//                    if (context != null) {
//                        String fileUrl = map.getPersistentUrl();
//                        if (searchUrl == null || !searchUrl.equals(fileUrl)) {
//                            boolean isLibrary = fileUrl.indexOf("jsstubs") != -1; // TODO - better algorithm
//                            if (!isLibrary && !isReachable(context, fileUrl)) {
//                                continue;
//                            }
//                        }
//                    }
//
//                    for (String signature : signatures) {
//                        // Lucene returns some inexact matches, TODO investigate why this is necessary
//                        if ((kind == NameKind.PREFIX) && !signature.startsWith(lcfqn)) {
//                            continue;
//                        } else if (kind == NameKind.CASE_INSENSITIVE_PREFIX && !signature.regionMatches(true, 0, lcfqn, 0, lcfqn.length())) {
//                            continue;
//                        } else if (kind == NameKind.CASE_INSENSITIVE_REGEXP) {
//                            int end = signature.indexOf(';');
//                            assert end != -1;
//                            String n = signature.substring(0, end);
//                            try {
//                                if (!n.matches(lcfqn)) {
//                                    continue;
//                                }
//                            } catch (Exception e) {
//                                // Silently ignore regexp failures in the search expression
//                            }
//                        } else if (originalKind == NameKind.EXACT_NAME) {
//                            // Make sure the name matches exactly
//                            // We know that the prefix is correct from the first part of
//                            // this if clause, by the signature may have more
//                            if (((signature.length() > lcfqn.length()) &&
//                                    (signature.charAt(lcfqn.length()) != ';'))) {
//                                continue;
//                            }
//                        }
//
//                        // XXX THIS DOES NOT WORK WHEN THERE ARE IDENTICAL SIGNATURES!!!
//                        assert map != null;
//
//                        String elementName = null;
//                        int nameEndIdx = signature.indexOf(';');
//                        assert nameEndIdx != -1 : signature;
//                        elementName = signature.substring(0, nameEndIdx);
//                        nameEndIdx++;
//
//                        String funcIn = null;
//                        int inEndIdx = signature.indexOf(';', nameEndIdx);
//                        assert inEndIdx != -1 : signature;
//                        inEndIdx++;
//
//                        int startCs = inEndIdx;
//                        inEndIdx = signature.indexOf(';', startCs);
//                        assert inEndIdx != -1;
//                        if (inEndIdx > startCs) {
//                            // Compute the case sensitive name
//                            elementName = signature.substring(startCs, inEndIdx);
//                            if (kind == NameKind.PREFIX && !elementName.startsWith(fqn)) {
//                                continue;
//                            } else if (kind == NameKind.EXACT_NAME && !elementName.equals(fqn)) {
//                                continue;
//                            }
//                        }
//                        inEndIdx++;
//
//                        int lastDot = elementName.lastIndexOf('.');
//                        IndexedElement element = null;
//                        if (name.length() < lastDot) {
//                            int nextDot = elementName.indexOf('.', fqn.length());
//                            if (nextDot != -1) {
//                                int flags = IndexedElement.decode(signature, inEndIdx, 0);
//                                ElementKind k = ElementKind.PACKAGE;
//                                // If there are no more dots after this one, it's a class, not a package
//                                int nextNextDot = elementName.indexOf('.', nextDot + 1);
//                                if (nextNextDot == -1) {
//                                    k = ElementKind.CLASS;
//                                }
//                                if (type != null && type.length() > 0) {
//                                    String pkg = elementName.substring(type.length() + 1, nextDot);
//                                    element = new IndexedPackage(null, pkg, null, this, map.getPersistentUrl(), signature, flags, ElementKind.PACKAGE);
//                                } else {
//                                    String pkg = elementName.substring(0, nextDot);
//                                    element = new IndexedPackage(null, pkg, null, this, map.getPersistentUrl(), signature, flags, ElementKind.PACKAGE);
//                                }
//                            } else {
//                                funcIn = elementName.substring(0, lastDot);
//                                elementName = elementName.substring(lastDot + 1);
//                            }
//                        } else if (lastDot != -1) {
//                            funcIn = elementName.substring(0, lastDot);
//                            elementName = elementName.substring(lastDot + 1);
//                        }
//                        if (element == null) {
//                            element = IndexedElement.create(signature, map.getPersistentUrl(), null, elementName, funcIn, inEndIdx, this, false);
//                        }
//                    }
//                }
        }


        JavaSourceAccessor.getINSTANCE().unlockJavaCompiler();
        return idxElements;
    }
}
