package com.eygraber.gradle.kotlin.kmp.spm

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkTask
import org.jetbrains.kotlin.konan.target.HostManager
import java.io.File

public abstract class PublishXCFrameworkTask : DefaultTask() {
  @get:Internal
  public abstract val publishedUrl: Property<String>
}

public fun Project.registerPublishSpm(
  frameworkName: String,
  zipOutputDirectory: Provider<Directory> = layout.buildDirectory.dir("outputs/spm/release"),
  publishTaskFactory: (TaskProvider<Zip>) -> TaskProvider<PublishXCFrameworkTask>,
  targetPredicate: (KotlinNativeTarget) -> Boolean = { true }
) {
  val assembleTaskHolder = registerAssembleXCFrameworkTasksFromFrameworks(frameworkName, targetPredicate)
  val packageDotSwiftFile = rootProject.file("Package.swift")

  registerPublishDebugSpm(
    frameworkName = frameworkName,
    packageDotSwiftFile = packageDotSwiftFile,
    assembleXCFrameworkDebugTask = assembleTaskHolder.debug
  )

  registerPublishReleaseSpm(
    frameworkName = frameworkName,
    packageDotSwiftFile = packageDotSwiftFile,
    assembleXCFrameworkReleaseTask = assembleTaskHolder.release,
    publishTaskFactory = publishTaskFactory,
    zipOutputDirectory = zipOutputDirectory
  )
}

internal fun Project.registerPublishDebugSpm(
  frameworkName: String,
  packageDotSwiftFile: File,
  assembleXCFrameworkDebugTask: TaskProvider<XCFrameworkTask>,
) {
  val rootFile = rootDir

  tasks.register("publish${frameworkName}DebugSPM") {
    group = "spm"

    onlyIf { HostManager.hostIsMac }

    val xcFrameworkDir = assembleXCFrameworkDebugTask.map { task -> task.outputs.files.first() }
    inputs.files(xcFrameworkDir)

    outputs.files(packageDotSwiftFile)

    doLast {
      packageDotSwiftFile.writeText(
        """
        |// swift-tools-version:5.3
        |import PackageDescription
        |
        |let packageName = "$frameworkName"
        |
        |let package = Package(
        |    name: packageName,
        |    platforms: [
        |        .iOS(.v13)
        |    ],
        |    products: [
        |        .library(
        |            name: packageName,
        |            targets: [packageName]
        |        ),
        |    ],
        |    targets: [
        |        .binaryTarget(
        |            name: packageName,
        |            path: "./${xcFrameworkDir.get().relativeTo(rootFile).path}"
        |        )
        |        ,
        |    ]
        |)
        """.trimMargin()
      )
    }
  }
}

internal fun Project.registerPublishReleaseSpm(
  frameworkName: String,
  packageDotSwiftFile: File,
  assembleXCFrameworkReleaseTask: TaskProvider<XCFrameworkTask>,
  publishTaskFactory: (TaskProvider<Zip>) -> TaskProvider<PublishXCFrameworkTask>,
  zipOutputDirectory: Provider<Directory>
) {
  val zipTask = registerZipXCFrameworkTask(
    frameworkName = frameworkName,
    assembleXCFrameworkReleaseTask = assembleXCFrameworkReleaseTask,
    outputDirectory = zipOutputDirectory
  )

  val publishTask = publishTaskFactory(zipTask)

  tasks.register("publish${frameworkName}ReleaseSPM") {
    group = "spm"

    onlyIf { HostManager.hostIsMac }

    dependsOn(publishTask)

    val publishedUrl = publishTask.flatMap { task -> task.publishedUrl }
    val zipFile = zipTask.flatMap { task -> task.archiveFile }

    inputs.property("publishedUrl", publishedUrl)
    inputs.files(zipFile)

    outputs.files(packageDotSwiftFile)

    doLast {
      require(publishedUrl.isPresent) {
        "The PublishXCFrameworkTask did not populate the publishedUrl property"
      }

      packageDotSwiftFile.writeText(
        """
        |// swift-tools-version:5.3
        |import PackageDescription
        |
        |let packageName = "$frameworkName"
        |
        |let package = Package(
        |    name: packageName,
        |    platforms: [
        |        .iOS(.v13)
        |    ],
        |    products: [
        |        .library(
        |            name: packageName,
        |            targets: [packageName]
        |        ),
        |    ],
        |    targets: [
        |        .binaryTarget(
        |            name: packageName,
        |            url: "${publishedUrl.get()}",
        |            checksum: "${findSpmChecksum(zipFile.get().asFile)}"
        |        )
        |        ,
        |    ]
        |)
        """.trimMargin()
      )
    }
  }
}
