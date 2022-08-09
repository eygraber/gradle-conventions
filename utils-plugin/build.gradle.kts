plugins {
  `kotlin-dsl`
  id("com.eygraber.detekt")
  id("com.eygraber.kotlin")
  id("com.eygraber.publish")
}

gradlePlugin {
  plugins {
    create("gradleUtils") {
      id = "com.eygraber.gradle.utils"
      implementationClass = "com.eygraber.gradle.GradleUtilsPlugin"
    }
  }
}

dependencies {
  api(project(":utils-base"))
  api(project(":utils-detekt"))
  api(project(":utils-kotlin"))
}
