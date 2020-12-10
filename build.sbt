import Common.CommonSettings

name := "verification-tools"

inThisBuild(
  CommonSettings
)

lazy val `verification-tools` = project in file(".")

lazy val `vam-tool` = project
  .in(file("vam-tool"))
  .settings(
    libraryDependencies ++= Dependencies.`vam-tool`
  )