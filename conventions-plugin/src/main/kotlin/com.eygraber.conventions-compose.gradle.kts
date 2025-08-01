import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

plugins {
  id("org.jetbrains.kotlin.plugin.compose")
}

val ext = gradleConventionsExtension
val composeDefaults = gradleConventionsDefaultsService.compose

ext.compose.includeSourceInformation = composeDefaults.includeSourceInformation
ext.compose.metricsDestination = composeDefaults.metricsDestination
ext.compose.reportsDestination = composeDefaults.reportsDestination
ext.compose.featureFlags = composeDefaults.featureFlags
ext.compose.stabilityConfigurationFiles = composeDefaults.stabilityConfigurationFiles
ext.compose.includeTraceMarkers = composeDefaults.includeTraceMarkers
ext.compose.targetKotlinPlatforms = composeDefaults.targetKotlinPlatforms

ext.awaitComposeConfigured {
  val gceExt = ext
  with(extensions.getByType<ComposeCompilerGradlePluginExtension>()) {
    gceExt.compose.includeSourceInformation?.let { includeSourceInformation = it }
    gceExt.compose.metricsDestination?.let { metricsDestination = it }
    gceExt.compose.reportsDestination?.let { reportsDestination = it }
    gceExt.compose.stabilityConfigurationFiles?.let { stabilityConfigurationFiles.addAll(it) }
    gceExt.compose.includeTraceMarkers?.let { includeTraceMarkers = it }
    gceExt.compose.targetKotlinPlatforms?.let { targetKotlinPlatforms = it }

    gceExt.compose.featureFlags?.let { featureFlags = it }
  }
}
