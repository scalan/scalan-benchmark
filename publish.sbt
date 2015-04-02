publishTo in ThisBuild := {
  val nexus = "http://10.122.85.37:9081/nexus"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "/content/repositories/snapshots")
  else
    Some("releases"  at nexus + "/content/repositories/releases")
}

credentials in ThisBuild += Credentials(Path.userHome / ".ivy2" / ".credentials")
