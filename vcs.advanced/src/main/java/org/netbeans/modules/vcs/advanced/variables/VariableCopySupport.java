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

package org.netbeans.modules.vcs.advanced.variables;

import java.awt.datatransfer.*;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.openide.cookies.InstanceCookie;
import org.openide.nodes.*;
import org.openide.util.datatransfer.PasteType;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;

class VariableCopySupport extends Object {

    public static DataFlavor VARIABLE_COPY_FLAVOR = new VariableDataFlavor(
        AbstractNode.class,
        "VARIABLE_COPY_FLAVOR"); // NOI18N

    public static DataFlavor VARIABLE_CUT_FLAVOR = new VariableDataFlavor(
        AbstractNode.class,
        "VARIABLE_COPY_FLAVOR"); // NOI18N

    static class VariableDataFlavor extends DataFlavor {
        
        private static final long serialVersionUID = 3504410287291109201L;
        
        VariableDataFlavor(Class representationClass, String name) {
            super(representationClass, name);
        }
    }

    // -----------

    public static class VariableTransferable implements Transferable {
        private AbstractNode var;
        private DataFlavor[] flavors;

        VariableTransferable(DataFlavor flavor, AbstractNode var) {
            this(new DataFlavor[] { flavor }, var);
        }

        VariableTransferable(DataFlavor[] flavors, AbstractNode var) {
            this.flavors = flavors;
            this.var = var;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i] == flavor) { // comparison based on exact instances, as these are static in this node
                    return true;
                }
            }
            return false;
        }

        public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, java.io.IOException
        {
            if (flavor instanceof VariableDataFlavor) {
                return var;
            }
            throw new UnsupportedFlavorException(flavor);
        }
    }

    // -----------

    /** Method for checking whether a component can be moved to a container
     * (the component should not be pasted to its own sub-container
     * or even to itself). *
    public static boolean canPasteCut(VcsCommand sourceCmd,
                                      FormModel targetForm,
                                      ComponentContainer targetContainer) {
        if (sourceComponent.getFormModel() != targetForm)
            return true;

        if (targetContainer == null
                || targetContainer == targetForm.getModelContainer())
            return targetForm.getModelContainer().getIndexOf(sourceComponent) < 0;

        RADComponent targetComponent = (RADComponent) targetContainer;

        return sourceComponent != targetComponent
               && sourceComponent.getParentComponent() != targetComponent
               && !sourceComponent.isParentComponent(targetComponent);
    } */

    // -----------

    /** Paste type for meta components.
     */
    public static class VariablePaste extends PasteType {
        private Transferable transferable;
        private AbstractNode targetNode;
        //private FormModel targetForm;

        public VariablePaste(Transferable t,
                             AbstractNode targetNode) {
            this.transferable = t;
            this.targetNode = targetNode;
        }

        public Transferable paste() throws java.io.IOException {
            boolean fromCut =
                transferable.isDataFlavorSupported(VARIABLE_CUT_FLAVOR);

            AbstractNode sourceNode = null;
            try {
                sourceNode = (AbstractNode)
                    transferable.getTransferData(fromCut ?
                        VARIABLE_CUT_FLAVOR : VARIABLE_COPY_FLAVOR);
            }
            catch (java.io.IOException e) { } // ignore - should not happen
            catch (UnsupportedFlavorException e) { } // ignore - should not happen

            //if (sourceCommand == null)
            //    return null;

            if (!fromCut) { // pasting copy of RADComponent
                copyVariables(sourceNode, targetNode);
                /*
                targetForm.getComponentCreator()
                    .copyComponent(sourceComponent, targetContainer);
                 */
                return null;
            }
            else { // pasting cut RADComponent (same instance)
                if (!targetNode.equals(sourceNode.getParentNode())) {
                    //CommandNode newNode = new CommandNode(Children.LEAF, sourceCommand.getCommand());
                    sourceNode.destroy();
                    copyVariables(sourceNode, targetNode);
                    //targetNode.getChildren().add(new Node[] { newNode });
                    return null;
                }
                /*
                FormModel sourceForm = sourceComponent.getFormModel();
                if (sourceForm != targetForm) { // taken from another form
                    Node sourceNode = sourceComponent.getNodeReference();
                    // delete component in the source
                    if (sourceNode != null)
                        sourceNode.destroy();
                    else throw new IllegalStateException();

                    sourceComponent.initialize(targetForm);
                }
                else { // moving within the same form
                    if (!canPasteCut(sourceComponent, targetForm, targetContainer))
                        return transferable; // ignore paste

                    // remove source component from its parent
                    sourceForm.removeComponent(sourceComponent);
                }
                 */

                // return new copy flavor, as the first one was used already
                return new VariableTransferable(VARIABLE_COPY_FLAVOR, sourceNode);
            }
        }
    }
    
    private static void copyVariables(AbstractNode sourceNode, AbstractNode targetNode) {
        VcsConfigVariable var;
        Map vbc;
        Condition[] conditions = null;
        if (sourceNode instanceof BasicVariableNode) {
            var = ((BasicVariableNode) sourceNode).getVariable();
            vbc = ((BasicVariableNode) sourceNode).getVarsByConditions();
        } else if (sourceNode instanceof AccessoryVariableNode) {
            var = ((AccessoryVariableNode) sourceNode).getVariable();
            vbc = ((AccessoryVariableNode) sourceNode).getVarsByConditions();
        } else return ;
        if (var == null) return ;
        var = new VcsConfigVariable(var.getName(), var.getLabel(), var.getValue(),
                                    var.isBasic(), var.isLocalFile(), var.isLocalDir(),
                                    var.getCustomSelector(), var.getOrder());
        Collection varNames = BasicVariableNode.getAllVariablesNames(targetNode);
        varNames.addAll(Variables.getContextVariablesNames());
        var.setName(VcsUtilities.createUniqueName(var.getName(), varNames));
        
        if (targetNode instanceof BasicVariableNode) {
            var.setBasic(true);
            String label = var.getLabel();
            if (label ==  null || label.length() == 0) var.setLabel(var.getName());
            int order = targetNode.getChildren().getNodesCount();
            var.setOrder(order);
        } else {
            var.setBasic(false);
            var.setLabel(null);
        }
        Map varsByConditions = null;
        if (vbc != null) {
            conditions = new Condition[vbc.size()];
            varsByConditions = new IdentityHashMap();
            int i = 0;
            for (Iterator it = vbc.keySet().iterator(); it.hasNext(); i++) {
                Condition c = (Condition) it.next();
                VcsConfigVariable cvar = (VcsConfigVariable) vbc.get(c);
                c = (Condition) c.clone();
                cvar = (VcsConfigVariable) cvar.clone();
                cvar.setName(var.getName());
                cvar.setBasic(var.isBasic());
                cvar.setLabel(var.getLabel());
                cvar.setOrder(var.getOrder());
                varsByConditions.put(c, cvar);
                conditions[i] = c;
            }
        }
        AbstractNode newNode;
        if (targetNode instanceof BasicVariableNode) {
            if (varsByConditions != null) {
                newNode = new BasicVariableNode(var.getName(), conditions, varsByConditions);
            } else {
                newNode = new BasicVariableNode(var);
            }
        } else if (targetNode instanceof AccessoryVariableNode) {
            if (varsByConditions != null) {
                newNode = new AccessoryVariableNode(var.getName(), conditions, varsByConditions);
            } else {
                newNode = new AccessoryVariableNode(var);
            }
        } else return ;
        targetNode.getChildren().add(new Node[] { newNode });
    }

}
