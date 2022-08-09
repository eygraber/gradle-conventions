import com.eygraber.gradle.detekt.configureDetekt
import com.eygraber.gradle.kotlin.configureKgp

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `kotlin-dsl`
  alias(libs.plugins.detekt)
  alias(libs.plugins.gradleUtils)
}

configureKgp(
  jdkVersion = libs.versions.jdk
)

configureDetekt(
  jdkVersion = libs.versions.jdk,
  configFile = project.file("${project.rootDir.parentFile}/detekt.yml")
)

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.buildscript.detekt)
  implementation(libs.buildscript.dokka)
  implementation(libs.buildscript.kotlin)
  implementation(libs.buildscript.publish)

  implementation(libs.buildscript.utils.kotlin)
  implementation(libs.buildscript.utils.detekt)

  detektPlugins(libs.detekt)
  detektPlugins(libs.detektEygraber.formatting)
  detektPlugins(libs.detektEygraber.style)
}
