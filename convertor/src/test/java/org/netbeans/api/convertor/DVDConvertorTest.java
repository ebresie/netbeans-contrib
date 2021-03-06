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

package org.netbeans.api.convertor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import org.netbeans.junit.*;
import junit.textui.TestRunner;
import org.netbeans.api.convertor.dvd.DVD;
import org.netbeans.spi.convertor.Convertor;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;


/**
 *
 * @author  David Konecny
 */
public class DVDConvertorTest extends NbTestCase {
    

    public DVDConvertorTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(DVDConvertorTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        FileUtil.getConfigRoot ();
    }
    
    private static Convertor conv;
    
    public static void setupConvertor() throws Exception {
        ModuleUtils.DEFAULT.install();
        ModuleUtils.DEFAULT.enableDVDConvertorModule(true);
    }
    
    public static void removeConvertor() throws Exception {
        ModuleUtils.DEFAULT.enableDVDConvertorModule(false);
        ModuleUtils.DEFAULT.uninstall();
    }
    
    public void testDVDConvertor() throws Exception {
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/dvd", "dvd"));
        assertFalse(Convertors.canWrite(new DVD()));
        
        setupConvertor();
        
        assertTrue(Convertors.canRead("http://www.netbeans.org/ns/dvd", "dvd"));
        assertTrue(Convertors.canWrite(new DVD()));

//        assertEquals(Convertors.getConvertorDescriptor(new DVD()), new ConvertorDescriptor("http://www.netbeans.org/ns/dvd", "org.netbeans.api.convertor.dvd.DVD"));

        String name = DVDConvertorTest.class.getResource("dvd").getFile() + "/data/DVD.xml";
        InputStream is = new FileInputStream(name);
        DVD d = (DVD)Convertors.read(is);
        assertEquals(d.ID, 125);
        assertEquals(d.title, "Tetsuo");
        assertEquals(d.publisher, "TartanTerror");
        assertEquals(d.price, 19);
        is.close();
        
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DVD dvd = new DVD(698, "Zentropa", "TartanClassic", 65);
        Convertors.write(os, dvd);
        
        byte[] ba = os.toByteArray();
        os.close();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(ba);
        Object o = Convertors.read(bis);
        assertEquals(dvd, o);
        
        removeConvertor();
        assertFalse(Convertors.canRead("http://www.netbeans.org/ns/dvd", "dvd"));
        assertFalse(Convertors.canWrite(new DVD()));
    }
    
}
