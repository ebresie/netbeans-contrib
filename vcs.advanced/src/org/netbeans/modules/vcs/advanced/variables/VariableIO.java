/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.variables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.text.MessageFormat;

import org.w3c.dom.*;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.XMLDataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.VcsConfigVariable;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;

/**
 * This class provides input/output of variables from/to xml file.
 *
 * @author  Martin Entlicher
 */
public class VariableIO extends Object {

    public static final String CONFIG_FILE_EXT = "xml";
    public static final String CONFIG_ROOT_ELEM = "configuration";
    public static final String LABEL_TAG = "label";
    public static final String VARIABLES_TAG = "variables";
    public static final String VARIABLE_TAG = "variable";
    public static final String VARIABLE_SELECTOR_TAG = "selector";
    public static final String VARIABLE_NAME_ATTR = "name";
    public static final String VARIABLE_LABEL_ATTR = "label";
    public static final String VARIABLE_BASIC_ATTR = "basic";
    public static final String VARIABLE_LOCAL_FILE_ATTR = "localFile";
    public static final String VARIABLE_LOCAL_DIR_ATTR = "localDir";
    public static final String VARIABLE_EXECUTABLE_ATTR = "executable";
    public static final String VARIABLE_ORDER_ATTR = "order";
    public static final String VARIABLE_VALUE_TAG = "value";
    
    public static final String BOOLEAN_VARIABLE_TRUE = "true";
    public static final String BOOLEAN_VARIABLE_FALSE = "false";

    /** Creates new VariableIO */
    private VariableIO() {
    }

    private static boolean bundleListContains(FileObject[] list, String name) {
        for(int i = 0; i < list.length; i++) {
            String buName = list[i].getName();
            if (buName.equals(name)) return true;
        }
        return false;
    }

    /**
     * Find out whether the locale extension exists as a locale name.
     */
    private static boolean isLocale(String localeExt) {
        String lang;
        int index = localeExt.indexOf('_');
        if (index > 0) lang = localeExt.substring(0, index);
        else lang = localeExt;
        String[] languages = Locale.getISOLanguages();
        List list = Arrays.asList(languages);
        return list.contains(lang);
    }
    
    /** Read list of available confugurations from the directory.
     * All files with extension ".properties" are considered to be configurations.
     * However only properties with current localization are read.
     * @return the available configurations.
     */
    public static ArrayList readConfigurations(FileObject file) {
        //ArrayList res = new ArrayList();
        FileObject[] ch = file.getChildren();
        ArrayList list = new ArrayList(Arrays.asList(ch));
        for(int i = 0; i < list.size(); i++) {
            FileObject fo = (FileObject) list.get(i);
            String ext = fo.getExt();
            if (!ext.equalsIgnoreCase(CONFIG_FILE_EXT)
                && !ext.equalsIgnoreCase(VariableIOCompat.CONFIG_FILE_EXT)
                || CommandLineVcsFileSystem.isTemporaryConfig(fo.getName())) {
                list.remove(i);
                i--;
            }
        }
        ch = (FileObject[]) list.toArray(new FileObject[0]);
        ArrayList res = getLocalizedConfigurations(ch);
        hideOlderConfs(res);
        return res;
    }
    
    private static void hideOlderConfs(ArrayList list) {
        for(int i = 0; i < list.size(); i++) {
            String fullName = (String) list.get(i);
            int dotIndex = fullName.lastIndexOf('.');
            String ext = fullName.substring(dotIndex + 1);
            if (VariableIOCompat.CONFIG_FILE_EXT.equals(ext)) {
                String name = fullName.substring(0, dotIndex);
                String newName = name + "." + CONFIG_FILE_EXT;
                if (list.contains(newName)) {
                    list.remove(i);
                    i--;
                }
            }
        }
        
    }
    
