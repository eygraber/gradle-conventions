package com.eygraber.gradle.detekt

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Provider
import org.gradle.internal.Actions

public fun Project.configureDetekt(
  jdkVersion: Provider<String>,
  useRootConfigFile: Boolean = true,
  useProjectConfigFile: Boolean = true,
  configFiles: ConfigurableFileCollection = files(),
  ignoredAndroidFlavors: List<String> = emptyList(),
  ignoredAndroidVariants: List<String> = emptyList(),
  configure: Action<DetektExtension> = Actions.doNothing()
) {
  configureDetekt(
    jdkVersion.get(),
    useRootConfigFile,
    useProjectConfigFile,
    configFiles,
    ignoredAndroidFlavors,
    ignoredAndroidVariants,
    configure
  )
}

public fun Project.configureDetekt(
  jdkVersion: String,
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

    config = configFiles.apply {
      val rootConfig = rootProject.file("detekt.yml")
      if(useRootConfigFile && rootConfig.exists()) {
        from(rootConfig)
      }

      val projectConfig = project.file("detekt.yml")
      if(useProjectConfigFile && projectConfig.exists()) {
        from(projectConfig)
      }
    }

    ignoredFlavors = ignoredFlavors + ignoredAndroidFlavors
    ignoredVariants = ignoredVariants + ignoredAndroidVariants

    configure.execute(this)
  }

  tasks.withType(Detekt::class.java).configureEach {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    jvmTarget = jdkVersion
    val projectDir = projectDir
    val buildDir = project.buildDir

    exclude {
      it.file.relativeTo(projectDir).startsWith(buildDir.relativeTo(projectDir))
    }
  }
}

internal val Project.detekt: DetektExtension
  get() = extensions.getByType(DetektExtension::class.java)

private fun Project.detekt(configure: DetektExtension.() -> Unit) =
  (this as ExtensionAware).extensions.configure("detekt", configure)
