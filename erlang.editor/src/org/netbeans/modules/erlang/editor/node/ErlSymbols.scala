/*
 * AstTypes.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.erlang.editor.node

import org.netbeans.modules.erlang.editor.ast.{AstRef,AstSym}

object ErlSymbols {
    case class ErlSymbol extends AstSym
    case class ErlFunction(var in:Option[String], var name:String, var arity:Int) extends ErlSymbol
}
