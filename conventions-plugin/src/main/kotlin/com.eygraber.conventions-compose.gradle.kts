import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

plugins {
  id("org.jetbrains.kotlin.plugin.compose")
}

val ext = gradleConventionsExtension
val composeDefaults = gradleConventionsDefaultsService.compose

ext.compose.generateFunctionKeyMetaClasses = composeDefaults.generateFunctionKeyMetaClasses
ext.compose.includeSourceInformation = composeDefaults.includeSourceInformation
ext.compose.metricsDestination = composeDefaults.metricsDestination
ext.compose.reportsDestination = composeDefaults.reportsDestination
ext.compose.enableIntrinsicRemember = composeDefaults.enableIntrinsicRemember
ext.compose.enableNonSkippingGroupOptimization = composeDefaults.enableNonSkippingGroupOptimization
ext.compose.enableStrongSkippingMode = composeDefaults.enableStrongSkippingMode
ext.compose.stabilityConfigurationFile = composeDefaults.stabilityConfigurationFile
ext.compose.includeTraceMarkers = composeDefaults.includeTraceMarkers
ext.compose.targetKotlinPlatforms = composeDefaults.targetKotlinPlatforms

ext.awaitComposeConfigured {
  with(extensions.getByType<ComposeCompilerGradlePluginExtension>()) {
    ext.compose.generateFunctionKeyMetaClasses?.let { generateFunctionKeyMetaClasses = it }
    ext.compose.includeSourceInformation?.let { includeSourceInformation = it }
    ext.compose.metricsDestination?.let { metricsDestination = it }
    ext.compose.reportsDestination?.let { reportsDestination = it }
    ext.compose.enableIntrinsicRemember?.let { enableIntrinsicRemember = it }
    ext.compose.enableNonSkippingGroupOptimization?.let { enableNonSkippingGroupOptimization = it }
    ext.compose.enableStrongSkippingMode?.let { enableStrongSkippingMode = it }
    ext.compose.stabilityConfigurationFile?.let { stabilityConfigurationFile = it }
    ext.compose.includeTraceMarkers?.let { includeTraceMarkers = it }
    ext.compose.targetKotlinPlatforms?.let { targetKotlinPlatforms = it }
  }
}
