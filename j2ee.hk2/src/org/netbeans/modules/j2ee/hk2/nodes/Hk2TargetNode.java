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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.j2ee.hk2.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Ludo
 */
public class Hk2TargetNode extends AbstractNode {
    
    public Hk2TargetNode(Lookup lookup) {
        super(new Children.Array());
        
        // J2EE Applications
        getChildren().add(new Node[] {
            new Hk2ItemNode(lookup,
                    new Hk2ApplicationsChildren(lookup),
                    NbBundle.getMessage(Hk2TargetNode.class, "LBL_Apps"),
                    Hk2ItemNode.ItemType.J2EE_APPLICATION_FOLDER)
        });
        
//////        // Native Data Sources
//////        getChildren().add(new Node[] {
//////            new Hk2ItemNode(lookup,
//////                    new OC4JNativeDataSourcesChildren(lookup),
//////                    NbBundle.getMessage(Hk2TargetNode.class, "LBL_NativeDataSources"),
//////                    Hk2ItemNode.ItemType.REFRESHABLE_FOLDER)
//////        });
//////        
//////        // Managed Data Sources
//////        getChildren().add(new Node[] {
//////            new Hk2ItemNode(lookup,
//////                    new OC4JManagedDataSourcesChildren(lookup),
//////                    NbBundle.getMessage(Hk2TargetNode.class, "LBL_ManagedDataSources"),
//////                    Hk2ItemNode.ItemType.REFRESHABLE_FOLDER)
//////        });
//////        
//////        // Connection Pools
//////        getChildren().add(new Node[] {
//////            new Hk2ItemNode(lookup,
//////                    new OC4JConnectionPoolsChildren(lookup),
//////                    NbBundle.getMessage(Hk2TargetNode.class, "LBL_ConnectionPools"),
//////                    Hk2ItemNode.ItemType.REFRESHABLE_FOLDER)
//////        });
    }
}