package org.koala.sporm.jpa

import javax.persistence.Tuple
import javax.persistence.criteria.Selection
import javax.persistence.{EntityManager, Query}

class SpormFacade extends JPA with NQBuilder {

  import javax.persistence.criteria.{CriteriaQuery, Predicate}

  def insert[T](entity: T) {
    withTransaction {
      _.persist(entity)
    }
  }

  def update[T](entity: T) {
    withTransaction {
      _.merge(entity)
    }
  }

  def delete[T](entity: T) {
    withTransaction {
      _.remove(entity)
    }
  }

  def get[T](resultType: Class[T], id: Any): Option[T] = {
    withEntityManager {
      _.find(resultType, id)
    }
  }

  def fetch[T, X](fromType: Class[T], resultType: Class[X])(call: (CriteriaQuery[T], CQExpression[T]) => Seq[Predicate]): Option[List[T]] = {
    withEntityManager {
      em =>
        CQBuilder(em, fromType, resultType).fetch(call)
    }
  }

  def fetch[T, X](fromType: Class[T], resultType: Class[X], limit: Int, offset: Int)(call: (CriteriaQuery[T], CQExpression[T]) => Seq[Predicate]): Option[List[T]] = {
    withEntityManager {
      em =>
        CQBuilder(em, fromType, resultType).fetch(limit, offset)(call)
    }
  }

  def single[T](fromType: Class[T])(call: (CriteriaQuery[T], CQExpression[T]) => Seq[Predicate]): Option[T] = {
    withEntityManager {
      em =>
        CQBuilder(em, fromType, fromType).single(call)
    }
  }

  def count[T](fromType: Class[T])(call: (CriteriaQuery[Tuple], CQExpression[T]) => Seq[Predicate]): Option[Long] = {
    withEntityManager {
      em =>
        CQBuilder(em, fromType, classOf[java.lang.Long]).count(call)
    }
  }

  def multi[T](fromType: Class[T])(selectsCall: (CriteriaQuery[Tuple], CQExpression[T]) => Seq[Selection[_]])(call: (CriteriaQuery[Tuple], CQExpression[T]) => Seq[Predicate]): Option[List[Tuple]] = {
    withEntityManager {
      em =>
        CQBuilder(em, fromType, classOf[Tuple]).multi(selectsCall, call)
    }
  }

  def fetch[T](qs: String, ops: Array[Any], limit: Int, offset: Int)(f: (EntityManager) => Query): Option[List[T]] = {
    withEntityManager {
      em => _fetch(f(em), ops, limit, offset)
    }
  }

  def fetch[T](qs: String, ops: Array[Any])(f: (EntityManager) => Query): Option[List[T]] = {
    fetch(qs, ops, -1, -1)(f)
  }

  def single[T](qs: String, ops: Array[Any])(f: (EntityManager) => Query): Option[T] = {
    withEntityManager {
      em => _single(f(em), ops)
    }
  }

  def count(name: String, ops: Array[Any])(f: (EntityManager) => Query): Option[Long] = {
    withEntityManager {
      em => _count(f(em), ops)
    }
  }

  def count(name: String)(f: (EntityManager) => Query): Option[Long] = {
    count(name)(f)
  }

  def multi(name: String, ops: Array[Any])(f: (EntityManager) => Query): Option[List[_]] = {
    withEntityManager {
      em => _multi(f(em), ops)
    }
  }

  def multi[T](name: String)(f: (EntityManager) => Query): Option[List[_]] = {
    multi(name, Array[Any]())(f)
  }

  def sql[T](sql: String, params: Seq[Any]): Option[List[T]] = {
    import scala.collection.JavaConversions._
    withEntityManager {
      em =>
        val query = em.createNativeQuery(sql)
        if (params != null && params.size > 0) {
          for (i <- 1 to params.size) {
            query.setParameter(i, params(i))
          }
        }
        query.getResultList.toList.asInstanceOf[List[T]]
    }
  }
}

object SpormFacade {
  private val facade_t = new ThreadLocal[SpormFacade]

  def apply(persistenceUnitName: String): SpormFacade = {
    JPA.initPersistenceName(persistenceUnitName)
    if (facade_t.get() == null) facade_t.set(new SpormFacade())
    facade_t.get()
  }
}