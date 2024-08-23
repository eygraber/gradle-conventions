package com.eygraber.conventions.compose

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.SetProperty
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

class GradleConventionsCompose {
  var generateFunctionKeyMetaClasses: Boolean? = null
  var includeSourceInformation: Boolean? = null
  var metricsDestination: DirectoryProperty? = null
  var reportsDestination: DirectoryProperty? = null
  var stabilityConfigurationFile: RegularFileProperty? = null
  var includeTraceMarkers: Boolean? = null
  var targetKotlinPlatforms: SetProperty<KotlinPlatformType>? = null

  var featureFlags: Set<ComposeFeatureFlag>? = null
}
