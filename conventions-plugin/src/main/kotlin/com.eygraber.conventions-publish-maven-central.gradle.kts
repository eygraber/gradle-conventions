import com.vanniktech.maven.publish.SonatypeHost

plugins {
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
  signAllPublications()
}
