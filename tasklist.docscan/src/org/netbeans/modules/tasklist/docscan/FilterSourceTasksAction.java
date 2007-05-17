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

package org.netbeans.modules.tasklist.docscan;

import org.openide.util.HelpCtx;

import java.awt.*;
import org.netbeans.modules.tasklist.filter.FilterAction;

/**
 * Filters source tasks. It hides south component and makes action modal.
 *
 * @author Petr Kuzel
 */
final class FilterSourceTasksAction extends FilterAction {

    private static final long serialVersionUID = 1;

    protected Component createSubpanel() {
        return null;
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(FilterSourceTasksAction.class);
    }

    public String getName() {
        return Util.getString("filter-todo");
    }

    protected boolean isModal() {
        return true;
    }
}
