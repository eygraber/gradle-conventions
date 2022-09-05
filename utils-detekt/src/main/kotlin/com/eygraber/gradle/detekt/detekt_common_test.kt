package com.eygraber.gradle.detekt

import com.eygraber.gradle.kotlin.kmp.kmpSourceSets
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

public fun Project.registerCommonTestDetektTask() {
  plugins.withType(KotlinMultiplatformPluginWrapper::class.java) {
    registerDetektTask(
      name = "metadataTest",
      sourceSet = kmpSourceSets.getByName("commonTest")
    )
  }
}
