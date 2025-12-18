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

public fun Project.configureDetekt2(
  jvmTargetVersion: Provider<JvmTarget>,
  autoCorrect: Boolean = true,
  parallel: Boolean = true,
  debug: Boolean = false,
  enableCompilerPlugin: Boolean = true,
  buildUponDefaultConfig: Boolean = true,
  useRootConfigFile: Boolean = true,
  useProjectConfigFile: Boolean = true,
  configFiles: ConfigurableFileCollection = files(),
  ignoredAndroidFlavors: List<String> = emptyList(),
  ignoredAndroidVariants: List<String> = emptyList(),
  configure: Action<DetektExtension> = Actions.doNothing(),
) {
  configureDetekt2(
    jvmTargetVersion = jvmTargetVersion.orNull,
    autoCorrect = autoCorrect,
    parallel = parallel,
    debug = debug,
    enableCompilerPlugin = enableCompilerPlugin,
    buildUponDefaultConfig = buildUponDefaultConfig,
    useRootConfigFile = useRootConfigFile,
    useProjectConfigFile = useProjectConfigFile,
    configFiles = configFiles,
    ignoredAndroidFlavors = ignoredAndroidFlavors,
    ignoredAndroidVariants = ignoredAndroidVariants,
    configure = configure,
  )
}

public fun Project.configureDetekt2(
  jvmTargetVersion: JvmTarget?,
  autoCorrect: Boolean = true,
  parallel: Boolean = true,
  debug: Boolean = false,
  enableCompilerPlugin: Boolean = true,
  buildUponDefaultConfig: Boolean = true,
  useRootConfigFile: Boolean = true,
  useProjectConfigFile: Boolean = true,
  configFiles: ConfigurableFileCollection = files(),
  ignoredAndroidFlavors: List<String> = emptyList(),
  ignoredAndroidVariants: List<String> = emptyList(),
  configure: Action<DetektExtension> = Actions.doNothing(),
) {
  detekt2 {
    source.from("build.gradle.kts")

    this.autoCorrect.set(autoCorrect)
    this.parallel.set(parallel)
    this.debug.set(debug)
    this.enableCompilerPlugin.set(enableCompilerPlugin)
    this.buildUponDefaultConfig.set(buildUponDefaultConfig)

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
        var isJvmTargetSet = false
        tasks.withType(KotlinCompilationTask::class.java).configureEach {
          if(this is KotlinJvmCompile && !isJvmTargetSet) {
            jvmTarget.set(
              compilerOptions.jvmTarget.map { compilerOptionJvmTarget ->
                compilerOptionJvmTarget.target
              }
            )
            isJvmTargetSet = true
          }
        }
      }
      else {
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

private fun Project.detekt2(configure: DetektExtension.() -> Unit) {
  (this as ExtensionAware).extensions.configure("detekt", configure)
}
