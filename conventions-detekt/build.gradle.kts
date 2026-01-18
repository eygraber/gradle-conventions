plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt2")
  // id("com.eygraber.conventions-publish-maven-central")
  id("com.vanniktech.maven.publish")
}

dependencies {
  api(projects.conventionsBase)
  api(projects.conventionsKotlin)

  compileOnly(libs.buildscript.detekt)
  compileOnly(libs.buildscript.detekt2)
  compileOnly(libs.buildscript.kotlin)
}

mavenPublishing {
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()
}
