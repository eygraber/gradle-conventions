package com.eygraber.conventions.kotlin.kmp.spm

import com.eygraber.conventions.capitalize
import com.eygraber.conventions.kotlin.kotlinMultiplatform
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkTask
import org.jetbrains.kotlin.konan.target.HostManager
import java.util.Locale

public class AssembleXCFrameworkTaskHolder(
  public val debug: TaskProvider<XCFrameworkTask>,
  public val release: TaskProvider<XCFrameworkTask>,
)

public fun Project.registerAssembleXCFrameworkTasksFromFrameworks(
  frameworkName: String,
  targetPredicate: (KotlinNativeTarget) -> Boolean = { true },
): AssembleXCFrameworkTaskHolder {
  plugins.withId("org.jetbrains.kotlin.multiplatform") {
    val xcFrameworkConfig = XCFramework(frameworkName)

    kotlinMultiplatform
      .targets
      .withType(KotlinNativeTarget::class.java)
      .filter { target -> target.konanTarget.family.isAppleFamily }
      .filter(targetPredicate)
      .flatMap { target -> target.binaries.filterIsInstance<Framework>() }
      .forEach { framework ->
        xcFrameworkConfig.add(framework)
      }
  }

  return AssembleXCFrameworkTaskHolder(
    debug = findXCFrameworkAssembleTask(frameworkName, NativeBuildType.DEBUG),
    release = findXCFrameworkAssembleTask(frameworkName, NativeBuildType.RELEASE),
  )
}

public fun Project.registerZipXCFrameworkTask(
  frameworkName: String,
  assembleXCFrameworkReleaseTask: TaskProvider<XCFrameworkTask>,
  outputDirectory: Provider<Directory>,
): TaskProvider<Zip> =
  tasks.register("zip${frameworkName}ReleaseXCFramework", Zip::class.java) {
    group = "spm"

    onlyIf { HostManager.hostIsMac }

    dependsOn(assembleXCFrameworkReleaseTask)

    from(assembleXCFrameworkReleaseTask.map { task -> task.outputs.files })
    into("$frameworkName.xcframework")

    destinationDirectory.set(outputDirectory)
    archiveFileName.set("$frameworkName.xcframework.zip")
  }

private fun Project.findXCFrameworkAssembleTask(
  frameworkName: String,
  buildType: NativeBuildType,
): TaskProvider<XCFrameworkTask> {
  @Suppress("Deprecation")
  val buildTypeString = buildType.name.toLowerCase(Locale.US).capitalize()

  val taskName = when(project.name) {
    frameworkName -> "assemble${buildTypeString}XCFramework"
    else -> "assemble${frameworkName.capitalize()}${buildTypeString}XCFramework"
  }

  return tasks.named(taskName, XCFrameworkTask::class.java)
}
