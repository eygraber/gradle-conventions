package com.eygraber.conventions.kotlin.kmp.spm

import com.eygraber.conventions.capitalize
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.jetbrains.kotlin.com.google.common.base.CaseFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager

public fun Project.registerPublishSpmToMavenTasks(
  frameworkName: String,
  artifactVersion: String,
  zipOutputDirectory: Provider<Directory> = layout.buildDirectory.dir("outputs/spm/release"),
  targetPredicate: (KotlinNativeTarget) -> Boolean = { true }
) {
  registerPublishSpm(
    frameworkName = frameworkName,
    zipOutputDirectory = zipOutputDirectory,
    publishTaskFactory = { zipTask ->
      val publishTask = createXCFrameworkMavenPublication(
        frameworkName = frameworkName,
        artifactVersion = artifactVersion,
        zipTask = zipTask
      )

      val artifactName =
        "${CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, frameworkName)}-${project.name}"

      tasks.register("publish${frameworkName}ToMaven", PublishXCFrameworkTask::class.java) {
        dependsOn(publishTask)

        publishedUrl.set(
          publishTask.map { t ->
            "${t.repository.url}/${rootProject.name}/$artifactName/$artifactVersion/$artifactName-$artifactVersion.zip"
          }
        )
      }
    },
    targetPredicate = targetPredicate
  )
}

internal fun Project.createXCFrameworkMavenPublication(
  frameworkName: String,
  artifactVersion: String,
  zipTask: TaskProvider<Zip>
): TaskProvider<PublishToMavenRepository> {
  val publicationName = "${frameworkName.capitalize()}ReleaseXCFramework"
  val artifactName = "${CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, frameworkName)}-${project.name}"

  val publishTasks = with(extensions.getByType(PublishingExtension::class.java)) {
    publications.create(publicationName, MavenPublication::class.java) {
      version = artifactVersion
      artifactId = artifactName
      artifact(zipTask.flatMap { task -> task.archiveFile })
    }

    repositories
      .filterIsInstance<MavenArtifactRepository>()
      .mapNotNull { repo ->
        runCatching {
          tasks.named(
            "publish${publicationName}PublicationTo${repo.name.capitalize()}Repository",
            PublishToMavenRepository::class.java
          )
        }.getOrNull()
      }
  }

  require(publishTasks.size == 1) {
    "There are too many publishing tasks for $publicationName; you may have too many Maven repositories defined."
  }

  return requireNotNull(publishTasks.firstOrNull()) {
    "There are no publishing tasks for $publicationName; do you have any remote Maven repositories defined?"
  }.also { task ->
    task.configure {
      onlyIf { HostManager.hostIsMac }
    }
  }
}
