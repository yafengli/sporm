package org.koala.sporm.expression

import scala.collection.mutable

trait Condition {

  import org.koala.sporm.expression.Condition._

  val linkCache = new StringBuffer()

  val paramsMap = mutable.Map[String, Any]()

  def and(condition: Condition): Condition = {
    //statement
    link(condition, AND)
    //parameter
    this.paramsMap ++= condition.paramsMap
    this
  }

  def or(condition: Condition): Condition = {
    //statement
    link(condition, OR)
    //parameter
    this.paramsMap ++= condition.paramsMap
    this
  }

  protected def key(column: String): String = {
    val pos = paramsMap.keys.filter(_.indexOf(column) >= 0).size
    s"${column}_${pos + 1}"
  }

  private def link(condition: Condition, operation: String): Unit = {
    this.linkCache.append(operation)
    condition.linkCache match {
      case lc if lc.indexOf(operation) >= 0 =>
        this.linkCache.append(START_BRACKET)
        this.linkCache.append(lc.toString)
        this.linkCache.append(END_BRACKET)
      case lc => this.linkCache.append(lc)
    }
  }
}

object Condition {
  val AND: String = " and "
  val OR: String = " or "
  val START_BRACKET: String = "("
  val END_BRACKET: String = ")"

  def apply(): Condition = {
    new Condition {}
  }
}

