package com.eygraber.conventions.ktlint

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider

class GradleConventionsKtlint {
  var version: String? = null
  var relative = false
  var verbose = false
  var debug = false
  var android = false
  var outputToConsole = true
  var coloredOutput = true
  var outputColorName: String? = null
  var ignoreFailures = false
  var workerMaxHeapSize: String? = null

  val scriptFileTrees = mutableListOf<String>()
  val includes = mutableListOf<String>()
  val excludes = mutableListOf<String>()

  internal var ktlintRulesetDependencies: MutableList<Any> = mutableListOf()

  fun ruleset(
    vararg dependencies: Provider<MinimalExternalModuleDependency>
  ) {
    ktlintRulesetDependencies.addAll(dependencies)
  }

  fun rulesetCoordinates(
    vararg dependencies: String
  ) {
    ktlintRulesetDependencies.addAll(dependencies)
  }
}
