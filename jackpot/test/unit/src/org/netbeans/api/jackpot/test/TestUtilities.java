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

package org.netbeans.api.jackpot.test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.modules.jackpot.engine.BuildErrorsException;
import org.netbeans.modules.jackpot.engine.CommandLineQueryContext;
import org.netbeans.modules.jackpot.engine.Engine;
import org.netbeans.modules.jackpot.engine.Result;
import org.openide.filesystems.FileUtil;

/**
 * Utilities to aid unit testing Jackpot rule files, Query and Transformer classes.
 *
 * @author Jaroslav Tulach
 * @author Tom Ball
 */
public final class TestUtilities {
    
    // do not instantiate
    private TestUtilities() {}
    
    /**
     * Tests whether a transformation makes an expected result.
     *
     * @param from   the source text to be transformed.
     * @param result the expected text after transformation.
     * @param rules  one or more rules that define the transformation to use.
     * @throws TransformationException if the transformed text doesn't match the result.
     * @throws java.lang.Exception 
     */
     public static void assertTransform(String from, String result, String rules) throws TransformationException, Exception {
        File src = copyStringToFile(getClassName(from), from);
        File rulesFile = copyStringToFile("test.rules", rules);
        apply(tempDirectory, rulesFile.getPath(), true);

        String txt = copyFileToString(src);
        if (!txt.equals(result))
            throw new TransformationException("expected: \"" + result + "\" got: \"" + txt + "\"");
    }
    
    /**
     * Tests whether a transformation makes an expected result.
     *
     * @param from   the source text to be transformed.
     * @param result the expected text after transformation.
     * @param clazz  the transformation class to use.
     * @throws TransformationException if the transformed text doesn't match the result.
     * @throws java.lang.Exception 
     */
     public static void assertTransform(String from, String result, Class clazz) throws TransformationException, Exception {
        File src = copyStringToFile(getClassName(from), from);
        String className = clazz.getName();
        apply(tempDirectory, className, false);

        String txt = copyFileToString(src);
        if (!txt.equals(result))
            throw new TransformationException("expected: \"" + result + "\" got: \"" + txt + "\"");
    }
     
     private static String getClassName(String src) {
         return null; // FIXME
     }

    /**
     * Applies a rule file to a directory of source files.
     * 
     * @param  dir the directory containing the source files to be modified.
     * @param  rules the rule file to apply to the source files.
     * @return the number of matches found
     * @throws BuildErrorsException 
     *         If any errors are found when building the source files.
     * @throws java.lang.Exception 
     */
    public static int applyRules(File dir, URL rules) 
            throws BuildErrorsException, Exception {
        return applyRules(dir, rules, false);
    }

    /**
     * Applies a rule file to a directory of source files.
     * 
     * @param  dir the directory containing the source files to be modified.
     * @param  rules the rule file to apply to the source files.
     * @param  allowErrors true if the rules should still be applied if there are build errors.
     * @return the number of matches found
     * @throws BuildErrorsException 
     *         If any errors are found when building the source files.
     * @throws java.lang.Exception 
     */
    public static int applyRules(File dir, URL rules, boolean allowErrors) 
            throws BuildErrorsException, Exception {
        File rulesFile = copyResourceToFile(rules);
        return apply(dir, rulesFile.getPath(), true).size();
    }

    /**
     * Applies a Query class to a directory of source files.
     * 
     * @param  dir the directory containing the source files to be modified.
     * @param  queryName the rules to apply to the source files.
     * @return the matches found
     * @throws BuildErrorsException 
     *         If any errors are found when building the source files.
     * @throws Exception
     *         If any changes were made to the source files after applying the 
     *         Query class.
     */
    public static List<Result> applyQuery(File dir, String queryName) 
            throws BuildErrorsException, Exception {
        return apply(dir, queryName, false);
    }

    /**
     * Applies a Transformer class to a directory of source files.
     * 
     * @param  dir the directory containing the source files to be modified.
     * @param  transformerName the rules to apply to the source files.
     * @return the number of matches found
     * @throws BuildErrorsException 
     *         If any errors are found when building the source files.
     * @throws java.lang.Exception 
     */
    public static int applyTransformer(File dir, String transformerName) 
            throws BuildErrorsException, Exception {
        return apply(dir, transformerName, false).size();
    }

    private static List<Result> apply(File dir, String cmd, boolean isScript) 
            throws BuildErrorsException, Exception {
        CommandLineQueryContext context = new CommandLineQueryContext();
        Engine eng = new Engine(context, dir.getPath(), System.getProperty("java.class.path"), null);

        ModificationResult result = isScript ? 
            eng.runScript("q", cmd) : eng.runCommand("q", cmd);
        result.commit();
        return context.getResults();
    }
    
    /**
     * Returns a string which contains the contents of a file.
     *
     * @param f the file to be read
     * @return the contents of the file(s).
     * @throws java.io.IOException 
     */
    public final static String copyFileToString (java.io.File f) throws java.io.IOException {
        int s = (int)f.length ();
        byte[] data = new byte[s];
        int len = new FileInputStream (f).read (data);
        if (len != s)
            throw new EOFException("truncated file");
        return new String (data);
    }
    
    /**
     * Returns a string which contains the contents of a GZIP compressed file.
     *
     * @param f the file to be read
     * @return the contents of the file(s).
     * @throws java.io.IOException 
     */
    public final static String copyGZipFileToString (java.io.File f) throws java.io.IOException {
        GZIPInputStream is = new GZIPInputStream(new FileInputStream(f));
        byte[] arr = new byte[256 * 256];
        int first = 0;
        for(;;) {
            int len = is.read(arr, first, arr.length - first);
            if (first + len < arr.length) {
                return new String(arr, 0, first + len);
            }
        }
    }
    
    /**
     * Copies a string to a specified file.
     *
     * @param f the file to use.
     * @param content the contents of the returned file.
     * @return the created file
     * @throws java.lang.Exception 
     */
    public final static File copyStringToFile (File f, String content) throws Exception {
        FileOutputStream os = new FileOutputStream(f);
        InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
        FileUtil.copy(is, os);
        os.close ();
            
        return f;
    }

    private final static File copyStringToFile(String filename, String res) throws Exception {
        File f = new File(tempDirectory, filename);
        f.deleteOnExit ();
        return copyStringToFile(f, res);
    }

    private final static File copyResourceToFile(URL u) throws Exception {
        File f = File.createTempFile("res", ".xml");
        f.deleteOnExit ();
       
        FileOutputStream os = new FileOutputStream(f);
        InputStream is = u.openStream();
        FileUtil.copy(is, os);
        os.close ();
            
        return f;
    }

    private static File tempDirectory;
    {
        try {
            File f = File.createTempFile("foo", "bar");
            tempDirectory = f.getParentFile();
        } catch (IOException e) {
            tempDirectory = new File("/tmp");
        }
    }
}