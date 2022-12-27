import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import com.android.build.gradle.BasePlugin as AndroidBasePlugin

val ext = gradleConventionsExtension
val composeDefaults = gradleConventionsDefaultsService.compose

ext.compose.applyToAndroidAndJvmOnly = composeDefaults.applyToAndroidAndJvmOnly
ext.compose.androidComposeCompilerVersionOverride = composeDefaults.androidComposeCompilerVersionOverride
ext.compose.androidComposeDependencyBomVersion = composeDefaults.androidComposeDependencyBomVersion
ext.compose.useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion =
  composeDefaults.useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion

ext.awaitComposeConfigured {
  plugins.withType<AndroidBasePlugin> {
    android {
      androidComposeCompilerVersionOverride?.let { versionOverride ->
        @Suppress("UnstableApiUsage")
        composeOptions.kotlinCompilerExtensionVersion = versionOverride
      }

      dependencies {
        androidComposeDependencyBomVersion?.let { bomVersion ->
          implementation(platform("androidx.compose:compose-bom:$bomVersion"))
        }
      }
    }
  }
}
