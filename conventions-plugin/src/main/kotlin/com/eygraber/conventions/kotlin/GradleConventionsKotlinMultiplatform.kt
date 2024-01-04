package com.eygraber.conventions.kotlin

import BinaryType

class GradleConventionsKotlinMultiplatform {
  var shouldCreateCommonJsSourceSet: Boolean = true

  var binaryType: BinaryType = BinaryType.Library
  var webOptions = KmpTarget.WebOptions(
    isNodeEnabled = true,
    isBrowserEnabled = true,
    isLibraryBrowserTestsEnabled = false,
    moduleName = null
  )

  internal var targets = mutableSetOf<KmpTarget>()

  fun targets(target: KmpTarget, vararg targets: KmpTarget) {
    this.targets.add(target)
    this.targets.addAll(targets)
  }
}

sealed interface KmpTarget {
  object Android : KmpTarget
  object AndroidNative : KmpTarget
  object Ios : KmpTarget
  object Js : KmpTarget
  object Jvm : KmpTarget
  object Linux : KmpTarget
  object Macos : KmpTarget
  object Mingw : KmpTarget
  object Tvos : KmpTarget
  object WasmJs : KmpTarget
  object WasmWasi : KmpTarget
  object Watchos : KmpTarget

  data class WebOptions(
    val isNodeEnabled: Boolean,
    val isBrowserEnabled: Boolean,
    val isLibraryBrowserTestsEnabled: Boolean,
    val moduleName: String? = null
  )
}
