name := "test-akka-stuff"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  // the following two dependencies were added to satisfy "multiple dependencies" warning
  "org.scala-lang" % "scala-reflect" % "2.11.8",
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.2",
  "com.typesafe.akka" %% "akka-actor" % "2.4.2"
)

scalacOptions ++= Seq("-unchecked", "-deprecation")