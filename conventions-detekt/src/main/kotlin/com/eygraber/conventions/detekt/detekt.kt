package com.eygraber.conventions.detekt

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Provider
import org.gradle.internal.Actions
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

public fun Project.configureDetekt(
  jvmTargetVersion: Provider<JvmTarget>,
  useRootConfigFile: Boolean = true,
  useProjectConfigFile: Boolean = true,
  configFiles: ConfigurableFileCollection = files(),
  ignoredAndroidFlavors: List<String> = emptyList(),
  ignoredAndroidVariants: List<String> = emptyList(),
  configure: Action<DetektExtension> = Actions.doNothing(),
) {
  configureDetekt(
    jvmTargetVersion.orNull,
    useRootConfigFile,
    useProjectConfigFile,
    configFiles,
    ignoredAndroidFlavors,
    ignoredAndroidVariants,
    configure,
  )
}

public fun Project.configureDetekt(
  jvmTargetVersion: JvmTarget?,
  useRootConfigFile: Boolean = true,
  useProjectConfigFile: Boolean = true,
  configFiles: ConfigurableFileCollection = files(),
  ignoredAndroidFlavors: List<String> = emptyList(),
  ignoredAndroidVariants: List<String> = emptyList(),
  configure: Action<DetektExtension> = Actions.doNothing(),
) {
  detekt {
    source.from("build.gradle.kts")

    autoCorrect.set(true)
    parallel.set(true)

    buildUponDefaultConfig.set(true)

    val rootConfig = rootProject.file("detekt.yml")
    if(useRootConfigFile && rootConfig.exists()) {
      configFiles.from(rootConfig)
    }

    val projectConfig = project.file("detekt.yml")
    if(useProjectConfigFile && projectConfig.exists()) {
      configFiles.from(projectConfig)
    }

    config.from(configFiles)

    ignoredFlavors.addAll(ignoredAndroidFlavors)
    ignoredVariants.addAll(ignoredAndroidVariants)

    configure.execute(this)
  }

  tasks.withType(Detekt::class.java).configureEach { task ->
    with(task) {
      // Target version of the generated JVM bytecode. It is used for type resolution.
      if(jvmTargetVersion == null) {
        var jvmTargetSet = false
        tasks.withType(KotlinCompilationTask::class.java).configureEach {
          if(this is KotlinJvmCompile && !jvmTargetSet) {
            jvmTarget.set(compilerOptions.jvmTarget.map { it.target })
            jvmTargetSet = true
          }
        }
      } else {
        jvmTarget.set(jvmTargetVersion.target)
      }

      val projectDir = projectDir
      val buildDir = project.layout.buildDirectory.asFile.get()

      exclude {
        it.file.relativeTo(projectDir).startsWith(buildDir.relativeTo(projectDir))
      }
    }
  }
}

internal val Project.detekt: DetektExtension
  get() = extensions.getByType(DetektExtension::class.java)

private fun Project.detekt(configure: DetektExtension.() -> Unit) =
  (this as ExtensionAware).extensions.configure("detekt", configure)
