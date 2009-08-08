/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.scala.editor.element

import _root_.java.io.{File, IOException}
import javax.lang.model.element.Element
import javax.swing.text.BadLocationException
import org.netbeans.api.lexer.TokenHierarchy
import org.netbeans.editor.BaseDocument
import org.netbeans.modules.csl.api.{ElementHandle, ElementKind, Modifier, OffsetRange}
import org.netbeans.modules.csl.spi.{GsfUtilities, ParserResult}
import org.openide.filesystems.{FileObject, FileUtil}
import org.openide.util.Exceptions

import _root_.scala.tools.nsc.io.{AbstractFile, PlainFile, VirtualFile}
import _root_.scala.tools.nsc.util.BatchSourceFile
import _root_.scala.tools.nsc.symtab.{Flags}

import org.netbeans.api.language.util.ast.{AstElementHandle}
import org.netbeans.modules.scala.editor.{JavaSourceUtil, ScalaGlobal, ScalaParserResult, ScalaSourceUtil, ScalaMimeResolver}


/**
 *
 * Wrapper scalac's symbol to AstElementHandle, these symbols are created by
 * scala compiler, that may be from class file instead of source files by
 * invoking ScalaGlobal#compilexxx methods.
 *
 * @author Caoyuan Deng
 */
trait ScalaElements {self: ScalaGlobal =>

  object ScalaElement {
    def apply(symbol: Symbol, info: ParserResult) = {
      new ScalaElement(symbol, info)
    }
  }

  class ScalaElement(val symbol: Symbol, val info: ParserResult) extends AstElementHandle {
    import ScalaElement._
  
    private var kind: ElementKind = _
    private var modifiers: Option[_root_.java.util.Set[Modifier]] = None
    private var inherited: Boolean = _
    private var smart: Boolean = _
    private var fo: Option[FileObject] = None
    private var path: String = _
    private var doc: Option[BaseDocument] = None
    private var offset: Int = _
    private var javaElement: Option[Element] = None
    private var loaded: Boolean = _


    def this(kind: ElementKind) = {
      this(null, null)
      this.kind = kind
    }

    def tpe: String = {
      ""
    }

    override def getFileObject: FileObject = {
      fo match {
        case Some(x) => return x
        case None =>
          fo = ScalaSourceUtil.getFileObject(info, symbol) // try to get
          fo match {
            case Some(x) => path = x.getPath; x
            case None => null
          }
      }
    }

    override def getIn: String = {
      symbol.owner.nameString
    }

    override def getKind: ElementKind = {
      ScalaUtil.getKind(symbol)
    }

    override def getMimeType: String = {
      ScalaMimeResolver.MIME_TYPE
    }

    override def getModifiers: _root_.java.util.Set[Modifier] = {
      if (!modifiers.isDefined) {
        modifiers = Some(ScalaUtil.getModifiers(symbol))
      }
      modifiers.get
    }

    override def getName: String = symbol.nameString

    override def qualifiedName: String = symbol.fullNameString

    override def signatureEquals(handle: ElementHandle): Boolean = {
      false
    }

    def getDocComment: String = {
      if (!isLoaded) load

      getDoc foreach {srcDoc =>
        if (isJava) {
          javaElement foreach {x =>
            try {
              val docComment: String = JavaSourceUtil.getDocComment(JavaSourceUtil.getCompilationInfoForScalaFile(info.getSnapshot.getSource.getFileObject), x)
              if (docComment.length > 0) {
                return new StringBuilder(docComment.length + 5).append("/**").append(docComment).append("*/").toString
              }
            } catch {case ex: IOException => Exceptions.printStackTrace(ex)}
          }
        } else {
          return ScalaSourceUtil.getDocComment(srcDoc, getOffset)
        }
      }

      ""
    }

    def getOffset: Int = {
      if (!isLoaded) load

      if (isJava) {
        javaElement foreach {x =>
          try {
            return JavaSourceUtil.getOffset(JavaSourceUtil.getCompilationInfoForScalaFile(info.getSnapshot.getSource.getFileObject), x)
          } catch {case ex: IOException => Exceptions.printStackTrace(ex)}
        }
      } else {
        val pos = symbol.pos
        if (pos.isDefined) {
          offset = pos.startOrPoint
        }
      }

      offset
    }

    override def getOffsetRange(result: ParserResult): OffsetRange = {
      throw new UnsupportedOperationException("Not supported yet.")
    }

    def getDoc: Option[BaseDocument] = {
      val srcFo = getFileObject
      if (srcFo != null) {
        doc match {
          case None => GsfUtilities.getDocument(srcFo, true) match {
              case null =>
              case x => doc = Some(x)
            }
          case _ =>
        }
        doc
      } else None
    }

    private def isLoaded: Boolean = {
      if (loaded) return true

      if (isJava) {
        javaElement.isDefined
      } else {
        symbol.pos.isDefined
      }
    }

    private def load: Unit = {
      if (isLoaded) return

      if (isJava) {
        javaElement = JavaSourceUtil.getJavaElement(JavaSourceUtil.getCompilationInfoForScalaFile(info.getSnapshot.getSource.getFileObject), symbol)
      } else {
        getDoc foreach {srcDoc =>
          assert(path != null)
          try {
            val text = srcDoc.getChars(0, srcDoc.getLength)
            val f = new File(path)
            val af = if (f != null) new PlainFile(f) else new VirtualFile("<current>", "")
            val srcFile = new BatchSourceFile(af, text)

            val th = TokenHierarchy.get(srcDoc)
            if (th == null) {
              return
            }

            /**
             * @Note by compiling the related source file, this symbol will
             * be automatically loaded next time, but the position/sourcefile
             * info is not updated for this symbol yet. But we can find the
             * position via the AST Tree, or use a tree visitor to update
             * all symbols Position
             */
            val root = compileSourceForPresentation(srcFile, th)
            root.findDfnMatched(symbol) match {
              case Some(x) => offset = x.idOffset(th)
              case None =>
            }
          } catch {case ex: BadLocationException => Exceptions.printStackTrace(ex)}
                
        }
      }

      loaded = true
    }

    def isJava: Boolean = {
      symbol hasFlag Flags.JAVA
    }

    def isDeprecated: Boolean = {
      try {
        symbol.isDeprecated
      } catch {case _ => false}
    }

    def setInherited(inherited: Boolean): Unit = {
      this.inherited = inherited
    }

    def isInherited: Boolean = {
      inherited
    }

    def isEmphasize: Boolean = {
      !isInherited
    }

    def setSmart(smart: Boolean): Unit = {
      this.smart = smart
    }

    override def toString = {
      symbol.toString
    }

    def paramNames: List[List[Symbol]] = {
      assert(symbol.isMethod)

      /** @todo not work yet */
      val argNamesMap = methodArgumentNames
      if (argNamesMap != null) {
        argNamesMap.get(symbol) match {
          case Some(x) => x
          case None => Nil
        }
      } else Nil
    }
  }
}