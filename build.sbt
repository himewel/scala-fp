scalaVersion := "2.13.6"
ThisBuild / organization := "com.himewel"

lazy val hello = 
  (project in file("."))
  .settings(
    name := "FP Course"
  )

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.9.0",
  "org.typelevel" %% "cats-laws" % "2.9.0",
  "org.typelevel" %% "discipline-scalatest" % "2.2.0",
  "org.scalacheck" %% "scalacheck" % "1.17.0",
  "org.scalatest" %% "scalatest" % "3.2.15"
)