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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.callgraph.impl;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.GridGraphLayout;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.modules.cnd.callgraph.api.Call;
import org.netbeans.modules.cnd.callgraph.api.CallModel;
import org.netbeans.modules.cnd.callgraph.api.Function;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Alexander Simon
 */
public class CallGraphPanel extends JPanel implements ExplorerManager.Provider, HelpCtx.Provider  {
    
    private ExplorerManager explorerManager = new ExplorerManager();
    private AbstractNode root;
    private Action[] actions;
    private CallModel model;
    private boolean isCalls = true;
    private CallGraphScene scene = new CallGraphScene();
    
    /** Creates new form CallGraphPanel */
    public CallGraphPanel() {
        initComponents();
        getTreeView().setRootVisible(false);
        Children.Array children = new Children.SortedArray();
        actions = new Action[]{new RefreshAction(),
                               null, new WhoIsCalledAction(), new WhoCallsAction(),
                               null, new ExportAction(scene, this)};
        root = new AbstractNode(children){
            @Override
            public Action[] getActions(boolean context) {
                return actions;
            }
        };
        getExplorerManager().setRootContext(root);
        jSplitPane1.setDividerLocation(0.5);
        initGraph();
    }
    
    private void initGraph() {
        GraphLayout<Function,Call> layout = new GridGraphLayout<Function,Call>();
        SceneLayout sceneLayout = LayoutFactory.createSceneGraphLayout(scene, layout);
        scene.setLayout(sceneLayout);
        sceneLayout.invokeLayout();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        refresh = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        calls = new javax.swing.JToggleButton();
        callers = new javax.swing.JToggleButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        treeView = new BeanTreeView();
        graphView = new JScrollPane(scene.createView());

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(1);
        jToolBar1.setRollover(true);

        refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/cnd/callgraph/resources/refresh.png"))); // NOI18N
        refresh.setToolTipText(org.openide.util.NbBundle.getMessage(CallGraphPanel.class, "RefreshAction")); // NOI18N
        refresh.setFocusable(false);
        refresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });
        jToolBar1.add(refresh);
        jToolBar1.add(jSeparator1);

        calls.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/cnd/callgraph/resources/who_is_called.png"))); // NOI18N
        calls.setToolTipText(org.openide.util.NbBundle.getMessage(CallGraphPanel.class, "CallsAction")); // NOI18N
        calls.setFocusable(false);
        calls.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        calls.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        calls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                callsActionPerformed(evt);
            }
        });
        jToolBar1.add(calls);

        callers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/cnd/callgraph/resources/who_calls.png"))); // NOI18N
        callers.setToolTipText(org.openide.util.NbBundle.getMessage(CallGraphPanel.class, "CallersAction")); // NOI18N
        callers.setFocusable(false);
        callers.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        callers.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        callers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                callersActionPerformed(evt);
            }
        });
        jToolBar1.add(callers);

        add(jToolBar1, java.awt.BorderLayout.LINE_START);

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setLeftComponent(treeView);
        jSplitPane1.setRightComponent(graphView);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
        update();
    }//GEN-LAST:event_refreshActionPerformed

    private void callsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_callsActionPerformed
        if (isCalls == calls.isSelected()) {
            return;
        }
        setDirection(true);
    }//GEN-LAST:event_callsActionPerformed

    private void callersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_callersActionPerformed
        if (isCalls != callers.isSelected()) {
            return;
        }
        setDirection(false);
    }//GEN-LAST:event_callersActionPerformed
    
    private void setDirection(boolean direction){
        isCalls = direction;
        updateButtons();
        update();
    }

    private void updateButtons(){
        calls.setSelected(isCalls);
        callers.setSelected(!isCalls);
    }
    
    public void setModel(CallModel model) {
        this.model = model;
        this.isCalls = model.isCalls();
        updateButtons();
        update();
    }

    private synchronized void update() {
        ArrayList<Function> nodes = new ArrayList<Function>(scene.getNodes());
        for(Function f : nodes){
            scene.removeNodeWithEdges(f);
        }
        scene.doLayout();
        model.refresh();
        final Function function = model.getRoot();
        if (function != null){
            final Children children = root.getChildren();
            if (!Children.MUTEX.isReadAccess()){
                Children.MUTEX.writeAccess(new Runnable(){
                    public void run() {
                        children.remove(children.getNodes());
                        CallGraphState state = new CallGraphState(model, scene, actions);
                        final Node node = new FunctionRootNode(function, state, isCalls);
                        scene.doLayout();
                        children.add(new Node[]{node});
                        try {
                            getExplorerManager().setSelectedNodes(new Node[]{node});
                        } catch (PropertyVetoException ex) {
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                getTreeView().expandNode(node);
                            }
                        });
                    }
                });
            }
        } else {
            final Children children = root.getChildren();
            if (!Children.MUTEX.isReadAccess()){
                Children.MUTEX.writeAccess(new Runnable(){
                    public void run() {
                        children.remove(children.getNodes());
                    }
                });
            }
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        return treeView.requestFocusInWindow();
    }
    
    public BeanTreeView getTreeView(){
        return (BeanTreeView)treeView;
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx("CallGraphView"); // NOI18N
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton callers;
    private javax.swing.JToggleButton calls;
    private javax.swing.JScrollPane graphView;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton refresh;
    private javax.swing.JScrollPane treeView;
    // End of variables declaration//GEN-END:variables
    
    private class RefreshAction extends AbstractAction implements Presenter.Popup {
        private JMenuItem menuItem;
        public RefreshAction() {
            putValue(Action.NAME, NbBundle.getMessage(CallGraphPanel.class, "RefreshAction"));  // NOI18N
            putValue(Action.SMALL_ICON, refresh.getIcon());
            menuItem = new JMenuItem((String)getValue(Action.NAME)); 
            menuItem.setAction(this);
        }

        public void actionPerformed(ActionEvent e) {
            refreshActionPerformed(e);
        }

        public final JMenuItem getPopupPresenter() {
            return menuItem;
        }
    }
    private class WhoCallsAction extends AbstractAction implements Presenter.Popup {
        private JRadioButtonMenuItem menuItem;
        public WhoCallsAction() {
            putValue(Action.NAME, NbBundle.getMessage(CallGraphPanel.class, "CallersAction"));  // NOI18N
            putValue(Action.SMALL_ICON, callers.getIcon());
            menuItem = new JRadioButtonMenuItem((String)getValue(Action.NAME)); 
            menuItem.setAction(this);
        }
 
        public void actionPerformed(ActionEvent e) {
            setDirection(false);
        }

        public final JMenuItem getPopupPresenter() {
            menuItem.setSelected(!isCalls);
            return menuItem;
        }
    }

    private class WhoIsCalledAction extends AbstractAction implements Presenter.Popup {
        private JRadioButtonMenuItem menuItem;
        public WhoIsCalledAction() {
            putValue(Action.NAME, NbBundle.getMessage(CallGraphPanel.class, "CallsAction"));  // NOI18N
            putValue(Action.SMALL_ICON, calls.getIcon());
            menuItem = new JRadioButtonMenuItem((String)getValue(Action.NAME)); 
            menuItem.setAction(this);
        }
 
        public void actionPerformed(ActionEvent e) {
            setDirection(true);
        }

        public final JMenuItem getPopupPresenter() {
            menuItem.setSelected(isCalls);
            return menuItem;
        }
    }
}