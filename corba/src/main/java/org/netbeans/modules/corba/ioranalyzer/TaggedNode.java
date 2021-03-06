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

/*
 * TaggedNode.java
 *
 * Created on November 7, 2000, 3:54 PM
 */

package org.netbeans.modules.corba.ioranalyzer;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.*;

/**
 *
 * @author  tzezula
 * @version
 */
public class TaggedNode extends AbstractNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/ioranalyzer/resources/taggedprofile";
    private static final char[] table = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    private int index;
    private IORTaggedProfile profile;
    

    /** Creates new TaggedNode */
    public TaggedNode(int index, IORTaggedProfile profile) {
        super (Children.LEAF);
        this.index = index;
        this.profile = profile;
        this.setIconBase (ICON_BASE);
    }
    
    public String getName () {
        return this.getDisplayName();
    }
    
    public String getDisplayName () {
        return NbBundle.getBundle(TaggedNode.class).getString("TXT_Profile") + " " + Integer.toString(this.index);
    }
    
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(TaggedNode.class).getString("TXT_ProfileP"), String.class, NbBundle.getBundle(TaggedNode.class).getString("TXT_ProfileP"), NbBundle.getBundle(TaggedNode.class).getString("TIP_ProfileP")) {
            public Object getValue () {
                return Integer.toString (profile.getTag());
            }
        });
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(TaggedNode.class).getString("TXT_ProfileData"), String.class, NbBundle.getBundle(TaggedNode.class).getString("TXT_ProfileData"), NbBundle.getBundle(TaggedNode.class).getString("TIP_ProfileData")) {
            public Object getValue () {
                StringBuffer buffer = new StringBuffer();
                byte[] data = profile.getData();
                for (int i=0; i< data.length; i++) {
                    if (data[i]<0x20) 
                        buffer.append ("\\"+toHexStr(data[i]));
                    else
                        buffer.append((char)data[i]);
                }
                return buffer.toString();
            }
            
            private String toHexStr (byte value) {
                char[] res = new char[4];
                res[0]='0';
                res[1]='x';
                res[2]=table[((value>>4)&0xf)];
                res[3]=table[(value&0xf)];
                return new String (res);
            }
        });
        return s;
    }
    

}
