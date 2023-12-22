import com.eygraber.conventions.gradleConventionsExtension
import org.jetbrains.compose.ComposeCompilerKotlinSupportPlugin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
  id("org.jetbrains.compose")
  id("com.eygraber.conventions-compose")
}

gradleConventionsExtension.awaitComposeConfigured {
  if(applyToAndroidAndJvmOnly) {
    // only apply to android/jvm targets if we're in a multiplatform project
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
      plugins.removeAll {
        it is ComposeCompilerKotlinSupportPlugin
      }

      class ComposeOnlyJvmPlugin : KotlinCompilerPluginSupportPlugin by ComposeCompilerKotlinSupportPlugin() {
        override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
          when(kotlinCompilation.target.platformType) {
            KotlinPlatformType.androidJvm, KotlinPlatformType.jvm -> true
            else -> false
          }
      }

      apply<ComposeOnlyJvmPlugin>()
    }
  }

  jetbrainsComposeCompilerOverride?.apply {
    compose.kotlinCompilerPlugin.set(
      "$group:$name${if(version == null) "" else ":$version"}"
    )
  }

  if(suppressKotlinVersionCompatForJetbrains != null) {
    compose.kotlinCompilerPluginArgs.add(
      "suppressKotlinVersionCompatibilityCheck=$suppressKotlinVersionCompatForJetbrains"
    )
  }
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
          withWasm()
        }
      }
    }
  }
}
