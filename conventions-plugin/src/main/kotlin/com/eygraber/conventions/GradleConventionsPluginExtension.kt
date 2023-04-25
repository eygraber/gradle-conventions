package com.eygraber.conventions

import com.eygraber.conventions.android.GradleConventionsAndroid
import com.eygraber.conventions.compose.GradleConventionsCompose
import com.eygraber.conventions.dependencies.GradleConventionsDependencies
import com.eygraber.conventions.detekt.GradleConventionsDetekt
import com.eygraber.conventions.github.GradleConventionsGitHub
import com.eygraber.conventions.kotlin.GradleConventionsKotlin
import com.eygraber.conventions.ktlint.GradleConventionsKtlint
import com.eygraber.conventions.spm.GradleConventionsSpm
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import java.util.concurrent.CopyOnWriteArrayList

internal interface GradleConventionsConfigurableListener {
  fun GradleConventionsAndroid.onAndroidConfigured(isUserConfigured: Boolean) {}
  fun GradleConventionsCompose.onComposeConfigured(isUserConfigured: Boolean) {}
  fun GradleConventionsDependencies.onDependenciesConfigured(isUserConfigured: Boolean) {}
  fun GradleConventionsDetekt.onDetektConfigured(isUserConfigured: Boolean) {}
  fun GradleConventionsGitHub.onGitHubConfigured(isUserConfigured: Boolean) {}
  fun GradleConventionsKotlin.onKotlinConfigured(isUserConfigured: Boolean) {}
  fun GradleConventionsKtlint.onKtlintConfigured(isUserConfigured: Boolean) {}
  fun GradleConventionsSpm.onSpmConfigured(isUserConfigured: Boolean) {}
}

abstract class GradleConventionsPluginExtension {
  private val configureListeners = CopyOnWriteArrayList<GradleConventionsConfigurableListener>()

  internal fun awaitAndroidConfigured(
    configure: GradleConventionsAndroid.(isConfigured: Boolean) -> Unit
  ) {
    android.configure(isAndroidConfigured)
    configureListeners += object : GradleConventionsConfigurableListener {
      override fun GradleConventionsAndroid.onAndroidConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitComposeConfigured(
    configure: GradleConventionsCompose.(isConfigured: Boolean) -> Unit
  ) {
    compose.configure(isComposeConfigured)
    configureListeners += object : GradleConventionsConfigurableListener {
      override fun GradleConventionsCompose.onComposeConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitDependenciesConfigured(
    configure: GradleConventionsDependencies.(isConfigured: Boolean) -> Unit
  ) {
    dependencies.configure(isDependenciesConfigured)
    configureListeners += object : GradleConventionsConfigurableListener {
      override fun GradleConventionsDependencies.onDependenciesConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitDetektConfigured(
    configure: GradleConventionsDetekt.(isConfigured: Boolean) -> Unit
  ) {
    detekt.configure(isDetektConfigured)
    configureListeners += object : GradleConventionsConfigurableListener {
      override fun GradleConventionsDetekt.onDetektConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitGitHubConfigured(
    configure: GradleConventionsGitHub.(isConfigured: Boolean) -> Unit
  ) {
    github.configure(isGitHubConfigured)
    configureListeners += object : GradleConventionsConfigurableListener {
      override fun GradleConventionsGitHub.onGitHubConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitKotlinConfigured(
    configure: GradleConventionsKotlin.(isConfigured: Boolean) -> Unit
  ) {
    kotlin.configure(isKotlinConfigured)
    configureListeners += object : GradleConventionsConfigurableListener {
      override fun GradleConventionsKotlin.onKotlinConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitKtlintConfigured(
    configure: GradleConventionsKtlint.(isConfigured: Boolean) -> Unit
  ) {
    ktlint.configure(isKtlintConfigured)
    configureListeners += object : GradleConventionsConfigurableListener {
      override fun GradleConventionsKtlint.onKtlintConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  internal fun awaitSpmConfigured(
    configure: GradleConventionsSpm.(isConfigured: Boolean) -> Unit
  ) {
    spm.configure(isSpmConfigured)
    configureListeners += object : GradleConventionsConfigurableListener {
      override fun GradleConventionsSpm.onSpmConfigured(isUserConfigured: Boolean) {
        configure(isUserConfigured)
      }
    }
  }

  private var isAndroidConfigured: Boolean = false
  internal val android = GradleConventionsAndroid()

  fun android(action: Action<GradleConventionsAndroid>) {
    action.execute(android)
    isAndroidConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        android.onAndroidConfigured(isAndroidConfigured)
      }
    }
  }

  private var isComposeConfigured: Boolean = false
  internal val compose = GradleConventionsCompose()

  fun compose(action: Action<GradleConventionsCompose>) {
    action.execute(compose)
    isComposeConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        compose.onComposeConfigured(isComposeConfigured)
      }
    }
  }

  private var isDependenciesConfigured: Boolean = false
  internal val dependencies = GradleConventionsDependencies()

  fun dependencies(action: Action<GradleConventionsDependencies>) {
    action.execute(dependencies)
    isDependenciesConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        dependencies.onDependenciesConfigured(isDependenciesConfigured)
      }
    }
  }

  private var isDetektConfigured: Boolean = false
  internal val detekt = GradleConventionsDetekt()

  fun detekt(action: Action<GradleConventionsDetekt>) {
    action.execute(detekt)
    isDetektConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        detekt.onDetektConfigured(isDetektConfigured)
      }
    }
  }

  private var isGitHubConfigured: Boolean = false
  internal val github = GradleConventionsGitHub()

  fun github(action: Action<GradleConventionsGitHub>) {
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
  internal val kotlin = GradleConventionsKotlin()
    get() {
      wasKotlinUserConfigured = true

      return field
    }

  fun kotlin(action: Action<GradleConventionsKotlin>) {
    action.execute(kotlin)
    isKotlinConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        kotlin.onKotlinConfigured(isKotlinConfigured)
      }
    }
  }

  private var wasKtlintUserConfigured: Boolean = false
  private var isKtlintConfigured: Boolean = false
  internal val ktlint = GradleConventionsKtlint()
    get() {
      wasKtlintUserConfigured = true

      return field
    }

  fun ktlint(action: Action<GradleConventionsKtlint>) {
    action.execute(ktlint)
    isKtlintConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        ktlint.onKtlintConfigured(isKtlintConfigured)
      }
    }
  }

  private var isSpmConfigured: Boolean = false
  internal val spm = GradleConventionsSpm()

  fun spm(action: Action<GradleConventionsSpm>) {
    action.execute(spm)
    isSpmConfigured = true
    configureListeners.forEach { listener ->
      with(listener) {
        spm.onSpmConfigured(isSpmConfigured)
      }
    }
  }
}

internal val Project.gradleConventionsExtension: GradleConventionsPluginExtension
  get() =
    extensions.findByName("gradleConventions") as? GradleConventionsPluginExtension
      ?: extensions.create("gradleConventions")
