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

package org.netbeans.spi.jackpot;

/**
 * Reports errors parsing rules files.
 */
public class ScriptParsingException extends RuntimeException {

    /**
     * Constructs an instance of <code>ScriptParsingException</code> with the
     * specified error message(s).
     * @param errorMsg the error message text.
     */
    public ScriptParsingException(String errorMsg) {
	super(errorMsg);
    }
    
    /**
     * Constructs an instance of <code>ScriptParsingException</code> with the 
     * specified detail message and cause.
     *
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public ScriptParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
