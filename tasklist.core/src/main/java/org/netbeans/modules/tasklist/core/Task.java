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
package org.netbeans.modules.tasklist.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;

/**
 * Class which represents a task in the tasklist.
 *
 * @author Tor Norbye
 * @author tl
 */
public class Task extends Suggestion implements Cloneable, Cookie {
    private static final Logger LOGGER = TLUtils.getLogger(Task.class);

    static {
        LOGGER.setLevel(Level.OFF);
    }

    /**
     * Some of this items attributes (such as its description - anything
     * except the subtask list) has changed
     *
     * @deprecated Suggestion provides specialized properties
     */
    static final String PROP_ATTRS_CHANGED = "attrs"; // NOI18N

    /** Set&lt;TaskListener> */
    protected EventListenerList listeners = new EventListenerList();

    private boolean visitable;

    private Task parent;

    /** key shared by all clones */
    private Object key;

    /** If this item has subtasks, they are stored in this list */
    private List subtasks = null;

    /** Last unmodifiable subtasks copy distributed to clients. */
    private List subtasksCopy;

    /** When true, this item has been removed from a list.
        The old list reference is still kept around so that
        we can use it to search for a reincarnation of the task.

        @deprecated duplicates isValid property
     */
    private boolean zombie = false;

    public Task() {
        super(null, null, null, null);
        parent = null;
        visitable = true;
        key = new Object();
    }

    public Task(String desc, Task parent) {
        super(null, null, desc, null);
        this.parent = parent;
        visitable = true;
        key = new Object();
    }

    /**
     * Searches for a task
     *
     * @param t task to be found
     * @return index of the task or -1
     */
    public int indexOf(Task t) {
        if (!hasSubtasks())
            return -1;
        return subtasks.indexOf(t);
    }
    
    /**
     * Removes all subtasks.
     */
    public void clear() {
        if (hasSubtasks()) {
            subtasks.clear();
            subtasksCopy = null;
            fireStructureChanged();
        }
    }

    /**
     * Returns indent level for this task. If parent == null returns 0
     *
     * @return indent level for this task
     */
    public int getLevel() {
        Task t = getParent();
        int level = 0;
        while (t != null) {
            level++;
            t = t.getParent();
        }
        return level;
    }

    /**
     * Set the description/summary of the task.
     *
     * @param ndesc The new description text
     */
    public void setSummary(String ndesc) {
        super.setSummary(ndesc);
    }

    public void setDetails(String ndesc) {
        super.setDetails(ndesc);
    }

    public void setPriority(SuggestionPriority priority) {
        super.setPriority(priority);
    }

    /**
     * @return true iff this task is "visitable"; returns true
     * if this node has its own content, false if it's just a "category"
     * node. Used for keyboard traversal: if you press Next (F12) you
     * don't want it to skip over all nonvisitable nodes.
     */
    public boolean isVisitable() {
        return visitable;
    }

    /**
     * Set whether or not this task is "visitable".
     *
     * @param visitable true if this node has its own content, false
     * if it's just a "category" node. Used for keyboard traversal: if
     * you press Next (F12) you don't want it to skip over all
     * nonvisitable nodes.
     */
    public void setVisitable(boolean visitable) {
        this.visitable = visitable;
    }

