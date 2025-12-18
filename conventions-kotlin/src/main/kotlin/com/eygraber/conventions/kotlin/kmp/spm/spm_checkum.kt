package com.eygraber.conventions.kotlin.kmp.spm

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File

internal fun Project.findSpmChecksum(zipFile: File): String {
  val packageSwiftFile = rootProject.file("Package.swift")
  val hasPackageSwift = packageSwiftFile.exists()

  if(!hasPackageSwift) {
    packageSwiftFile.writeText("")
  }

  return ProcessBuilder()
    .command(
      "swift",
      "package",
      "compute-checksum",
      zipFile.path,
    )
    .start()
    .let { process ->
      val output = process.inputReader().readText().trim()
      val error = process.errorReader().readText()

      if(output.isBlank()) {
        process.destroy()
        throw GradleException(
          error.ifBlank {
            "Running swift package compute-checksum ${zipFile.path} failed with code ${process.exitValue()}"
          },
        )
      }
      else {
        if(error.isNotBlank()) {
          project.logger.warn("Received an error from compute-checksum: $error")
        }

        output
      }
    }
    .also {
      if(!hasPackageSwift) {
        packageSwiftFile.delete()
      }
    }
}
