name := "github-pr-robber"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "0.1.2"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.0"

libraryDependencies += "org.mongodb" %% "casbah" % "3.1.1"

resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("scalaz", "releases"),
  Resolver.bintrayRepo("megamsys", "scala"))

libraryDependencies += "io.megam" %% "newman" % "1.3.12"

libraryDependencies += "io.spray" %%  "spray-json" % "1.3.2"

libraryDependencies += "org.json4s" % "json4s-jackson_2.11" % "3.3.0"

libraryDependencies += "org.json4s" % "json4s-native_2.11" % "3.3.0"