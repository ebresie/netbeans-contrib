/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import junit.framework.*;
import org.netbeans.junit.*;

public class SCCSTest extends NbTestCase {
    
    private static void readProperties() throws IOException {
        File testPropertyFile = new File(
            System.getProperty("xtest.tmpdir"),
            "test.properties");
        System.getProperties().load(new FileInputStream(testPropertyFile));
    }
    
    private void copyFiles() throws IOException {
        clearWorkDir();
        File sccsDir = new File(getWorkDir(), "SCCS");
        sccsDir.mkdir();
        String dataDir = System.getProperty("data.dir");
        File[] dataFiles = new File(dataDir, "SCCS").listFiles();
        assertNotNull("Put some SCCS files in " + dataDir, dataFiles);
        byte[] buffer = new byte[4096];
        for (int i = 0; i < dataFiles.length; i++) {
            File outFile = new File(sccsDir, dataFiles[i].getName());
            copyFile(dataFiles[i], outFile);
            outFile.setReadOnly();
        }
    }
    
    protected File getFileCopy(File sourceFile) throws IOException {
        File sccsDir = new File(getWorkDir(), "SCCS");
        sccsDir.mkdir();
        File sourceSFile = new File(sourceFile.getParent(),
                "SCCS" + File.separator + "s." + sourceFile.getName());
        File destSFile = new File(sccsDir,
                "s." + sourceFile.getName());
        copyFile(sourceSFile, destSFile);
        return new File(getWorkDir(), sourceFile.getName());
    }
    
    protected static void copyFile(File source, File dest) throws IOException {
        byte[] buffer = new byte[4096];
        InputStream in =
            new BufferedInputStream(new FileInputStream(source));
        OutputStream out =
            new BufferedOutputStream(new FileOutputStream(dest));
        for (int bytesRead; (bytesRead = in.read(buffer)) != -1;) {
            out.write(buffer, 0, bytesRead);
        }
        in.close();
        out.close();
    }
    
    public SCCSTest(java.lang.String testName) {
        super(testName);
    }
    
    public SCCSTest() {
        super("");
        setName(getClass().getName());
    }
    
    protected void exec(File dir, String[] args,
        ByteArrayOutputStream stdout,
        ByteArrayOutputStream stderr) throws Exception {
        
        String[] argv = new String[args.length + 1];
        argv[0] = System.getProperty("sccs");
        System.arraycopy(args, 0, argv, 1, args.length);
        Process p = Runtime.getRuntime().exec(argv, null, dir);
        class StreamCopier implements Runnable {
            InputStream in;
            OutputStream out;
            StreamCopier(InputStream in, OutputStream out) {
                this.in = in;
                this.out = out;
            }
            public void run() {
                try {
                    for (int i; (i = in.read()) != -1;) {
                        if (out != null) {
                            out.write(i);
                        }
                    }
                } catch (IOException e) { }
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) { }
                try {
                    in.close();
                } catch (IOException e) { }
            }
        }
        Thread t1 = new Thread(new StreamCopier(p.getInputStream(), stdout));
        Thread t2 = new Thread(new StreamCopier(p.getErrorStream(), stderr));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
    
