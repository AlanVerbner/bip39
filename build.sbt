organization := "com.github.alanverbner"

name := "bip39"

version := "0.2"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.consensusresearch" %% "scrypto" % "1.2.0-RC3",
  "org.scodec" %% "scodec-bits" % "1.1.5",

  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

useGpg := true

pomIncludeRepository := { _ => false }

licenses := Seq("MIT" -> url("https://github.com/AlanVerbner/bip39/blob/master/LICENSE"))

homepage := Some(url("https://github.com/AlanVerbner/bip39"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/AlanVerbner/bip39"),
    "scm:git@github.com:AlanVerbner/bip39.git"
  )
)

developers := List(
  Developer(
    id    = "lalanv",
    name  = "Alan Verbner",
    email = "alverbner@gmail.com",
    url   = url("https://onename.com/lalanv")
  )
)

publishMavenStyle := true
publishArtifact in Test := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

