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

package org.netbeans.modules.tasklist.core;


import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.filter.FilterAction;
import org.openide.ErrorManager;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.actions.PropertiesAction;
import org.openide.loaders.InstanceSupport;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.Sheet;

import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListener;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.PasteType;

public class TaskNode extends AbstractNode implements PropertyChangeListener {
    protected Task item;  

    // Leaf
    public TaskNode(Task item) {
        super(Children.LEAF);
        init(item);
    } 

    // Non-leaf/parent
    public TaskNode(Task item, List subtasks) {
        super(new TaskChildren(item));
        init(item);
    }

    private void init(Task item) {
        this.item = item;
        setName(item.getSummary());
        //setIconBase("org/netbeans/modules/tasklist/core/task"); // NOI18N
        //setDefaultAction(SystemAction.get(ShowTaskAction.class));
        item.addPropertyChangeListener(WeakListener.propertyChange(this, item));
        updateDisplayStuff();
        getCookieSet().add(new InstanceSupport.Instance(item));
        
        // Make reorderable:
        //TODO getCookieSet().add(new ReorderMe ());
    }

    public TaskChildren getTaskChildren() {
        return (TaskChildren) getChildren();
    }
     
    public Task getTask() {
        return item;
    }

    // Handle cloning specially (so as not to invoke the overhead of FilterNode):
    public Node cloneNode () {
        if (item.hasSubtasks()) {
            return new TaskNode(item, item.getSubtasks());
        } else {
            return new TaskNode(item);
        }
    }

    /**
       @todo Should "task has associated filepos" and "task is sourcescan task"
         have separate icons?
    */
    protected void updateDisplayStuff() {
        setDisplayName(item.getDisplayName());
        updateIcon();
    }

    protected void updateIcon() {
        setIconBase((item.getAction() != null) ?
			// This lightbulb icon is really ugly, get something
			// better!
		    "org/netbeans/modules/tasklist/core/lightbulb" : // NOI18N
                    "org/netbeans/modules/tasklist/core/task"); // NOI18N
    }
    
    public Image getIcon(int type) {
	if (item.getIcon() != null) {
	    return item.getIcon();
	} else {
	    return super.getIcon(type);
	}
    }

