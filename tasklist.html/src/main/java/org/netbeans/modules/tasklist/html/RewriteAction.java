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

package org.netbeans.modules.tasklist.html;

import java.awt.Component;
import java.awt.Dialog;
import org.openide.text.Line;
import org.openide.loaders.DataObject;
import java.io.*;
import org.openide.ErrorManager;

import javax.swing.text.*;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.EditCookie;

import org.netbeans.api.diff.*;

import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.client.Suggestion;

import org.w3c.tidy.*;

/**
 * Rewrite the document
 * <p>
 * @todo Use a single button OK panel, not an OK/Cancel
 *    dialog for the preview dialog
 *
 * @author Tor Norbye
 */

public class RewriteAction extends NodeAction 
     implements ErrorReporter  {

    private static final long serialVersionUID = 1;

     protected boolean asynchronous() {
         return false;
     }
         
    public void reportError(int line, int col, boolean error, String message) {
        //System.err.println("reportError(" + line + ", " + col + ", " + error + ", " + message + ")");
    }

    protected boolean enable(Node[] node) {
        if ((node == null) || (node.length != 1)) {
            return false;
        }

        DataObject dobj = node[0].getLookup().lookup(DataObject.class);
        if (dobj == null) {
            return false;
        }
        EditorCookie edit = dobj.getLookup().lookup(
                                                     EditorCookie.class);
        if (edit == null) {
            return false;
        }
        if (TidySuggester.isHTML(dobj)) {
            return true;
        }
        if (TidySuggester.isJSP(dobj)) {
            return true;
        }
        if (TidySuggester.isXML(dobj)) {
            return true;
        }
        return false;
    }

    private Tidy tidy = null;

    protected void performAction(Node[] node) {
        // Figure out which data object the node is associated
        // with.
               // XXX Later I could store this in the Suggestion
               // rather than relying on the Line object (since
               // for example category nodes don't have Line objects)
               // (e.g. the suggestion manager would associate the
               // data object with the node)
        Suggestion item = (Suggestion)TaskNode.getTask(node[0]);
        DataObject dobj;
        if (item != null) {
            Line l = item.getLine();
            dobj = DataEditorSupport.findDataObject(l);
        } else {
            dobj = node[0].getLookup().lookup(DataObject.class);
        }
        if (dobj == null) {
            return;
        }
        Document doc = TLUtils.getDocument(dobj);
        if (doc == null) {
            EditorCookie edit = dobj.getLookup().lookup(
                                                     EditorCookie.class);
            if (edit == null) {
                return; // Signal error?
                //ErrorManager.getDefault().log(ErrorManager.USER, 
                //   "no editor cookie!");
            }
	    EditCookie ec = dobj.getLookup().lookup(EditCookie.class);
	    if (ec == null) {
	    	OpenCookie oc = dobj.getLookup().lookup(OpenCookie.class);
		    if (oc != null) oc.open();
	    } else {
		    ec.edit();
	    }
            doc = TLUtils.getDocument(dobj);
	    if (doc == null) {
		    // HMMMMMM....
                try {
                    doc = edit.openDocument();
                } catch (java.io.IOException e) {
                    ErrorManager.getDefault().notify(e);
                    return;
                }
	    }
        }

        boolean isHTML = TidySuggester.isHTML(dobj);
        boolean isJSP = false;
        boolean isXML = false;
        if (!isHTML) {
            isJSP = TidySuggester.isJSP(dobj);
            if (!isJSP) {
                isXML = TidySuggester.isXML(dobj);
            }
        }
        if (!(isHTML || isJSP || isXML)) {
            return;
        }

        // Set configuration settings
        if (tidy == null) {
            tidy = new Tidy();
        }
        tidy.setOnlyErrors(false);
        tidy.setShowWarnings(false);
        tidy.setQuiet(true);
        
        // XXX Apparently JSP pages (at least those involving
        // JSF) need XML handling in order for JTidy not to choke on them
        tidy.setXmlTags(isXML || isJSP);

        RewritePanel panel = new RewritePanel(this, doc, dobj);
        panel.setXHTML(tidy.getXHTML());

        panel.setWrapCol(tidy.getWraplen());
        panel.setOmit(tidy.getHideEndTags());
        panel.setUpper(tidy.getUpperCaseTags());
        panel.setIndent(tidy.getIndentContent());
        panel.setReplace(tidy.getMakeClean());
        panel.setXML(tidy.getXmlTags());

        DialogDescriptor d = new DialogDescriptor(panel,
                    NbBundle.getMessage(RewriteAction.class,
                    "TITLE_rewrite")); // NOI18N
        d.setModal(true);
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.show();
        if (d.getValue() != NotifyDescriptor.OK_OPTION) {
            return;
        }

        tidy.setXHTML(panel.getXHTML());
        tidy.setMakeClean(panel.getReplace());
        tidy.setIndentContent(panel.getIndent());
        tidy.setSmartIndent(panel.getIndent());
        tidy.setUpperCaseTags(panel.getUpper());
        tidy.setHideEndTags(panel.getOmit());
        tidy.setWraplen(panel.getWrapCol());

        String rewritten = rewrite(doc);

        try {
            // JDK14
            /* Grrr ... turns out replace() is only available as of JDK 1.4...
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).replace(0, doc.getLength(), rewritten,
                                                null);
            }
            else {
            */
                doc.remove(0, doc.getLength());
                doc.insertString(0, rewritten, null);
            //}
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
        }
        Suggestion s = node[0].getLookup().lookup(Suggestion.class);
        if (s != null) {
            Object seed = s.getSeed();
            if (seed instanceof TidySuggester) {
                //TODO notify done ((TidySuggester)seed).rescan();
            }
        }
    }
    
    private String rewrite(Document doc) {
        InputStream input = null;
        try {
            String text = doc.getText(0, doc.getLength());
            input = new StringBufferInputStream(text);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().
                notify(ErrorManager.WARNING, e);
            return "";
        }
        StringBuffer sb = new StringBuffer(doc.getLength()+500);
        OutputStream output = new StringBufferOutputStream(sb);
        tidy.parse(input, output);
        return sb.toString();
    }

    void preview(RewritePanel panel, Document doc, DataObject dobj) {
        tidy.setXHTML(panel.getXHTML());
        tidy.setMakeClean(panel.getReplace());
        tidy.setIndentContent(panel.getIndent());
        tidy.setSmartIndent(panel.getIndent());
        tidy.setUpperCaseTags(panel.getUpper());
        tidy.setHideEndTags(panel.getOmit());
        tidy.setWraplen(panel.getWrapCol());

        String before;
        try {
            before = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            ErrorManager.getDefault().
                notify(ErrorManager.WARNING, e);
            return;
        }
        String rewritten = rewrite(doc);
        String mime = dobj.getPrimaryFile().getMIMEType();
        diff(before, rewritten, mime);

    }

    void diff(String before, String after, String mime) {
        Diff diff = Diff.getDefault();
        if (diff == null) {
            // TODO Check for this condition and hide the Diff button
            // if this is the case
            return ;
        }

        String beforeDesc = NbBundle.getMessage(RewriteAction.class,
                            "DiffBefore"); // NOI18N
        String afterDesc = NbBundle.getMessage(RewriteAction.class,
                            "DiffAfter"); // NOI18N
        String beforeTitle = beforeDesc; 
        String afterTitle = afterDesc; 

        Component tp = null;
        try {
            tp = diff.createDiff(beforeDesc, beforeTitle,
                                 new StringReader(before),
                                 afterDesc, afterTitle,
                                 new StringReader(after),
                                 mime);
        } catch (IOException ioex) {
            ErrorManager.getDefault().notify(ioex);
            return ;
        }
        if (tp == null) {
            return;
        }

        //NotifyDescriptor d =
        // new NotifyDescriptor.Message("Hello...", NotifyDescriptor.INFORMATION_MESSAGE);
        // TopManager.getDefault().notify(d);

        
        DialogDescriptor d = new DialogDescriptor(tp,
                    NbBundle.getMessage(RewriteAction.class,
                    "TITLE_diff")); // NOI18N
        d.setModal(true);
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setOptionType(NotifyDescriptor.DEFAULT_OPTION);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.show();
    }


    public String getName() {
        return NbBundle.getMessage(RewriteAction.class,
                                   "Rewrite"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/html/rewrite.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }

    // Grr... tidy uses input/output stream instead of input/output writer
    private class StringBufferOutputStream extends OutputStream {
        private StringBuffer sb;
        StringBufferOutputStream(StringBuffer sb) {
            this.sb = sb;
        }

        public void write(int b) {
            sb.append((char)b);
        }
    }

    
}
