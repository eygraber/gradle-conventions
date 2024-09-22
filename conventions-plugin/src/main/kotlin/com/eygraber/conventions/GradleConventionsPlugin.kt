package com.eygraber.conventions

import com.eygraber.conventions.android.GradleConventionsAndroid
import com.eygraber.conventions.compose.GradleConventionsCompose
import com.eygraber.conventions.detekt.GradleConventionsDetekt
import com.eygraber.conventions.github.GradleConventionsGitHub
import com.eygraber.conventions.kotlin.GradleConventionsKmpDefaultExtension
import com.eygraber.conventions.kotlin.GradleConventionsKmpDefaults
import com.eygraber.conventions.kotlin.GradleConventionsKotlin
import com.eygraber.conventions.project.common.GradleConventionsProjectCommon
import com.eygraber.conventions.spm.GradleConventionsSpm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters.None
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.registerIfAbsent

internal abstract class GradleConventionsDefaults : BuildService<None> {
  val android = GradleConventionsAndroid()
  val compose = GradleConventionsCompose()
  val projectCommon = GradleConventionsProjectCommon()
  val detekt = GradleConventionsDetekt()
  val github = GradleConventionsGitHub()
  val kotlin = GradleConventionsKotlin()
  val spm = GradleConventionsSpm()
}

abstract class GradleConventionsPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    require(target.rootProject == target) {
      "com.eygraber.conventions plugin should only be applied on the root project"
    }

    target.extensions.create<GradleConventionsKmpDefaultExtension>("gradleConventionsKmpDefaults")

    with(target.extensions.create<GradleConventionsPluginExtension>("gradleConventionsDefaults")) {
      with(target.gradleConventionsDefaultsService) {
        awaitAndroidConfigured {
          android.compileSdk = compileSdk
          android.targetSdk = targetSdk
          android.minSdk = minSdk
          android.doNotRunLintWhenRunningReleaseBuildTasks = doNotRunLintWhenRunningReleaseBuildTasks
          android.sourceCompatibility = sourceCompatibility
          android.targetCompatibility = targetCompatibility
          android.publishEverything = publishEverything
          android.coreLibraryDesugaringDependency = coreLibraryDesugaringDependency
          android.flavors = flavors
          android.optInsToDependencyPredicate = optInsToDependencyPredicate
        }

        awaitComposeConfigured {
          compose.generateFunctionKeyMetaClasses = generateFunctionKeyMetaClasses
          compose.includeSourceInformation = includeSourceInformation
          compose.metricsDestination = metricsDestination
          compose.reportsDestination = reportsDestination
          compose.featureFlags = featureFlags
          compose.stabilityConfigurationFile = stabilityConfigurationFile
          compose.includeTraceMarkers = includeTraceMarkers
          compose.targetKotlinPlatforms = targetKotlinPlatforms
        }

        awaitProjectCommonConfigured {
          projectCommon.resolutionVersionSelector = resolutionVersionSelector
          projectCommon.projectDependencies = projectDependencies
        }

        awaitDetektConfigured {
          detekt.ignoredAndroidFlavors = ignoredAndroidFlavors
          detekt.ignoredAndroidVariants = ignoredAndroidVariants
          detekt.detektPluginDependencies = detektPluginDependencies
        }

        awaitGitHubConfigured {
          github.owner = owner
          github.repoName = repoName
        }

        awaitKotlinConfigured {
          kotlin.jvmTargetVersion = jvmTargetVersion
          kotlin.jdkToolchainVersion = jdkToolchainVersion
          kotlin.jvmDistribution = jvmDistribution
          kotlin.allWarningsAsErrors = allWarningsAsErrors
          kotlin.explicitApiMode = explicitApiMode
          kotlin.configureJavaTargetVersion = configureJavaTargetVersion
          kotlin.languageVersion = languageVersion
          kotlin.apiVersion = apiVersion
          kotlin.isProgressiveModeEnabled = isProgressiveModeEnabled
          kotlin.freeCompilerArgs = freeCompilerArgs
          kotlin.optIns = optIns
        }

        awaitSpmConfigured {
          spm.frameworkName = frameworkName
          spm.version = version
          spm.includeMacos = includeMacos
        }
      }
    }
  }
}

internal val Project.gradleConventionsDefaultsService
  get() = gradle.sharedServices.registerIfAbsent("gradleConventionsDefaults", GradleConventionsDefaults::class) {}.get()

internal val Project.gradleConventionsKmpDefaultsService
  get() =
    gradle.sharedServices.registerIfAbsent("gradleConventionsKmpDefaults", GradleConventionsKmpDefaults::class) {}.get()
