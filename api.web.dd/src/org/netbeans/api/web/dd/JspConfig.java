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

package org.netbeans.api.web.dd;
import org.netbeans.api.web.dd.common.*;
/**
 * Generated interface for JspConfig element.
 *
 *<p><b><font color="red"><em>Important note: Do not provide an implementation of this interface unless you are a DD API provider!</em></font></b>
 *</p>
 */
public interface JspConfig extends CommonDDBean, FindCapability, CreateCapability {
        /** Setter for taglib element.
         * @param index position in the array of elements
         * @param valueInterface taglib element (Taglib object)
         */
	public void setTaglib(int index, org.netbeans.api.web.dd.Taglib valueInterface);
        /** Getter for taglib element.
         * @param index position in the array of elements
         * @return taglib element (Taglib object)
         */
	public org.netbeans.api.web.dd.Taglib getTaglib(int index);
        /** Setter for taglib elements.
         * @param value array of taglib elements (Taglib objects)
         */
	public void setTaglib(org.netbeans.api.web.dd.Taglib[] value);
        /** Getter for taglib elements.
         * @return array of taglib elements (Taglib objects)
         */
	public org.netbeans.api.web.dd.Taglib[] getTaglib();
        /** Returns number of taglib elements.
         * @return number of taglib elements 
         */
	public int sizeTaglib();
        /** Adds taglib element.
         * @param valueInterface taglib element (Taglib object)
         * @return index of new taglib
         */
	public int addTaglib(org.netbeans.api.web.dd.Taglib valueInterface);
        /** Removes taglib element.
         * @param valueInterface taglib element (Taglib object)
         * @return index of the removed taglib
         */
	public int removeTaglib(org.netbeans.api.web.dd.Taglib valueInterface);
        /** Setter for jsp-property-group element.
         * @param index position in the array of elements
         * @param valueInterface jsp-property-group element (JspPropertyGroup object)
         */
	public void setJspPropertyGroup(int index, org.netbeans.api.web.dd.JspPropertyGroup valueInterface);
        /** Getter for jsp-property-group element.
         * @param index position in the array of elements
         * @return jsp-property-group element (JspPropertyGroup object)
         */
	public org.netbeans.api.web.dd.JspPropertyGroup getJspPropertyGroup(int index);
        /** Setter for jsp-property-group elements.
         * @param value array of jsp-property-group elements (JspPropertyGroup objects)
         */
	public void setJspPropertyGroup(org.netbeans.api.web.dd.JspPropertyGroup[] value);
        /** Getter for jsp-property-group elements.
         * @return array of jsp-property-group elements (JspPropertyGroup objects)
         */
	public org.netbeans.api.web.dd.JspPropertyGroup[] getJspPropertyGroup();
        /** Returns number of jsp-property-group elements.
         * @return number of jsp-property-group elements 
         */
	public int sizeJspPropertyGroup();
        /** Adds jsp-property-group element.
         * @param valueInterface jsp-property-group element (JspPropertyGroup object)
         * @return index of new jsp-property-group
         */
	public int addJspPropertyGroup(org.netbeans.api.web.dd.JspPropertyGroup valueInterface);
        /** Removes jsp-property-group element.
         * @param valueInterface jsp-property-group element (JspPropertyGroup object)
         * @return index of the removed jsp-property-group
         */
	public int removeJspPropertyGroup(org.netbeans.api.web.dd.JspPropertyGroup valueInterface);

}
