/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the Accelerators module.
 * The Initial Developer of the Original Code is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.

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
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.filesearch;

import org.openide.filesystems.FileObject;

/**
 *
 * @author Andrei Badea
 */
public class PrefixSearchFilter implements SearchFilter {

    private final String prefix;
    private final boolean caseSensitive;

    public PrefixSearchFilter(String prefix, boolean caseSensitive) {
        assert prefix != null;
        this.prefix = prefix;
        this.caseSensitive = caseSensitive;
    }

    public boolean accept(FileObject fo) {
        String fileName = fo.getNameExt();
        if (caseSensitive) {
            return fileName.startsWith(prefix);
        } else {
            return caseInsensitiveStartsWith(fileName, prefix);
        }
    }
    
    private static boolean caseInsensitiveStartsWith(String string, String prefix) {
        int len = prefix.length();
        if (len > string.length()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (Character.toLowerCase(string.charAt(i)) != Character.toLowerCase(prefix.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
