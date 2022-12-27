import com.android.build.gradle.BasePlugin
import com.eygraber.gradle.gradleUtilsExtension
import org.jetbrains.compose.ComposeCompilerKotlinSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
  id("org.jetbrains.compose")
  id("com.eygraber.gradle-compose")
}

gradleUtilsExtension.awaitComposeConfigured {
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

  if(useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion) {
    androidComposeCompilerVersionOverride?.let { compilerVersion ->
      compose.kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:$compilerVersion")
    }
  }

  plugins.withType<BasePlugin> {
    android {
      dependencies {
        // jetbrains compose plugin rewrites compose dependencies for android to point to androidx
        // if we want to use the compose BOM we need to rewrite the rewritten dependencies to not include a version
        if(androidComposeDependencyBomVersion != null) {
          components {
            all {
              val isCompiler = id.group.endsWith("compiler")
              val isCompose = id.group.startsWith("androidx.compose")
              val isBom = id.name == "compose-bom"

              val override = isCompose && !isCompiler && !isBom
              if(override) {
                // copied from Jetbrains Compose RedirectAndroidVariants - https://shorturl.at/dioY9
                listOf(
                  "debugApiElements-published",
                  "debugRuntimeElements-published",
                  "releaseApiElements-published",
                  "releaseRuntimeElements-published"
                ).forEach { variantNameToAlter ->
                  withVariant(variantNameToAlter) {
                    withDependencies {
                      removeAll { true } // remove androidx artifact with version
                      add("${id.group}:${id.name}") // add androidx artifact without version
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