    public Image getOpenedIcon(int type) {
	if (item.getIcon() != null) {
	    return item.getIcon();
	} else {
	    return super.getOpenedIcon(type);
	}
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == Task.PROP_CHILDREN_CHANGED) {
            // Special case -- we've made a leaf into one containing children!
            Children c = getChildren();
            if ((c == Children.LEAF) && (item.hasSubtasks())) {
                // XXX This seems to get called more frequently than is necessary!
                setChildren(new TaskChildren(item));
            }
        }
        // Some aspects of the module may have changed. Redisplay everything.
        updateDisplayStuff();
        firePropertyChange(null, null, null);
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(TaskNode.class);
    }
    
    protected SystemAction[] createActions() {
	
	// TODO Perform lookup here to compute an aggregate
	// menu from other modules as well. But how do we determine
	// order? I think NetBeans 4.0's actions re-work will have
	// some better support for integrating context menus so I won't
	// try to be too clever here...

	// XXX look up and locate actions

        return new SystemAction[] {
            // XXX moved to usertasks package: SystemAction.get(ShowTaskAction.class),
		// XXX moved to editor package: SystemAction.get(GoToTaskAction.class),
            null,
            SystemAction.get(FilterAction.class),
            null,
            SystemAction.get(ExpandAllAction.class),
            null,
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(PasteAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            null,
            SystemAction.get(PropertiesAction.class),
        };
    }

    public void destroy() {
        TaskList tl = item.getList();
        try {
            tl.remove(item);
        } catch (java.lang.NullPointerException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public boolean canDestroy() {
        return true;
    }
    
    /** Creates properties.
     */
    protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        
        Sheet.Set sse = Sheet.createExpertSet();
        s.put(sse);
        
        try {
            Node.Property p;
            p = new PropertySupport.Reflection(item, String.class, "getSummary", "setSummary"); // NOI18N
            p.setName(TaskListView.PROP_TASK_SUMMARY);
            p.setDisplayName(NbBundle.getMessage(TaskNode.class, "Description")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(TaskNode.class, "DescriptionHint")); // NOI18N
            ss.put(p);
        } catch (NoSuchMethodException nsme) {
            ErrorManager.getDefault().notify(nsme);
        }
        return s;
    }
    
    public boolean canRename() {
        return true;
    }

    public void setName(String nue) {
        super.setName(nue);
        if (!nue.equals(item.getSummary())) {
            item.setSummary(nue);
        }
    }
    
    protected void createPasteTypes(Transferable t, List s) {
        super.createPasteTypes(t, s);
        PasteType p = TaskTransfer.createTodoPasteType(t, item);
        if (p != null) {
            s.add(p);
        }
    }
    
    // Handle copying and cutting specially:
    public boolean canCopy () {
        return true;
    }
    public boolean canCut () {
        return true;
    }

    public Transferable clipboardCopy() throws IOException {
        Transferable deflt = super.clipboardCopy();
        ExTransferable enriched = ExTransferable.create(deflt);
        
        // TODO: remove DataFlavor.stringFlavor
        // ExClipboard.Convertor doesn't work yet
        // see http://www.netbeans.org/issues/show_bug.cgi?id=30923
        // That's why we create DataFlavor.stringFlavor here
        enriched.put(new ExTransferable.Single(TaskTransfer.TODO_FLAVOR) {
            protected Object getData() {
                try {
                    return item.clone();
                } catch (java.lang.CloneNotSupportedException e) {
                    // This should NOT happen - item is a Task and
                    // task implements Cloneable. But clone() uses
                    // a checked exception so I'm forced to catch it.
                    return null;
                }
            }
        });
        enriched.put(new ExTransferable.Single(DataFlavor.stringFlavor) {
            protected Object getData() throws IOException {
                StringWriter wr = new StringWriter();
                Task.generate(item, wr);
                return wr.toString();
            }
        });
        return enriched;
    }
    public Transferable clipboardCut() throws IOException {
        destroy();
        return clipboardCopy();
    }

    /* This isn't ready yet; I need to change the dialog such that it 
       can work as a property sheet (no explicit "ok" action which copies
       GUI values into the todo item object.)
    // Permit user to customize whole node at once (instead of per-property):
    public boolean hasCustomizer () {
        return true;
    }
    public Component getCustomizer () {
        return new NewTodoItemPanel(this);
    }
    */

    /*    
    public Node.Handle getHandle() {
        return new TodoItemHandle(item);
    }
    */
    
    // Permit node to be reordered (you may also want to put
    // MoveUpAction and MoveDownAction on the subnodes, if you can,
    // but ReorderAction on the parent is enough):
            /*
    private class ReorderMe extends Index.Support {

        public Node[] getNodes () {
            return TaskNode.this.getChildren().getNodes();
        }

        public int getNodesCount () {
            return getNodes().length;
        }

        // This assumes that there is exactly one child node per key.
        // If you are using e.g. Children.Array, you can use shortcut implementations
        // of the Index cookie.
        public void reorder (int[] perm) {
            // Remember: {2, 0, 1} cycles three items forwards.
            List old = TaskNode.this.getTaskChildren().myKeys;
            if (list.size () != perm.length) {
                throw new IllegalArgumentException();
            }
            List nue = new ArrayList(perm.length);
            for (int i = 0; i < perm.length; i++)
                nue.set (i, old.get(perm[i]));
            TaskNode.this.getTaskChildren().setKeys(nue);



	    // Remember: {2, 0, 1} cycles three items forwards.
	    MyDataElement[] items = model.getChildElements();
	    if (items.length != perm.length) throw new IllegalArgumentException();
	    MyDataElement[] nue = new MyDataElement[perm.length];
	    for (int i = 0; i < perm.length; i++) {
		nue[i] = old[perm[i]];
            }
            // Should trigger an automatic child node update because the children
            // should be listening:
            model.setChildElements(nue);
        }
    }
            */

    /** Given a root node, locate the node below it which represents
     *  the given todoitem.
     */
    public static Node find(Node root, Task target) {
        Task item = getTask(root);
        if (item == target) {
            // Done - you called this method on the node which contains the item
            return root;
        }
        
        // First we've gotta locate the ancestry of the todo item,
        // such that we can descend the node hierarchy and know which
        // todoitem to look for (which ancestor) to pursue - that way
        // we don't have to look at the whole tree of nodes.
        // (Of course, I suspect that the tree will be really flat - most
        // todo item will be at the toplevel, at least the way -I- use
        // the todowindow - but of course other users may use more of
        // a hierarchical approach and then this will really help)

        // Find parent children objects
        Task p = target;
        LinkedList ancestry = new LinkedList();
        while ((p != null) && (p != item)) {
            ancestry.addFirst(p);
            p = p.getParent();
        }
        
        Node n = root;
        ListIterator it = ancestry.listIterator();
        while (it.hasNext()) {
            Task parent = (Task)it.next();
            // Locate this parent
            org.openide.nodes.Children c = n.getChildren();
            Node[] nc = c.getNodes();
            for (int i = 0; i < nc.length; i++) {
                n = nc[i];
                if (getTask(n) == parent) {
                    break;
                }
            }
        }
        if (getTask(n) == target) {
            return n;
        } else {
            return null;
        }
    }

    /** Find the Task corresponding to a given node, or null
        if this node does not represent a task */
    public static Task getTask(Node n) {
        if (n == null) {
            return null;
        }
        if (n instanceof TaskNode) {
            return ((TaskNode)n).getTask();
        } else if (n instanceof FilterTaskNode) {
            n = ((FilterTaskNode)n).getOriginal();
            if (n instanceof TaskNode) {
                return ((TaskNode)n).getTask();
            }
        }
        return null;
    }
    
    /** Find the TaskNode corresponding to a given node, or null
        if this node does not represent a TaskNode */
    public static TaskNode getTaskNode(Node n) {
        if (n == null) {
            return null;
        }
        if (n instanceof TaskNode) {
            return (TaskNode)n;
        } else if (n instanceof FilterTaskNode) {
            n = ((FilterTaskNode)n).getOriginal();
            if (n instanceof TaskNode) {
                return (TaskNode)n;
            }
        }
        return null;
    }

    public static class FilterTaskNode extends FilterNode {
        private boolean overrideIcon;
        
        public FilterTaskNode(Node n, org.openide.nodes.Children children,
                       boolean overrideIcon) {
            super(n, children);
            this.overrideIcon = overrideIcon;
        }

        /** Override the icon? */
        public Image getIcon (int type) {
            if (overrideIcon) {
                return Utilities.loadImage(
                      "org/netbeans/modules/tasklist/core/unmatched.gif"); // NOI18N
            } else {
                return super.getIcon(type);
            }
        }

        public Image getOpenedIcon(int type) {
            if (overrideIcon) {
                return Utilities.loadImage(
                      "org/netbeans/modules/tasklist/core/unmatched.gif"); // NOI18N
            } else {
                return super.getOpenedIcon(type);
            }
        }

        
        public Node getOriginal() {
            return super.getOriginal();
        }        
    }
    
    public static class FilteredChildren extends FilterNode.Children {
        private Filter filter;
        private TaskListView view;
        
        public FilteredChildren(TaskListView view, Node n, Filter filter) {
            super(n);
            this.filter = filter;
            this.view = view;
        }

        protected Node [] createNodes(Object key) {
            if ((key != null) && (key instanceof Node)) {
                Node n = (Node)key;
                if (filter.accept(n)) {
                    org.openide.nodes.Children children;
                    if (n.getChildren() == org.openide.nodes.Children.LEAF) {
                        children = org.openide.nodes.Children.LEAF;
                    } else {
                        children = new FilteredChildren(view, n, filter);
                    }
                    return new Node[] { new FilterTaskNode(n, children, false) };
                } else {
                    if (filter.isFlattened()) {
                        // Add all matching subtasks
                        ArrayList matches = new ArrayList();
                        findMatches(matches, n);

                        Node[] nodes = new Node[matches.size()];
                        ListIterator it = matches.listIterator();
                        int index = 0;
                        while (it.hasNext()) {
                            Node node = (Node)it.next();
                            org.openide.nodes.Children children;
                            if (n.getChildren() == org.openide.nodes.Children.LEAF) {
                                children = org.openide.nodes.Children.LEAF;
                            } else {
                                children = new FilteredChildren(view, node, filter);
                            }
                            nodes[index++] = new FilterTaskNode(node, children,
                                                                false);
                        }
                        return nodes;
                    } else {
                        // Perhaps the node has subtasks which accept. If so,
                        // we check those as well...
                        org.openide.nodes.Children c = n.getChildren();
                        Node[] nc = c.getNodes();
                        for (int i = 0; i < nc.length; i++) {
                            if (hasMatch(nc[i])) {
                                // Yes - add task, but mark it with a
                                // special nonmatching icon.
                                org.openide.nodes.Children children;
                                if (n.getChildren() == org.openide.nodes.Children.LEAF) {
                                    children = org.openide.nodes.Children.LEAF;
                                } else {
                                    children = new FilteredChildren(view, n, filter);
                                }                                
                                return new Node[] {
                                    new FilterTaskNode(n, children, true) };
                            }
                        }
                    }
                }
            }
            return new Node[0];
        }
        
        /** Return true iff node n or one of its children matches
            the filter */
        private boolean hasMatch(Node n) {
            if (filter.accept(n)) {
                return true;
            } else {
                org.openide.nodes.Children c = n.getChildren();
                Node[] nc = c.getNodes();
                for (int i = 0; i < nc.length; i++) {
                    boolean b = hasMatch(nc[i]);
                    if (b) {
                        return true;
                    }
                }
            }
            return false;
        }

        /** Return all matching nodes that are subnodes of the given node */
        private void findMatches(List matches, Node n) {
            // we check those as well...
            org.openide.nodes.Children c = n.getChildren();
            Node[] nc = c.getNodes();
            for (int i = 0; i < nc.length; i++) {
                if (filter.accept(nc[i])) {
                    matches.add(nc[i]);
                } else {
                    findMatches(matches, nc[i]);
                }
            }
        }
        
        
        public void filterChildrenAdded(NodeMemberEvent ev) {
            super.filterChildrenAdded(ev);
            if (view != null) {
                view.updateFilterCount(this);
            }
        }

        public void filterChildrenRemoved(NodeMemberEvent ev) {
            super.filterChildrenRemoved(ev);
            if (view != null) {
                view.updateFilterCount(this);
            }
        }
    }    
}

