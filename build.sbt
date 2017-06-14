import io.gatling.sbt.GatlingPlugin

name := "AlteryxClient"

version := "1.0"

scalaVersion := "2.11.11"


enablePlugins(GatlingPlugin)


libraryDependencies ++= Seq(
  "org.asynchttpclient" % "async-http-client" % "2.0.32" % "test",
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2" % "test",
  "io.gatling" % "gatling-test-framework" % "2.2.5",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.57",
  "org.apache.commons" % "commons-lang3" % "3.5",
  "commons-codec" % "commons-codec" % "1.10",
  "com.google.api-client" % "google-api-client" % "1.22.0"
)


