package com.eygraber.conventions.kotlin

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency

public fun Project.doOnFirstMatchingIncomingDependencyBeforeResolution(
  configurationName: String,
  dependencyPredicate: Dependency.() -> Boolean,
  onMatch: (Dependency) -> Unit,
) {
  configurations.named(configurationName).configure {
    incoming.beforeResolve {
      for(dependency in dependencies) {
        if(dependencyPredicate(dependency)) {
          onMatch(dependency)
          break
        }
      }
    }
  }
}
