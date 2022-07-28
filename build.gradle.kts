import com.eygraber.gradle.Env
import com.eygraber.gradle.kotlin.setupKgp
import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt
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

tasks.withType<JavaCompile> {
  sourceCompatibility = libs.versions.jdk.get()
  targetCompatibility = libs.versions.jdk.get()
}

setupKgp(
  jdkVersion = libs.versions.jdk.get(),
  explicitApiMode = ExplicitApiMode.Strict
)

detekt {
  source.from("build.gradle.kts")

  autoCorrect = true
  parallel = true

  buildUponDefaultConfig = true

  config = project.files("${project.rootDir}/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
  // Target version of the generated JVM bytecode. It is used for type resolution.
  jvmTarget = libs.versions.jdk.get()
}

dependencies {
  implementation(gradleApi())

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
