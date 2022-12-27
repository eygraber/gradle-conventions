package com.eygraber.conventions.detekt

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider

class GradleConventionsDetekt {
  internal var ignoredAndroidFlavors: MutableList<String> = mutableListOf()
  internal var ignoredAndroidVariants: MutableList<String> = mutableListOf()

  internal var detektPluginDependencies: MutableList<Any> = mutableListOf()

  fun ignoreAndroidFlavors(vararg flavors: String) {
    ignoredAndroidFlavors += flavors
  }

  fun ignoreAndroidVariants(vararg variants: String) {
    ignoredAndroidVariants += variants
  }

  fun plugins(
    vararg dependencies: Provider<MinimalExternalModuleDependency>
  ) {
    detektPluginDependencies.addAll(dependencies)
  }

  fun pluginsCoordinates(
    vararg dependencies: String
  ) {
    detektPluginDependencies.addAll(dependencies)
  }
}
