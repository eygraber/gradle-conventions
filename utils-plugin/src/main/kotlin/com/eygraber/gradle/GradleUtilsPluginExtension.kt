package com.eygraber.gradle

import com.eygraber.gradle.android.GradleUtilsAndroid
import com.eygraber.gradle.compose.GradleUtilsCompose
import com.eygraber.gradle.dependencies.GradleUtilsDependencies
import com.eygraber.gradle.detekt.GradleUtilsDetekt
import com.eygraber.gradle.github.GradleUtilsGitHub
import com.eygraber.gradle.kotlin.GradleUtilsKotlin
import com.eygraber.gradle.spm.GradleUtilsSpm
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import java.util.concurrent.CopyOnWriteArrayList

internal interface GradleUtilsConfigurableListener {
  fun GradleUtilsAndroid.onAndroidConfigured(isUserConfigured: Boolean) {}
  fun GradleUtilsCompose.onComposeConfigured(isUserConfigured: Boolean) {}
  fun GradleUtilsDependencies.onDependenciesConfigured(isUserConfigured: Boolean) {}
  fun GradleUtilsDetekt.onDetektConfigured(isUserConfigured: Boolean) {}
  fun GradleUtilsGitHub.onGitHubConfigured(isUserConfigured: Boolean) {}
  fun GradleUtilsKotlin.onKotlinConfigured(isUserConfigured: Boolean) {}
  fun GradleUtilsSpm.onSpmConfigured(isUserConfigured: Boolean) {}
}

abstract class GradleUtilsPluginExtension {
  private val configureListeners = CopyOnWriteArrayList<GradleUtilsConfigurableListener>()

  var applyFoojayToolchainResolver = true

  internal fun awaitAndroidConfigured(
    configure: GradleUtilsAndroid.(isConfigured: Boolean) -> Unit
  ) {
    android.configure(isAndroidConfigured)
    configureListeners += object : GradleUtilsConfigurableListener {
      override fun GradleUtilsAndroid.onAndroidConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitComposeConfigured(
    configure: GradleUtilsCompose.(isConfigured: Boolean) -> Unit
  ) {
    compose.configure(isComposeConfigured)
    configureListeners += object : GradleUtilsConfigurableListener {
      override fun GradleUtilsCompose.onComposeConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitDependenciesConfigured(
    configure: GradleUtilsDependencies.(isConfigured: Boolean) -> Unit
  ) {
    dependencies.configure(isDependenciesConfigured)
    configureListeners += object : GradleUtilsConfigurableListener {
      override fun GradleUtilsDependencies.onDependenciesConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitDetektConfigured(
    configure: GradleUtilsDetekt.(isConfigured: Boolean) -> Unit
  ) {
    detekt.configure(isDetektConfigured)
    configureListeners += object : GradleUtilsConfigurableListener {
      override fun GradleUtilsDetekt.onDetektConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitGitHubConfigured(
    configure: GradleUtilsGitHub.(isConfigured: Boolean) -> Unit
  ) {
    github.configure(isGitHubConfigured)
    configureListeners += object : GradleUtilsConfigurableListener {
      override fun GradleUtilsGitHub.onGitHubConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitKotlinConfigured(
    configure: GradleUtilsKotlin.(isConfigured: Boolean) -> Unit
  ) {
    kotlin.configure(isKotlinConfigured)
    configureListeners += object : GradleUtilsConfigurableListener {
      override fun GradleUtilsKotlin.onKotlinConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitSpmConfigured(
    configure: GradleUtilsSpm.(isConfigured: Boolean) -> Unit
  ) {
    spm.configure(isSpmConfigured)
    configureListeners += object : GradleUtilsConfigurableListener {
      override fun GradleUtilsSpm.onSpmConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  private var isAndroidConfigured: Boolean = false
  internal val android = GradleUtilsAndroid()

  fun android(action: Action<GradleUtilsAndroid>) {
    action.execute(android)
    isAndroidConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        android.onAndroidConfigured(isAndroidConfigured)
      }
    }
  }

  private var isComposeConfigured: Boolean = false
  internal val compose = GradleUtilsCompose()

  fun compose(action: Action<GradleUtilsCompose>) {
    action.execute(compose)
    isComposeConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        compose.onComposeConfigured(isComposeConfigured)
      }
    }
  }

  private var isDependenciesConfigured: Boolean = false
  internal val dependencies = GradleUtilsDependencies()

  fun dependencies(action: Action<GradleUtilsDependencies>) {
    action.execute(dependencies)
    isDependenciesConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        dependencies.onDependenciesConfigured(isDependenciesConfigured)
      }
    }
  }

  private var isDetektConfigured: Boolean = false
  internal val detekt = GradleUtilsDetekt()

  fun detekt(action: Action<GradleUtilsDetekt>) {
    action.execute(detekt)
    isDetektConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        detekt.onDetektConfigured(isDetektConfigured)
      }
    }
  }

  private var isGitHubConfigured: Boolean = false
  internal val github = GradleUtilsGitHub()

  fun github(action: Action<GradleUtilsGitHub>) {
    action.execute(github)
    isGitHubConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        github.onGitHubConfigured(isGitHubConfigured)
      }
    }
  }

  private var wasKotlinUserConfigured: Boolean = false
  private var isKotlinConfigured: Boolean = false
  internal val kotlin = GradleUtilsKotlin()
    get() {
      wasKotlinUserConfigured = true

      return field
    }

  fun kotlin(action: Action<GradleUtilsKotlin>) {
    action.execute(kotlin)
    isKotlinConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        kotlin.onKotlinConfigured(isKotlinConfigured)
      }
    }
  }

  private var isSpmConfigured: Boolean = false
  internal val spm = GradleUtilsSpm()

  fun spm(action: Action<GradleUtilsSpm>) {
    action.execute(spm)
    isSpmConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        spm.onSpmConfigured(isSpmConfigured)
      }
    }
  }
}

internal val Project.gradleUtilsExtension: GradleUtilsPluginExtension
  get() =
    extensions.findByName("gradleUtils") as? GradleUtilsPluginExtension
      ?: extensions.create("gradleUtils")
