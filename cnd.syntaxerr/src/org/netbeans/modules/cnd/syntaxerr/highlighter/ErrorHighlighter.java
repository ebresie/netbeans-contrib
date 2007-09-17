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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.syntaxerr.highlighter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.cnd.syntaxerr.provider.ErrorInfo;
import org.netbeans.modules.cnd.syntaxerr.provider.ErrorProvider;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.Annotation;
import org.openide.text.NbDocument;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

/**
 * 
 * @author Vladimir Kvashin
 */
public class ErrorHighlighter implements PropertyChangeListener, DocumentListener {
    
    private static class FileInfo {
	
	private boolean upToDate = false;
	private boolean hasErrors = false;
	private boolean hasWarnings = false;
	
	private DataObject dao;
	private BaseDocument doc;
	
	private Collection<Annotation> annotations = new ArrayList<Annotation>();
	
	public FileInfo(DataObject dao, BaseDocument doc) {
	    this.dao = dao;
	    this.doc = doc;
	}
	
	
	private void addAnnotation(ErrorInfo info) {
	    Annotation annotation = new AnnotationImpl(info);
	    if( info.getSeverity() == ErrorInfo.Severity.ERROR ) {
		hasErrors = true;
	    }
	    else if( info.getSeverity() == ErrorInfo.Severity.WARNING ) {
		hasWarnings = true;
	    }
	    annotations.add(annotation);
	    int line = info.getLineNumber();
	    final int offset = NbDocument.findLineOffset((StyledDocument) doc, line);
            Position pos = new Position() {
                public int getOffset() {
                    return offset;
                }

            };
            NbDocument.addAnnotation((StyledDocument) doc, pos, -1, annotation);
	}

	public DataObject getDao() {
	    return dao;
	}

	private void setAnnotations(Collection<ErrorInfo> errors) {
	    for( Annotation annotation : annotations ) {
                NbDocument.removeAnnotation((StyledDocument)doc, annotation);
	    }
	    for( ErrorInfo info : errors ) {
		addAnnotation(info);
	    }
	}
	
    }
    
    private Map<Document, FileInfo> infoMap = new HashMap<Document, FileInfo>();
    
    private RequestProcessor processor = new RequestProcessor("C/C++ Syntax Error Highlighting", 1); // NOI18N
    
    private static final ErrorHighlighter instance = new ErrorHighlighter();
    
    public static final int delay = Integer.getInteger("cnd.synterr.delay", 2000);
    
    public static final ErrorHighlighter instance() {
        return instance;
    }
    
    public void startup() {
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }
    
    public void shutdown() {
        TopComponent.getRegistry().removePropertyChangeListener(this);
    }

    private FileInfo getOrCreateFileInfo(DataObject dao, BaseDocument doc) {
	FileInfo info = infoMap.get(doc);
	if( info == null ) {
	    info = new FileInfo(dao, doc);
	    infoMap.put(doc, info);
	}
//	else {
//	    assert doc.equals(info.getDocument())
//	}
	return info;
    }
    
    private FileInfo getFileInfo(Document doc) {
        return infoMap.get(doc);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {

	System.err.printf("ErrorHighlighter.propertyChange %s\n", evt.getPropertyName());

	if (TopComponent.Registry.PROP_CURRENT_NODES.equals(evt.getPropertyName())) {
            Node[] nodes = TopComponent.getRegistry().getCurrentNodes();
	    for (int i = 0; i < nodes.length; i++) {
		Node node = nodes[i];
		EditorCookie editor = node.getLookup().lookup(EditorCookie.class);
		if( editor != null ) {
		    DataObject dao = node.getLookup().lookup(DataObject.class);
		    if( dao != null ) {
                        onActivated(dao, editor);
		    }
		}
	    }

        }
	else if(TopComponent.Registry.PROP_OPENED.equals(evt.getPropertyName())) {
            // TODO: process closure; remove doc listener
	}
	    
    }

    public void changedUpdate(DocumentEvent e) {
	documentChanged(e);
    }

    public void insertUpdate(DocumentEvent e) {
	documentChanged(e);
    }

    public void removeUpdate(DocumentEvent e) {
	documentChanged(e);
    }

    private void documentChanged(DocumentEvent e) {
	Document doc = e.getDocument();
	if( doc != null ) {
            FileInfo info = getFileInfo(doc);
            if( info != null ) {
                scheduleHighlighting(info);
            }
	}
    }
    
    

    private void onActivated(DataObject dao, EditorCookie editor) {
	Document doc = editor.getDocument();
	if (doc instanceof BaseDocument) {
	    FileInfo info = getOrCreateFileInfo(dao, (BaseDocument) doc);
	    doc.addDocumentListener(this);
            scheduleHighlighting(info);
	}
    }

    private void scheduleHighlighting(final FileInfo info) {
	Runnable r = new Runnable() {
	    public void run() {
		 Collection<ErrorInfo> errors = ErrorProvider.getDefault().getErrors(info.getDao());
		 info.setAnnotations(errors);

	    }
	};
	RequestProcessor.Task task = processor.create(r, true);
	task.schedule(delay);
    }
}
