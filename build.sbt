name := """capillary"""

version := "1.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.12"

scalacOptions ++= Seq("-feature", "-deprecation")

libraryDependencies ++= Seq(
    "org.apache.kafka" % "kafka_2.11" % "0.10.2.2"
      exclude("javax.jms", "jms")
      exclude("com.sun.jdmk", "jmxtools")
      exclude("com.sun.jmx", "jmxri")
      exclude("org.slf4j", "slf4j-simple"),
  "nl.grons" %% "metrics-scala" % "3.5.5",
  "io.dropwizard.metrics" % "metrics-json" % "3.1.2",
  "io.dropwizard.metrics" % "metrics-jvm" % "3.1.2",
  "org.apache.curator" % "curator-framework" % "2.10.0",
  "org.apache.curator" % "curator-recipes" % "2.10.0",
  "org.coursera" % "metrics-datadog" % "1.1.2"
)

mappings in Universal += file("stats-to-datadog.py") -> "stats-to-datadog.py"

resolvers ++= Seq(
  "Maven Central" at "https://repo1.maven.org/maven2/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"
)
