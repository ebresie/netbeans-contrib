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
import org.openide.nodes.Node;
import org.netbeans.modules.corba.browser.ir.util.Refreshable;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRTypeCodeKey;
import org.netbeans.modules.corba.browser.ir.nodes.keys.IRContainedKey;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;

public class ExceptionChildren extends Children implements Refreshable {


    private ExceptionDef exception;
    private boolean container;

    /** Creates new ExceptionChildren */
    public ExceptionChildren(Contained contained) {
        super();
        try {
            this.container = (contained._is_a("IDL:omg.org/CORBA/Container:1.0") || contained._is_a("IDL:omg.org/CORBA/Container:2.3"));
	}catch (Exception e) {
            this.container = false;
	}
        this.exception = ExceptionDefHelper.narrow(contained);
    }
    
    public ExceptionDef getExceptionStub () {
        return this.exception;
    }


    public void addNotify(){
        synchronized (this) {
            if (this.state == SYNCHRONOUS) {
                this.createKeys();
                this.state = INITIALIZED;
            }
            else {
                this.state = TRANSIENT;
                this.waitNode = new WaitNode ();
                this.add ( new Node[] { this.waitNode});
                org.netbeans.modules.corba.browser.ir.IRRootNode.getDefault().performAsync (this);
            }
        }
    }


    public void createKeys(){
        try {
            StructMember [] members = this.exception.members();
            Contained[] contained = null;
            
            if (this.container) {
                contained = this.exception.contents (DefinitionKind.dk_all, false);
            }
            else {
                contained = new Contained [0];
            }
        
            java.lang.Object[] keys = new java.lang.Object[members.length + contained.length];
            for (int i =0; i< contained.length; i++)
                keys[i] = new IRContainedKey (contained[i]);
            for (int i =0; i< members.length; i++)
                keys[contained.length + i] = new IRTypeCodeKey (members[i].name, members[i].type_def);
            setKeys(keys);
        }catch (final SystemException e) {
            setKeys (new java.lang.Object[0]);
            java.awt.EventQueue.invokeLater ( new Runnable () {
                public void run () {
                    TopManager.getDefault().notify(new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                }});
        }
    }


    public Node[] createNodes (java.lang.Object key){
        if (key != null){
            if (key instanceof IRTypeCodeKey) {
                return new Node[]{new IRPrimitiveNode(((IRTypeCodeKey)key).type, ((IRTypeCodeKey)key).name)};
            }
            else if (key instanceof IRContainedKey) {
                Contained contained = ((IRContainedKey)key).contained;
                switch (contained.def_kind().value()) {
                    case DefinitionKind._dk_Struct:
                        return new Node[] { new IRStructDefNode (contained)};
                    case DefinitionKind._dk_Union:
                        return new Node[]{ new IRUnionDefNode(contained)};
                    case DefinitionKind._dk_Enum:
                        return new Node[]{ new IREnumDefNode(contained)};
                    default:
                        return new Node[]{ new IRUnknownTypeNode()};
                }
            }
        }
        return new Node[]{new IRUnknownTypeNode()};
    }

}
