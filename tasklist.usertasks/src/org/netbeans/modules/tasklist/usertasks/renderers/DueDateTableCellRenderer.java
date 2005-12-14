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

package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Date;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableRenderer;

/**
 * Renderer for the due date
 *
 * @author tl
 */
public class DueDateTableCellRenderer extends DateTableCellRenderer implements
TreeTableRenderer {
    private Font boldFont, normalFont;
    
    /**
     * Constructor
     */
    public DueDateTableCellRenderer() {
    }

    protected Duration getDuration(Object obj) {
        UserTask ut = (UserTask) obj;
        if (ut == null) {
            return null;
        } else {
            return new Duration(ut.getEffort(),
                Settings.getDefault().getHoursPerDay(), 
                Settings.getDefault().getDaysPerWeek());
        }
    }

    public Component getTreeTableCellRendererComponent(
        org.netbeans.modules.tasklist.usertasks.treetable.TreeTable table, 
        Object node, Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        if (normalFont == null || !normalFont.equals(table.getFont())) {
            normalFont = table.getFont();
            boldFont = normalFont.deriveFont(Font.BOLD);
        }
        setForeground(null);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, 
            row, column);
        if (node instanceof UserTaskTreeTableNode) {
            UserTaskTreeTableNode n = (UserTaskTreeTableNode) node;
            UserTask ut = (UserTask) n.getUserTask();
            Date due = ut.getDueDate();
            boolean b = !ut.isDone() && due != null && 
                due.getTime() >= System.currentTimeMillis();
            setFont(b ? normalFont : boldFont);
            if (!isSelected && !b)
                setForeground(Color.RED);
        }
        return this;
    }
}
