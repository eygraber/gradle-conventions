package com.eygraber.conventions.project.common

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.ModuleVersionSelector
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.kotlin.dsl.closureOf
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinDependencies
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

interface ResolutionVersionSelector {
  val configurationName: String
  val useVersion: (Any) -> Unit
}

interface ConventionDependencyHandler {
  fun add(configurationName: String, dependencyNotation: Any, action: Action<in Dependency> = Action {})

  fun api(dependencyNotation: Any, action: Action<in Dependency> = Action {})
  fun compileOnly(dependencyNotation: Any, action: Action<in Dependency> = Action {})
  fun implementation(dependencyNotation: Any, action: Action<in Dependency> = Action {})
  fun runtimeOnly(dependencyNotation: Any, action: Action<in Dependency> = Action {})
  fun testCompileOnly(dependencyNotation: Any, action: Action<in Dependency> = Action {})
  fun testImplementation(dependencyNotation: Any, action: Action<in Dependency> = Action {})
  fun testRuntimeOnly(dependencyNotation: Any, action: Action<in Dependency> = Action {})

  fun platform(dependencyNotation: Any): Any
  fun platform(dependencyNotation: Provider<MinimalExternalModuleDependency>): Provider<out ModuleDependency>
  fun platform(
    dependencyNotation: ProviderConvertible<MinimalExternalModuleDependency>,
  ): Provider<out ModuleDependency>
}

class JvmConventionDependencyHandler(
  dependencyHandler: DependencyHandler,
  val project: Project,
) : ConventionDependencyHandler, DependencyHandler by dependencyHandler {
  override fun add(
    configurationName: String,
    dependencyNotation: Any,
    action: Action<in Dependency>,
  ) {
    add(configurationName, dependencyNotation, closureOf<Dependency> { action.execute(this) })
  }

  override fun api(dependencyNotation: Any, action: Action<in Dependency>) {
    add("api", dependencyNotation, closureOf<Dependency> { action.execute(this) })
  }

  override fun compileOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    add("compileOnly", dependencyNotation, closureOf<Dependency> { action.execute(this) })
  }

  override fun implementation(dependencyNotation: Any, action: Action<in Dependency>) {
    add("implementation", dependencyNotation, closureOf<Dependency> { action.execute(this) })
  }

  override fun runtimeOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    add("runtimeOnly", dependencyNotation, closureOf<Dependency> { action.execute(this) })
  }

  override fun testCompileOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    add("testCompileOnly", dependencyNotation, closureOf<Dependency> { action.execute(this) })
  }

  override fun testImplementation(dependencyNotation: Any, action: Action<in Dependency>) {
    add("testImplementation", dependencyNotation, closureOf<Dependency> { action.execute(this) })
  }

  override fun testRuntimeOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    add("testRuntimeOnly", dependencyNotation, closureOf<Dependency> { action.execute(this) })
  }

  override fun platform(
    dependencyNotation: Provider<MinimalExternalModuleDependency>,
  ) = super.platform(dependencyNotation)

  override fun platform(
    dependencyNotation: ProviderConvertible<MinimalExternalModuleDependency>,
  ) = super.platform(dependencyNotation)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
class KmpTopLevelConventionDependencyHandler(
  private val dependencies: KotlinDependencies,
  val project: Project,
) : ConventionDependencyHandler {
  override fun add(configurationName: String, dependencyNotation: Any, action: Action<in Dependency>) {
    error("KMP dependency APIs don't currently support arbitrary configurations")
  }

  override fun api(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.api.add(dependencyNotation, action)
  }

  override fun compileOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.compileOnly.add(dependencyNotation, action)
  }

  override fun implementation(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.implementation.add(dependencyNotation, action)
  }

  override fun runtimeOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.runtimeOnly.add(dependencyNotation, action)
  }

  override fun testCompileOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.testCompileOnly.add(dependencyNotation, action)
  }

  override fun testImplementation(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.testImplementation.add(dependencyNotation, action)
  }

  override fun testRuntimeOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.testRuntimeOnly.add(dependencyNotation, action)
  }

  override fun platform(
    dependencyNotation: Any,
  ) = when(dependencyNotation) {
    is CharSequence -> dependencies.platform(dependencyNotation)
    else -> error("Dependency of type ${dependencyNotation::class} is not supported")
  }

  override fun platform(
    dependencyNotation: Provider<MinimalExternalModuleDependency>,
  ) = dependencies.platform(dependencyNotation)

  @Suppress("UnstableApiUsage")
  override fun platform(
    dependencyNotation: ProviderConvertible<MinimalExternalModuleDependency>,
  ) = dependencies.platform(dependencyNotation)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
