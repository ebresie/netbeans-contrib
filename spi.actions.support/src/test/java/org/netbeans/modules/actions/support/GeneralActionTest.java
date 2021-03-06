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
 * Software is Sun Microsystems, Inc. Portions Copyright 2006 Sun
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

package org.netbeans.modules.actions.support;

import java.awt.event.ActionListener;
import javax.swing.Action;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;

/**
 *
 * @author Jaroslav Tulach
 */
public class GeneralActionTest extends NbTestCase {
    private FileObject folder;
    
    public GeneralActionTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        Lookup.getDefault().lookup(org.openide.modules.ModuleInfo.class);
        folder = FileUtil.getConfigFile("actions/support/test");
        assertNotNull("testing layer is loaded: ", folder);
    }
    
    protected void tearDown() throws Exception {
    }
    
    protected boolean runInEQ() {
        return true;
    }
    
    public void testKeyMustBeProvided() {
        String key = null;
        Action defaultDelegate = null;
        Lookup context = Lookup.EMPTY;
        
        ContextAwareAction expResult = null;
        try {
            ContextAwareAction result = GeneralAction.callback(key, defaultDelegate, context, false);
            fail("Shall fail as key is null");
        } catch (NullPointerException ex) {
            // ok
        }
    }
    
    public void testIconIsCorrect() throws Exception {
        myListenerCounter = 0;
        myIconResourceCounter = 0;
        Action a = readAction("testIconIsCorrect.instance");
        
        assertNotNull("Action created", a);
        assertEquals("No myListener called", 0, myListenerCounter);
        assertEquals("No myIconURL called", 0, myIconResourceCounter);
        
        Object name = a.getValue(a.NAME);
        Object mnem = a.getValue(a.MNEMONIC_KEY);
        Object smallIcon = a.getValue(a.SMALL_ICON);
        //Object icon = a.getValue(a.ICON)
            
        assertEquals("Right localized name", "Icon &Name Action", name);
        assertEquals("Mnemonic is N", Character.valueOf('N'), mnem);
        assertNotNull("small icon present", smallIcon);

        assertEquals("once icon called", 1, myIconResourceCounter);

        
        Object base = a.getValue("iconBase"); 
        assertEquals("iconBase attribute is delegated", 2, myIconResourceCounter);
     
        assertTrue("Always enabled", a.isEnabled());
        a.setEnabled(false);
        assertTrue("Still Always enabled", a.isEnabled());
        
        
        assertEquals("No icon in menu", Boolean.TRUE, a.getValue("noIconInMenu"));
        
        if (a instanceof ContextAwareAction) {
            fail("Should not be context sensitive, otherwise it would have to implement equal correctly: " + a);
        }
    }
    
    private static int myListenerCounter;
    private static ActionListener myListener() {
        myListenerCounter++;
        return null;
    }
    private static int myIconResourceCounter;
    private static String myIconResource() {
        myIconResourceCounter++;
        return "/org/netbeans/modules/actions/support/TestIcon.png";
    }
    
    
    private Action readAction(String fileName) throws Exception {
        FileObject fo = this.folder.getFileObject(fileName);
        assertNotNull("file " + fileName, fo);
        
        Object obj = fo.getAttribute("instanceCreate");
        assertNotNull("File object has not null instanceCreate attribute", obj);
        
        if (!(obj instanceof Action)) {
            fail("Object needs to be action: " + obj);
        }
        
        return (Action)obj;
    }
}
