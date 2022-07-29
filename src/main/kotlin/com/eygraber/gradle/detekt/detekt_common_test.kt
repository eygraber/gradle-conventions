package com.eygraber.gradle.detekt

import com.eygraber.gradle.kotlin.kmpSourceSets
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

public fun Project.registerCommonTestDetektTask() {
  plugins.withType(KotlinMultiplatformPluginWrapper::class.java) {
    registerDetektTask(
      name = "metadataTest",
      sourceSet = kmpSourceSets.getByName("commonTest"),
      detektExtension = detekt
    )
  }
}
