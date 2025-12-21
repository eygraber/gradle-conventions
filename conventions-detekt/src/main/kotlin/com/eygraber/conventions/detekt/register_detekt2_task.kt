package com.eygraber.conventions.detekt

import com.eygraber.conventions.capitalize
import com.eygraber.conventions.tasks.dependsOn
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.extensions.DetektReport
import dev.detekt.gradle.extensions.DetektReports
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File

public fun Project.configureDetekt2ForMultiplatform(
  targets: Collection<KotlinTarget>,
  sourceSets: Set<KotlinSourceSet>,
) {
  val detektAll = tasks.register("detektAll")
  sourceSets.forEach { sourceSet ->
    detektAll.dependsOn("detekt${sourceSet.name.capitalize()}SourceSet")
  }
  detektAll.dependsOn("detektMetadataTest")

  // targets that support type resolution
  targets
    .filter { target -> target.platformType == KotlinPlatformType.jvm }
    .forEach { target ->
      target.compilations.configureEach { compilation ->
        detektAll.dependsOn("detekt${compilation.name.capitalize()}${target.name.capitalize()}")
      }
    }
}

// borrowed from:
// https://github.com/detekt/detekt/blob/main/detekt-gradle-plugin/src/main/kotlin/io/gitlab/arturbosch/detekt/internal/DetektMultiplatform.kt
public fun Project.registerDetekt2Task(
  name: String,
  sourceSet: KotlinSourceSet,
): TaskProvider<Detekt> = tasks.register("detekt${name.capitalize()}", Detekt::class.java) { task ->
  val detekt = detekt2

  with(task) {
    debug.set(detekt.debug)
    parallel.set(detekt.parallel)
    disableDefaultRuleSets.set(detekt.disableDefaultRuleSets)
    buildUponDefaultConfig.set(detekt.buildUponDefaultConfig)
    autoCorrect.set(detekt.autoCorrect)
    config.setFrom(detekt.config)
    ignoreFailures.set(detekt.ignoreFailures)
    basePath.set(detekt.basePath.map { it.asFile.absolutePath })
    allRules.set(detekt.allRules)

    setSource(sourceSet.kotlin.sourceDirectories)
    setReportOutputConventions2(reports, detekt, name)
    description = "Run detekt analysis for source set $name"
  }
}

private fun Project.setReportOutputConventions2(reports: DetektReports, detekt: DetektExtension, name: String) {
  setReportOutputConvention2(
    extension = detekt,
    report = reports.checkstyle,
    name = name,
    format = "xml",
  )

  setReportOutputConvention2(
    extension = detekt,
    report = reports.html,
    name = name,
    format = "html",
  )

  setReportOutputConvention2(
    extension = detekt,
    report = reports.sarif,
    name = name,
    format = "sarif",
  )

  setReportOutputConvention2(
    extension = detekt,
    report = reports.markdown,
    name = name,
    format = "md",
  )
}

private fun Project.setReportOutputConvention2(
  extension: DetektExtension,
  report: DetektReport,
  name: String,
  format: String,
) {
  report.outputLocation.convention(
    layout.projectDirectory.file(
      extension.reportsDir.map { reportsDir ->
        File(reportsDir.asFile, "$name.$format").absolutePath
      },
    ),
  )
}

private val Project.detekt2: DetektExtension
  get() = extensions.getByType(DetektExtension::class.java)
