import com.eygraber.conventions.gradleConventionsExtension
import org.jetbrains.compose.ComposeCompilerKotlinSupportPlugin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion
import org.jetbrains.kotlin.tooling.core.toKotlinVersion

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

      class ComposeOnlyJvmPlugin @Inject constructor(
        private val buildEventsListenerRegistry: BuildEventsListenerRegistry
      ) : KotlinCompilerPluginSupportPlugin by ComposeCompilerKotlinSupportPlugin(buildEventsListenerRegistry) {
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
}

plugins.withId("org.jetbrains.kotlin.multiplatform") {
  with(extensions.getByType<KotlinMultiplatformExtension>()) {
    val isAtLeast192 = project.kotlinToolingVersion.toKotlinVersion().isAtLeast(major = 1, minor = 9, patch = 20)
    if(isAtLeast192) {
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
    else {
      @Suppress("DEPRECATION")
      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      targetHierarchy.default {
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
}