    protected File[] getTestFiles() throws IOException {
        File sccsDir = new File(getWorkDir(), "SCCS");
        File[] files = sccsDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(files[i].getParentFile().getParent(),
                files[i].getName().substring(2));
        }
        return files;
    }
    
    protected static File[] getReadOnlyTestFiles() throws IOException {
        readProperties();
        String dataDir = System.getProperty("data.dir");
        File sccsDir = new File(dataDir, "SCCS");
        File[] dataFiles = sccsDir.listFiles();
        for (int i = 0; i < dataFiles.length; i++) {
            dataFiles[i] = new File(dataDir,
                dataFiles[i].getName().substring(2));
        }
        return dataFiles;
    }
    
    protected List parseLines(byte[] b) throws IOException {
        List lines = new ArrayList();
        boolean dosFormat = true;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            if (b[i] == '\n') {
                if (dosFormat && (i == 0 || b[i - 1] != '\r')) {
                    dosFormat = false;
                    // go back and add trailing CRs
                    for (ListIterator j = lines.listIterator(); j.hasNext();) {
                        String s = (String) j.next();
                        j.set(s + "\r");
                    }
                }
                if (dosFormat) {
                    sb.setLength(sb.length() - 1);
                }
                lines.add(sb.toString());
                sb.setLength(0);
            } else {
                if (b[i] >= 0) {
                    sb.append((char) b[i]);
                } else {
                    sb.append(new String(b, i, 1));
                }
            }
        }
        return lines;
    }
    
    protected void log(ByteArrayOutputStream out) {
        log(new String(out.toByteArray()));
    }
    
    public void log(String s) {
        if (s.indexOf("\n") != -1) {
            int i = s.indexOf("\n");
            int start = 0;
            int line = 1;
            StringBuffer sb = new StringBuffer();
            while (i != -1) {
                String lineString = String.valueOf(line++);
                sb.append(lineString);
                for (int j = lineString.length(); j < 6; j++) {
                    sb.append(" ");
                }
                sb.append(s.substring(start, i + 1));
                
                start = i + 1;
                i = s.indexOf("\n", start);
            }
            String lineString = String.valueOf(line++);
            sb.append(lineString);
            for (int j = lineString.length(); j < 6; j++) {
                sb.append(" ");
            }
            sb.append(s.substring(start));
            super.log(sb.toString());
        } else {
            super.log(s);
        }
    }
    
    protected void logToFile(String name, ByteArrayOutputStream out) throws IOException {
        OutputStream os = new FileOutputStream(new File(getWorkDir(), name));
        os.write(out.toByteArray());
        os.close();
    }
    
    protected void assertEquals(byte[] b1, byte[] b2) {
        if (b1 == b2) {
            return;
        }
        assertNotNull(b1);
        assertNotNull(b2);
        for (int i = 0; i < b1.length && i < b2.length; i++) {
            try {
                assertEquals(b1[i], b2[i]);
            } catch (AssertionFailedError e) {
                throw new AssertionFailedError(
                    "Arrays differ at element " + i
                    + ": expected " + b1[i] + " but found " + b2[i]
                    + "\nExpected '" + new String(b1, 0, i + 1)
                    + "'\nbut found '" + new String(b2, 0, i + 1) + "'");
            }
        }
        assertEquals(b1.length, b2.length);
    }
    
    protected void assertStringCollectionEquals(List linesA, List linesB) {
        try {
            assertEquals(linesA, linesB);
        } catch (AssertionFailedError e) {
            for (int j = 0; j < linesA.size() && j < linesB.size(); j++) {
                String lineA = (String) linesA.get(j);
                String lineB = (String) linesB.get(j);
                if (lineA.equals(lineB)) {
                    log(lineA);
                } else {
                    int atLine = j + 1;
                    log("Difference in line " + atLine + ":");
                    log("Expected  '" + lineA + "'");
                    log("But found '" + lineB + "'");
                    log("");
                    log("Result continues: ");
                    while (++j < linesB.size()) {
                        log((String) linesB.get(j));
                    }
                    log("*** Expected: ***");
                    for (j = atLine; j < linesA.size(); j++) {
                        log((String) linesA.get(j));
                    }
                    throw new AssertionFailedError(
                        "Difference in line " + atLine + ": "
                        + "Expected  '" + lineA + "' but found '" + lineB + "'");
                }
            }
            if (linesA.size() > linesB.size()) {
                log("*** Result stops here - it should continue as follows ***");
                for (int j = linesB.size(); j < linesA.size(); j++) {
                    log((String) linesA.get(j));
                }
                throw new AssertionFailedError(
                    "Result stops at line " + linesB.size());
            } else {
                log("*** Result should stop here, but continues as follows ***");
                for (int j = linesA.size(); j < linesB.size(); j++) {
                    log((String) linesB.get(j));
                }
                throw new AssertionFailedError(
                    "Result should stop at line " + linesA.size());
            }
        }
    }
    
}
