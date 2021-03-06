/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */
package org.netbeans.api.tableview;

import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.util.datatransfer.PasteType;

import java.awt.datatransfer.*;
import java.awt.dnd.*;

import javax.swing.JTable;
import javax.swing.SwingUtilities;


/**
*
* @author Dafe Simonek
*/
final class TableViewDropSupport implements DropTargetListener, Runnable {
    // Attributes

    /** true if support is active, false otherwise */
    boolean active = false;
    boolean dropTargetPopupAllowed;

    /** Drop target asociated with the tree */
    DropTarget dropTarget;

    /** The index of last item the cursor hotspot was above */
    int lastIndex = -1;

    // Associations

    /** View manager. */
    protected TableView view;

    /** The component we are supporting with drop support */
    protected JTable table;

    // Operations
    public TableViewDropSupport(TableView view, JTable table) {
        this(view, table, true);
    }

    /** Creates new TreeViewDropSupport */
    public TableViewDropSupport(TableView view, JTable table, boolean dropTargetPopupAllowed) {
        this.view = view;
        this.table = table;
        this.dropTargetPopupAllowed = dropTargetPopupAllowed;
    }

    public void setDropTargetPopupAllowed(boolean value) {
        dropTargetPopupAllowed = value;
    }

    public boolean isDropTargetPopupAllowed() {
        return dropTargetPopupAllowed;
    }

    /** User is starting to drag over us */
    public void dragEnter(DropTargetDragEvent dtde) {
        int dropAction = ExplorerDnDManager.getDefault().getAdjustedDropAction(
                dtde.getDropAction(), view.getAllowedDropActions()
            );
        ExplorerDnDManager.getDefault().prepareCursor(
            DragDropUtilities.chooseCursor(
                dtde.getDropTargetContext().getComponent(), dropAction, (dropAction & view.getAllowedDropActions()) != 0
            )
        );

        lastIndex = indexWithCheck(dtde);

        if (lastIndex < 0) {
            dtde.rejectDrag();
        } else {
            dtde.acceptDrag(dropAction);
            // TODO
//            NodeRenderer.dragEnter(list.getModel().getElementAt(lastIndex));
//            list.repaint(list.getCellBounds(lastIndex, lastIndex));
        }
    }

    /** User drags over us */
    public void dragOver(DropTargetDragEvent dtde) {
        int dropAction = ExplorerDnDManager.getDefault().getAdjustedDropAction(
                dtde.getDropAction(), view.getAllowedDropActions()
            );
        ExplorerDnDManager.getDefault().prepareCursor(
            DragDropUtilities.chooseCursor(
                dtde.getDropTargetContext().getComponent(), dropAction, (dropAction & view.getAllowedDropActions()) != 0
            )
        );

        int index = indexWithCheck(dtde);

        if (index < 0) {
            dtde.rejectDrag();

            if (lastIndex >= 0) {
                // TODO
//                NodeRenderer.dragExit();
//                list.repaint(list.getCellBounds(lastIndex, lastIndex));
                lastIndex = -1;
            }
        } else {
            dtde.acceptDrag(dropAction);

            if (lastIndex != index) {
                if (lastIndex < 0) {
                    lastIndex = index;
                }

                // TODO
//                NodeRenderer.dragExit();
//                NodeRenderer.dragEnter(list.getModel().getElementAt(index));
//                list.repaint(list.getCellBounds(lastIndex, index));
                lastIndex = index;
            }
        }
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
        // PENDING...?
    }

    /** User exits the dragging */
    public void dragExit(DropTargetEvent dte) {
        if (lastIndex >= 0) {
            // TODO
//            NodeRenderer.dragExit();
//            list.repaint(list.getCellBounds(lastIndex, lastIndex));
        }
    }

