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

package org.netbeans.modules.vcscore.util.virtuals;

import org.openide.actions.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.UniFileLoader;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.util.actions.SystemAction;

import java.util.WeakHashMap;



/**
 * The loader of virtual files (files that are not locally present
 *  on the disk, but reside in the repository)
 * @author  Milos Kleint
 */
public class VirtualsDataLoader extends UniFileLoader {


    /** Create the loader.
    * Should <em>not</em> be used by subclasses.
    */
    public VirtualsDataLoader() {
        this("org.netbeans.modules.vcscore.util.virtuals.VirtualsDataObject"); // NOI18N
    }

    /** Create the loader from a subclass.
    * @param recognizedObject the class of data object
    *        recognized by the loader
    */
    public VirtualsDataLoader(String recognizedObject) {
        super(recognizedObject);
    }

    /** Create the loader from a subclass.
    * @param representationClass the class of data object
    *        recognized by the loader
    */
    public VirtualsDataLoader(Class representationClass) {
        super(representationClass);
    }

    protected MultiDataObject createMultiObject(FileObject fileObject) throws DataObjectExistsException, java.io.IOException {
        return new VirtualsDataObject(fileObject, this);
    }
    
    protected FileObject findPrimaryFile(FileObject fo) {
        if (fo.isFolder())
            return null;
        else
            return fo;
    }
    
    private SystemAction[] createDefaultActions() {
        return new SystemAction[] {
//            SystemAction.get(OpenAction.class),
            //SystemAction.get(CustomizeBeanAction.class),
//            SystemAction.get(RefreshRevisionsAction.class),
            SystemAction.get(FileSystemAction.class),
            null,
//            SystemAction.get(NewAction.class),
//            SystemAction.get(DeleteAction.class),
//            SystemAction.get(RenameAction.class),
            //null,
            //SystemAction.get(SaveAsTemplateAction.class),
//            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class)
        };
    }

    private static SystemAction[] standardActions;
    
    protected SystemAction[] defaultActions() {
        if (standardActions != null)
            return standardActions;
        synchronized (VirtualsDataLoader.class) {
            if (standardActions == null) {
                standardActions = createDefaultActions();
            }
        }
        return standardActions;
    }

}
