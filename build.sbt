ThisBuild / scalaVersion := "3.3.4"
ThisBuild / organization := "com.programmera"
ThisBuild / version      := "1.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "simpletimer",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.2" % Test,
    scalacOptions ++= Seq("-deprecation", "-feature", "-Wunused:all")
  )
