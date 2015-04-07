package models

import javax.persistence._
import scala.beans.BeanProperty
import java.util

@Entity
@Table(name = "t_user")
case class User(@BeanProperty var name: String, @BeanProperty var age: Int) {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Long = _

  @OneToOne(cascade = Array(CascadeType.REMOVE), mappedBy = "user")
  @BeanProperty
  var address: Address = _


  @ManyToMany(fetch = FetchType.EAGER, mappedBy = "users")
  var teachers: util.Set[Teacher] = new util.HashSet[Teacher]()

  def this() = this(null, -1)

  def this(name: String, age: Int, address: Address) = {
    this(name, age)
    this.address = address
  }
}
