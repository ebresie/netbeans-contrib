/*
 * CDDL HEADER START
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.sun.com/cddl/cddl.html and legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 * CDDL HEADER END
 */

package org.netbeans.modules.portalpack.portlets.taglib;

import javax.servlet.jsp.tagext.BodyTagSupport;


/**
 * This class provides methods that are common to both PortletURL and ResourceURL.
 * 
 * This class applicable for Portlet v2.0
 */
public abstract class BaseURLTag extends BodyTagSupport{
   
    public void setEscapeXml(boolean escapeXml) {
   
    }

    protected boolean isEscapeXml() {
        return true;
    }
            
    public void setSecure(String secure) {
        
    }

    protected String getSecure() {
       return null;
    }
    
    public void setVar(String var) {
        
    }

    protected String getVar() {
        return null;
    }
    
}
