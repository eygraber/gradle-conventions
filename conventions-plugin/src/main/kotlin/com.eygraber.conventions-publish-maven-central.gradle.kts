import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension

plugins {
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
}

val ext = gradleConventionsExtension
val publishDefaults = gradleConventionsDefaultsService.publish
ext.publish.host = publishDefaults.host

ext.awaitPublishConfigured {
  mavenPublishing {
    publishToMavenCentral(host, automaticRelease = true)
    signAllPublications()
  }
}
