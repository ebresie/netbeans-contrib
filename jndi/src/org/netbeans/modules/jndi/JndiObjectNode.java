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

package org.netbeans.modules.jndi;

import javax.naming.CompositeName;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.BasicAttribute;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.IOException;
import java.awt.datatransfer.*;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import org.openide.TopManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node.Cookie;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.ExClipboard;
import org.netbeans.modules.jndi.utils.JndiPropertyMutator;

/** Common base class for JndiNode and JndiLeafNode.
* The class provides copy (source generating)/delete actions.
*
* @author Ales Novak, Tomas Zezula
*/
abstract class JndiObjectNode extends JndiAbstractNode implements Cookie, TemplateCreator, JndiPropertyMutator{

    public static final String JNDI_PROPERTIES = "Jndi";

    private JndiKey key;

    /**
    * @param JndiKey key
    * @param children
    */
    public JndiObjectNode(JndiKey key, Children children) {
        this (key.name.getName(), children);
        this.key = key;
    }
    
    public JndiObjectNode (String name, Children children) {
        super (children,name);
        getCookieSet().add(this);
    }


    /** Returns the key for which this node was created or
     *  null for root of the naming system
     *  @return JndiKey key
     */
    public JndiKey getKey(){
        return this.key;
    }

    /** @return true */
    public final boolean canCopy() {
        return false;
    }

    /** @return @link isRoot */
    public boolean canDestroy() {
        return true;
    }

    /** Creates property sheet for the node
     *  @return Sheet the property sheet
     */
    public Sheet createSheet () {
        Sheet sheet = Sheet.createDefault ();
        Sheet.Set jndiSet = new Sheet.Set();
        jndiSet.setName(JNDI_PROPERTIES);
        jndiSet.setDisplayName(JndiRootNode.getLocalizedString("TITLE_JndiProperty"));
        sheet.put( jndiSet);
        sheet.get (Sheet.PROPERTIES).put (
            new JndiProperty ("NAME",
                              String.class,
                              JndiRootNode.getLocalizedString("TXT_Name"),
                              JndiRootNode.getLocalizedString("TIP_Name"),
                              this.getName ()));
        sheet.get (Sheet.PROPERTIES).put (
            new JndiProperty ("OFFSET",
                              String.class,
                              JndiRootNode.getLocalizedString("TXT_Path"),
                              JndiRootNode.getLocalizedString("TIP_Path"),
                              this.getOffsetAsString()));
        sheet.get(Sheet.PROPERTIES).put (
            new JndiProperty ("CLASS",
                              String.class,
                              JndiRootNode.getLocalizedString("TXT_Class"),
                              JndiRootNode.getLocalizedString("TIP_Class"),
                              this.getClassName()));
        try{
            Enumeration keys =	this.getContext().getEnvironment ().keys();
            Enumeration elements =this.getContext().getEnvironment ().elements ();
            while (keys.hasMoreElements()){
                String key = (String)keys.nextElement();
                String value = (String)elements.nextElement();
                if (key.equals(JndiRootNode.NB_ROOT)){
                    if (value.length()>0){
                        sheet.get (Sheet.PROPERTIES).put (
                            new JndiProperty ("ROOT",
                                              String.class,
                                              JndiRootNode.getLocalizedString("TXT_Start"),
                                              value));
                    }
                }
                else if (key.equals(JndiRootNode.NB_LABEL)) {
                    continue;
                }
                else{
                    sheet.get (Sheet.PROPERTIES).put (
                        new JndiProperty (key,
                                          String.class,
                                          key,
                                          value));
                }
            }
        }catch(NamingException ne) {}
        setSheet (sheet);
        return sheet;
    }

    /** Returns initial dir context
     *  @return DirContext initial context of this JNDI subtree 
     */
    public abstract Context getContext();

    /** Creates a java source code for obtaining
     *  reference to this node
     *  @return String the java source code
     *  @exception NamingException when a JNDI fault happends.
     */
    public abstract String createTemplate() throws NamingException;

    /** Returns the offset of this Node in subtree of his context
     * @return CompositeName the offset in subtree
     */
    public abstract CompositeName getOffset();
    
    /** Returns the offset of this Node in subtree as string
     * @return String the stringified offset
     */ 
    public abstract String getOffsetAsString ();
    
    
    /** Returns class name of Jndi Object
     *  @return String class name
     */
    public abstract String getClassName();

    /** Inserts generated text into the clipboard */
    public final void lookupCopy() {
        try {
            ExClipboard clipboard = TopManager.getDefault().getClipboard();
            StringSelection code = new StringSelection(createTemplate());
            clipboard.setContents(code,code);
            JndiRootNode.showLocalizedStatus("STS_CopyLookupCode");
        } catch (NamingException ne) {
            JndiRootNode.notifyForeignException(ne);
        }
    }


    /** Uniform destroy for Nodes in Childer.Array and Children.Kyes
     *  @exception IOException
     */
    public void destroy () throws IOException {
        super.destroy();
        Node node = this.getParentNode();
        if (node instanceof JndiNode){
            // Children Kyes is used, refresh it
            ((JndiNode)node).refresh();
        }
    }

    /** Does nothing. */
    public void refresh() {
        throw new UnsupportedOperationException();
    }


    /** Changes the value of property
     *  @param String name of the property
     *  @param Object value of property
     */
    public boolean changeJndiPropertyValue(String name,Object value) {
        try{
            BasicAttributes attrs = new BasicAttributes();
            BasicAttribute attr = new BasicAttribute(name);
            if (value instanceof String) {
                attrs.put (stringToAttr (attr, (String)value));
            }
            else {
                attr.add (value);
                attrs.put (attr);
            }
            this.handleChangeJndiPropertyValue (attrs);
        }catch (NamingException ne){
            JndiRootNode.notifyForeignException(ne);
            return false;
        }
        return true;
    }
    
    protected String attrToString (Attribute attr) throws NamingException {
        String result = new String ();
        for (int i=0; i< attr.size(); i++) {
            if (i != 0)
                result = result + JndiRootNode.getLocalizedString ("TXT_ValueSeparator");
            result = result + attr.get (i).toString ();
        }
        return result;
    }
    
    protected Attribute stringToAttr (Attribute attr, String str) {
        StringTokenizer tk = new StringTokenizer (str, JndiRootNode.getLocalizedString ("TXT_ValueSeparator").trim ());
        while (tk.hasMoreTokens ()) {
            String element = tk.nextToken ().trim ();
            attr.add (element);
        }
        return attr;
    }
    
    protected abstract void handleChangeJndiPropertyValue (Attributes attrs) throws NamingException;
}

/*
* <<Log>>
*  12   Gandalf-post-FCS1.10.2.0    2/24/00  Ian Formanek    Post FCS changes
*  11   Gandalf   1.10        1/14/00  Tomas Zezula    
*  10   Gandalf   1.9         1/14/00  Tomas Zezula    
*  9    Gandalf   1.8         12/17/99 Tomas Zezula    
*  8    Gandalf   1.7         12/15/99 Tomas Zezula    
*  7    Gandalf   1.6         12/15/99 Tomas Zezula    
*  6    Gandalf   1.5         11/5/99  Tomas Zezula    
*  5    Gandalf   1.4         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  4    Gandalf   1.3         10/6/99  Tomas Zezula    
*  3    Gandalf   1.2         8/7/99   Ian Formanek    getString->getLocalizedString
*        to avoid compiler warnings
*  2    Gandalf   1.1         7/9/99   Ales Novak      localization + code 
*       requirements followed
*  1    Gandalf   1.0         6/18/99  Ales Novak      
* $
*/
