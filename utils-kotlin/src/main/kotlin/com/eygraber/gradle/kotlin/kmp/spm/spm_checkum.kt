package com.eygraber.gradle.kotlin.kmp.spm

import org.gradle.api.Project
import java.io.File

internal fun Project.findSpmChecksum(zipFile: File): String {
  val packageSwiftFile = file("Package.swift")
  val hadPackageSwift = packageSwiftFile.exists()

  if(!hadPackageSwift) {
    packageSwiftFile.writeText("")
  }

  return ProcessBuilder()
    .command(
      "swift",
      "package",
      "compute-checksum",
      zipFile.path
    )
    .start()
    .inputReader()
    .readText()
    .trim()
    .also {
      if(!hadPackageSwift) {
        packageSwiftFile.delete()
      }
    }
}
