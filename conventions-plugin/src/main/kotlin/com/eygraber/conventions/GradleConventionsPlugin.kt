package com.eygraber.conventions

import com.eygraber.conventions.android.GradleConventionsAndroid
import com.eygraber.conventions.compose.GradleConventionsCompose
import com.eygraber.conventions.dependencies.GradleConventionsDependencies
import com.eygraber.conventions.detekt.GradleConventionsDetekt
import com.eygraber.conventions.github.GradleConventionsGitHub
import com.eygraber.conventions.kotlin.GradleConventionsKotlin
import com.eygraber.conventions.spm.GradleConventionsSpm
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

internal abstract class GradleConventionsDefaults : BuildService<None> {
  val android = GradleConventionsAndroid()
  val compose = GradleConventionsCompose()
  val dependencies = GradleConventionsDependencies()
  val detekt = GradleConventionsDetekt()
  val github = GradleConventionsGitHub()
  val kotlin = GradleConventionsKotlin()
  val spm = GradleConventionsSpm()
}

abstract class GradleConventionsPlugin : Plugin<Settings> {
  override fun apply(target: Settings) {
    target.gradle.rootProject {
      with(extensions.create<GradleConventionsPluginExtension>("gradleConventionsDefaults")) {
        if(applyFoojayToolchainResolver) {
          target.applyFoojayToolchainResolver()
        }

        with(gradleConventionsDefaultsService) {
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

internal val Project.gradleConventionsDefaultsService
  get() = gradle.sharedServices.registerIfAbsent("gradleConventionsDefaults", GradleConventionsDefaults::class) {}.get()
