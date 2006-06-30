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

/*
 * IDLSourceStep.java
 *
 * Created on 15.7.02 13:51
 */
package org.netbeans.jellytools.modules.corba.idlwizard;

import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "New Wizard - Empty" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class IDLSourceStep extends NewWizardOperator {

    /** Creates new IDLSourceStep that can handle it.
     */
    public IDLSourceStep() {
        stepsWaitSelectedValue ("IDL Source");
    }

    private JRadioButtonOperator _rbIDLWizard;
    private JRadioButtonOperator _rbInterfaceRepository;
    private JLabelOperator _lblGenerateIDLFrom;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "IDL Wizard" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbIDLWizard() {
        if (_rbIDLWizard==null) {
            _rbIDLWizard = new JRadioButtonOperator(this, "IDL Wizard");
        }
        return _rbIDLWizard;
    }

    /** Tries to find "Interface Repository" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbInterfaceRepository() {
        if (_rbInterfaceRepository==null) {
            _rbInterfaceRepository = new JRadioButtonOperator(this, "Interface Repository");
        }
        return _rbInterfaceRepository;
    }

    /** Tries to find "Generate IDL from:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblGenerateIDLFrom() {
        if (_lblGenerateIDLFrom==null) {
            _lblGenerateIDLFrom = new JLabelOperator(this, "Generate IDL from:");
        }
        return _lblGenerateIDLFrom;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** clicks on "IDL Wizard" JRadioButton
     */
    public void iDLWizard() {
        rbIDLWizard().push();
    }

    /** clicks on "Interface Repository" JRadioButton
     */
    public void interfaceRepository() {
        rbInterfaceRepository().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of IDLSourceStep by accessing all its components.
     */
    public void verify() {
        rbIDLWizard();
        rbInterfaceRepository();
        lblGenerateIDLFrom();
    }

    /** Performs simple test of IDLSourceStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new IDLSourceStep().verify();
        System.out.println("IDLSourceStep verification finished.");
    }
}

