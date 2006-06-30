/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.web.dd;
/**
 * Generated interface for ServiceRef element.
 *
 *<p><b><font color="red"><em>Important note: Do not provide an implementation of this interface unless you are a DD API provider!</em></font></b>
 *</p>
 * @deprecated Use the API for web module deployment descriptor in j2ee/ddapi module.
 */
public interface ServiceRef extends org.netbeans.api.web.dd.common.ComponentInterface {
        /** Setter for service-ref-name property.
         * @param value property value
         */
	public void setServiceRefName(java.lang.String value);
        /** Getter for service-ref-name property.
         * @return property value 
         */
	public java.lang.String getServiceRefName();
        /** Setter for service-interface property.
         * @param value property value
         */
	public void setServiceInterface(java.lang.String value);
        /** Getter for service-interface property.
         * @return property value 
         */
	public java.lang.String getServiceInterface();
        /** Setter for wsdl-file property.
         * @param value property value
         */
	public void setWsdlFile(java.net.URI value);
        /** Getter for wsdl-file property.
         * @return property value 
         */
	public java.net.URI getWsdlFile();
        /** Setter for jaxrpc-mapping-file property.
         * @param value property value
         */
	public void setJaxrpcMappingFile(java.lang.String value);
        /** Getter for jaxrpc-mapping-file property.
         * @return property value 
         */
	public java.lang.String getJaxrpcMappingFile();
        /** Setter for service-qname property.
         * @param value property value
         */
	public void setServiceQname(java.lang.String value);
        /** Getter for service-qname property.
         * @return property value 
         */
	public java.lang.String getServiceQname();
        /** Setter for port-component-ref element.
         * @param index position in the array of elements
         * @param valueInterface port-component-ref element (PortComponentRef object)
         */
	public void setPortComponentRef(int index, org.netbeans.api.web.dd.PortComponentRef valueInterface);
        /** Getter for port-component-ref element.
         * @param index position in the array of elements
         * @return port-component-ref element (PortComponentRef object)
         */
	public org.netbeans.api.web.dd.PortComponentRef getPortComponentRef(int index);
        /** Setter for port-component-ref elements.
         * @param value array of port-component-ref elements (PortComponentRef objects)
         */
	public void setPortComponentRef(org.netbeans.api.web.dd.PortComponentRef[] value);
        /** Getter for port-component-ref elements.
         * @return array of port-component-ref elements (PortComponentRef objects)
         */
	public org.netbeans.api.web.dd.PortComponentRef[] getPortComponentRef();
        /** Returns size of port-component-ref elements.
         * @return number of port-component-ref elements 
         */
	public int sizePortComponentRef();
        /** Adds port-component-ref element.
         * @param valueInterface port-component-ref element (PortComponentRef object)
         * @return index of new port-component-ref
         */
	public int addPortComponentRef(org.netbeans.api.web.dd.PortComponentRef valueInterface);
        /** Removes port-component-ref element.
         * @param valueInterface port-component-ref element (PortComponentRef object)
         * @return index of the removed port-component-ref
         */
	public int removePortComponentRef(org.netbeans.api.web.dd.PortComponentRef valueInterface);
        /** Setter for handler element.
         * @param index position in the array of elements
         * @param valueInterface handler element (SeviceRefHandler object)
         */
	public void setHandler(int index, org.netbeans.api.web.dd.ServiceRefHandler valueInterface);
        /** Getter for handler element.
         * @param index position in the array of elements
         * @return handler element (SeviceRefHandler object)
         */
	public org.netbeans.api.web.dd.ServiceRefHandler getHandler(int index);
        /** Setter for handler elements.
         * @param value array of handler elements (SeviceRefHandler objects)
         */
	public void setHandler(org.netbeans.api.web.dd.ServiceRefHandler[] value);
        /** Getter for handler elements.
         * @return array of handler elements (SeviceRefHandler objects)
         */
	public org.netbeans.api.web.dd.ServiceRefHandler[] getHandler();
        /** Returns size of handler elements.
         * @return number of handler elements 
         */
	public int sizeHandler();
        /** Adds handler element.
         * @param valueInterface handler element (SeviceRefHandler object)
         * @return index of new handler
         */
	public int addHandler(org.netbeans.api.web.dd.ServiceRefHandler valueInterface);
        /** Removes handler element.
         * @param valueInterface handler element (SeviceRefHandler object)
         * @return index of the removed handler
         */
	public int removeHandler(org.netbeans.api.web.dd.ServiceRefHandler valueInterface);

}
