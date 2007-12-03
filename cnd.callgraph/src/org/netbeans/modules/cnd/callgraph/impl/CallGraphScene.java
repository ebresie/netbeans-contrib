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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.cnd.callgraph.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Set;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.cnd.callgraph.api.Call;
import org.netbeans.modules.cnd.callgraph.api.Function;

/**
 * @author David Kaspar
 */
public class CallGraphScene extends GraphScene<Function,Call> {

    private static final Border BORDER_4 = BorderFactory.createLineBorder (4);

    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;
    private Router router;
    private SceneLayout sceneLayout;

    private WidgetAction moveAction = ActionFactory.createMoveAction();
    private WidgetAction hoverAction = createWidgetHoverAction();

    Object lock = new String("Call Graph lock"); // NOI18N

    public CallGraphScene() {
        mainLayer = new LayerWidget (this);
        addChild(mainLayer);

        connectionLayer = new LayerWidget (this);
        addChild(connectionLayer);
        router = RouterFactory.createOrthogonalSearchRouter (mainLayer, connectionLayer);
    }
    
    public void setLayout(SceneLayout sceneLayout){
        this.sceneLayout = sceneLayout;
    }
    
    public void doLayout(){
        synchronized(lock) {
            try {
                sceneLayout.invokeLayout();
                validate();
            } catch (ConcurrentModificationException ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void paintWidget() {
        synchronized (lock) {
            super.paintWidget();
        }
    }

    @Override
    protected void paintChildren() {
        synchronized (lock) {
            if (isValidated()) {
                super.paintChildren();
                this.getBounds();
            }
        }
    }

    protected Widget attachNodeWidget(Function node) {
        LabelWidget label = null;
        synchronized (lock) {
            label = new MyLabelWidget(this, node.getName());
            label.setBorder(BORDER_4);
            label.getActions().addAction(moveAction);
            label.getActions().addAction(hoverAction);
            label.getActions().addAction(ActionFactory.createEditAction(new NodeEditProvider(node)));
            mainLayer.addChild(label);
        }
        return label;
    }

    protected Widget attachEdgeWidget(Call edge) {
        ConnectionWidget connection = null;
        synchronized (lock) {
            connection = new ConnectionWidget (this);
            connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
            connection.getActions().addAction(hoverAction);
            connection.getActions().addAction(ActionFactory.createEditAction(new EdgeEditProvider(edge)));
            connectionLayer.addChild(connection);
        }
        return connection;
    }

    public void addLoopEdge(Call edge, Function targetNode) {
        ConnectionWidget connection = (ConnectionWidget)addEdge(edge);
        Widget w = findWidget(targetNode);
        connection.setRouter(router);
        MyVMDNodeAnchor anchor = new MyVMDNodeAnchor(w);
        setEdgeSource(edge, targetNode);
        connection.setSourceAnchor(anchor);
        setEdgeTarget(edge, targetNode);
        connection.setTargetAnchor(anchor);
    }

    protected void attachEdgeSourceAnchor(Call edge, Function oldSourceNode, Function sourceNode) {
        synchronized (lock) {
            Widget w = sourceNode != null ? findWidget(sourceNode) : null;
            ((ConnectionWidget) findWidget(edge)).setSourceAnchor (AnchorFactory.createRectangularAnchor(w));
        }
    }

    protected void attachEdgeTargetAnchor(Call edge, Function oldTargetNode, Function targetNode) {
        synchronized (lock) {
            Widget w = targetNode != null ? findWidget(targetNode) : null;
            ((ConnectionWidget) findWidget(edge)).setTargetAnchor (AnchorFactory.createRectangularAnchor(w));
        }
    }

    private static class NodeEditProvider implements EditProvider {
        private Function node;
        private NodeEditProvider(Function node){
            this.node = node;
        }
        
        public void edit(Widget widget) {
            node.open();
        }
    }

    private static class EdgeEditProvider implements EditProvider {
        private Call call;
        private EdgeEditProvider(Call call){
            this.call = call;
        }
        
        public void edit(Widget widget) {
            call.open();
        }
    }

    private static final class MyLabelWidget extends LabelWidget {
        public MyLabelWidget (Scene scene, String label) {
            super (scene, label);
        }

        @Override
        protected void notifyStateChanged (ObjectState previousState, ObjectState state) {
            if (previousState.isHovered ()  == state.isHovered ()) {
                return;
            }
            setForeground (getScene().getLookFeel().getLineColor(state));
            repaint ();
        }
    }

    private static class MyVMDNodeAnchor extends Anchor {

        private boolean requiresRecalculation = true;
        private HashMap<Entry, Result> results = new HashMap<Entry, Result>();
        private final boolean vertical;

        public MyVMDNodeAnchor(Widget widget) {
            super(widget);
            this.vertical = true;
        }

        /**
         * Notifies when an entry is registered
         * @param entry the registered entry
         */
        @Override
        protected void notifyEntryAdded(Entry entry) {
            requiresRecalculation = true;
        }

        /**
         * Notifies when an entry is unregistered
         * @param entry the unregistered entry
         */
        @Override
        protected void notifyEntryRemoved(Entry entry) {
            results.remove(entry);
            requiresRecalculation = true;
        }

        /**
         * Notifies when the anchor is going to be revalidated.
         * @since 2.8
         */
        @Override
        protected void notifyRevalidate() {
            requiresRecalculation = true;
        }

        private void recalculate() {
            if (!requiresRecalculation) {
                return;
            }

            Widget widget = getRelatedWidget();
            Point relatedLocation = getRelatedSceneLocation();

            Rectangle bounds = widget.convertLocalToScene(widget.getBounds());

            HashMap<Entry, Float> topmap = new HashMap<Entry, Float>();
            HashMap<Entry, Float> bottommap = new HashMap<Entry, Float>();

            for (Entry entry : getEntries()) {
                Point oppositeLocation = getOppositeSceneLocation(entry);
                if (oppositeLocation == null || relatedLocation == null) {
                    results.put(entry, new Result(new Point(bounds.x, bounds.y), DIRECTION_ANY));
                    continue;
                }

                int dy = oppositeLocation.y - relatedLocation.y;
                int dx = oppositeLocation.x - relatedLocation.x;

                if (vertical) {
                    if (dy > 0) {
                        bottommap.put(entry, (float) dx / (float) dy);
                    } else if (dy < 0) {
                        topmap.put(entry, (float) -dx / (float) dy);
                    } else {
                        topmap.put(entry, dx < 0 ? Float.MAX_VALUE : Float.MIN_VALUE);
                    }
                } else {
                    if (dx > 0) {
                        bottommap.put(entry, (float) dy / (float) dx);
                    } else if (dy < 0) {
                        topmap.put(entry, (float) -dy / (float) dx);
                    } else {
                        topmap.put(entry, dy < 0 ? Float.MAX_VALUE : Float.MIN_VALUE);
                    }
                }
            }

            Entry[] topList = toArray(topmap);
            Entry[] bottomList = toArray(bottommap);

            int pinGap = 0;
            int y = bounds.y - pinGap;
            int x = bounds.x - pinGap;
            int len = topList.length;

            for (int a = 0; a < len; a++) {
                Entry entry = topList[a];
                if (vertical) {
                    x = bounds.x + (a + 1) * bounds.width / (len + 1);
                } else {
                    y = bounds.y + (a + 1) * bounds.height / (len + 1);
                }
                results.put(entry, new Result(new Point(x, y), vertical ? Direction.TOP : Direction.LEFT));
            }

            y = bounds.y + bounds.height + pinGap;
            x = bounds.x + bounds.width + pinGap;
            len = bottomList.length;

            for (int a = 0; a < len; a++) {
                Entry entry = bottomList[a];
                if (vertical) {
                    x = bounds.x + (a + 1) * bounds.width / (len + 1);
                } else {
                    y = bounds.y + (a + 1) * bounds.height / (len + 1);
                }
                results.put(entry, new Result(new Point(x, y), vertical ? Direction.BOTTOM : Direction.RIGHT));
            }

            requiresRecalculation = false;
        }

        private Entry[] toArray(final HashMap<Entry, Float> map) {
            Set<Entry> keys = map.keySet();
            Entry[] entries = keys.toArray(new Entry[keys.size()]);
            Arrays.sort(entries, new Comparator<Entry>() {

                public int compare(Entry o1, Entry o2) {
                    float f = map.get(o1) - map.get(o2);
                    if (f > 0.0f) {
                        return 1;
                    } else if (f < 0.0f) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            return entries;
        }

        /**
         * Computes a result (position and direction) for a specific entry.
         * @param entry the entry
         * @return the calculated result
         */
        public Result compute(Entry entry) {
            recalculate();
            return results.get(entry);
        }
    }
}
