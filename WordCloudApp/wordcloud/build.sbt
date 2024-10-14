import Dependencies._

ThisBuild / scalaVersion := "3.3.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "WordCloud",
    scalaVersion := "3.3.3", // This line can be omitted as it is already defined at the top
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "20.0.0-R31",
      "org.apache.commons" % "commons-collections4" % "4.4",
      "org.openjfx" % "javafx-controls" % "20",
      "org.openjfx" % "javafx-fxml" % "20" // Check for the latest version
    )
  )
