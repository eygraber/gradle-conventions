package com.eygraber.conventions.detekt

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
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
    jvmTargetVersion = jvmTargetVersion.orNull,
    useRootConfigFile = useRootConfigFile,
    useProjectConfigFile = useProjectConfigFile,
    configFiles = configFiles,
    ignoredAndroidFlavors = ignoredAndroidFlavors,
    ignoredAndroidVariants = ignoredAndroidVariants,
    configure = configure,
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

    autoCorrect = true
    parallel = true

    buildUponDefaultConfig = true

    val rootConfig = isolated.rootProject.projectDirectory.file("detekt.yml").asFile
    if(useRootConfigFile && rootConfig.exists()) {
      configFiles.from(rootConfig)
    }

    val projectConfig = project.file("detekt.yml")
    if(useProjectConfigFile && projectConfig.exists()) {
      configFiles.from(projectConfig)
    }

    config.from(configFiles)

    ignoredFlavors = ignoredFlavors + ignoredAndroidFlavors
    ignoredVariants = ignoredVariants + ignoredAndroidVariants

    configure.execute(this)
  }

  tasks.withType(Detekt::class.java).configureEach { task ->
    with(task) {
      // Target version of the generated JVM bytecode. It is used for type resolution.
      if(jvmTargetVersion == null) {
        var isJvmTargetSet = false
        tasks.withType(KotlinCompilationTask::class.java).configureEach {
          if(this is KotlinJvmCompile && !isJvmTargetSet) {
            jvmTarget = compilerOptions.jvmTarget.get().target
            isJvmTargetSet = true
          }
        }
      }
      else {
        jvmTarget = jvmTargetVersion.target
      }

      val projectDir = projectDir
      val buildDir = project.layout.buildDirectory.asFile.get()

      exclude {
        it.file.relativeTo(projectDir).startsWith(buildDir.relativeTo(projectDir))
      }
    }
  }
}

private fun Project.detekt(configure: DetektExtension.() -> Unit) {
  (this as ExtensionAware).extensions.configure("detekt", configure)
}
