import com.eygraber.conventions.detekt.registerDetektKmpIntermediateTask
import com.eygraber.conventions.detekt.registerSourceSetDetektTask
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.tooling.core.toKotlinVersion

fun KotlinMultiplatformExtension.allKmpTargets(
  project: Project,
  isWasmLeafModule: Boolean = false,
  wasmModuleName: String? = null,
  jsBrowser: Boolean = true,
  jsNode: Boolean = true,
  isJsLeafModule: Boolean = false,
  jsModuleName: String? = null,
  createJsWasmSourceSetIfApplicable: Boolean = true,
  requireAtLeastOneTarget: Boolean = true,
  useDefaultTargetHierarchy: Boolean = true
) {
  kmpTargets(
    project = project,
    android = true,
    androidNative = true,
    jvm = true,
    ios = true,
    macos = true,
    tvos = true,
    watchos = true,
    linux = true,
    windows = true,
    wasm = true,
    isWasmLeafModule = isWasmLeafModule,
    wasmModuleName = wasmModuleName,
    js = true,
    jsBrowser = jsBrowser,
    jsNode = jsNode,
    isJsLeafModule = isJsLeafModule,
    jsModuleName = jsModuleName,
    createJsWasmSourceSetIfApplicable = createJsWasmSourceSetIfApplicable,
    requireAtLeastOneTarget = requireAtLeastOneTarget,
    useDefaultTargetHierarchy = useDefaultTargetHierarchy
  )
}

fun KotlinMultiplatformExtension.kmpTargets(
  project: Project,
  android: Boolean = false,
  androidNative: Boolean = false,
  jvm: Boolean = false,
  ios: Boolean = false,
  macos: Boolean = false,
  tvos: Boolean = false,
  watchos: Boolean = false,
  linux: Boolean = false,
  windows: Boolean = false,
  wasm: Boolean = false,
  isWasmLeafModule: Boolean = false,
  wasmModuleName: String? = null,
  js: Boolean = false,
  jsBrowser: Boolean = true,
  jsNode: Boolean = true,
  isJsLeafModule: Boolean = false,
  jsModuleName: String? = null,
  createJsWasmSourceSetIfApplicable: Boolean = true,
  requireAtLeastOneTarget: Boolean = true,
  useDefaultTargetHierarchy: Boolean = true
) {
  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  require(project.kotlinToolingVersion.toKotlinVersion().isAtLeast(major = 1, minor = 9)) {
    "A minimum Kotlin version of 1.9.0 is required to use kmpTargets"
  }

  val apple = ios || macos || tvos || watchos

  if(requireAtLeastOneTarget) {
    check(android || androidNative || jvm || apple || linux || windows || js || wasm) {
      "At least one of android, jvm, ios, macos, tvos, watchos, linux, windows, js, or wasm needs to be set to true"
    }
  }

  if(useDefaultTargetHierarchy) {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    targetHierarchy.default {
      if(createJsWasmSourceSetIfApplicable) {
        group("jsWasm") {
          withJs()
          withWasm()
        }
      }
    }
  }

  if(android) {
    androidTarget {
      publishAllLibraryVariants()
    }
  }

  if(androidNative) {
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
  }

  if(apple) {
    project.afterEvaluate {
      project.registerSourceSetDetektTask("apple", "ios", "macos", "tvos", "watchos")
    }

    if(ios) {
      val targets = listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
      )

      project.registerDetektKmpIntermediateTask(intermediateName = "ios", targets)
    }

    if(macos) {
      val targets = listOf(
        macosX64(),
        macosArm64()
      )

      project.registerDetektKmpIntermediateTask(intermediateName = "macos", targets)
    }

    if(tvos) {
      val targets = listOf(
        tvosX64(),
        tvosArm64(),
        tvosSimulatorArm64()
      )

      project.registerDetektKmpIntermediateTask(intermediateName = "tvos", targets)
    }

    if(watchos) {
      val targets = listOf(
        watchosX64(),
        watchosArm32(),
        watchosArm64(),
        watchosDeviceArm64(),
        watchosSimulatorArm64(),
      )

      project.registerDetektKmpIntermediateTask(intermediateName = "watchos", targets)
    }
  }

  if(linux) {
    val targets = listOf(
      linuxX64(),
      linuxArm64()
    )

    project.registerDetektKmpIntermediateTask(intermediateName = "linux", targets)
  }

  if(windows) {
    project.registerDetektKmpIntermediateTask(intermediateName = "windows", listOf(mingwX64()))
  }

  if(wasm) {
    @OptIn(ExperimentalWasmDsl::class)
    wasm {
      if(wasmModuleName != null) {
        moduleName = wasmModuleName
      }

      browser {
        if(isWasmLeafModule) {
          binaries.executable()
        }
      }
    }
  }

  if(js) {
    js(IR) {
      if(jsModuleName != null) {
        moduleName = jsModuleName
      }

      if(jsBrowser) {
        browser {
          if(isJsLeafModule) {
            binaries.executable()
          }
        }
      }

      if(jsNode) {
        nodejs {
          if(isJsLeafModule) {
            binaries.executable()
          }
        }
      }
    }
  }

  if(js && wasm) {
    project.registerDetektKmpIntermediateTask(
      intermediateName = "jsWasm",
      targets = listOf(targets.getByName("js"), targets.getByName("wasm"))
    )
  }

  if(jvm) {
    jvm()
  }
}
