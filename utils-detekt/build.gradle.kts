

// buildscript {
//   repositories {
//     mavenCentral()
//     maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
//   }
//
//   dependencies {
//     classpath(libs.buildscript.utils)
//   }
// }

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

// configureKgp(
//   jdkVersion = libs.versions.jdk,
//   explicitApiMode = ExplicitApiMode.Strict
// )

dependencies {
  api(project(":utils-base"))
  api(project(":utils-kotlin"))

  compileOnly(libs.buildscript.detekt)
  compileOnly(libs.buildscript.kotlin)
}
