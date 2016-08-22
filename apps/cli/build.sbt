enablePlugins(JavaAppPackaging)

name := "weather-sim-cli"

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.5.0"
)

test in assembly := {}
