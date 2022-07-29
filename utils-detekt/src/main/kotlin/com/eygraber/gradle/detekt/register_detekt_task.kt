package com.eygraber.gradle.detekt

import com.eygraber.gradle.kotlin.kmpSourceSets
import com.eygraber.gradle.tasks.dependsOn
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.DetektReport
import io.gitlab.arturbosch.detekt.extensions.DetektReports
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File
import java.util.Locale

public fun Project.registerDetektKmpIntermediateTask(
  intermediateName: String,
  targets: List<KotlinTarget>
) {
  plugins.withType(KotlinMultiplatformPluginWrapper::class.java) {
    val sourceSets = kmpSourceSets

    val mainDetektTask = registerDetektTask(
      name = "${intermediateName}Main",
      sourceSet = sourceSets.getByName("${intermediateName}Main")
    )

    val testDetektTask = registerDetektTask(
      name = "${intermediateName}Test",
      sourceSet = sourceSets.getByName("${intermediateName}Test")
    )

    targets.map { it.name.capitalize() }.forEach { name ->
      mainDetektTask.dependsOn("detekt${name}Main")
      testDetektTask.dependsOn("detekt${name}Test")
    }
  }
}

public fun Project.registerSourceSetDetektTask(
  sourceSetName: String,
  vararg dependsOnSourceSetNames: String
) {
  val sourceSets = kmpSourceSets

  val mainDetektTask = registerDetektTask(
    name = "${sourceSetName}Main",
    sourceSet = sourceSets.getByName("${sourceSetName}Main")
  )

  val testDetektTask = registerDetektTask(
    name = "${sourceSetName}Test",
    sourceSet = sourceSets.getByName("${sourceSetName}Test")
  )

  for(dependsOnSourceSetName in dependsOnSourceSetNames) {
    val capitalizedName = dependsOnSourceSetName.capitalize()
    if(sourceSets.findByName("${dependsOnSourceSetName}Main") != null) {
      mainDetektTask.dependsOn("detekt${capitalizedName}Main")
      testDetektTask.dependsOn("detekt${capitalizedName}Test")
    }
  }
}

// borrowed from:
// https://github.com/detekt/detekt/blob/main/detekt-gradle-plugin/src/main/kotlin/io/gitlab/arturbosch/detekt/internal/DetektMultiplatform.kt
public fun Project.registerDetektTask(
  name: String,
  sourceSet: KotlinSourceSet
): TaskProvider<Detekt> = tasks.register("detekt${name.capitalize()}", Detekt::class.java) {
  val detekt = detekt

  debug = detekt.debug
  parallel = detekt.parallel
  disableDefaultRuleSets = detekt.disableDefaultRuleSets
  buildUponDefaultConfig = detekt.buildUponDefaultConfig
  @Suppress("DEPRECATION")
  failFast = detekt.failFast
  autoCorrect = detekt.autoCorrect
  config.setFrom(detekt.config)
  ignoreFailures = detekt.ignoreFailures
  detekt.basePath?.let { basePath = it }
  allRules = detekt.allRules

  setSource(sourceSet.kotlin.sourceDirectories)
  setReportOutputConventions(reports, detekt, name)
  description = "Run detekt analysis for source set $name"
}

private fun Project.setReportOutputConventions(reports: DetektReports, detekt: DetektExtension, name: String) {
  setReportOutputConvention(detekt, reports.xml, name, "xml")
  setReportOutputConvention(detekt, reports.html, name, "html")
  setReportOutputConvention(detekt, reports.txt, name, "txt")
  setReportOutputConvention(detekt, reports.sarif, name, "sarif")
  setReportOutputConvention(detekt, reports.md, name, "md")
}

private fun Project.setReportOutputConvention(
  extension: DetektExtension,
  report: DetektReport,
  name: String,
  format: String
) {
  report.outputLocation.convention(
    layout.projectDirectory.file(
      providers.provider {
        File(extension.reportsDir, "$name.$format").absolutePath
      }
    )
  )
}

private fun String.capitalize(): String = capitalize(Locale.US)
