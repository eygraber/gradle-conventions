package com.eygraber.gradle.kotlin.kmp

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * [Fixed in Kotlin 1.7.20](https://youtrack.jetbrains.com/issue/KT-48436)
 */
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

/**
 * apply this hack for Android Studio because AGP and KGP aren't handling it correctly yet
 *
 * [Kotlin Slack](https://kotlinlang.slack.com/archives/C3PQML5NU/p1652777328313809)
 *
 * [Issue Tracker](https://issuetracker.google.com/issues/231701341)
 */
public fun KotlinMultiplatformExtension.androidStudioHackForCommonMain() {
  sourceSets.configureEach {
    if(name == "androidMain") {
      kotlin.srcDir("src/commonMain/kotlin")
    }
  }
}
