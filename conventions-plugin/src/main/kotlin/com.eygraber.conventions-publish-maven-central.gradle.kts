import com.eygraber.conventions.gradleConventionsExtension

plugins {
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
}

val ext = gradleConventionsExtension

ext.awaitPublishConfigured {
  mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
  }
}
