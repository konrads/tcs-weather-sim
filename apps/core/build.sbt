name := "weather-sim-core"

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.12.0",
  "org.typelevel"          %% "cats"        % "0.6.1",
  "com.typesafe"           %  "config"      % "1.3.0",
  "org.scalatest"          %% "scalatest"   % "3.0.0"   % "test"
)

fork in Test := true