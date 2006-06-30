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

package org.netbeans.modules.vcscore.commands;

/**
 * This extension of CommandSupport can be asked whether the created commands
 * will be able to process folders nonrecursively.
 * This is necessary for flat views, which do not show subfolders.
 *
 * @author  Martin Entlicher
 */
public interface RecursionAwareCommandSupport {

    /**
     * Tells, whether the created commands will be able to process folders
     * non-recursively. If this is true, the created commands should implement
     * {@link RecursionAwareCommand} and when <code>setRecursionBanned(true)</code>
     * is called on them, they should still return non-<code>null</code>
     * from <code>getApplicableFiles()</code> method for some files.
     * @return true when the created commands can process folders non-recursively.
     */
    boolean canProcessFoldersNonRecursively();
    
}
