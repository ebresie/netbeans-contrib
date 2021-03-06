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

package org.netbeans.modules.tasklist.suggestions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import org.openide.loaders.XMLDataObject.Processor;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.openide.util.actions.SystemAction;

import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import javax.xml.parsers.SAXParserFactory;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.loaders.XMLDataObject;
import org.xml.sax.helpers.DefaultHandler;
import org.openide.xml.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.openide.util.Lookup;


/** Processor of the Suggestion Type XML file.
 * The parsing result is an instance of a SuggestionType object
 * <p>
 * Based on AnnotationTypeProcessor in the editor package.
 * <p>
 *
 * @author  Tor Norbye, David Konecny, Peter Nejedly
 */
public final class SuggestionTypeProcessor implements InstanceCookie, Processor {
    static final String DTD_PUBLIC_ID = "-//NetBeans//DTD suggestion type 1.0//EN"; // NOI18N
    static final String DTD_SYSTEM_ID = "http://www.netbeans.org/dtds/suggestion-type-1_0.dtd"; // NOI18N
    
    static final String TAG_TYPE = "type"; //NOI18N
    static final String ATTR_TYPE_NAME = "name"; // NOI18N
    static final String ATTR_TYPE_LOCALIZING_BUNDLE = "localizing_bundle"; // NOI18N
    static final String ATTR_TYPE_DESCRIPTION_KEY = "description_key"; // NOI18N
    static final String ATTR_TYPE_LONGDESCRIPTION_KEY = "long_description_key"; // NOI18N
    static final String ATTR_TYPE_ICON = "icon"; // NOI18N
    static final String TAG_TYPE_ACTIONS = "actions"; // NOI18N
    static final String TAG_TYPE_ACTION = "action"; // NOI18N
    static final String ATTR_ACTION_CLASS = "class"; // NOI18N

    /** XML data object. */
    private XMLDataObject xmlDataObject;
    
    /**
     * Suggestion type created from XML file.
     */
    private SuggestionType  suggestionType;

    /** When the XMLDataObject creates new instance of the processor,
     * it uses this method to attach the processor to the data object.
     *
     * @param xmlDO XMLDataObject
     */
    public void attachTo(XMLDataObject xmlDO) {
        xmlDataObject = xmlDO;
    }
    
    /** Create an instance.
     * @return the instance of type {@link #instanceClass}
     * @exception IOException if an I/O error occured
     * @exception ClassNotFoundException if a class was not found
     */
    public Object instanceCreate() throws IOException, ClassNotFoundException {
        if (suggestionType != null)
            return suggestionType;

        parse();
        return suggestionType;
    }
    
    /** The representation type that may be created as instances.
     * Can be used to test whether the instance is of an appropriate
     * class without actually creating it.
     *
     * @return the representation class of the instance
     * @exception IOException if an I/O error occurred
     * @exception ClassNotFoundException if a class was not found
     */
    public Class instanceClass() {
        return SuggestionType.class;
    }
    
    /** The bean name for the instance.
     * @return the name
     */
    public String instanceName() {
        return instanceClass().getName();
    }

    ////////////////////////////////////////////////////////////////////////

    private synchronized SuggestionType parse() {
        if (suggestionType == null) {
            try {
                    XMLReader reader = XMLUtil.createXMLReader(false);
                    
                    TypeHandler handler = new TypeHandler();
                    reader.setContentHandler(handler);
                    reader.setErrorHandler(handler);
                    reader.setEntityResolver(handler);
                    reader.parse(new InputSource(xmlDataObject.getPrimaryFile().getInputStream()));
                    suggestionType = handler.getSuggestionType();
            } catch (Exception e) { 
                ErrorManager.getDefault().notify(e);
            }

        }
        return suggestionType;
    }
    
    private static class TypeHandler extends DefaultHandler {
        private SuggestionType type = null;
        
        String id = null;
        List actions = null;
        String localizer = null;
        String key = null;
        String longkey = null;
        URL icon = null;
        
        SuggestionType getSuggestionType() {
            return type;
        }

        public void startElement(String uri, String localName,
                                 String name, Attributes attrs) 
            throws SAXException {
            if (TAG_TYPE.equals(name)) {
            // basic properties
            id = attrs.getValue(ATTR_TYPE_NAME);
            actions = null;
            
            // localization stuff
            localizer = attrs.getValue(ATTR_TYPE_LOCALIZING_BUNDLE);
            key = attrs.getValue(ATTR_TYPE_DESCRIPTION_KEY);
            longkey = attrs.getValue(ATTR_TYPE_LONGDESCRIPTION_KEY);
            
            // icon
            icon = null;
            String ur = attrs.getValue(ATTR_TYPE_ICON);
            if (ur != null) {
                try {
                    icon = new URL(ur);
                } catch (MalformedURLException ex) {
                    SAXException saxe = new SAXException(ex);
                    ErrorManager.getDefault().
                        copyAnnotation(saxe, ex);
                    throw saxe;
                }
            }
            } else if (TAG_TYPE_ACTIONS.equals(name)) {
                // Ignore
            } else if (TAG_TYPE_ACTION.equals(name)) {
                String cln = attrs.getValue(ATTR_ACTION_CLASS);
                if (cln == null) {
                    return;
                }
                try {
                    ClassLoader l = 
                        (ClassLoader)Lookup.getDefault().lookup(
                                                        ClassLoader.class);
                    Class c = Class.forName(cln, true, l);

                    if (c != null) {
                        SystemAction a = SystemAction.get(c);
                        if (a != null) {
                            if (actions == null) {
                                actions = new ArrayList();
                            }
                            actions.add(a);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    ErrorManager.getDefault().annotate(e, "TL: cannot load " + cln + " action, ignoring..."); // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            } else {
                throw new SAXException("malformed SuggestionType xml file"); // NOI18N
            }
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            if (TAG_TYPE_ACTION.equals(name)) {
                // Ignore
            } else if (TAG_TYPE_ACTIONS.equals(name)) {
                // Ignore
            } else if (TAG_TYPE.equals(name)) {
                if (id != null) {
                    type = new SuggestionType(id, localizer, 
                                              key, longkey, icon, 
                                              actions);
                }
            }
        }


        public InputSource resolveEntity(java.lang.String pid,
                                         java.lang.String sid) throws SAXException {
            if (DTD_PUBLIC_ID.equals(pid)) {
                return new InputSource(new ByteArrayInputStream(new byte[0]));
            }            
            return new InputSource (sid);            
        }
    }
    
}
