package com.eygraber.gradle.dependencies

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleVersionSelector
import org.gradle.api.artifacts.dsl.DependencyHandler

interface ResolutionVersionSelector {
  val configurationName: String
  val useVersion: (Any) -> Unit
}

class ConventionDependencyHandler(
  dependencyHandler: DependencyHandler,
  val project: Project
) : DependencyHandler by dependencyHandler {
  fun api(dependencyNotation: Any): Dependency? = add("api", dependencyNotation)
  fun compileOnly(dependencyNotation: Any): Dependency? = add("compileOnly", dependencyNotation)
  fun implementation(dependencyNotation: Any): Dependency? = add("implementation", dependencyNotation)
}

class GradleUtilsDependencies {
  var resolutionVersionSelector: (ModuleVersionSelector.(ResolutionVersionSelector) -> Unit)? = null

  internal var projectDependencies: (ConventionDependencyHandler.() -> Unit)? = null

  fun projectDependencies(
    dependencies: ConventionDependencyHandler.() -> Unit
  ) {
    projectDependencies = dependencies
  }
}
