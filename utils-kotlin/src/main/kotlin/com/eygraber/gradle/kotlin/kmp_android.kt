package com.eygraber.gradle.kotlin

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

public fun Project.removeUnusedKmpAndroidSourceSets() {
  afterEvaluate {
    extensions.findByType(KotlinMultiplatformExtension::class.java)?.let { kmpExt ->
      kmpExt.sourceSets.removeAll { sourceSet ->
        sourceSet.name == "androidAndroidTestRelease" ||
          sourceSet.name.startsWith("androidTestFixtures")
      }
    }
  }
}
