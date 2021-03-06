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

package org.netbeans.modules.htmldiff;

import java.io.*;
import junit.framework.*;

import org.netbeans.junit.*;

import org.netbeans.modules.htmldiff.ContentDiff.*;

/** Test diff for set of pages.
 *
 * @author Jaroslav Tulach
 */
public final class ContentDiffTest extends NbTestCase {

    public ContentDiffTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new NbTestSuite(ContentDiffTest.class);
        
        return suite;
    }
    
    static ContentDiff diff (final String[] oldPages, final String[] newPages) throws IOException {
        final java.net.URL o = new java.net.URL ("file:/old/");
        final java.net.URL n = new java.net.URL ("file:/new/");
        
        class S implements ContentDiff.Source {
            private Reader page (String[] arr, String page) throws IOException {
                for (int i = 0; i < arr.length; i += 2) {
                    if (arr[i].equals (page)) {
                        return new StringReader (arr[i + 1]);
                    }
                }
                throw new IOException ("Page not found: " + page);
            }
            
            public java.io.Reader getReader(java.net.URL url) throws IOException {
                if (url.toExternalForm().startsWith (o.toExternalForm())) {
                    return page (oldPages, url.toExternalForm ().substring (o.toExternalForm ().length()));
                }
                if (url.toExternalForm().startsWith (n.toExternalForm())) {
                    return page (newPages, url.toExternalForm ().substring (n.toExternalForm ().length()));
                }
                
                throw new IOException ("Wrong url: " + url);
            }    
        }
        
        
        
        return ContentDiff.diff (
            o, even (oldPages),
            n, even (newPages),
            new S ()
        );
    }
    
    private static java.util.Set even (String[] arr) {
        java.util.HashSet r = new java.util.HashSet ();
        for (int i = 0; i < arr.length; i += 2) {
            r.add (arr[i]);
        }
        return r;
    }
    
    public void testSimplePage () throws Exception {
        String[] pages = {
            "index.html", "<h1>Hi</h1> This is a simple page"
        };
        
        ContentDiff diff = diff (pages, pages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 1, diff.getClusters().length);
        assertEquals ("One page", 1, diff.getClusters()[0].getPages ().size ());
        assertTrue ("Cluster index.html", diff.getClusters()[0].getPages ().contains (diff.findPage ("index.html")));
        
        
        assertChanged ("No change should be there", diff.getClusters()[0]);
        assertEquals ("No change", 0, diff.getClusters()[0].getChanged ());
    }
    
    public void testSimplePageWithURL () throws Exception {
        String[] pages = {
            "index.html", "<h1>Hi</h1> This is a simple <a href=\"index.html\">page</a>."
        };
        
        ContentDiff diff = diff (pages, pages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 1, diff.getClusters().length);
        assertEquals ("One page", 1, diff.getClusters()[0].getPages ().size ());
        assertTrue ("Cluster index.html", diff.getClusters()[0].getPages ().contains (diff.findPage ("index.html")));
    }
    
    public void testSimplePageWithExternalURL () throws Exception {
        String[] pages = {
            "index.html", "<h1>Hi</h1> This is a simple <a href=\"http://www.netbeans.org\">page</a>."
        };
        
        ContentDiff diff = diff (pages, pages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 1, diff.getClusters().length);
        assertEquals ("One page", 1, diff.getClusters()[0].getPages ().size ());
        assertTrue ("Cluster index.html", diff.getClusters()[0].getPages ().contains (diff.findPage ("index.html")));
    }

    public void testTwoPages () throws Exception {
        String[] oldPages = {
            "index.html", "<h1>Hi</h1> This is a simple <a href=\"index.html\">page</a>."
        };
        String[] newPages = {
            "new.html", "<h1>Hi</h1> This is a simple <a href=\"new.html\">page</a>."
        };
        
        ContentDiff diff = diff (oldPages, newPages);
        
        assertNotNull (diff);
        assertEquals ("Two independent clusters", 2, diff.getClusters().length);
        assertEquals ("One page in first", 1, diff.getClusters()[0].getPages ().size ());
        assertEquals ("One page in second", 1, diff.getClusters()[1].getPages ().size ());
        
        assertEquals ("No refs outside", 0, diff.getClusters()[0].getReferences ().length);
        assertEquals ("No refs outside", 0, diff.getClusters()[1].getReferences ().length);
        
        assertEquals ("Two pages", 2, diff.getPages ().size ());
        
        Page index = diff.findPage ("index.html");
        assertTrue ("Is there", diff.getPages ().contains (index));
        assertTrue ("Is removed", index.isRemoved());
        assertFalse("Is not added", index.isAdded ());
        
        Page n = diff.findPage ("new.html");
        assertTrue ("Is there", diff.getPages ().contains (n));
        assertFalse ("Is not removed", n.isRemoved());
        assertTrue ("Is added", n.isAdded ());
        
        assertEquals ("Complete change for removed page", 100, index.getChanged());
        assertEquals ("Complete change for added page", 100, n.getChanged ());
        
        assertChanged ("Complete remove, 100%", diff.getClusters()[0]);
        assertChanged ("Complete add, 100%", diff.getClusters()[1]);
        
    }
    
    public void testTwoPagesInNewVersionReferingToEachOther () throws Exception {
        String[] oldPages = {
            "index.html", "<h1>Hi</h1> This is a simple <a href=\"index.html\">page</a>."
        };
        String[] newPages = {
            "index.html", "<h1>Hi</h1> This is new simple <a href=\"new.html\">page</a>.",
            "new.html", "<h1>Hi</h1> This is a refence to new <a href=\"index.html\">page</a>."
        };
        
        ContentDiff diff = diff (oldPages, newPages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 1, diff.getClusters().length);
        assertEquals ("Both pages there", 2, diff.getClusters()[0].getPages ().size ());
        assertTrue ("Cluster index.html", diff.getClusters()[0].getPages ().contains (diff.findPage ("index.html")));
        assertTrue ("Cluster new.html", diff.getClusters()[0].getPages ().contains (diff.findPage ("new.html")));
        
        
        Page index = diff.findPage ("index.html");
        assertFalse ("Is not new", index.isAdded ());
        assertFalse ("Is not removed", index.isRemoved ());
        assertTrue ("Small change", 0 < index.getChanged ());
        assertTrue ("Small change", index.getChanged () < 10);
    }
    
    public void testOnePageRefersToAnother () throws Exception {
        String[] oldPages = {
        };
        String[] newPages = {
            "index.html", "<h1>Hi</h1> This is new simple <a href=\"new.html\">page</a>.",
            "new.html", "<h1>Hi</h1> This is a refence to new <a href=\"new.html\">page</a>."
        };
        
        ContentDiff diff = diff (oldPages, newPages);
        
        assertNotNull (diff);
        assertEquals ("Two clusters", 2, diff.getClusters().length);
        assertTrue ("First contains index.html as it is the `root`", diff.getClusters()[0].getPages ().contains (diff.findPage ("index.html")));
        assertTrue ("Second contains new.html", diff.getClusters()[1].getPages ().contains (diff.findPage ("new.html")));
        
        assertEquals ("There is a dep from first cluster to the other",
            diff.getClusters()[1], 
            diff.getClusters()[0].getReferences()[0]
        );
        
        assertEquals ("Second cluster has no deps", 0, diff.getClusters()[1].getReferences().length);
    }

    public void testPercentageIsBetween () throws Exception {
        String[] oldPages = {
            "index.html", "<a href=\"new.html\">new</a>",
            "new.html", "<a href=\"index.html\">index</a>"
        };
        String[] newPages = {
            "index.html", "<a href=\"new.html\">index</a>",
            "new.html", "<a href=\"index.html\">new</a>"
        };
        
        ContentDiff diff = diff (oldPages, newPages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 1, diff.getClusters().length);
        assertChanged ("Change is bettween", diff.getClusters()[0]);
    }
    
    public void testAlphabeticallySorted () throws Exception {
        String[] pages = new String[1000];
        String page = "000";
        for (int i = 0; i < pages.length - 1;) {
            pages[i] = page;
            i++;
            page = java.text.MessageFormat.format ("{0, number,000}", new Object[] { new Integer (i) });
            pages[i] = "<a href=\"" + page + "\" />";
            i++;
                
        }
        pages[pages.length - 1] = "<a href=\"000\" />";
        
        ContentDiff diff = diff (pages, pages);
        
        assertNotNull (diff);
        assertEquals ("One big cluster", 1, diff.getClusters().length);
        
        ContentDiff.Page[] arr = (ContentDiff.Page[])diff.getPages ().toArray (new ContentDiff.Page[0]);
        
        for (int i = 0; i < arr.length; i++) {
            assertEquals ("Pages are correctly sorted at index " + i, pages[i * 2], arr[i].getFileName());
        }
        
        arr = (ContentDiff.Page[])diff.getClusters()[0].getPages ().toArray (new ContentDiff.Page[0]);
        
        for (int i = 0; i < arr.length; i++) {
            assertEquals ("Pages are correct in cluster at " + i, pages[i * 2], arr[i].getFileName());
        }
    }
    
    public void testPercentageIsInheritedFromDependentClusters () throws Exception {
        String[] oldPages = {
            "root.html",  "<a href=\"index.html\">index</a>",
            "index.html", "<a href=\"newly.html\">newly</a>",
            "newly.html", "<a href=\"index.html\">index</a>"
        };
        String[] newPages = {
            "root.html", "<a href=\"index.html\">index</a>",
            "index.html", "<a href=\"newly.html\">index</a>",
            "newly.html", "<a href=\"index.html\">newly</a>"
        };
        
        ContentDiff diff = diff (oldPages, newPages);
        
        assertNotNull (diff);
        assertEquals ("Two clusters", 2, diff.getClusters().length);
        ContentDiff.Cluster c = diff.getClusters()[0];
        assertEquals ("One page", 1, c.getPages().size ());
        assertEquals ("Not changed", 0, ((ContentDiff.Page)c.getPages().toArray()[0]).getChanged());
        
        
        assertTrue ("But cluster changed as it depends on changed one", c.getChanged () > 0);
        
        assertEquals ("Actually it is half of the differences of the referenced cluster, " + 
            "as there is no change in own cluster and we inherit half from the other one. " +
            "Moreover all the pages have the same size",
            c.getChanged (), diff.getClusters()[1].getChanged () / 2
        );
    }

    public void testRelativeReferences () throws Exception {
        String[] oldPages = {
            "index.html", "<h1>Hi</h1> This is a simple <a href=\"index.html#in-my-middle\">page</a>."
        };
        String[] newPages = {
        };
        
        ContentDiff diff = diff (oldPages, newPages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 1, diff.getClusters().length);
        
        
        Page index = diff.findPage ("index.html");
        assertTrue ("Is removed", index.isRemoved ());
    }
    
    
    
    private static void assertChanged (String txt, ContentDiff.Cluster c) {
        java.util.Iterator it = c.getPages().iterator ();
        int min = 0;
        int max = 100;
        
        while (it.hasNext()) {
            ContentDiff.Page p = (ContentDiff.Page)it.next ();
            if (min > p.getChanged ()) {
                min = p.getChanged ();
            } 
            if (max < p.getChanged ()) {
                max = p.getChanged (); 
            }
        }
        
        if (min > c.getChanged () || max < c.getChanged ()) {
            fail (txt + " cluster change <" + c.getChanged () + "> is not between " + min + " and " + max);
        }
    }
}
