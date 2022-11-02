package com.eygraber.gradle.kotlin.kmp.spm

import com.eygraber.gradle.kotlin.kotlinMultiplatform
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

public class AssembleFatXCFrameworkTaskHolder(
  public val debug: TaskProvider<XCFrameworkTask>,
  public val release: TaskProvider<XCFrameworkTask>
)

public fun Project.registerAssembleFatXCFrameworkTask(
  frameworkName: String,
  targetPredicate: (KotlinNativeTarget) -> Boolean = { true }
): AssembleFatXCFrameworkTaskHolder {
  plugins.withId("org.jetbrains.kotlin.multiplatform") {
    val xcFrameworkConfig = XCFramework(frameworkName)

    kotlinMultiplatform
      .targets
      .withType(KotlinNativeTarget::class.java)
      .filter { it.konanTarget.family.isAppleFamily }
      .filter(targetPredicate)
      .flatMap { it.binaries.filterIsInstance<Framework>() }
      .forEach { framework ->
        xcFrameworkConfig.add(framework)
      }
  }

  return AssembleFatXCFrameworkTaskHolder(
    debug = findXCFrameworkAssembleTask(frameworkName, NativeBuildType.DEBUG),
    release = findXCFrameworkAssembleTask(frameworkName, NativeBuildType.RELEASE)
  )
}

public fun Project.registerZipFatXCFrameworkTask(
  frameworkName: String,
  assembleXCFrameworkReleaseTask: TaskProvider<XCFrameworkTask>,
  outputDirectory: Provider<Directory>
): TaskProvider<Zip> =
  tasks.register("zip${frameworkName}ReleaseXCFramework", Zip::class.java) {
    group = "spm"

    onlyIf { HostManager.hostIsMac }

    dependsOn(assembleXCFrameworkReleaseTask)

    from(assembleXCFrameworkReleaseTask.map { it.outputs.files.first() })

    destinationDirectory.set(outputDirectory)
    archiveFileName.set("$frameworkName.xcframework.zip")
  }

private fun Project.findXCFrameworkAssembleTask(
  frameworkName: String,
  buildType: NativeBuildType
): TaskProvider<XCFrameworkTask> {
  val buildTypeString = buildType.name.toLowerCase(Locale.US).capitalize()

  val taskName = when(project.name) {
    frameworkName -> "assemble${buildTypeString}XCFramework"
    else -> "assemble${frameworkName.capitalize()}${buildTypeString}XCFramework"
  }

  return tasks.named(taskName, XCFrameworkTask::class.java)
}
