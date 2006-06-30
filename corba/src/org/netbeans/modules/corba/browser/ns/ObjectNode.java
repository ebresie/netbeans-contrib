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

package org.netbeans.modules.corba.browser.ns;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

import java.io.*;
import java.net.*;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;

import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.settings.*;
/*
 * @author Karel Gardas
 */

public class ObjectNode extends NamingServiceNode implements Node.Cookie {

    static final String ICON_BASE
    = "org/netbeans/modules/corba/browser/ns/resources/interface";

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    private Binding binding;

    public ObjectNode () {
        super (Children.LEAF);
        //super (Children.LEAF);
        init ();
    }

    public ObjectNode (Binding b, String ref) {
        super (new ObjectNodeChildren());
        binding = b;
        setName (binding.binding_name[0].id);
        setKind (binding.binding_name[0].kind);
        setIOR (ref);
        init ();
    }

    public void init () {
        if (DEBUG)
            System.out.println ("ObjectNode () :-)");
        setDisplayName (getName ());
        setIconBase (ICON_BASE);

        systemActions = new SystemAction[] {
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.UnbindObject.class),
                            null,
                            SystemAction.get (org.netbeans.modules.corba.browser.ns.CopyClientCode.class),
                            null,
                            SystemAction.get(org.openide.actions.PropertiesAction.class)
                        };
    }


    public Node.Cookie getCookie(Class c) {
        if (c.isInstance(this))
            return this;
        else
            return super.getCookie(c);
    }

    public void unbind () {
        NameComponent name_component = new NameComponent (getName (), getKind ()); // name, kind
        NameComponent[] context_name = new NameComponent[1];
        context_name[0] = name_component;
        try {
            ((ContextNode)getParentNode ()).getContext ().unbind (context_name);
            ((ContextChildren)((ContextNode)getParentNode ()).getChildren ()).addNotify ();
        } catch (Exception e) {
            TopManager.getDefault().notify (new NotifyDescriptor.Message (e.toString(), NotifyDescriptor.Message.ERROR_MESSAGE));
        }
    }
    
    public org.omg.CORBA.InterfaceDef createInterface () {
        try {
            if (this.getIOR() != null) {
                org.omg.CORBA.Object ref = this.getORB().string_to_object (getIOR());
                return org.omg.CORBA.InterfaceDefHelper.narrow (ref._get_interface_def ());
            }
            return null;
        }catch (Exception e) {
            return null;
        }
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (ObjectNode.class.getName());
    }

}

/*
 * $Log
 * $
 */
