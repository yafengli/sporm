import sbt.Keys._
import sbt._
import scala.io.Source
import net.virtualvoid.sbt.graph.Plugin._

object SpormBuild extends Build {
  val reg = "(.+)=(.+)".r
  val $ = Source.fromFile(new File("version.properties")).getLines().map(it => {
    val m = reg.findFirstMatchIn(it).get
    (m.group(1), m.group(2))
  }).toMap

  lazy val sporm = Project(
    id = "sporm",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "sporm",
      organization := "org.koala",
      version := $("prod"),
      scalaVersion := $("scala"),
      resolvers ++= Seq(
        "Local Maven Repository" at "file:///d:/repository/",
        "OSC Nexus" at "http://maven.oschina.net/content/groups/public/"
      ),
      libraryDependencies ++= Seq(
        "org.hibernate.javax.persistence" % "hibernate-jpa-2.1-api" % $("jpa"),
        "org.hibernate" % "hibernate-entitymanager" % $("hibernate") % "provided",
        "com.alibaba" % "druid" % $("druid") % "provided",
        "ch.qos.logback" % "logback-classic" % $("logback"),
        "postgresql" % "postgresql" % "9.1-901.jdbc4" % "test",
        "org.specs2" %% "specs2" % "1.14" % "test",
        "junit" % "junit" % "4.10" % "test"
      )
    )
  ) settings(graphSettings: _*)
}