    /**
     * Pick the right localized configurations.
     */
    public static final ArrayList getLocalizedConfigurations(FileObject[] ch) {
        ArrayList res = new ArrayList();
        Locale locale = Locale.getDefault();
        for(int i = 0; i < ch.length; i++) {
            String name = ch[i].getName();
            //System.out.println("name = "+name+", locale = "+locale.toString());
            int nameIndex = name.indexOf('_');
            String baseName = name;
            String localeExt = "";
            if (nameIndex > 0) {
                baseName = name.substring(0, nameIndex);
                localeExt = name.substring(nameIndex + 1);
            }
            if (localeExt.equals(locale.toString())) ; // OK
            else if (localeExt.equals(locale.getLanguage()+"_"+locale.getCountry())) {
                if (bundleListContains(ch, baseName+"_"+locale.toString())) continue; // current variant is somewhere
            } else if (localeExt.equals(locale.getLanguage())) {
                if (bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()) ||
                    bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()+"_"+locale.getVariant())) continue;
            } else if (localeExt.length() == 0) {
                if (bundleListContains(ch, baseName+"_"+locale.getLanguage()) ||
                    bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()) ||
                    bundleListContains(ch, baseName+"_"+locale.getLanguage()+"_"+locale.getCountry()+"_"+locale.getVariant())) continue;
            } else if (localeExt.length() > 0 && isLocale(localeExt)) continue;
            //System.out.println("adding: "+name+"."+ch[i].getExt());
            res.add(name+"."+ch[i].getExt());
        }
        return res;
    }

    /** Open file and load configurations from it.
     * @param configRoot the directory which contains properties.
     * @param name the name of properties to read.
     */
    public static Document readPredefinedConfigurations(FileObject configRoot, final String name){
        Document doc = null;
        FileObject config = configRoot.getFileObject(name);
        if (config == null) {
            org.openide.TopManager.getDefault().notifyException(new FileNotFoundException("Problems while reading predefined properties.") {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
            //E.err(g("EXC_Problems_while_reading_predefined_properties",name)); // NOI18N
            return doc;
        }
        try {
            DataObject dobj = DataObject.find(config);
            if (dobj != null && dobj instanceof XMLDataObject) {
                doc = ((XMLDataObject) dobj).getDocument();
            }
            //InputStream in = config.getInputStream();
            //props.load(in);
            //in.close();
        } catch(DataObjectNotFoundException e) {
            org.openide.TopManager.getDefault().notifyException(new DataObjectNotFoundException(config) {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
        } catch(IOException e){
            org.openide.TopManager.getDefault().notifyException(new IOException("Problems while reading predefined properties.") {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
        } catch (org.xml.sax.SAXException sexc) {
            org.openide.TopManager.getDefault().notifyException(new org.xml.sax.SAXException("Problems while reading predefined properties.", sexc) {
                public String getLocalizedMessage() {
                    return g("EXC_Problems_while_reading_predefined_properties", name);
                }
            });
        }
        return doc;
    }

    public static String getConfigurationLabel(Document doc) throws DOMException {
        NodeList labelList = doc.getElementsByTagName(LABEL_TAG);
        String label = "";
        if (labelList.getLength() > 0) {
            Node labelNode = labelList.item(0);
            NodeList textList = labelNode.getChildNodes();
            if (textList.getLength() > 0) {
                Node subNode = textList.item(0);
                if (subNode instanceof Text) {
                    Text textNode = (Text) subNode;
                    label = textNode.getData();
                }
            }
        }
        return label;
    }

    /** Read list of VCS variables from the document.
     * If there is only value specified, label is empty string and basic, localFile
     * and localDir are false.
     */
    public static Vector readVariables(Document doc) throws DOMException {
        Element rootElem = doc.getDocumentElement();
        if (!CONFIG_ROOT_ELEM.equals(rootElem.getNodeName())) return null;
        Vector vars;
        NodeList varList = rootElem.getElementsByTagName(VARIABLES_TAG);
        if (varList.getLength() > 0) {
            Node varsNode = varList.item(0);
            vars = getVariables(varsNode.getChildNodes());
        } else return new Vector(); // an empty vector of variables
        return vars;
    }
    
    private static Vector getVariables(NodeList varList) throws DOMException {
        Vector vars = new Vector();
        int n = varList.getLength();
        for (int i = 0; i < n; i++) {
            Node varNode = varList.item(i);
            if (VARIABLE_TAG.equals(varNode.getNodeName())) {
                String name = "";
                //boolean isBasic = false;
                String value = "";
                NamedNodeMap varAttrs = varNode.getAttributes();
                Node nameAttr = varAttrs.getNamedItem(VARIABLE_NAME_ATTR);
                if (nameAttr == null) continue;
                name = nameAttr.getNodeValue();
                NodeList valueList = varNode.getChildNodes();
                //System.out.println("valueList.length = "+valueList.getLength());
                int m = valueList.getLength();
                for (int j = 0; j < m; j++) {
                    Node valueNode = valueList.item(j);
                    //System.out.println("value("+j+") name = "+valueNode.getNodeName());
                    if (VARIABLE_VALUE_TAG.equals(valueNode.getNodeName())) {
                        NodeList textList = valueNode.getChildNodes();
                        for (int itl = 0; itl < textList.getLength(); itl++) {
                            Node subNode = textList.item(itl);
                            //System.out.println("    subNode = "+subNode);
                            if (subNode instanceof Text) {
                                Text textNode = (Text) subNode;
                                value += textNode.getData();
                            }
                            if (subNode instanceof EntityReference) {
                                EntityReference entityNode = (EntityReference) subNode;
                                //System.out.println("Have EntityReference = "+entityNode+", value = "+entityNode.getNodeValue());
                                NodeList entityList = entityNode.getChildNodes();
                                for (int iel = 0; iel < entityList.getLength(); iel++) {
                                    Node entitySubNode = entityList.item(iel);
                                    //System.out.println("    entitySubNode = "+entitySubNode);
                                    if (entitySubNode instanceof Text) {
                                        Text textEntityNode = (Text) entitySubNode;
                                        value += textEntityNode.getData();
                                    }
                                }
                            }
                            //System.out.println("    propertyValue = "+propertyValue);
                        }
                        /*
                        if (textList.getLength() > 0) {
                            Node subNode = textList.item(0);
                            //System.out.println("subNode = "+subNode.getNodeName());
                            if (subNode instanceof Text) {
                                Text textNode = (Text) subNode;
                                value = textNode.getData();
                                //System.out.println("value = '"+value+"'");
                            }
                        }
                         */
                    }
                }
                VcsConfigVariable var;
                Node basicAttr = varAttrs.getNamedItem(VARIABLE_BASIC_ATTR);
                if (basicAttr != null && BOOLEAN_VARIABLE_TRUE.equalsIgnoreCase(basicAttr.getNodeValue())) {
                    var = getBasicVariable(name, value, varNode, varAttrs);
                } else {
                    var = new VcsConfigVariable(name, "", value, false, false, false, "");
                }
                vars.add(var);
            }
        }
        return vars;
    }
    
    private static boolean getBooleanAttrVariable(String attrName, NamedNodeMap varAttrs) throws DOMException {
        Node attrNode = varAttrs.getNamedItem(attrName);
        if (attrNode != null && BOOLEAN_VARIABLE_TRUE.equalsIgnoreCase(attrNode.getNodeValue())) {
            return true;
        }
        return false;
    }
    
    private static VcsConfigVariable getBasicVariable(String name, String value, Node varNode, NamedNodeMap varAttrs) throws DOMException {
        String label = "";
        boolean localFile = false;
        boolean localDir = false;
        boolean executable = false;
        String customSelector = "";
        int order = -1;
        Node attrNode = varAttrs.getNamedItem(VARIABLE_LABEL_ATTR);
        if (attrNode != null) label = attrNode.getNodeValue();
        localFile = getBooleanAttrVariable(VARIABLE_LOCAL_FILE_ATTR, varAttrs);
        localDir = getBooleanAttrVariable(VARIABLE_LOCAL_DIR_ATTR, varAttrs);
        executable = getBooleanAttrVariable(VARIABLE_EXECUTABLE_ATTR, varAttrs);
        attrNode = varAttrs.getNamedItem(VARIABLE_ORDER_ATTR);
        if (attrNode != null) {
            try {
                order = Integer.parseInt(attrNode.getNodeValue());
            } catch (NumberFormatException exc) {
                order = -1;
            }
        }
        NodeList childList = varNode.getChildNodes();
        int n = childList.getLength();
        for (int i = 0; i < n; i++) {
            Node childNode = childList.item(i);
            if (VARIABLE_SELECTOR_TAG.equals(childNode.getNodeName())) {
                customSelector = childNode.getNodeValue();
                break;
            }
        }
        VcsConfigVariable var = new VcsConfigVariable(name, label, value, true, localFile, localDir, customSelector, order);
        var.setExecutable(executable);
        return var;
    }

    /** Write list of VCS variables to the document.
     * If there is only value specified, label is empty string and basic, localFile
     * and localDir are false.
     */
    public static void writeVariables(Document doc, String label, Vector vars) throws DOMException {
        Element rootElem = doc.createElement(CONFIG_ROOT_ELEM);
        doc.appendChild(rootElem);
        Element labelNode = doc.createElement(LABEL_TAG);
        Text labelText = doc.createTextNode(label);
        labelNode.appendChild(labelText);
        rootElem.appendChild(labelNode);
        Element varsNode = doc.createElement(VARIABLES_TAG);
        putVariables(doc, varsNode, vars);
        rootElem.appendChild(varsNode);
    }
    
    private static void putVariables(Document doc, Node varsNode, Vector vars) throws DOMException {
        int n = vars.size();
        for (int i = 0; i < n; i++) {
            VcsConfigVariable var = (VcsConfigVariable) vars.get(i);
            Element varElem = doc.createElement(VARIABLE_TAG);
            varElem.setAttribute(VARIABLE_NAME_ATTR, var.getName());
            Element valueElem = doc.createElement(VARIABLE_VALUE_TAG);
            Text valueNode = doc.createTextNode(var.getValue());
            valueElem.appendChild(valueNode);
            varElem.appendChild(valueElem);
            //varElem.setNodeValue(var.getValue());
            varElem.setAttribute(VARIABLE_BASIC_ATTR, (var.isBasic()) ? BOOLEAN_VARIABLE_TRUE : BOOLEAN_VARIABLE_FALSE);
            if (var.isBasic()) {
                varElem.setAttribute(VARIABLE_LABEL_ATTR, var.getLabel());
                varElem.setAttribute(VARIABLE_LOCAL_FILE_ATTR, (var.isLocalFile()) ? BOOLEAN_VARIABLE_TRUE : BOOLEAN_VARIABLE_FALSE);
                varElem.setAttribute(VARIABLE_LOCAL_DIR_ATTR, (var.isLocalDir()) ? BOOLEAN_VARIABLE_TRUE : BOOLEAN_VARIABLE_FALSE);
                varElem.setAttribute(VARIABLE_EXECUTABLE_ATTR, (var.isLocalDir()) ? BOOLEAN_VARIABLE_TRUE : BOOLEAN_VARIABLE_FALSE);
                varElem.setAttribute(VARIABLE_ORDER_ATTR, ""+var.getOrder());
            }
            String selector = var.getCustomSelector();
            if (selector != null && selector.length() > 0) {
                Element selectorElem = doc.createElement(VARIABLE_SELECTOR_TAG);
                selectorElem.setNodeValue(selector);
                varElem.appendChild(selectorElem);
            }
            varsNode.appendChild(varElem);
        }
    }
    
    private static String g(String s) {
        return NbBundle.getBundle(VariableIO.class).getString (s);
    }
    private static String g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
}
