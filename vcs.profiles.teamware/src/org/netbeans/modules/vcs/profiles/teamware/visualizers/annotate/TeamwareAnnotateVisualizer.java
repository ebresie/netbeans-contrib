/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.teamware.visualizers.annotate;

import java.awt.Dialog;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import java.util.Iterator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.netbeans.modules.vcs.profiles.teamware.visualizers.OutputVisualizer;

import org.openide.windows.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.table.RevisionComparator;
import org.netbeans.modules.vcscore.util.table.DateComparator;
import org.netbeans.modules.vcscore.util.table.IntegerComparator;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 * The cvs annotate visaulizer.
 *
 * @author  Richard Gregor
 */
public class TeamwareAnnotateVisualizer extends OutputVisualizer {
    
    private static final String UNKNOWN = "server: nothing known about";  //NOI18N
    private static final String ANNOTATING = "Annotations for ";  //NOI18N
    private static final String STARS = "***************";  //NOI18N
    
    private ArrayList annotationLines; 
    private int lineNum;
    private String filePath;
    private ArrayList resultList;
    private HashMap file_infoMap;
    private HashMap output;
    
    /** Creates new CvsAnnotateVisualizer */
    public TeamwareAnnotateVisualizer() {
        super();
        annotationLines = new ArrayList();
        resultList = new ArrayList();
    }

    public Map getOutputPanels() {
        debug("getOutputPanel");       
        output = new HashMap();  
        Iterator it = files.iterator();
        while(it.hasNext()) {            
            String fileName = (String)it.next();
            filePath = rootDir+File.separator+fileName;
            debug("filePath:"+filePath);
            File file = new File(filePath);
            output.put(file.getName(),showAnnotations(file));
        }            
 
            return output;
    }
    
    private javax.swing.JComponent showAnnotations(File file) {
        debug("showAnn:"+file.getName());
        AnnotatePanel panel = new AnnotatePanel();
        createTableDefinition(panel);
        panel.clearAllLines();
        panel.setFileName(filePath);
        debug("annotationLines size:"+annotationLines.size());
        for (Iterator it = annotationLines.iterator(); it.hasNext(); ) {
            AnnotateLine line = (AnnotateLine) it.next();
            if(line != null)
                panel.addLine(line);
        }
        panel.doRepaintAndSort();
        return panel;
        
    }
     
    private void createTableDefinition(AnnotatePanel panel) {
        Class classa = AnnotateLine.class;
        try {
            Class[] noParams = { };
            Method method0 = classa.getMethod("getLineNumInteger", noParams);    //NOI18N
            Method method1 = classa.getMethod("getRevision", noParams);     //NOI18N
            Method method2 = classa.getMethod("getAuthor", noParams);     //NOI18N
            Method method3 = classa.getMethod("getDateString", noParams);     //NOI18N
            Method method4 = classa.getMethod("getContent", noParams);   //NOI18N
            panel.addLineNumColumnDefinition(method0, new IntegerComparator());
            panel.addRevisionColumnDefinition(method1, new RevisionComparator());
            panel.addAuthorColumnDefinition(method2, null);
            panel.addDateColumnDefinition(method3, new DateComparator());
            panel.addContentColumnDefinition(method4, null);
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
    }
 
    
    public boolean doesDisplayError() {
        return false;
    }
    
    /**
     * This method is called, with the output line.
     * @param line The output line.
     */
    public void stdOutputLine(String line) {
        debug("Line:"+line); 
        AnnotateLine annLine = processLine(line);
        if (annLine != null) {
            annLine.setLineNum(lineNum);            
            debug("line number:"+lineNum);
        }
        debug("annotationLines.add");
        annotationLines.add(annLine);
    }
    
    public AnnotateLine processLine(String line) {
        debug("processLine");
        int i = line.indexOf(":");
        AnnotateLine annLine = new AnnotateLine();
        annLine.setRevision(line.substring(0, i));
        int j = line.indexOf(":", i + 1);
        annLine.setDateString(line.substring(i + 1, j));
        i = j;
        j = line.indexOf(":", i + 1);
        annLine.setAuthor(line.substring(i + 1, j));
        annLine.setContent(line.substring(j + 1));
        annLine.setLineNum(++lineNum);
        return annLine;
    }
    
    /**
     * Receive a line of error output.
     */
    public void errOutputLine(final String line) {
        debug("errOutputLine:"+line);
    }
    
    /**
     * Receive the data output.
     */
    public void stdOutputData(final String[] data) {
        debug("stdOutputData:"+data);
    }
    
    /**
     * Receive the error data output.
     */
    public void errOutputData(final String[] data) {
        debug("errOutputData:"+data);
    }
    private File createFile(String fileName) {
        File file = new File(filePath, fileName);
        debug("file:"+file.getAbsolutePath());
        return file;
    }
    
    private static boolean DEBUG = false;
    private static void debug(String msg){
        if(DEBUG)
            System.err.println("CvsAnnotateVisualizer: "+msg);
    }
    
    
    
}
