/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.loaders.clazz;

import java.io.IOException;

import com.netbeans.ide.filesystems.FileObject;
import com.netbeans.ide.loaders.FileEntry;
import com.netbeans.ide.loaders.MultiFileLoader;
import com.netbeans.ide.loaders.DataObject;
import com.netbeans.ide.loaders.DataObjectExistsException;
import com.netbeans.ide.loaders.MultiDataObject;
import com.netbeans.ide.util.actions.SystemAction;
import com.netbeans.ide.actions.*;

/** The DataLoader for ClassDataObjects.
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Jan Jancura, Ian Formanek, Dafe Simonek
*/
public final class ClassDataLoader extends MultiFileLoader {

  /** Extension constants */
  private static final String SER_EXT = "ser";
  private static final String CLASS_EXT = "class";

  private static final char INNER_CLASS_DIVIDER = '$';

  /** Creates a new ClassDataLoader */
  public ClassDataLoader () {
    super (ClassDataObject.class);
    initialize();
  }

  private void initialize () {
    setActions (new SystemAction [] {
      SystemAction.get(CustomizeBeanAction.class),
      null,
      SystemAction.get(ExecuteAction.class),
      null,
      SystemAction.get(CutAction.class),
      SystemAction.get(CopyAction.class),
      SystemAction.get(PasteAction.class),
      null,
      SystemAction.get(DeleteAction.class),
      null,
      SystemAction.get(SaveAsTemplateAction.class),
      null,
      SystemAction.get(PropertiesAction.class),
    });
  }

  /** For a given file finds a primary file.
  * @param fo the file to find primary file for
  *
  * @return the primary file for the file or null if the file is not
  *   recognized by this loader
  */
  protected FileObject findPrimaryFile (FileObject fo) {
    if (SER_EXT.equals(fo.getExt())) {
      // serialized file, return itself
      return fo;
    }
    if (CLASS_EXT.equals(fo.getExt())) {
      // class file
      return findPrimaryForClass(fo);
    }
    // not recognized
    return null;
  }

  /** Creates the right data object for given primary file.
  * It is guaranteed that the provided file is realy primary file
  * returned from the method findPrimaryFile.
  *
  * @param primaryFile the primary file
  * @return the data object for this file
  * @exception DataObjectExistsException if the primary file already has data object
  */
  protected MultiDataObject createMultiObject (FileObject primaryFile)
  throws DataObjectExistsException, IOException {
    if (SER_EXT.equals(primaryFile.getExt())) {
      // serialized file, return bean data object
        return new SerDataObject (primaryFile, this);
    }
    if (CLASS_EXT.equals(primaryFile.getExt())) {
      // class file, return class data object
      return new ClassDataObject (primaryFile, this);
    }
    // otherwise
    return null;
  }

  /** Creates the right primary entry for given primary file.
  *
  * @param primaryFile primary file recognized by this loader
  * @return primary entry for that file
  */
  protected MultiDataObject.Entry createPrimaryEntry (FileObject primaryFile) {
    return new FileEntry(primaryFile);
  }

  /** Creates right secondary entry for given file. The file is said to
  * belong to an object created by this loader.
  *
  * @param secondaryFile secondary file for which we want to create entry
  * @return the entry
  */
  protected MultiDataObject.Entry createSecondaryEntry (FileObject secondaryFile) {
    return new FileEntry.Numb(secondaryFile);
  }

  /** finds file with the same name and specified extension in the same folder as param javaFile */
  static protected FileObject findFile(FileObject javaFile, String ext) {
    if (javaFile == null) return null;
    return javaFile.getParent().getFileObject (javaFile.getName(), ext);
  }

  /** Utility method, finds primary class file for given class file.
  * (input class file can be innerclass class file) */
  private FileObject findPrimaryForClass (final FileObject fo) {
    final String name = fo.getName();
    final int index = name.indexOf(INNER_CLASS_DIVIDER);
    if (index > 0) {
      // could be innerclass class file - try to find outer class file
      FileObject outer =
        fo.getParent().getFileObject(name.substring(0, index), CLASS_EXT);
      if (outer != null) return outer;
    }
    return fo;
  }

}

/*
 * Log
 *  3    Gandalf   1.2         1/19/99  David Simonek   
 *  2    Gandalf   1.1         1/6/99   Ian Formanek    Reflecting change in 
 *       datasystem package
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 * Beta Change History:
 *  0    Tuborg    0.20        --/--/98 Jan Formanek    SWITCHED TO NODES
 *  0    Tuborg    0.23        --/--/98 Jan Jancura     Bugxix
 *  0    Tuborg    0.24        --/--/98 Jan Formanek    reflecting changes in DataSystem
 *  0    Tuborg    0.25        --/--/98 Jan Jancura     Error data object removed.
 */
