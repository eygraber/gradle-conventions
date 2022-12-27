package com.eygraber.gradle.android

import com.eygraber.gradle.kotlin.KotlinOptIn
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider

data class ProductFlavor(
  val name: String,
  val enabled: Boolean = true
)

class GradleUtilsAndroid {
  var compileSdk: Int = 0
  var targetSdk: Int = 0
  var minSdk: Int = 0

  var publishEverything: Boolean = true

  internal var coreLibraryDesugaringDependency: Any? = null

  internal var flavors: MutableList<Pair<String, List<ProductFlavor>>> = mutableListOf()

  internal var optInsToDependencyPredicate: MutableList<Pair<List<KotlinOptIn>, Dependency.() -> Boolean>> =
    mutableListOf()

  fun sdkVersions(
    compileSdk: Provider<String>,
    minSdk: Provider<String>
  ) {
    this.compileSdk = compileSdk.get().toInt()
    this.minSdk = minSdk.get().toInt()
  }

  fun sdkVersions(
    compileSdk: Provider<String>,
    targetSdk: Provider<String>,
    minSdk: Provider<String>
  ) {
    this.compileSdk = compileSdk.get().toInt()
    this.targetSdk = targetSdk.get().toInt()
    this.minSdk = minSdk.get().toInt()
  }

  fun useCoreLibraryDesugaring(
    dependency: Provider<MinimalExternalModuleDependency>
  ) {
    coreLibraryDesugaringDependency = dependency
  }

  fun useCoreLibraryDesugaring(
    dependency: String
  ) {
    coreLibraryDesugaringDependency = dependency
  }

  fun addProductFlavors(
    dimension: String,
    flavors: List<ProductFlavor>
  ) {
    this.flavors += dimension to flavors
  }

  fun addOptInsIfDependencyIsPresent(
    optIns: List<KotlinOptIn>,
    predicate: Dependency.() -> Boolean
  ) {
    optInsToDependencyPredicate += optIns to predicate
  }
}
