name := "tcs-weather-sim"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.12.0",
  "org.typelevel"          %% "cats"        % "0.6.1",
  "com.github.scopt"       %% "scopt"       % "3.5.0",
  "com.typesafe"           %  "config"      % "1.3.0",
  "org.scalatest"          %% "scalatest"   % "3.0.0"   % "test"
)

fork in Test := true
