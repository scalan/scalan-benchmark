version := "0.2.0-SNAPSHOT"

scalaVersion := "2.10.2"

scalaOrganization := "org.scala-lang.virtualized"

organization := "com.huawei.scalan"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-adapted-args",
  "-Ywarn-inaccessible",
  "-Ywarn-nullary-override",
  "-language:existentials",
  "-language:implicitConversions")

publishArtifact := false

libraryDependencies ++= Seq(
  "com.huawei.scalan" %% "lms-backend" % "0.2.7-SNAPSHOT",
  "org.scodec" %% "scodec-bits" % "1.0.5",
  "org.scodec" %% "scodec-core" % "1.7.0")

libraryDependencies ++= {
  if (scalaBinaryVersion.value startsWith "2.10")
    Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full))
  else Nil
}
