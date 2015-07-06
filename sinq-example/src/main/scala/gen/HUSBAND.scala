package gen

import io.sinq._

object HUSBAND extends Table[models.Husband]("t_husband"){
    def name = Column(this,classOf[String],"name")
    def age = Column(this,classOf[Int],"age")
    def id = Column(this,classOf[java.math.BigInteger],"id")
    def teacher = Column(this,classOf[java.math.BigInteger],"teacher")
}