/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.python.api;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Utilities;

/**
 *
 * @author alley
 * @author Lou Dasaro
 */
public class PythonAutoDetector {
    private Logger LOGGER = Logger.getLogger(PythonAutoDetector.class.getName());

    private ArrayList<String> matches = new ArrayList<>();
    private boolean searchNestedDirectoies = true;
    private void processAction(File dir) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Inspecting: " + dir.getAbsolutePath());
        }
        if(dir.isFile()){
            int pos = dir.getName().indexOf(".");
            String name = null;
            String ext = null;
            if (pos > -1 ){
                 name = dir.getName().substring(0, pos);
                 ext = dir.getName().substring(pos+1);
            }else{
                name = dir.getName();
                ext = "";
            }
            if( name.equalsIgnoreCase("jython") || name.equalsIgnoreCase("python")) {
                if (Utilities.isWindows()){
                    if (ext.equalsIgnoreCase("exe") || ext.equalsIgnoreCase("bat")) {
                        matches.add(dir.getAbsolutePath());
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.FINE, "Match (Windows): " + dir.getAbsolutePath());
                        }
                    }
                } else if(Utilities.isMac()) {
                    if (ext.equalsIgnoreCase("")) {
                        matches.add(dir.getAbsolutePath());
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.FINE, "Match (Mac): " + dir.getAbsolutePath());
                        }
                    }
                } else { // Not Windows or Mac, must be Unix-like system...
                    if (ext.equalsIgnoreCase("")) {
                        matches.add(dir.getAbsolutePath());
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.FINE, "Match (Unix-like): " + dir.getAbsolutePath());
                        }                     
                    }
                }
            }
        }
        
        if(dir.isDirectory()){
            if(dir.getName().toLowerCase().contains("jython") ||
                    dir.getName().toLowerCase().contains("python")){
                String[] children = dir.list();
                if(children != null){
                    for (String child : children) {
                        traverse(new File(dir, child), searchNestedDirectoies);
                    }
                }
            }
        }

    }

    public void traverse(File dir, boolean recersive) {

        processAction(dir);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children != null){
                if(searchNestedDirectoies){
                    if(searchNestedDirectoies != recersive)
                        searchNestedDirectoies = recersive;
                    for (String child : children) {
                        traverse(new File(dir, child), recersive);
                    }
                }
            }
        }

    }

    public ArrayList<String> getMatches() {
        return matches;
    }

    
}
