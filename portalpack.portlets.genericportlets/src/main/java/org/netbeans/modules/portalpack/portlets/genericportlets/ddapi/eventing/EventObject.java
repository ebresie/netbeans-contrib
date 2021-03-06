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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing;

import java.net.URI;
import javax.xml.namespace.QName;

/**
 *
 * @author Satyaranjan
 */
public class EventObject {
    
    private QName qName;
    private String name;
    private String valueType;
    private URI defaultNameSpace;
    private QName[] aliases;
    private String type;
    private String publicRenderParamId;
    public static final String PUBLIC_RENDER_PARAMETER_TYPE = "public_render_parameter";
    public static final String EVENT_TYPE = "eventing";
    
    public void setQName(QName qName)
    {
        this.qName = qName;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public QName getQName()
    {
        return qName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public URI getDefaultNameSpace()
    {
        return defaultNameSpace;
    }
    
    public String getDefaultNameSpaceAsString()
    {
        if(defaultNameSpace == null) return null;
        return defaultNameSpace.toString();
    }
    
    public void setDefaultNameSpace(URI defaultNameSpace)
    {
        this.defaultNameSpace = defaultNameSpace;
    }
    
    public QName[] getAlias()
    {
        if(aliases == null) return new QName[0];
        return aliases;
    }
    
    public void setAlias(QName[] qnames)
    {
        this.aliases = qnames;
    }
    
    public boolean isQName()
    {
        if(qName != null)
            return true;
        else
            return false;
    }
    
    public boolean isName()
    {
        if(name != null)
            return true;
        else
            return false;
    }
    
    public void setValueType(String valueType)
    {
        this.valueType = valueType;
    }
    
    public String getValueType()
    {
        return valueType;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public void setPublicRenderParamId(String id)
    {
        this.publicRenderParamId = id;
    }
    public String getPublicRenderParamId()
    {
        return publicRenderParamId;
    }
    
}
