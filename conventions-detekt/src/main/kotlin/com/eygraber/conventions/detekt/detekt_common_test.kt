package com.eygraber.conventions.detekt

import com.eygraber.conventions.kotlin.kmp.kmpSourceSets
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

public fun Project.registerCommonTestDetektTask() {
  plugins.withType(KotlinMultiplatformPluginWrapper::class.java) {
    try {
      tasks.named("detektMetadataTest")
    }
    catch(_: UnknownTaskException) {
      // only register the task if it hasn't already been registered
      registerDetektTask(
        name = "metadataTest",
        sourceSet = kmpSourceSets.getByName("commonTest")
      )
    }
  }
}
