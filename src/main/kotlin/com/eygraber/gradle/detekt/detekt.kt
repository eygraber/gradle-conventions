package com.eygraber.gradle.detekt

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Provider
import org.gradle.internal.Actions
import java.io.File

public fun Project.configureDetekt(
  jdkVersion: Provider<String>,
  configFile: File = File("$rootDir/detekt.yml"),
  configure: Action<DetektExtension> = Actions.doNothing()
) {
  configureDetekt(jdkVersion.get(), configFile, configure)
}

public fun Project.configureDetekt(
  jdkVersion: String,
  configFile: File = File("$rootDir/detekt.yml"),
  configure: Action<DetektExtension> = Actions.doNothing()
) {
  detekt {
    source.from("build.gradle.kts")

    autoCorrect = true
    parallel = true

    buildUponDefaultConfig = true

    config = files(configFile)

    configure.execute(this)
  }

  tasks.withType(Detekt::class.java).configureEach { detekt ->
    with(detekt) {
      // Target version of the generated JVM bytecode. It is used for type resolution.
      jvmTarget = jdkVersion
    }
  }
}

internal val Project.detekt: DetektExtension
  get() = extensions.getByType(DetektExtension::class.java)

private fun Project.detekt(configure: DetektExtension.() -> Unit) =
  (this as ExtensionAware).extensions.configure("detekt", configure)
