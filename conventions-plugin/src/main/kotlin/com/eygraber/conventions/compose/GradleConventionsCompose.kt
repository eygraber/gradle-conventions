package com.eygraber.conventions.compose

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.api.internal.artifacts.dependencies.DefaultImmutableVersionConstraint
import org.gradle.api.internal.artifacts.dependencies.DefaultMinimalDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint
import org.gradle.api.provider.Provider

class GradleConventionsCompose {
  var androidComposeCompilerVersionOverride: String? = null
  var androidComposeDependencyBomVersion: String? = null
  var enableAndroidCompilerMetrics: Boolean = false
  var applyToAndroidAndJvmOnly: Boolean = false
  var jetbrainsComposeCompilerOverride: MinimalExternalModuleDependency? = null
  var useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion: Boolean = false

  @Suppress("ObjectPropertyNaming")
  companion object {
    const val JetbrainsCompilerArtifact = "org.jetbrains.compose.compiler:compiler"
    const val JetpackCompilerArtifact = "androidx.compose.compiler:compiler"
  }

  fun android(
    compilerVersionOverride: Provider<String>? = null,
    compilerOverride: Provider<MinimalExternalModuleDependency>? = null,
    bomVersion: Provider<String>? = null,
    bom: Provider<MinimalExternalModuleDependency>? = null,
    enableAndroidCompilerMetrics: Boolean = false
  ) {
    compilerVersionOverride?.let { androidComposeCompilerVersionOverride = it.get() }
    compilerOverride?.let {
      val dependency = it.get()
      check(dependency.group == "androidx.compose.compiler" && dependency.name == "compiler") {
        "Only the $JetpackCompilerArtifact artifact should be used as an override"
      }
      val version = it.get().version
      requireNotNull(version) {
        "Please specify a version for the $JetpackCompilerArtifact artifact"
      }
      androidComposeCompilerVersionOverride = version
    }
    bomVersion?.let { androidComposeDependencyBomVersion = it.get() }
    bom?.let { androidComposeDependencyBomVersion = it.get().version }
    this.enableAndroidCompilerMetrics = enableAndroidCompilerMetrics
  }

  fun multiplatformWithAndroidCompiler(
    androidCompilerVersion: Provider<String>,
    applyToAndroidAndJvmOnly: Boolean = false
  ) {
    multiplatform(
      compilerMavenCoordinatesOverride = "$JetpackCompilerArtifact:$androidCompilerVersion",
      applyToAndroidAndJvmOnly = applyToAndroidAndJvmOnly
    )
  }

  fun multiplatformWithJetbrainsCompiler(
    jetbrainsCompilerVersion: Provider<String>,
    applyToAndroidAndJvmOnly: Boolean = false
  ) {
    multiplatform(
      compilerMavenCoordinatesOverride = "$JetbrainsCompilerArtifact:$jetbrainsCompilerVersion",
      applyToAndroidAndJvmOnly = applyToAndroidAndJvmOnly
    )
  }

  fun multiplatform(
    compilerMavenCoordinatesOverride: String? = null,
    compilerOverride: Provider<MinimalExternalModuleDependency>? = null,
    applyToAndroidAndJvmOnly: Boolean = false
  ) {
    if(compilerMavenCoordinatesOverride != null) {
      val coords = compilerMavenCoordinatesOverride.split(":")
      jetbrainsComposeCompilerOverride = when(coords.size) {
        3 -> DefaultMinimalDependency(
          DefaultModuleIdentifier.newId(
            coords[0],
            coords[1]
          ),
          DefaultMutableVersionConstraint(coords[2])
        )

        2 -> DefaultMinimalDependency(
          DefaultModuleIdentifier.newId(
            coords[0],
            coords[1]
          ),
          DefaultMutableVersionConstraint(DefaultImmutableVersionConstraint.of())
        )

        else -> error("Please specify full maven coordinates for the Compose compiler you'd like to use")
      }
    }
    compilerOverride?.let { jetbrainsComposeCompilerOverride = it.get() }
    this.applyToAndroidAndJvmOnly = applyToAndroidAndJvmOnly
  }
}
