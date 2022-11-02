package com.eygraber.gradle.kotlin.kmp.spm

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkTask
import org.jetbrains.kotlin.konan.target.HostManager
import java.io.File

public interface ReleaseSpmPublisher {
  public val publishedUrl: Provider<String>
}

public fun Project.registerPublishSpm(
  frameworkName: String,
  zipOutputDirectory: Provider<Directory> = layout.buildDirectory.dir("outputs/spm/release"),
  publisherFactory: (TaskProvider<Zip>) -> ReleaseSpmPublisher,
  targetPredicate: (KotlinNativeTarget) -> Boolean = { true }
) {
  val assembleTaskHolder = registerAssembleFatXCFrameworkTask(frameworkName, targetPredicate)
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
    publisherFactory = publisherFactory,
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

    val xcFrameworkDir = assembleXCFrameworkDebugTask.map { it.outputs.files.first() }
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
  publisherFactory: (TaskProvider<Zip>) -> ReleaseSpmPublisher,
  zipOutputDirectory: Provider<Directory>
) {
  val zipTask = registerZipFatXCFrameworkTask(
    frameworkName = frameworkName,
    assembleXCFrameworkReleaseTask = assembleXCFrameworkReleaseTask,
    outputDirectory = zipOutputDirectory
  )

  val publisher = publisherFactory(zipTask)

  tasks.register("publish${frameworkName}ReleaseSPM") {
    group = "spm"

    onlyIf { HostManager.hostIsMac }

    val zipFile = zipTask.flatMap { it.archiveFile }

    inputs.property("publishedUrl", publisher.publishedUrl)
    inputs.files(zipFile)

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
        |            url: "${publisher.publishedUrl.get()}",
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
