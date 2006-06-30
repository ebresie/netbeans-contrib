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
package org.netbeans.modules.latex.ui;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import org.netbeans.editor.Coloring;
import org.netbeans.modules.editor.highlights.spi.DefaultHighlight;
import org.netbeans.modules.editor.highlights.spi.Highlighter;
import org.netbeans.modules.latex.model.command.DebuggingSupport;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class DebugImpl implements PropertyChangeListener {
    
    /** Creates a new instance of DebugImpl */
    public DebugImpl() {
    }

    private FileObject lastFile;
    
    private static final Coloring HIGHLIGHT_COLORING = new Coloring(null, null, Color.GRAY);
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (!DebuggingSupport.getDefault().isDebuggingEnabled())
            return ;
        
        Node n = DebuggingSupport.getDefault().getCurrentSelectedNode();
        
        Highlighter.getDefault().setHighlights(lastFile, "latex-debugging", Collections.EMPTY_LIST);
        
        lastFile = null;
        
        if (n != null) {
            SourcePosition spos = n.getStartingPosition();
            SourcePosition epos = n.getEndingPosition();
            
            lastFile = (FileObject) spos.getFile();
            
            Highlighter.getDefault().setHighlights(lastFile, "latex-debugging", Collections.singletonList(new DefaultHighlight(HIGHLIGHT_COLORING, spos.getOffset(), epos.getOffset())));
        }
    }
    
}
