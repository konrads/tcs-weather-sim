import sbt.Keys._

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.11.8"
)

lazy val core = (project in file("apps/core")).
  settings(commonSettings: _*)

lazy val cli = (project in file("apps/cli")).
  settings(commonSettings: _*).dependsOn(core)

lazy val tcp_server = (project in file("apps/tcp_server")).
  settings(commonSettings: _*).
  dependsOn(core)
