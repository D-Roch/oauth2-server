scalacOptions += "-deprecation"

resolvers += "Jawsy.fi M2 releases" at "http://oss.jawsy.fi/maven2/releases"

resolvers += Resolver.url("sbt-plugin-releases",
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(
    Resolver.ivyStylePatterns)

resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.typesafe.startscript" % "xsbt-start-script-plugin" % "0.5.2")

addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.4.0")

addSbtPlugin("fi.jawsy.sbtplugins" %% "sbt-jrebel-plugin" % "0.9.0")

addSbtPlugin("org.ensime" % "ensime-sbt-cmd" % "0.0.10")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.1.2")

libraryDependencies <+= sbtVersion(v => "com.mojolly.scalate" %% "xsbt-scalate-generator" % (v + "-0.1.7"))

externalResolvers <<= resolvers map { Resolver.withDefaultResolvers(_, scalaTools = false) }

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.11.1"))

libraryDependencies ++= Seq(
        "org.mozilla" % "rhino" % "1.7R4",
        "com.google.javascript" % "closure-compiler" % "r2079",
        "net.liftweb" %% "lift-json" % "2.4")