/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
 /*
 * ClassNameFilter.java
 *
 * Created on February 23, 2004, 9:57 PM
 */
package org.netbeans.modules.paintcatcher;

import java.awt.Component;
import java.awt.event.PaintEvent;
import java.util.Set;
import org.openide.util.WeakSet;

/**
 *
 * @author tim
 */
public class ComponentPaintFilter implements Filter {

    private boolean subs = false;
    private boolean anc = false;
    private final String typeName;
    private final boolean fuzzyMatch;
    private final Set<Component> matched = new WeakSet<Component>();

    /**
     * Creates a new instance of ClassNameFilter
     */
    public ComponentPaintFilter(String className, boolean allowSubclasses, boolean matchIfAncestor, boolean fuzzyMatch) {
        this.typeName = className.trim();
        this.subs = allowSubclasses;
        this.anc = matchIfAncestor;
        this.fuzzyMatch = fuzzyMatch;
    }

    public boolean match(Component c) {
        if (c == null) {
            return false;
        }
        boolean result = matched.contains(c);
        if (!result) {
            result = isAssignable(c);
            if (subs) {
                result |= isAssignable(c);
            }
            if (anc) {
                result |= hasAncestorOfClass(c);
            }
            if (result) {
                matched.add(c);
            }
        }
        return result;
    }

    private boolean isAssignable(Component c) {
        if (c.getClass().getName().equals(typeName) || (fuzzyMatch && c.getClass().getName().contains(typeName))) {
            return true;
        }
        if (subs) {
            Class<?> type = c.getClass();
            while (type != Object.class) {
                if (type.getName().equals(typeName) || (fuzzyMatch && type.getName().contains(typeName))) {
                    return true;
                }
                for (Class<?> iface : type.getInterfaces()) {
                    if (type.getName().contains(typeName)) {
                        return true;
                    }
                }
                type = type.getSuperclass();
            }
        }
        return false;
    }

    private boolean hasAncestorOfClass(Component c) {
        while (c != null) {
            if (isAssignable(c)) {
                return true;
            }
            c = c.getParent();
        }
        return false;
    }

    public boolean match(java.util.EventObject eo) {
        /*
        boolean result = false;
        if (eo instanceof PaintEvent) {
            result = match ((Component) eo.getSource());
        }
         */
        boolean result = eo instanceof PaintEvent;
        return result;
    }

    public void foo() {

    }

}