    /**
     * Fires a PropertyChangeEvent
     *
     * @param propertyName changed property
     * @param oldValue old value (may be null)
     * @param newValue new value (may be null)
     */
    protected void firePropertyChange(String propertyName, Object oldValue,
    Object newValue) {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void addTaskListener(TaskListener l) {
        if (LOGGER.isLoggable(Level.FINE))
            Thread.dumpStack();
        listeners.add(TaskListener.class, l);
    }

    public void removeTaskListener(TaskListener l) {
        if (LOGGER.isLoggable(Level.FINE))
            Thread.dumpStack();
        listeners.remove(TaskListener.class, l);
    }

    protected final void fireStructureChanged() {
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        for (int i = l.length - 2; i >= 0; i -= 2) {
            if (l[i] == TaskListener.class) {
                ((TaskListener) l[i+1]).structureChanged(this);
            }
        }

        if (this instanceof TaskListener) {
            ((TaskListener) this).structureChanged(this);
        }
    }

    /**
     * Fires an addedTask event
     *
     * @param t task that was added
     */
    protected final void fireAddedTask(Task t) {
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        for (int i = l.length - 2; i >= 0; i -= 2) {
            if (l[i] == TaskListener.class) {
                ((TaskListener) l[i+1]).addedTask(t);
            }
        }
        if (this instanceof TaskListener) {
            ((TaskListener) this).addedTask(t);
        }
    }


    /**
     * Fires an removedTask event
     *
     * @param t task that was removed
     * @param index old index of the task
     */
    protected final void fireRemovedTask(Task t, int index) {
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        for (int i = l.length - 2; i >= 0; i -= 2) {
            if (l[i] == TaskListener.class) {
                ((TaskListener) l[i+1]).removedTask(this, t, index);
            }
        }

        if (this instanceof TaskListener) {
            ((TaskListener) this).removedTask(this, t, index);
        }
    }
    
    protected void recursivePropertyChange() {
        firePropertyChange(PROP_ATTRS_CHANGED, null, null);
        if (subtasks != null) {
            Iterator it = getSubtasks().iterator();
            while (it.hasNext()) {
                Task item = (Task)it.next();
                item.recursivePropertyChange();
            }
        }
    }

    /**
     * Returns subtasks of this task
     *
     * @todo all usages require iterator() or size() calls only, so it could be replaced by
     *       subtasksIterator and subtasksCount methods. Add TLUtil.iteratorToCollection.
     * @return children never null
     */
    public final List getSubtasks() {
        if (subtasks == null) {
            return Collections.EMPTY_LIST;
        } else {
            // #48953 clone it for iterators safeness
            if (subtasksCopy == null) {
                synchronized (subtasks) {
                    subtasksCopy = Collections.unmodifiableList(
                            new ArrayList(subtasks));
                }
            }
            return subtasksCopy;
        }
    }

    /**
     * Create subtasks iterator. It's remove method is not
     * supported yet. You need to call removeTask().
     *
     * @return non-recursive subtask iterator
     */
    public final Iterator subtasksIterator() {  // in JRE 1.5 could be turned to Iterable by renaming to Iterator<T> iterator().
        return getSubtasks().iterator();
    }

    /** 
     * Returns the number of subtasks.
     *
     * @return subtasks count 
     */
    public final int subtasksCount() {
        if (subtasks == null) {
            return 0;
        } else {
            return subtasks.size();
        }
    }

    /** 
     * @return true if task exits in non-recursive subtasks. 
     */
    public final boolean containsSubtask(Task task) {
        if (subtasks == null) {
            return false;
        } else {
            return subtasks.contains(task);
        }
    }

    /**
     * Add subtask to this task. The task will be prepended
     * to the task list.
     *
     * @param subtask task to be added as a subtask, to the front
     * of the list.
     */
    public void addSubtask(Task subtask) {
        addSubtask(subtask, false);
    }

    /**
     * Add subtask in a particular place in the parent's
     * subtask list
     *
     * @param subtask The subtask to be added
     * @param after The task which will be immediately before
     * the new subtask after the addition (e.g. add
     * this subtask directly AFTER the specified
     * task)
     */
    public void addSubtask(Task subtask, Task after) {
        subtask.parent = this;
        if (subtasks == null) {
            // Internal error - shouldn't call this unless you already have a subtask "after")
            ErrorManager.getDefault().log("addSubtask(subtask,after) called where subtasks==null"); // NOI18N
            return;
        }
        int pos = subtasks.indexOf(after);
        subtasks.add(pos+1, subtask);
        subtasksCopy = null;
        fireAddedTask(subtask);
    }

    /** Add a list of subtasks to this task.
     * @param subtasks The tasks to add
     * @param append When true, append to the list, otherwise prepend. Ignored
     *  if after is not null.
     * @param after The task which will be immediately before
     * the new subtask after the addition (e.g. add
     * this subtask directly AFTER the specified
     * task). Overrides the append parameter.
    */
    public void addSubtasks(List tasks, boolean append, Task after) {
        ListIterator it = tasks.listIterator();
        while (it.hasNext()) {
            Task task = (Task)it.next();
            task.parent = this;
        }

        if (subtasks == null) {
            subtasks = Collections.synchronizedList(new LinkedList());
        }
        if (after != null) {
            int pos = subtasks.indexOf(after);
            subtasks.addAll(pos+1, tasks);
        } else if (append) {
            subtasks.addAll(tasks);
        } else {
            subtasks.addAll(0, tasks);
        }
        subtasksCopy = null;
        fireStructureChanged();
    }

   /**
    * Add a subtask to this task.
    * @param append When true, add to the end of the list of subtasks instead
    * of the beginning.
    */
    public void addSubtask(Task subtask, boolean append) {
        subtask.parent = this;
        if (subtasks == null) {
            subtasks = Collections.synchronizedList(new LinkedList());
        }

        // XXX does not work with SuggetionList.addCategory:152
        // assert !subtasks.contains(subtask);
        if (subtasks.contains(subtask)) return;

        if (append) {
            subtasks.add(subtask);
        } else {
            subtasks.add(0, subtask);
        }
        subtasksCopy = null;
        fireAddedTask(subtask);
    }

    /** 
     * Remove a particular subtask
     *
     * @param subtask The subtask to be removed 
     */
    public void removeSubtask(Task subtask) {
        // We need the list reference later, when looking for a reincarnation
        // of the task. So instead use the zombie field to mark deleted items.
        subtask.zombie = true;
        if (subtasks == null) {
            return;
        }
        int index = subtasks.indexOf(subtask);
        subtasks.remove(index);
        if (subtasks.size() == 0) {
            subtasks = null;
        }

        subtasksCopy = null;
        fireRemovedTask(subtask, index);
    }

    /**
     * Indicate whether or not this task has any subtasks
     * @return true iff the item has any subtasks
     */
    public final boolean hasSubtasks() {
        return ((subtasks != null) && (subtasks.size() != 0));
    }

    public final Task getParent() {
        return parent;
    }

    /** Traverse to root task (or self)*/
    public final Task getRoot() {
        Task parent = getParent();
        if (parent != null) {
            return parent.getRoot();
        } else {
            return this;
        }
    }


    /** Determines whether given task lies in this context. */
    public final boolean isParentOf(Task task) {
        if (task.getKey() == getKey()) return true;
        Task nextLevel = task.getParent();
        if (nextLevel == null) return false;
        return isParentOf(nextLevel);  // recursion
    }

    /**
     * Indicate if this item is a "zombie" (e.g. it has been removed
     * from a tasklist. The list it was removed from is still pointed to
     * by the list field. See the Suggestion module's FixAction for an
     * example of why this is useful.
     */
    public boolean isZombie() {
        return zombie;
    }

    /**
     * Write a TodoItem to a text stream. NOT DONE.
     * @param item The task to write out
     * @param w The writer to write the string to
     * @throws IOException Not thrown explicitly by this code, but perhaps
     * by the call it makes to w's write() method
     *
     * @todo Finish the implementation here such that it
     * writes out all the fields, not just the
     * description.
     */
    public static void generate(Task item, Writer w) throws IOException {
	w.write(item.getSummary());
    }

    /**
     * Parse a task from a text stream.
     *
     * @param r The reader to read the task from
     * @throws IOException Not thrown directly by this method, but
     * possibly by r's read() method which it calls
     * @return A new task object which represents the
     * data read from the reader
     * @todo Finish the implementation
     * @see generate
     */
    public static Task parse(Reader r) throws IOException {
        LOGGER.fine("parsing");

        BufferedReader reader = new BufferedReader(r);
        //List notes = new LinkedList(); // List<Note>
        String line;
        while ((line = reader.readLine()) != null) {
            // XXX TodoTransfer's convert
            // method never seems to get called (see explanations in
                // TaskNode.clipboardCopy), so I haven't been
            // able to test this, that's why I haven't expanded the
            // code as much as it should be.
            Task item = new Task();
            item.setSummary(line);
            return item;
        }
        return null;
    }


    /**
     * Counts all subtasks of this task recursively.
     *
     * @return number of subtasks
     */
    public int getSubtaskCountRecursively() {
        if(subtasks == null) return 0;

        int n = 0;
        synchronized(subtasks) {
            Iterator it = subtasks.iterator();
            while(it.hasNext()) {
                Task t = (Task) it.next();
                n += t.getSubtaskCountRecursively() + 1;
            }
            return n;
        }
    }

    /**
     * Create default nodes for this item.
     * Actual view may use them or use any replacement.
     */
    public Node[] createNode() {
        //if (hasSubtasks()) {
        if (subtasks != null) {  // Want to make root a non-leaf; empty list, not null
            return new Node[] {new TaskNode(this, new TaskChildren(this))};
        } else {
            return new Node[] {new TaskNode(this)};
        }
    }

    /** 
     * Create an identical copy of a task (a deep copy, e.g. the
     * list of subtasks will be cloned as well 
     */
    protected Object clone() {
        Task t = new Task();
        t.copyFrom(this);
        return t;
    }

    /**
     * Returns a key shared by all task clones.
     */
    public final Object getKey() {
        return key;
    }

    /**
     * Get the provider. Not defined for tasks - will be subclassed
     * in SuggestionImpl but we don't want Task to be abstract...
     */
    public Object getSeed() {
         return null;
    }


    /** Copy all the fields in the given task into this object.
        Should only be called on an object of the EXACT same type.
        Thus, if you're implementing a subclass of Task, say
        UserTask, you can implement copy assuming that the passed
        in Task parameter is of type UserTask. When overriding,
        remember to call super.copy.
        <p>
        Make a deep copy - except when that doesn't make sense.
        For example, you can share the same icon reference.
        And in particular, the tasklist reference should be the same.
        But the list of subitems should be unique. You get the idea.
    */
    protected void copyFrom(Task from) {
        visitable = from.visitable;
        zombie = from.zombie;

        assert from.key != null;
        key = from.key;

        // Copy fields from the parent implementation
        super.setSummary(from.getSummary());
        super.setPriority(from.getPriority());
        super.setIcon(from.getIcon());
        super.setType(from.getType());
        super.setLine(from.getLine());
        super.setAction(from.getAction());
        super.setDetails(from.getDetails());

        // Copying the parent reference may seem odd, since for children
        // it should be changed - but this only affects the root node.
        // For children nodes, we override the parent reference after
        // cloning the child.
        parent = from.parent;

        // Copy the subtasks reference

        // XXX
	// Please note -- I'm NOT copying the universal id, these have to
	// be unique, even for copies
        if (from.subtasks != null) {
            synchronized(from.subtasks) {
                Iterator it = from.subtasks.iterator();
                subtasks = Collections.synchronizedList(new LinkedList());
                while (it.hasNext()) {
                    Task task = (Task)it.next();
                    Task mycopy = (Task)task.clone();
                    mycopy.parent = this;
                    subtasks.add(mycopy);
                }
                subtasksCopy = null;
            }
        }
    }
}

