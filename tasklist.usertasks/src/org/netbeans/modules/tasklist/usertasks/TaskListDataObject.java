/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
 
package org.netbeans.modules.tasklist.usertasks;

import java.io.IOException;
import javax.swing.SwingUtilities;
import org.openide.ErrorManager;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;


/**
 * Represents a tasklist object in the Repository.
 *
 * @author Tor Norbye
 * @author Trond Norbye
 */
public class TaskListDataObject extends MultiDataObject implements OpenCookie {

    private static final long serialVersionUID = 1;

    public TaskListDataObject(FileObject pf, TaskListLoader loader)
			    throws DataObjectExistsException {
	super(pf, loader);
    	CookieSet cookies = getCookieSet();
	cookies.add(this); // OpenCookie
    }
  
    protected Node createNodeDelegate() {
	return new TaskListDataNode(this);
    }

    // Implements OpenCookie
    
    public void open() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                open_();
            }
        });
    }
    
    /**
     * Opens the TC in the Swing thread
     */
    private void open_() {
	UserTaskView view = UserTaskView.findView(getPrimaryEntry().getFile());
        if (view == null) {
            FileObject fo = getPrimaryEntry().getFile();
            try {
                UserTaskList tl = UserTaskList.readDocument(fo);
                view = new UserTaskView(tl, false);
                view.showInMode();
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        } else {
            // This view already exists, show it...
            view.showInMode();
        }
    }   
}
