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
  jvmTargetVersion: Provider<JvmTarget?>,
  useRootConfigFile: Boolean = true,
  useProjectConfigFile: Boolean = true,
  configFiles: ConfigurableFileCollection = files(),
  ignoredAndroidFlavors: List<String> = emptyList(),
  ignoredAndroidVariants: List<String> = emptyList(),
  configure: Action<DetektExtension> = Actions.doNothing()
) {
  configureDetekt(
    jvmTargetVersion.get(),
    useRootConfigFile,
    useProjectConfigFile,
    configFiles,
    ignoredAndroidFlavors,
    ignoredAndroidVariants,
    configure
  )
}

public fun Project.configureDetekt(
  jvmTargetVersion: JvmTarget?,
  useRootConfigFile: Boolean = true,
  useProjectConfigFile: Boolean = true,
  configFiles: ConfigurableFileCollection = files(),
  ignoredAndroidFlavors: List<String> = emptyList(),
  ignoredAndroidVariants: List<String> = emptyList(),
  configure: Action<DetektExtension> = Actions.doNothing()
) {
  detekt {
    source.from("build.gradle.kts")

    autoCorrect = true
    parallel = true

    buildUponDefaultConfig = true

    val rootConfig = rootProject.file("detekt.yml")
    if(useRootConfigFile && rootConfig.exists()) {
      configFiles.setFrom(rootConfig)
    }

    val projectConfig = project.file("detekt.yml")
    if(useProjectConfigFile && projectConfig.exists()) {
      configFiles.setFrom(projectConfig)
    }

    config.setFrom(configFiles)

    ignoredFlavors = ignoredFlavors + ignoredAndroidFlavors
    ignoredVariants = ignoredVariants + ignoredAndroidVariants

    configure.execute(this)
  }

  tasks.withType(Detekt::class.java).configureEach {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    if(jvmTargetVersion == null) {
      var jvmTargetSet = false
      tasks.withType(KotlinCompilationTask::class.java).configureEach {
        if(this is KotlinJvmCompile && !jvmTargetSet) {
          jvmTarget = compilerOptions.jvmTarget.get().target
          jvmTargetSet = true
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

internal val Project.detekt: DetektExtension
  get() = extensions.getByType(DetektExtension::class.java)

private fun Project.detekt(configure: DetektExtension.() -> Unit) =
  (this as ExtensionAware).extensions.configure("detekt", configure)
