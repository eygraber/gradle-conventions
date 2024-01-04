import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import com.eygraber.conventions.kotlin.KmpTarget
import com.eygraber.conventions.kotlin.kotlinMultiplatform

plugins {
  kotlin("multiplatform")
  id("com.eygraber.conventions-kotlin-library")
}

val kotlinMultiplatformDefaults = gradleConventionsDefaultsService.kotlinMultiplatform

with(gradleConventionsExtension) {
  with(kotlinMultiplatform) {
    shouldCreateCommonJsSourceSet = kotlinMultiplatformDefaults.shouldCreateCommonJsSourceSet
    binaryType = kotlinMultiplatformDefaults.binaryType
    webOptions = kotlinMultiplatformDefaults.webOptions
    targets = kotlinMultiplatformDefaults.targets
  }

  awaitKotlinConfigured {
    with(kotlinMultiplatform) {
      val isLibraryBrowserTestsEnabled = binaryType == BinaryType.Library && webOptions.isLibraryBrowserTestsEnabled

      project.kotlinMultiplatform.kmpTargets(
        project = project,
        android = KmpTarget.Android in targets,
        androidNative = KmpTarget.AndroidNative in targets,
        ios = KmpTarget.Ios in targets,
        jvm = KmpTarget.Jvm in targets,
        js = KmpTarget.Js in targets,
        jsBrowser = isLibraryBrowserTestsEnabled || webOptions.isBrowserEnabled,
        jsModuleName = webOptions.moduleName,
        jsNode = webOptions.isNodeEnabled,
        linux = KmpTarget.Linux in targets,
        macos = KmpTarget.Macos in targets,
        mingw = KmpTarget.Mingw in targets,
        tvos = KmpTarget.Tvos in targets,
        wasmJs = KmpTarget.WasmJs in targets,
        wasmJsBrowser = isLibraryBrowserTestsEnabled || webOptions.isBrowserEnabled,
        wasmJsModuleName = webOptions.moduleName,
        wasmJsNode = webOptions.isNodeEnabled,
        wasmWasi = KmpTarget.WasmWasi in targets,
        watchos = KmpTarget.Watchos in targets,
        createCommonJsSourceSetIfApplicable = shouldCreateCommonJsSourceSet
      )
    }
  }
}