class KmpSourceSetConventionDependencyHandler(
  private val dependencies: KotlinDependencyHandler,
  val project: Project,
) : ConventionDependencyHandler {
  override fun add(configurationName: String, dependencyNotation: Any, action: Action<in Dependency>) {
    error("KMP dependency APIs don't currently support arbitrary configurations")
  }

  override fun api(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.api(dependencyNotation)?.let(action::execute)
  }

  override fun compileOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.compileOnly(dependencyNotation)?.let(action::execute)
  }

  override fun implementation(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.implementation(dependencyNotation)?.let(action::execute)
  }

  override fun runtimeOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    dependencies.runtimeOnly(dependencyNotation)?.let(action::execute)
  }

  override fun testCompileOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    error("Use compileOnly on a test source set instead")
  }

  override fun testImplementation(dependencyNotation: Any, action: Action<in Dependency>) {
    error("Use implementation on a test source set instead")
  }

  override fun testRuntimeOnly(dependencyNotation: Any, action: Action<in Dependency>) {
    error("Use runtimeOnly on a test source set instead")
  }

  override fun platform(
    dependencyNotation: Any,
  ) = when(dependencyNotation) {
    is CharSequence -> project.dependencies.platform(dependencyNotation)
    else -> error("Dependency of type ${dependencyNotation::class} is not supported")
  }

  override fun platform(
    dependencyNotation: Provider<MinimalExternalModuleDependency>,
  ) = project.dependencies.platform(dependencyNotation)

  override fun platform(
    dependencyNotation: ProviderConvertible<MinimalExternalModuleDependency>,
  ) = project.dependencies.platform(dependencyNotation)
}

@Suppress("UnstableApiUsage", "UNCHECKED_CAST")
private fun DependencyCollector.add(dependencyNotation: Any, action: Action<in Dependency>) {
  when(dependencyNotation) {
    is CharSequence -> add(dependencyNotation, action)
    is FileCollection -> add(dependencyNotation, action)
    is ProviderConvertible<*> -> add(dependencyNotation as ProviderConvertible<MinimalExternalModuleDependency>, action)
    is Provider<*> -> add(dependencyNotation as Provider<MinimalExternalModuleDependency>, action)
    is Dependency -> add(dependencyNotation, action)
    else -> error("Dependency notation of type ${dependencyNotation::class} is not supported")
  }
}

internal typealias SourceSetDependencyHandler =
  Pair<(KotlinSourceSet) -> Boolean, KmpSourceSetConventionDependencyHandler.() -> Unit>

class GradleConventionsProjectCommon {
  var resolutionVersionSelector: (ModuleVersionSelector.(ResolutionVersionSelector) -> Unit)? = null

  internal var kmpSourceSetProjectDependencies: MutableList<SourceSetDependencyHandler> = mutableListOf()
  internal var projectDependencies: MutableList<ConventionDependencyHandler.() -> Unit> = mutableListOf()

  fun projectDependencies(
    dependencies: ConventionDependencyHandler.() -> Unit,
  ) {
    projectDependencies += dependencies
  }

  fun kmpSourceSetProjectDependencies(
    sourceSetPredicate: (KotlinSourceSet) -> Boolean,
    dependencies: KmpSourceSetConventionDependencyHandler.() -> Unit,
  ) {
    kmpSourceSetProjectDependencies += sourceSetPredicate to dependencies
  }
}
