name := "weather-sim-tcp-server"

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= {
  val akkaVsn = "2.4.8"
  Seq(
    "com.typesafe.akka" %% "akka-actor"  % akkaVsn,
    "com.typesafe.akka" %% "akka-stream" % akkaVsn,
    "com.github.scopt"  %% "scopt"       % "3.5.0"
  )
}

test in assembly := {}