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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.netbeans.modules.latex.bibtex.IncrementalParserTestStub.*;

/**
 *
 * @author Jan Lahoda
 */
public class IncrementalParserTest extends TestCase {
    
    /** Creates a new instance of IncrementalParserTest */
    public IncrementalParserTest(String name) {
        super(name);
    }
    
    public PrintStream getLog() {
        return System.err;
    }
    
    public void setUp() {
        System.setProperty("org.openide.util.Lookup", "org.netbeans.modules.latex.bibtex.UnitLookup");
    }
    
    private URL getTestFile() throws MalformedURLException {
        URL file = IncrementalParserTest.class.getResource("data/test.bib");
        
        if (file == null) {
            throw new NullPointerException("The given resource not found!");
        }
        
        return file;
//        return new URL("file://" + new File(getDataDir(), "test.tex").getAbsolutePath());//TODO: CORRECT!
    }
    
    private void performTest(URL file, Description description) throws Exception {
        try {
            IncrementalParserTestStub.performTest(file, description);
        } catch (Exception e) {
            AssertionFailedError error = new AssertionFailedError("Test Case Failed");
            
            error.initCause(e);
            
            throw error;
        }
    }
    
    public void testTyping() throws Exception {
        performTest(getTestFile(),
        new Description(
"\n@INPROCEEDINGS{FB03,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}\n"+
"\n"+
"@INPROCEEDINGS{FB05,\n"+
"    title = \"title\",\n"+
"    journal = \"test - journal\",\n"+
"    author = \"test\",\n"+
"}\n", new Change[] {
        new AddChange(IncrementalParserTestStub.DEFAULT_VALIDATOR, "year = \"2003\",\n", "note = \"this is a note\",\n"),
        new AddChange(IncrementalParserTestStub.DEFAULT_VALIDATOR, "\n@INPROCEEDINGS", "@ARTICLE{FB01,\nauthor=\"X. Test and Y. Coder\",\ntitle=\"Testing Environment\"\n}"),
        new RemoveChange(IncrementalParserTestStub.DEFAULT_VALIDATOR, "    pages = \"142-143\",", "    pages = \"142-143\",".length()),
        new AddChange(IncrementalParserTestStub.DEFAULT_VALIDATOR, "author = \"X. Foo and Y. Bar\",\n", " pages = \"142-143\",\n"),
        }));
    }
    
    public void testCopyPaste1() throws Exception {
        Validator pasteValidator = new ProxyValidator(new Validator[] {
            IncrementalParserTestStub.DEFAULT_VALIDATOR,
            new TagValidator(new String[] {"FB01", "FB03", "FB05"}),
        }
        );
        performTest(getTestFile(),
        new Description(
"%d\n@INPROCEEDINGS{FB03,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}\n"+
"\n"+
"@INPROCEEDINGS{FB05,\n"+
"    title = \"title\",\n"+
"    journal = \"test - journal\",\n"+
"    author = \"test\",\n"+
"}\n", new Change[] {
        new PasteChange(pasteValidator, "@INPROCEEDINGS{FB03", "@INPROCEEDINGS{FB01,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}"),
        new DeleteChange(IncrementalParserTestStub.DEFAULT_VALIDATOR, "@INPROCEEDINGS{FB01", ("@INPROCEEDINGS{FB01,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}").length()),
        }));
    }
    
    public void testAddEntry1() throws Exception {
        Validator v = new ProxyValidator(new Validator[] {
            IncrementalParserTestStub.DEFAULT_VALIDATOR,
            new TagValidator(new String[] {"FB03", "FB05", "FB07"})
        });
        
        PublicationEntry newEntry = new PublicationEntry();
        
        newEntry.setTag("FB07");
        newEntry.setType("ARTICLE");
        newEntry.setTitle("On BiBTeX Files Processing");
        newEntry.setAuthor("Joe Hacker");
        
        Map content = new HashMap();
        
        content.put("url", "http://somwhere.org");
        
        newEntry.setContent(content);
        
        performTest(getTestFile(),
        new Description(
"%d\n@INPROCEEDINGS{FB03,\n" +
"    year = \"2003\",\n" +
"    pages = \"142-143\",\n"+
"    title = \"Do anything:\" # test # \" Continued\",\n"+
"    booktitle = \"Proc. of the 3rd Conference on LaTeX editors\",\n"+
"    author = \"X. Foo and Y. Bar\",\n"+
"}\n"+
"\n"+
"@INPROCEEDINGS{FB05,\n"+
"    title = \"title\",\n"+
"    journal = \"test - journal\",\n"+
"    author = \"test\",\n"+
"}\n\n", new Change[] {
        new AddEntryChange(v, newEntry)
        }));
    }

    public void testAddEntryIntoEmptyFile() throws Exception {
        Validator v = new ProxyValidator(new Validator[] {
            IncrementalParserTestStub.DEFAULT_VALIDATOR,
            new TagValidator(new String[] {"FB07"})
        });
        
        PublicationEntry newEntry = new PublicationEntry();
        
        newEntry.setTag("FB07");
        newEntry.setType("ARTICLE");
        newEntry.setTitle("On BiBTeX Files Processing");
        newEntry.setAuthor("Joe Hacker");
        
        Map content = new HashMap();
        
        content.put("url", "http://somwhere.org");
        
        newEntry.setContent(content);
        
        performTest(getTestFile(),
        new Description(
"", new Change[] {
        new AddEntryChange(v, newEntry)
        }));
    }

}
