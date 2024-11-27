package com.eygraber.conventions.detekt

import com.eygraber.conventions.tasks.dependsOn
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.DetektReport
import io.gitlab.arturbosch.detekt.extensions.DetektReports
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File
import java.util.Locale

public fun Project.configureDetektForMultiplatform(
  targets: Collection<KotlinTarget>,
  sourceSets: Set<KotlinSourceSet>,
) {
  val detektAll = tasks.register("detektAll")

  val ancestorSourceSetsUsedForTypeResolution = HashSet<KotlinSourceSet>()
  val targetSourceSets = HashSet<KotlinSourceSet>()
  val targetSourceSetsWithoutAncestorSourceSets = HashSet<KotlinSourceSet>()

  // for some reason the metadata target doesn't
  // have a compilation that includes the commonTest source set
  // https://slack-chats.kotlinlang.org/t/22308802/is-it-expected-that-the-metadata-kmp-target-doesn-t-have-com
  sourceSets.find { it.name == "commonTest" }?.let { targetSourceSets += it }

  targets.forEach { target ->
    val nameForTask = target.name.capitalize()

    target.compilations.forEach { compilation ->
      val compilationName = compilation.name.capitalize()
      val ancestorSourceSets = compilation.ancestorSourceSets
      targetSourceSets.addAll(compilation.kotlinSourceSets)

      if(ancestorSourceSets.isEmpty()) {
        targetSourceSetsWithoutAncestorSourceSets.addAll(compilation.kotlinSourceSets)
      }
      else if(ancestorSourceSets.size == 1 && ancestorSourceSets.first().name in setOf("commonMain", "commonTest")) {
        targetSourceSetsWithoutAncestorSourceSets.addAll(compilation.kotlinSourceSets)
      }

      if(target.isTypeResolutionSupported) {
        if(ancestorSourceSets.isNotEmpty()) {
          includeAncestorSourcesInTargetDetektTask(
            taskName = "${DetektPlugin.DETEKT_TASK_NAME}${nameForTask}$compilationName",
            ancestorSourceSets = ancestorSourceSets,
            sourceSetsUsedForTypeResolution = ancestorSourceSetsUsedForTypeResolution,
          )
        }
      }
    }
  }

  val sourceSetToDependantTaskProviders = HashMap<KotlinSourceSet, HashSet<TaskProvider<*>>>()

  sourceSets.forEach { sourceSet ->
    val taskName = sourceSet.taskName
    val taskProvider = when(sourceSet) {
      in targetSourceSets -> runCatching { tasks.named(taskName) }.getOrNull()?.also {
        detektAll.dependsOn(taskName)
      }

      in ancestorSourceSetsUsedForTypeResolution -> tasks.register(taskName).also {
        detektAll.dependsOn(taskName)
      }

      else -> registerDetektTask(
        name = sourceSet.name,
        sourceSet = sourceSet,
      ).also {
        detektAll.dependsOn(taskName)
      }
    }

    if(taskProvider != null) {
      sourceSet.dependsOn.forEach { sourceSetDependency ->
        sourceSetToDependantTaskProviders.getOrPut(sourceSetDependency) { HashSet() }.add(taskProvider)
      }
    }
  }

  sourceSetToDependantTaskProviders.forEach { (sourceSet, dependentTaskProviders) ->
    val taskName = sourceSet.taskName

    tasks.named(taskName).configure {
      dependentTaskProviders.forEach { dependentTaskProvider ->
        dependsOn(dependentTaskProvider)
      }
    }
  }
}

// borrowed from:
// https://github.com/detekt/detekt/blob/main/detekt-gradle-plugin/src/main/kotlin/io/gitlab/arturbosch/detekt/internal/DetektMultiplatform.kt
public fun Project.registerDetektTask(
  name: String,
  sourceSet: KotlinSourceSet,
): TaskProvider<Detekt> = tasks.register("detekt${name.capitalize()}", Detekt::class.java) {
  val detekt = detekt

  debug = detekt.debug
  parallel = detekt.parallel
  disableDefaultRuleSets = detekt.disableDefaultRuleSets
  buildUponDefaultConfig = detekt.buildUponDefaultConfig
  autoCorrect = detekt.autoCorrect
  config.setFrom(detekt.config)
  ignoreFailures = detekt.ignoreFailures
  detekt.basePath?.let { detektBasePath -> basePath = detektBasePath }
  allRules = detekt.allRules

  setSource(sourceSet.kotlin.sourceDirectories)
  setReportOutputConventions(reports, detekt, name)
  description = "Run detekt analysis for source set $name"
}

private val KotlinCompilation<*>.ancestorSourceSets: Set<KotlinSourceSet> get() = allKotlinSourceSets - kotlinSourceSets

private val KotlinTarget.isTypeResolutionSupported: Boolean
  get() = platformType in setOf(
    KotlinPlatformType.androidJvm,
    KotlinPlatformType.jvm,
  )

private fun Project.includeAncestorSourcesInTargetDetektTask(
  taskName: String,
  ancestorSourceSets: Set<KotlinSourceSet>,
  sourceSetsUsedForTypeResolution: MutableSet<KotlinSourceSet>,
) {
  tasks.named<Detekt>(taskName).configure {
    ancestorSourceSets.forEach { ancestorSourceSet ->
      if(sourceSetsUsedForTypeResolution.add(ancestorSourceSet)) {
        source(ancestorSourceSet.kotlin.sourceDirectories)
      }
    }
  }
}

private val KotlinSourceSet.taskName
  get() = when(name) {
    "commonMain" -> "${DetektPlugin.DETEKT_TASK_NAME}MetadataMain"
    "commonTest" -> "${DetektPlugin.DETEKT_TASK_NAME}MetadataTest"
    else -> "${DetektPlugin.DETEKT_TASK_NAME}${name.capitalize()}"
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
  format: String,
) {
  report.outputLocation.convention(
    layout.projectDirectory.file(
      providers.provider {
        File(extension.reportsDir, "$name.$format").absolutePath
      },
    ),
  )
}

@Suppress("Deprecation")
private fun String.capitalize(): String = capitalize(Locale.US)
