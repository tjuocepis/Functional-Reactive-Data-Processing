name := "edgaras_juocepis_cs474_final"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  // jackson
  "org.json4s" %% "json4s-native" % "3.5.0",
  "org.json4s" %% "json4s-jackson" % "3.5.0",
  // akka
  "com.typesafe.akka" % "akka-stream_2.11" % "2.4.2",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.11",
  "com.typesafe.akka" %% "akka-remote" % "2.4.11",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.11",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.11"
)