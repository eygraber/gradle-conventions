package com.eygraber.conventions.kotlin

import BinaryType
import KmpTarget
import com.eygraber.conventions.gradleConventionsKmpDefaultsService
import org.gradle.api.Project
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import javax.inject.Inject

interface GradleConventionsKmp {
  var createCommonJsSourceSet: Boolean
  var webOptions: KmpTarget.WebOptions

  var binaryType: BinaryType

  fun allTargets()
  fun targets(target: KmpTarget, vararg targets: KmpTarget)
}

internal abstract class GradleConventionsKmpDefaults : BuildService<BuildServiceParameters.None>, GradleConventionsKmp {
  override var createCommonJsSourceSet: Boolean = true

  override var binaryType: BinaryType = BinaryType.Library
  override var webOptions = KmpTarget.WebOptions()

  internal var targets = mutableSetOf<KmpTarget>()

  override fun allTargets() {
    targets = mutableSetOf(
      KmpTarget.Android,
      KmpTarget.AndroidNative,
      KmpTarget.Ios,
      KmpTarget.Js,
      KmpTarget.Jvm,
      KmpTarget.Linux,
      KmpTarget.Macos,
      KmpTarget.Mingw,
      KmpTarget.Tvos,
      KmpTarget.WasmJs,
      KmpTarget.WasmWasi,
      KmpTarget.Watchos,
    )
  }

  override fun targets(target: KmpTarget, vararg targets: KmpTarget) {
    this.targets.add(target)
    this.targets.addAll(targets)
  }
}

abstract class GradleConventionsKmpDefaultExtension @Inject constructor(
  project: Project,
) : GradleConventionsKmp by project.gradleConventionsKmpDefaultsService
