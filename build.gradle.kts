import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

kotlin {
  explicitApi()

  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
    vendor.set(JvmVendorSpec.AZUL)
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    allWarningsAsErrors = true
    jvmTarget = libs.versions.jdk.get()
  }
}

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

if(System.getenv("CI") == "true") {
  @Suppress("UnstableApiUsage")
  mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)

    signAllPublications()
  }
}
