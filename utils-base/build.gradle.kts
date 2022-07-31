@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

gradlePlugin {
  plugins {
    create("githubPackagesRepository") {
      id = "com.eygraber.github.packages.repository"
      implementationClass = "com.eygraber.gradle.settings.GitHubPackagesRepositoryPlugin"
    }
  }
}

dependencies {
  implementation(libs.buildscript.ejson)
  implementation(libs.kotlinx.serialization.json)
}
