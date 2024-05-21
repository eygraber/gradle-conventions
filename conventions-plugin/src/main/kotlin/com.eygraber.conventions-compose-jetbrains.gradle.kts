import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
  id("org.jetbrains.compose")
  id("com.eygraber.conventions-compose")
}

plugins.withId("org.jetbrains.kotlin.multiplatform") {
  with(extensions.getByType<KotlinMultiplatformExtension>()) {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
      common {
        group("cmp") {
          withIos()
          withJs()
          withJvm()
          withWasmJs()
        }
      }
    }
  }
}
