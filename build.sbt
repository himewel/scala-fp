scalaVersion := "3.2.0"
ThisBuild / organization := "com.himewel"

lazy val hello = 
  (project in file("."))
  .settings(
    name := "FP Course"
  )

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.9.0",
  "org.typelevel" %% "cats-laws" % "2.9.0",
  "org.typelevel" %% "discipline-scalatest" % "2.2.0",
  "org.scalacheck" %% "scalacheck" % "1.17.0",
  "org.scalatest" %% "scalatest" % "3.2.15",
)