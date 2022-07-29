import com.eygraber.gradle.Env
import com.eygraber.gradle.detekt.configureDetekt
import com.eygraber.gradle.kotlin.configureKgp
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

buildscript {
  repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
  }

  dependencies {
    classpath(libs.buildscript.utils)
  }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.detekt)
  alias(libs.plugins.dokka)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.publish)
}

configureKgp(
  jdkVersion = libs.versions.jdk,
  explicitApiMode = ExplicitApiMode.Strict
)

configureDetekt(
  jdkVersion = libs.versions.jdk
)

dependencies {
  implementation(gradleApi())

  implementation(libs.buildscript.detekt)
  implementation(libs.buildscript.ejson)
  implementation(libs.buildscript.kotlin)

  implementation(libs.kotlinx.serialization.json)

  detektPlugins(libs.detekt)
  detektPlugins(libs.detektEygraber.formatting)
  detektPlugins(libs.detektEygraber.style)
}

if(Env.isCI) {
  @Suppress("UnstableApiUsage")
  mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)

    signAllPublications()
  }
}
