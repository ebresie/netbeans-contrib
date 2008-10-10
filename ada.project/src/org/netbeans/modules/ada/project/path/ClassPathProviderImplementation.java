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

package org.netbeans.modules.ada.project.path;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.ada.project.AdaProject;
import org.netbeans.modules.ada.project.Pair;
import org.netbeans.modules.ada.project.SourceRoots;
import org.netbeans.modules.gsfpath.api.classpath.ClassPath;
import org.netbeans.modules.gsfpath.spi.classpath.ClassPathFactory;
import org.netbeans.modules.gsfpath.spi.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * 
 * @author Andrea Lucarelli
 */
public final class ClassPathProviderImplementation implements ClassPathProvider {

    private final SourceRoots sources;
    private final SourceRoots tests;
    private final Map<Pair<String, Integer>, ClassPath> cache = new HashMap<Pair<String, Integer>, ClassPath>();

    public ClassPathProviderImplementation(final AdaProject project) {

        this.sources = project.getSourceRoots();
        assert this.sources != null;
        this.tests = project.getTestRoots();
        assert this.tests != null;
    }
    private static final int MAX_TYPES = 2;

    /**
     * Find what a given file represents.
     * @param file a file in the project
     * @return one of: <dl>
     *         <dt>0</dt> <dd>normal source</dd>
     *         <dt>1</dt> <dd>test source</dd>
     *         <dt>2</dt> <dd>built class (unpacked)</dd>
     *         <dt>3</dt> <dd>built test class</dd>
     *         <dt>4</dt> <dd>built class (in dist JAR)</dd>
     *         <dt>-1</dt> <dd>something else</dd>
     *         </dl>
     */
    private int getType(FileObject file) {
        for (FileObject root : sources.getRoots()) {
            if (root.equals(file) || FileUtil.isParentOf(root, file)) {
                return 0;
            }
        }
        for (FileObject root : tests.getRoots()) {
            if (root.equals(file) || FileUtil.isParentOf(root, file)) {
                return 1;
            }
        }
        return -1;
    }

    private synchronized ClassPath getSourcepath(FileObject file) {
        int type = getType(file);
        return this.getSourcepath(type);
    }

    private ClassPath getSourcepath(int type) {
        if (type < 0 || type > MAX_TYPES) {
            return null;
        }
        final Pair<String, Integer> key = Pair.of(ClassPath.SOURCE, type);
        ClassPath cp = cache.get(key);
        if (cp == null) {
            switch (type) {
                case 0:
                    cp = ClassPathFactory.createClassPath(new SourcePathImplementation(sources));
                    break;
                case 1:
                    cp = ClassPathFactory.createClassPath(new SourcePathImplementation(tests));
                    break;
                default:
                    throw new UnsupportedOperationException("Only sources are available in the Ada project at this point");
            }
            cache.put(key, cp);
        }
        return cp;
    }

    private synchronized ClassPath getBootClassPath() {
        final Pair<String, Integer> key = Pair.of(ClassPath.BOOT, 0);
        ClassPath cp = cache.get(key);
        if (cp == null) {
            cp = ClassPathFactory.createClassPath(new BootClassPathImplementation());
            cache.put(key, cp);
        }
        return cp;
    }

    public ClassPath findClassPath(FileObject file, String type) {
        if (type.equals(ClassPath.SOURCE)) {
            return getSourcepath(file);
        } else if (type.equals(ClassPath.BOOT)) {
            return getBootClassPath();
        } else if (type.equals(ClassPath.COMPILE)) {
            // Bogus
            return getBootClassPath();
        } else {
            return null;
        }
    }

    /**
     * Returns array of all classpaths of the given type in the project.
     * The result is used for example for GlobalPathRegistry registrations.
     */
    public ClassPath[] getProjectClassPaths(String type) {
        if (ClassPath.BOOT.equals(type)) {
            return new ClassPath[]{getBootClassPath()};
        }
        if (ClassPath.SOURCE.equals(type)) {
            ClassPath[] l = new ClassPath[1];
            l[0] = getSourcepath(0);
            return l;
        }
        return null;
    }

    /**
     * Returns the given type of the classpath for the project sources
     * (i.e., excluding tests roots). Valid types are BOOT, SOURCE and COMPILE.
     */
    public ClassPath getProjectSourcesClassPath(String type) {
        if (ClassPath.BOOT.equals(type)) {
            return getBootClassPath();
        }
        if (ClassPath.SOURCE.equals(type)) {
            return getSourcepath(0);
        }
        return null;
    }

}