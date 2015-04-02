import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

// settings

lazy val buildSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.10.5",
  organization := "com.huawei.scalan",
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
    "-language:implicitConversions"))

lazy val lmsSettings = Seq(
  scalaVersion := "2.10.2",
  scalaOrganization := "org.scala-lang.virtualized",
  crossScalaVersions := Seq("2.10.2"))

lazy val crossBuildSettings = Seq(
  crossScalaVersions := Seq("2.10.5"))

lazy val formattingSettings = scalariformSettings ++ Seq(
  excludeFilter in ScalariformKeys.format <<= excludeFilter { _ || "*Impl.scala" },
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
//    .setPreference(RewriteArrowSymbols, true)
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentClassDeclaration, true))

lazy val commonSettings = buildSettings ++ crossBuildSettings ++ formattingSettings

// dependencies

def scalan(name: String) = "com.huawei.scalan" %% name % "0.2.7-SNAPSHOT"

val scalaLang = ExclusionRule(organization = "org.scala-lang")

//val scalacheck = "org.scalacheck" %% "scalacheck" % "1.12.2"
//val scalatest  = "org.scalatest"  %% "scalatest"  % "2.2.4"

// projects

lazy val benchmark = project
  .settings( libraryDependencies ++= Seq(scalan("lms-backend")) )
  .settings(
    libraryDependencies ++= Seq(
      "org.scodec" %% "scodec-bits" % "1.0.5"
    , "org.scodec" %% "scodec-core" % "1.7.0"
    ).++( if( scalaBinaryVersion.value startsWith "2.10" )
            Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full))
          else Nil
    )
  )
  .settings(commonSettings: _*)
  .settings(lmsSettings: _*)
  .settings(jmhSettings: _*)
  .settings(publishArtifact := false)

lazy val root = project.in(file("."))
  .enablePlugins(CrossPerProjectPlugin)
  .aggregate(benchmark)
  .settings(commonSettings: _*)
  .settings(publishArtifact := false)