    /** Performs the drop action, if we are dropping on
    * right node and target node agrees.
    */
    public void drop(DropTargetDropEvent dtde) {
        // obtain the node we have cursor on
        int index = table.rowAtPoint(dtde.getLocation());
        Node dropNode = view.getNodeFromRow(index);

        int dropAction = ExplorerDnDManager.getDefault().getAdjustedDropAction(
                dtde.getDropAction(), view.getAllowedDropActions()
            );

        // return if conditions are not satisfied
        if ((index < 0) || !canDrop(dropNode, dropAction)) {
            dtde.rejectDrop();

            return;
        }

        // get paste types for given transferred transferable
        PasteType pt = DragDropUtilities.getDropType(
                dropNode,
                ExplorerDnDManager.getDefault().getDraggedTransferable((DnDConstants.ACTION_MOVE & dropAction) != 0),
                dropAction
            );

        if (pt == null) {
            dtde.dropComplete(false);

            // something is wrong, notify user
            // ugly hack, but if we don't wait, deadlock will come
            // (sun's issue....)
            RequestProcessor.getDefault().post(this, 500);

            return;
        }

        // finally perform the drop
        dtde.acceptDrop(dropAction);

        if (dropAction == DnDConstants.ACTION_LINK) {
            // show popup menu to the user
            // PENDING
        } else {
            DragDropUtilities.performPaste(pt, null);
        }
    }

    /** Can node recieve given drop action? */

    // XXX canditate for more general support
    private boolean canDrop(Node n, int dropAction) {
        if (n == null) {
            return false;
        }

        if (ExplorerDnDManager.getDefault().getNodeAllowedActions() == DnDConstants.ACTION_NONE) {
            return false;
        }

        // test if a parent of the dragged nodes isn't the node over
        // only for MOVE action
        if ((DnDConstants.ACTION_MOVE & dropAction) != 0) {
            Node[] nodes = ExplorerDnDManager.getDefault().getDraggedNodes();
            if (nodes != null) {
                for (int i = 0; i < nodes.length; i++) {
                    if (n.equals(nodes[i].getParentNode())) {
                        return false;
                    }
                }
            }
        }

        Transferable trans = ExplorerDnDManager.getDefault().getDraggedTransferable(
                (DnDConstants.ACTION_MOVE & dropAction) != 0
            );

        if (trans == null) {
            return false;
        }

        // get paste types for given transferred transferable
        PasteType pt = DragDropUtilities.getDropType(n, trans, dropAction);

        return (pt != null);
    }

    /** Activates or deactivates Drag support on asociated JTree
    * component
    * @param active true if the support should be active, false
    * otherwise
    */
    public void activate(boolean active) {
        if (this.active == active) {
            return;
        }

        this.active = active;
        getDropTarget().setActive(active);
    }

    /** Implementation of the runnable interface.
    * Notifies user in AWT thread. */
    public void run() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this);

            return;
        }

        DragDropUtilities.dropNotSuccesfull();
    }

    /** @return The tree path to the node the cursor is above now or
    * null if no such node currently exists or if conditions were not
    * satisfied to continue with DnD operation.
    */
    int indexWithCheck(DropTargetDragEvent dtde) {
        int dropAction = ExplorerDnDManager.getDefault().getAdjustedDropAction(
                dtde.getDropAction(), view.getAllowedDropActions()
            );

        // check actions
        if ((dropAction & view.getAllowedDropActions()) == 0) {
            return -1;
        }

        // check location
        int index = table.rowAtPoint(dtde.getLocation());
        if (index == -1) return -1;
        Object obj = view.getNodeFromRow(index);

        if (index < 0) {
            return -1;
        }

        if (!(obj instanceof Node)) {
            return -1;
        }

        // succeeded
        return index;
    }

    /** Safe accessor to the drop target which is asociated
    * with the tree */
    DropTarget getDropTarget() {
        if (dropTarget == null) {
            dropTarget = new DropTarget(table, view.getAllowedDropActions(), this, false);
        }

        return dropTarget;
    }
}
