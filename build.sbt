name := """hackergarten-pizza"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "3.0.0",
  "net.ceedubs" %% "ficus" % "1.1.2",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  jdbc,
  "com.typesafe.play" %% "play-slick" % "1.0.0",
  "com.h2database" % "h2" % "1.4.187",
  "org.scala-lang.modules" %% "scala-pickling" % "0.10.1",
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
