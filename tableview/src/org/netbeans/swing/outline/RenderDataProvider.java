/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * RenderDataProvider.java
 *
 * Created on January 29, 2004, 12:01 AM
 */

package org.netbeans.swing.outline;

import java.awt.Color;
import javax.swing.Icon;

/** A class which can provide rendering data for the tree portion an Outline,
 * such as converting values to text, providing tooltip text and icons.
 *
 * @author  Tim Boudreau
 */
public interface RenderDataProvider {
    /** Convert an object in the tree to the string that should be used to
     * display its node */
    public String getDisplayName (Object o);
    /** Returns true of the display name for this object should use HTML 
     * rendering (future support for integration of the lightweight HTML
     * renderer into NetBeans).  */
    public boolean isHtmlDisplayName (Object o);
    /** Get the background color to be used for rendering this node.  Return
     * null if the standard table background or selected color should be used.
     */
    public Color getBackground (Object o);
    /** Get the foreground color to be used for rendering this node.  Return
     * null if the standard table foreground or selected foreground should be
     * used. */
    public Color getForeground (Object o);
    /** Get a description for this object suitable for use in a tooltip.  Return
     * null if no tooltip is desired.  */
    public String getTooltipText (Object o);
    /** Get an icon to be used for this object.  Return null if the look and 
     * feel's default tree folder/leaf icons should be used as appropriate. */
    public Icon getIcon (Object o);
}
