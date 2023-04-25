package com.eygraber.conventions

import com.eygraber.conventions.android.GradleConventionsAndroid
import com.eygraber.conventions.compose.GradleConventionsCompose
import com.eygraber.conventions.dependencies.GradleConventionsDependencies
import com.eygraber.conventions.detekt.GradleConventionsDetekt
import com.eygraber.conventions.github.GradleConventionsGitHub
import com.eygraber.conventions.kotlin.GradleConventionsKotlin
import com.eygraber.conventions.ktlint.GradleConventionsKtlint
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
  val dependencies = GradleConventionsDependencies()
  val detekt = GradleConventionsDetekt()
  val github = GradleConventionsGitHub()
  val kotlin = GradleConventionsKotlin()
  val ktlint = GradleConventionsKtlint()
  val spm = GradleConventionsSpm()
}

abstract class GradleConventionsPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    require(target.rootProject == target) {
      "com.eygraber.conventions plugin should only be applied on the root project"
    }

    with(target.extensions.create<GradleConventionsPluginExtension>("gradleConventionsDefaults")) {
      with(target.gradleConventionsDefaultsService) {
        awaitAndroidConfigured {
          android.compileSdk = compileSdk
          android.targetSdk = targetSdk
          android.minSdk = minSdk
          android.publishEverything = publishEverything
          android.coreLibraryDesugaringDependency = coreLibraryDesugaringDependency
          android.flavors = flavors
          android.optInsToDependencyPredicate = optInsToDependencyPredicate
        }

        awaitComposeConfigured {
          compose.applyToAndroidAndJvmOnly = applyToAndroidAndJvmOnly
          compose.androidComposeCompilerVersionOverride = androidComposeCompilerVersionOverride
          compose.androidComposeDependencyBomVersion = androidComposeDependencyBomVersion
          compose.useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion =
            useAndroidComposeCompilerVersionForJetbrainsComposeCompilerVersion
        }

        awaitDependenciesConfigured {
          dependencies.resolutionVersionSelector = resolutionVersionSelector
          dependencies.projectDependencies = projectDependencies
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
          kotlin.jdkVersion = jdkVersion
          kotlin.jvmDistribution = jvmDistribution
          kotlin.allWarningsAsErrors = allWarningsAsErrors
          kotlin.explicitApiMode = explicitApiMode
          kotlin.configureJava = configureJava
          kotlin.useK2 = useK2
          kotlin.freeCompilerArgs = freeCompilerArgs
          kotlin.optIns = optIns
        }

        awaitKtlintConfigured {
          ktlint.version = version
          ktlint.relative = relative
          ktlint.verbose = verbose
          ktlint.debug = debug
          ktlint.android = android
          ktlint.outputToConsole = outputToConsole
          ktlint.coloredOutput = coloredOutput
          ktlint.outputColorName = outputColorName
          ktlint.ignoreFailures = ignoreFailures
          ktlint.workerMaxHeapSize = workerMaxHeapSize
          ktlint.scriptFileTrees.addAll(scriptFileTrees)
          ktlint.includes.addAll(includes)
          ktlint.excludes.addAll(excludes)
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
