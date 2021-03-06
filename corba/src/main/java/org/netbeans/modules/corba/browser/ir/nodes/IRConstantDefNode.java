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

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;

import java.io.PrintWriter;
import java.io.IOException;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import org.openide.nodes.*;
import org.openide.TopManager;
import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;
import org.openide.util.datatransfer.ExClipboard;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.Generatable;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupportFactory;
import org.netbeans.modules.corba.browser.ir.util.StringConvert;

/**
 * Class ConstantDefNode
 *
 */
public class IRConstantDefNode extends IRLeafNode implements Node.Cookie, Generatable {

    ConstantDef _constant;
  

    private static final String CONSTANT_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/const";
  
  
    private class ConstantCodeGenerator implements GenerateSupport {
    
    
        public String generateHead (int indent, StringHolder currentPrefix){
            return Util.generatePreTypePragmas (_constant.id(), _constant.absolute_name(), currentPrefix, indent);
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead (indent, currentPrefix);
            String  fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "const ";
            code = code + Util.idlType2TypeString ( _constant.type_def(),((IRContainerNode)getParentNode()).getOwner())+ " ";
            code = code + _constant.name() + " = ";
            code = code + getValue() + ";\n";
            code = code + generateTail(indent) +"\n";
            return code;
        }
    
        public String generateTail (int indent){
            return Util.generatePostTypePragmas (_constant.name(), _constant.id(), indent);
        }
    
        /** Returns the value of constant as String
         *  @return String value
         */
        public String getValue() {
            Any value = _constant.value();
            switch (value.type().kind().value()){
            case TCKind._tk_boolean:
                if (value.extract_boolean())
                    return "TRUE";
                else
                    return "FALSE";
            case TCKind._tk_char:
                return "\'" + StringConvert.convert(value.extract_char()) + "\'";
            case TCKind._tk_wchar:
                return "L\'" + StringConvert.convert(value.extract_wchar()) + "\'";
            case TCKind._tk_string:
                return "\"" + StringConvert.convert(value.extract_string()) + "\"";
            case TCKind._tk_wstring:
                return "L\"" + StringConvert.convert(value.extract_wstring()) + "\"";
            case TCKind._tk_float:
                return new Float(value.extract_float()).toString();
            case TCKind._tk_double:
                return new Double(value.extract_double()).toString();
            case TCKind._tk_longdouble:
                return new Double(value.extract_double()).toString();
            case TCKind._tk_fixed:
                return value.extract_fixed().toString();
            case TCKind._tk_short:
                return new Short(value.extract_short()).toString();
            case TCKind._tk_long:
                return new Integer(value.extract_long()).toString();
            case TCKind._tk_longlong:
                return new Long (value.extract_longlong()).toString();
            case TCKind._tk_ushort:
                return new Short(value.extract_ushort()).toString();
            case TCKind._tk_ulong:
                return new Integer(value.extract_ulong()).toString();
            case TCKind._tk_ulonglong:
                return new Long (value.extract_ulonglong()).toString();
            case TCKind._tk_enum:
                    try{
                        org.omg.CORBA.portable.InputStream in = value.create_input_stream();
                            int val = in.read_long();
                            String name = value.type().member_name(val);
                            return name;
                        }catch(Exception e){
                            return "";
                        }
            default:
                return "";
            }
        }
        
        public String getRepositoryId () {
            return _constant.id();
        }
  
    }
  

    public IRConstantDefNode(Contained value) {
        super ();
        _constant = ConstantDefHelper.narrow (value);
        this.getCookieSet().add(this);
        setIconBase (CONSTANT_ICON_BASE);
        this.getCookieSet().add ( new ConstantCodeGenerator ());
    }

    public String getDisplayName () {
        if (this.name == null) {
            if (_constant != null)
                this.name = _constant.name ();
            else 
                this.name = "";
        }
        return this.name;
    }

    public String getName () {
        return this.getDisplayName ();
    }
  
    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_ConstName")) {
                public java.lang.Object getValue () {
                    return _constant.name ();
                }
            });
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"), String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString("TIP_ConstId")) {
                public java.lang.Object getValue () {
                    return _constant.id ();
                }
            });
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), 
                                              Util.getLocalizedString("TIP_ConstVersion")) {
                public java.lang.Object getValue () {
                    return _constant.version ();
                }
            });
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Type"), String.class,Util.getLocalizedString("TITLE_Type"), 
                                              Util.getLocalizedString("TIP_ConstType")) {
                public java.lang.Object getValue () {
                    return Util.typeCode2TypeString (_constant.type ());
                }
            });
    
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Value"),String.class,Util.getLocalizedString("TITLE_Value"),Util.getLocalizedString("TIP_ConstValue")){
                public java.lang.Object getValue(){
                    return ((ConstantCodeGenerator)IRConstantDefNode.this.getCookie(GenerateSupport.class)).getValue();
                }
            });
    
        return s;
    }

    public org.omg.CORBA.IRObject getIRObject () {
        return this._constant;
    }
    
    public void generateCode() {     
        ExClipboard clipboard = TopManager.getDefault().getClipboard();
        StringSelection genCode = new StringSelection ( this.generateHierarchy ());
        clipboard.setContents(genCode,genCode);
    }

    public void generateCode (PrintWriter out) throws IOException {
        out.println ( this.generateHierarchy ());
    }

    private String generateHierarchy () {
        Node node = this.getParentNode();
        String code ="";
        // Generate the start of namespace
        ArrayList stack = new ArrayList();
        while ( node instanceof IRContainerNode){
            stack.add(node.getCookie(GenerateSupport.class));
            node = node.getParentNode();
        }
        StringHolder currentPrefix = new StringHolder ("");
        int size = stack.size();
        for (int i = size -1 ; i>=0; i--)
            code = code + ((GenerateSupport)stack.get(i)).generateHead((size -i -1), currentPrefix);
        // Generate element itself
        code = code + ((GenerateSupport) this.getCookie (GenerateSupport.class)).generateSelf(size, currentPrefix);
        //Generate tail of namespace
        for (int i = 0; i< stack.size(); i++)
            code = code + ((GenerateSupport)stack.get(i)).generateTail((size -i));
        return code;
    }
}

/*
 * $Log
 * $
 */
