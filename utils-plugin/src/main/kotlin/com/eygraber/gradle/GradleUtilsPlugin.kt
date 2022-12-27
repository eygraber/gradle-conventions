package com.eygraber.gradle

import com.eygraber.gradle.android.GradleUtilsAndroid
import com.eygraber.gradle.compose.GradleUtilsCompose
import com.eygraber.gradle.dependencies.GradleUtilsDependencies
import com.eygraber.gradle.detekt.GradleUtilsDetekt
import com.eygraber.gradle.github.GradleUtilsGitHub
import com.eygraber.gradle.kotlin.GradleUtilsKotlin
import com.eygraber.gradle.spm.GradleUtilsSpm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters.None
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.jvm
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.toolchains.foojay.FoojayToolchainResolver
import org.gradle.toolchains.foojay.FoojayToolchainsPlugin

internal abstract class GradleUtilsDefaults : BuildService<None> {
  val android = GradleUtilsAndroid()
  val compose = GradleUtilsCompose()
  val dependencies = GradleUtilsDependencies()
  val detekt = GradleUtilsDetekt()
  val github = GradleUtilsGitHub()
  val kotlin = GradleUtilsKotlin()
  val spm = GradleUtilsSpm()
}

abstract class GradleUtilsPlugin : Plugin<Settings> {
  override fun apply(target: Settings) {
    target.gradle.rootProject {
      with(extensions.create<GradleUtilsPluginExtension>("gradleUtilsDefaults")) {
        if(applyFoojayToolchainResolver) {
          target.applyFoojayToolchainResolver()
        }

        with(gradleUtilsDefaultsService) {
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

          awaitSpmConfigured {
            spm.frameworkName = frameworkName
            spm.version = version
            spm.includeMacos = includeMacos
          }
        }
      }
    }
  }
}

private fun Settings.applyFoojayToolchainResolver() {
  plugins.apply(FoojayToolchainsPlugin::class.java)

  @Suppress("UnstableApiUsage")
  toolchainManagement {
    jvm {
      javaRepositories {
        repository("foojay") {
          resolverClass.set(FoojayToolchainResolver::class.java)
        }
      }
    }
  }
}

internal val Project.gradleUtilsDefaultsService
  get() = gradle.sharedServices.registerIfAbsent("gradleUtilsDefaults", GradleUtilsDefaults::class) {}.get()
