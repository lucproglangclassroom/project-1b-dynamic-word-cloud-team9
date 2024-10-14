name := "topwords"

version := "0.3"

scalaVersion := "3.3.3"

scalacOptions += "@.scalacOptions.txt"

libraryDependencies ++= Seq(
  "org.scalatest"  %% "scalatest"  % "3.2.19"  % Test,
  "org.scalacheck" %% "scalacheck" % "1.18.0" % Test,
  "org.apache.commons" % "commons-collections4" % "4.4",
  "net.aichler" % "jupiter-interface" % "0.11.1" % Test,
  "net.jqwik" % "jqwik" % "1.9.0" % Test,
  "org.junit.jupiter" % "junit-jupiter-api" % "5.8.2" % Test,
  "org.scalafx" %% "scalafx" % "16.0.0-RC1" // Check for the latest version

)

enablePlugins(JavaAppPackaging)

fork in Test := true

testFrameworks += new TestFramework("org.scalatest.tools.Framework")

compile / javacOptions += "-Xlint:all"