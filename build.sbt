ThisBuild / scalaVersion := "2.13.15"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
    .enablePlugins(PlayScala)
    .settings(
        name := """tarot-be""",
        libraryDependencies ++= Seq(
            guice,
            ws,
            "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
        )
    )
