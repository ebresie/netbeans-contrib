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

package org.netbeans.signatures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import junit.framework.TestCase;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

/**
 * @author Jesse Glick
 */
public class SignatureTaskTest extends TestCase {
    
    public SignatureTaskTest(String n) {
        super(n);
    }
    
    private File antJar;
    private File antLauncherJar;
    private File antModuleJar;
    
    protected void setUp() throws Exception {
        super.setUp();
        antJar = new File(Class.forName("org.apache.tools.ant.Project").getProtectionDomain().getCodeSource().getLocation().toURI());
        assertTrue(antJar.getAbsolutePath(), antJar.isFile());
        antLauncherJar = new File(antJar.getParentFile(), "ant-launcher.jar");
        assertTrue(antLauncherJar.getAbsolutePath(), antLauncherJar.isFile());
        antModuleJar = new File(antJar.getParentFile().getParentFile().getParentFile().getParentFile(), "nbbuild/netbeans/ide8/modules/org-apache-tools-ant-module.jar");
        assertTrue(antModuleJar.getAbsolutePath(), antModuleJar.isFile());
    }
    
    public void testExecute() throws Throwable {
        Project p = new Project();
        SignatureTask task = new SignatureTask();
        task.setProject(p);
        File out = File.createTempFile("signatures", ".java");
        task.setOut(out);
        FileSet fs = new FileSet();
        fs.setProject(p);
        fs.setDir(antJar.getParentFile());
        fs.setIncludes(antJar.getName());
        task.addFileSet(fs);
        fs = new FileSet();
        fs.setProject(p);
        fs.setDir(antLauncherJar.getParentFile());
        fs.setIncludes(antLauncherJar.getName());
        task.addFileSet(fs);
        fs = new FileSet();
        fs.setProject(p);
        fs.setDir(antModuleJar.getParentFile());
        fs.setIncludes(antModuleJar.getName());
        task.addFileSet(fs);
        task.execute();
        assertTrue(out.isFile());
        Reader r = new FileReader(out);
        try {
            BufferedReader b = new BufferedReader(r);
            while (true) {
                String l = b.readLine();
                assertNotNull("found matching line in " + out, l);
                if (l.equals("{Class _ = org.apache.tools.ant.Task.class;}")) {
                    break;
                }
            }
        } finally {
            r.close();
        }
    }

}
