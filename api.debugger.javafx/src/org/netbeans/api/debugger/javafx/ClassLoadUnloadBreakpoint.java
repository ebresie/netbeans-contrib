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

package org.netbeans.api.debugger.javafx;


/**
 * Notifies about class load and class unload events.
 *
 * <br><br>
 * <b>How to use it:</b>
 * <pre style="background-color: rgb(255, 255, 153);">
 *    DebuggerManager.addBreakpoint (ClassLoadUnloadBreakpoint.create (
 *        "org.netbeans.modules.editor.*",
 *        false,
 *        ClassLoadUnloadBreakpoint.TYPE_CLASS_LOADED
 *    ));</pre>
 * This breakpoint stops when some class from org.netbeans.modules.editor
 * package is loaded.
 *
 * @author Jan Jancura
 */
public final class ClassLoadUnloadBreakpoint extends JavaFXBreakpoint {

    /** Property name constant */
    public static final String          PROP_CLASS_FILTERS = "classFilters"; // NOI18N
    /** Property name constant */
    public static final String          PROP_CLASS_EXCLUSION_FILTERS = "classExclusionFilters"; // NOI18N
    /** Name of property for breakpoint type. */
    public static final String          PROP_BREAKPOINT_TYPE = "breakpointType"; // NOI18N

    /** Catch type property value constant. */
    public static final int             TYPE_CLASS_LOADED = 1;
    /** Catch type property value constant. */
    public static final int             TYPE_CLASS_UNLOADED = 2;
    /** Catch type property value constant. */
    public static final int             TYPE_CLASS_LOADED_UNLOADED = 3;

    /** Property variable. */
    private int                         type = TYPE_CLASS_LOADED;
    private String[]                    classFilters = new String [0];
    private String[]                    classExclusionFilters = new String [0];

    
    private ClassLoadUnloadBreakpoint () {
    }
    
    /**
     * Creates a new breakpoint for given parameters.
     *
     * @param classNameFilter class name filter
     * @param isExclusionFilter if true filter is used as exclusion filter
     * @param breakpointType one of constants: TYPE_CLASS_LOADED, 
     *   TYPE_CLASS_UNLOADED, TYPE_CLASS_LOADED_UNLOADED
     * @return a new breakpoint for given parameters
     */
    public static ClassLoadUnloadBreakpoint create (
        String classNameFilter,
        boolean isExclusionFilter,
        int breakpointType
    ) {
        ClassLoadUnloadBreakpoint b = new ClassLoadUnloadBreakpoint ();
        if (isExclusionFilter)
            b.setClassExclusionFilters (new String[] {classNameFilter});
        else
            b.setClassFilters (new String[] {classNameFilter});
        b.setBreakpointType (breakpointType);
        return b;
    }
    
    /**
     * Creates a new breakpoint for given parameters.
     *
     * @param breakpointType one of constants: TYPE_CLASS_LOADED, 
     *   TYPE_CLASS_UNLOADED, TYPE_CLASS_LOADED_UNLOADED
     * @return a new breakpoint for given parameters
     */
    public static ClassLoadUnloadBreakpoint create (
        int breakpointType
    ) {
        ClassLoadUnloadBreakpoint b = new ClassLoadUnloadBreakpoint ();
        b.setBreakpointType (breakpointType);
        return b;
    }

    /**
     * Returns type of breakpoint.
     *
     * @return type of breakpoint
     */
    public int getBreakpointType () {
        return type;
    }

    /**
     * Sets type of breakpoint.
     *
     * @param type a new value of breakpoint type property
     */
    public void setBreakpointType (int type) {
        if (type == this.type) return;
        if ((type & (TYPE_CLASS_LOADED | TYPE_CLASS_UNLOADED)) == 0)
            throw new IllegalArgumentException  ();
        int old = this.type;
        this.type = type;
        firePropertyChange (PROP_BREAKPOINT_TYPE, new Integer (old), new Integer (type));
    }

    /**
     * Get list of class filters to stop on.
     *
     * @return list of class filters to stop on
     */
    public String[] getClassFilters () {
        return classFilters;
    }

    /**
     * Set list of class filters to stop on.
     *
     * @param classFilters a new value of class filters property
     */
    public void setClassFilters (String[] classFilters) {
        if (classFilters == this.classFilters) return;
        Object old = this.classFilters;
        this.classFilters = classFilters;
        firePropertyChange (PROP_CLASS_FILTERS, old, classFilters);
    }

    /**
     * Get list of class exclusion filters to stop on.
     *
     * @return list of class exclusion filters to stop on
     */
    public String[] getClassExclusionFilters () {
        return classExclusionFilters;
    }

    /**
     * Set list of class exclusion filters to stop on.
     *
     * @param classExclusionFilters a new value of class exclusion filters property
     */
    public void setClassExclusionFilters (String[] classExclusionFilters) {
        if (classExclusionFilters == this.classExclusionFilters) return;
        Object old = this.classExclusionFilters;
        this.classExclusionFilters = classExclusionFilters;
        firePropertyChange (PROP_CLASS_EXCLUSION_FILTERS, old, classExclusionFilters);
    }

    /**
     * Returns a string representation of this object.
     *
     * @return  a string representation of the object
     */
    public String toString () {
        return "ClassLoadUnloadBreakpoint " + classFilters;
    }
}
