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

package org.netbeans.modules.corba.wizard.nodes;

import org.openide.nodes.Node;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;
/**
 *
 * @author  root
 * @version
 */
public class StructNode extends SENode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/struct";

    /** Creates new StructNode */
    public StructNode(NamedKey key) {
        super (key);
        this.setName (key.getName());
        this.setIconBase (ICON_BASE);
    }
  
    public String generateSelf (int indent) {
        String code = new String ();
        String fill = new String ();
        for ( int i=0; i< indent; i++)
            fill = fill + SPACE;
        code = fill + "struct "+this.getName()+" {\n"; // No I18N
        Node[] nodes = this.getChildren().getNodes();
        for (int i=0; i<nodes.length; i++) {
            code = code + ((AbstractMutableIDLNode)nodes[i]).generateSelf (indent+1);
        }
        code = code + fill + "};\n";
        return code;
    }
    
    public ExPanel getEditPanel () {
        ModulePanel p = new ModulePanel ();
        p.setName (this.getName());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof ModulePanel) {
            String newName = ((ModulePanel)p).getName();
            if (!this.key.getName().equals(newName)) {
                this.key.setName (newName);
                this.setName (newName);
            }
        }
    }
}
