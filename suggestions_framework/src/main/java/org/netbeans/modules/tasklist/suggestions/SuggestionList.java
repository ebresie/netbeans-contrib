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

package org.netbeans.modules.tasklist.suggestions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.client.SuggestionManager;

import org.openide.nodes.Node;
import org.openide.util.NbBundle;


/**
 * A list of suggestions adding category grouping capabilities to
 * orginary tasklist.
 *
 * @author Tor Norbye
 */
public class SuggestionList extends TaskList {

    /** Number of tasks we allow before for a type before dropping
     * the task into a sublevel with a category task containing the
     * tasks of that type.
     */
    private static final int MAX_INLINE = 20;

    static final Object CATEGORY_NODE_SEED = new Object();

    private final int groupTreshold;

    /** Construct a new SuggestionManager instance. */
    public SuggestionList() {
        this(MAX_INLINE);
    }

    public SuggestionList(int groupTreshold) {
        this.groupTreshold = groupTreshold;
    }

    synchronized SuggestionImpl getCategoryTask(SuggestionType type,
                                                boolean create) {
        SuggestionImpl category = null;
        if (categoryTasks != null) {
            category = (SuggestionImpl)categoryTasks.get(type);
        }
        if (create && (category == null)) {
            category = new SuggestionImpl(null,
                type.getLocalizedName(),type, null, CATEGORY_NODE_SEED);
            category.setType(type.getName());
            category.setIcon(type.getIconImage());
            category.setVisitable(false);
            // Don't duplicate the provider field! We don't want
            // SMI.stuffCache to keep category task nodes stashed...
            SuggestionManagerImpl manager =
                (SuggestionManagerImpl)SuggestionManager.getDefault();
            if (manager.isExpandedType(type)) {
                // TODO it's callers reponsibity
//                SuggestionsView view;
//                if (getView() instanceof SuggestionsView) {
//                    view = (SuggestionsView)getView();
//                } else {
//                    view = SuggestionsView.getCurrentView();
//                }
//                if (view != null) {
//                    manager.scheduleNodeExpansion(view,
//                                                  category);
//                }
            }

            if (categoryTasks == null) {
                categoryTasks = new HashMap(20);
            }
            categoryTasks.put(type, category);
            // Add the category in the given position
            SuggestionImpl after = findAfter(type);
            if (after != null) {
                addCategory(category, after);
            } else {
                addCategory(category, false);
            }
        }
        return category;
    }

    /** Add a task to the task list.
     * @param task The task to be added.
     * @param after The task which will be immediately before
     * the new subtask after the addition (e.g. add
     * this subtask directly AFTER the specified
     * task)
     * @deprecated use Task.addSubtask(Task subtask, Task after) instead
     */
    private void addCategory(Task task, Task after) {
        if (task.getParent() == null) {  //XXX null more often than before
            appendTask(task);
        } else {
            Task parent = task.getParent();
            // User insert: prepend to the list
            parent.removeSubtask(task);
            parent.addSubtask(task, after);
        }
    }


    /** Add a task to the task list.
     * @param task The task to be added.
     * @param append If true, append the item to the list, otherwise prepend
     * @deprecated use Task.addSubtask(Task subtask, boolean append) instead
     */
    private void addCategory(Task task, boolean append) { //XXX null more often than before
        if (task.getParent() == null) {
            appendTask(task);
        } else {
            // it's really funny contruct  (XXX probably want to add it to list end)
            Task parent = task.getParent();
            parent.removeSubtask(task);
            parent.addSubtask(task, append);
        }
    }


    private Map categoryTasks = null;

    /** Return the task that we need to put this new category type
     * immadiately following. */
    SuggestionImpl findAfter(SuggestionType type) {
        SuggestionImpl after = null;
        int pos = type.getPosition();
        Iterator it = getTasks().iterator();
        int i = 0;
        while (it.hasNext()) {
            i++;
            SuggestionImpl s = (SuggestionImpl)it.next();
            int spos = s.getSType().getPosition();
            if (spos > pos) {
                break;
            } else {
                after = s;
            }
        }

        // #42618 hotfix
        if (i == getTasks().size()) {
            return null;
        } else {
            return after;
        }
    }

    /** Remove the given category node, if unused.
        @param force If true, remove the category node even if it has subtasks
    */
    synchronized void removeCategory(SuggestionImpl category, boolean force) {
        //SuggestionImpl category = (SuggestionImpl)s.getParent();
        if ((category != null) && (force || !category.hasSubtasks())) {
            category.getParent().removeSubtask(category);
            categoryTasks.remove(category.getSType());
        }
    }

    synchronized void removeCategory(SuggestionType type) {
        if (getTasks().size() == 0) {
            categoryTasks = null;
            return;
        }
        Iterator ti = getTasks().iterator();
        ArrayList removeTasks = new ArrayList(50);
        while (ti.hasNext()) {
            SuggestionImpl suggestion = (SuggestionImpl)ti.next();
            if (suggestion.getSType() == type) {
                removeTasks.add(suggestion);
            }
        }
        addRemove(null, removeTasks, false, null, null);
        if (categoryTasks != null) {
            categoryTasks.remove(type);
        }
   }


    /** Return the set of category tasks (SuggestionImpl objects) */
    public Collection getCategoryTasks() {
        if (categoryTasks != null) {
            return categoryTasks.values();
        }
        return null;
    }

    void clearCategoryTasks() {
        categoryTasks = null; // recreate such that they get reinserted etc.
    }


    /**
     * Gets number of items then displays inlined once
     * exceeded it's grouped (by category).
     * @return
     */
    final int getGroupTreshold() {
        return groupTreshold;  // TODO #37068 turn into user's option
    }

}
