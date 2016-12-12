name := """Collectioneer"""

version := "0.1.0"

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalafx" %% "scalafx" % "8.0.102-R11",
  "org.json4s" %% "json4s-native" % "3.5.0",
  "com.typesafe.slick" %% "slick" % "3.2.0-M2",
  "com.h2database" % "h2" % "1.4.193",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" %  "logback-classic" % "1.1.7",
  "com.lihaoyi" % "ammonite" % "0.8.1" % "test" cross CrossVersion.full
)

initialCommands in (Test, console) := """ammonite.Main().run()"""

fork in run := true
