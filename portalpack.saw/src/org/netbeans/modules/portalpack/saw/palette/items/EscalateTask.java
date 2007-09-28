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

package org.netbeans.modules.portalpack.saw.palette.items;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.text.ActiveEditorDrop;
import org.openide.util.NbBundle;
/**
 *
 * @author Vihang
 */
public class EscalateTask implements ActiveEditorDrop {
private static Logger logger = Logger.getLogger("SAW_Logger");
    private String portaltaskvo;


    public EscalateTask() {
    }

    public boolean handleTransfer(JTextComponent targetComponent) {
      
       
        BaseDocument document = (BaseDocument) targetComponent.getDocument();
        FileObject fObject = NbEditorUtilities.getFileObject(document);
        logger.log(Level.INFO,this.getClass() + ":",fObject.toString());
        ClassPath cp = ClassPath.getClassPath(fObject, ClassPath.COMPILE);
        if (cp != null) {
            logger.log(Level.INFO,this.getClass() + ":",cp.toString());
        }
        String className = cp.getResourceName(fObject, '.', false);
        logger.log(Level.INFO,this.getClass() + ":","Class Name is:" + className);
        Class clazz = null;
        boolean flag = false;
        return RefactoringUtil.addMethod(fObject,NbBundle.getBundle(EscalateTask.class).getString("Escalate_tasks"));
        
    }
}