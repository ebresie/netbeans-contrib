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
    case class ErlModule(name:String) extends ErlSymbol
    case class ErlInclude(isLib:Boolean, path:String) extends ErlSymbol
    case class ErlExport(functions:List[ErlFunction]) extends ErlSymbol
    case class ErlRecord(name:String, fields:List[String]) extends ErlSymbol
    case class ErlMacro(name:String, params:List[String], var body:String) extends ErlSymbol

}
