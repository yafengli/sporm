package io.sinq.expression

import io.sinq.rs.Column

case class NotEq[T](val col: Column, val paramValue: T) extends Tuple1Condition[T]{
  override def toField(): String = s"${col.identifier()} != ?"
}
