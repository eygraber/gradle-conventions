import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension

val ext = gradleConventionsExtension
val composeDefaults = gradleConventionsDefaultsService.compose

ext.compose.androidComposeCompilerVersionOverride = composeDefaults.androidComposeCompilerVersionOverride
ext.compose.enableAndroidCompilerMetrics = composeDefaults.enableAndroidCompilerMetrics
ext.compose.applyToAndroidAndJvmOnly = composeDefaults.applyToAndroidAndJvmOnly
ext.compose.jetbrainsComposeCompilerOverride = composeDefaults.jetbrainsComposeCompilerOverride
ext.compose.useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion =
  composeDefaults.useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion
ext.compose.suppressKotlinVersionCompatForJetbrains = composeDefaults.suppressKotlinVersionCompatForJetbrains
