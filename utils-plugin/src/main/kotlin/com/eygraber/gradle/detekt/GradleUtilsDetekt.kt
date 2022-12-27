package com.eygraber.gradle.detekt

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider

class GradleUtilsDetekt {
  internal var ignoredAndroidFlavors: MutableList<String> = mutableListOf()
  internal var ignoredAndroidVariants: MutableList<String> = mutableListOf()

  internal var detektPluginDependencies: List<Any> = emptyList()

  fun ignoreAndroidFlavors(vararg flavors: String) {
    ignoredAndroidFlavors += flavors
  }

  fun ignoreAndroidVariants(vararg variants: String) {
    ignoredAndroidVariants += variants
  }

  fun plugins(
    vararg dependencies: Provider<MinimalExternalModuleDependency>
  ) {
    detektPluginDependencies = dependencies.toList()
  }

  fun pluginsCoordinates(
    vararg dependencies: String
  ) {
    detektPluginDependencies = dependencies.toList()
  }
}
