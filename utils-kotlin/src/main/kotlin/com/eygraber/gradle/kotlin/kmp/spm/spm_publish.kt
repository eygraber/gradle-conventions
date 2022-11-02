package com.eygraber.gradle.kotlin.kmp.spm

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.com.google.common.base.CaseFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkTask
import org.jetbrains.kotlin.konan.target.HostManager
import java.io.File

public fun Project.registerPublishSpmToMavenTasks(
  frameworkName: String,
  artifactVersion: String,
  zipOutputDirectory: Provider<Directory> = layout.buildDirectory.dir("outputs/spm/release"),
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
    artifactVersion = artifactVersion,
    packageDotSwiftFile = packageDotSwiftFile,
    assembleXCFrameworkReleaseTask = assembleTaskHolder.release,
    zipOutputDirectory = zipOutputDirectory
  )
}

private fun Project.registerPublishDebugSpm(
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

private fun Project.registerPublishReleaseSpm(
  frameworkName: String,
  artifactVersion: String,
  packageDotSwiftFile: File,
  assembleXCFrameworkReleaseTask: TaskProvider<XCFrameworkTask>,
  zipOutputDirectory: Provider<Directory>
) {
  val zipTask = registerZipFatXCFrameworkTask(
    frameworkName = frameworkName,
    assembleXCFrameworkReleaseTask = assembleXCFrameworkReleaseTask,
    outputDirectory = zipOutputDirectory
  )

  val publishTask = createFatXCFrameworkMavenPublication(
    frameworkName = frameworkName,
    artifactVersion = artifactVersion,
    zipTask = zipTask
  )

  tasks.register("publish${frameworkName}ReleaseSPM") {
    group = "spm"

    onlyIf { HostManager.hostIsMac }

    val zipFile = zipTask.flatMap { it.archiveFile }
    val artifactName = "${CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, frameworkName)}-${project.name}"
    val artifactUrl = publishTask.map {
      "${it.repository.url}/${rootProject.name}/$artifactName/$artifactVersion/$artifactName-$artifactVersion.zip"
    }
    inputs.property("artifactUrl", artifactUrl)
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
        |            url: "${artifactUrl.get()}",
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
