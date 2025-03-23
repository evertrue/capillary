resolvers ++= Seq(
  "Maven Central" at "https://repo1.maven.org/maven2/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",
  Resolver.sbtPluginRepo("releases")
)

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.11")

// Web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.2.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.1.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-js-engine" % "1.1.3")

// Packaging plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.6")
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.5")

// Code quality plugins
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")
