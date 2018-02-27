resolvers += Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("uk.gov.hmrc" % "sbt-git-stamp" % "5.3.0")

// addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.3")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.2.1")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.8.0")

addSbtPlugin("com.github.saurfang" % "sbt-spark-submit" % "0.0.4")
