import com.eygraber.conventions.detekt.registerDetektKmpIntermediateTask
import com.eygraber.conventions.detekt.registerSourceSetDetektTask
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.tooling.core.toKotlinVersion

enum class BinaryType {
  Library,
  Executable
}

fun KotlinMultiplatformExtension.allKmpTargets(
  project: Project,
  jsBrowser: Boolean = true,
  jsNode: Boolean = true,
  jsModuleName: String? = null,
  wasmJsBrowser: Boolean = true,
  wasmJsNode: Boolean = true,
  wasmJsModuleName: String? = null,
  binaryType: BinaryType = BinaryType.Library,
  createCommonJsSourceSet: Boolean = true,
  requireAtLeastOneTarget: Boolean = true
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
    mingw = true,
    wasmJs = true,
    wasmWasi = true,
    js = true,
    jsBrowser = jsBrowser,
    jsNode = jsNode,
    jsModuleName = jsModuleName,
    wasmJsBrowser = wasmJsBrowser,
    wasmJsNode = wasmJsNode,
    wasmJsModuleName = wasmJsModuleName,
    binaryType = binaryType,
    createCommonJsSourceSet = createCommonJsSourceSet,
    requireAtLeastOneTarget = requireAtLeastOneTarget
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
  mingw: Boolean = false,
  wasmJs: Boolean = false,
  wasmWasi: Boolean = false,
  js: Boolean = false,
  jsBrowser: Boolean = true,
  jsNode: Boolean = true,
  jsModuleName: String? = null,
  wasmJsBrowser: Boolean = true,
  wasmJsNode: Boolean = true,
  wasmJsModuleName: String? = null,
  binaryType: BinaryType = BinaryType.Library,
  createCommonJsSourceSet: Boolean = true,
  requireAtLeastOneTarget: Boolean = true
) {
  require(project.kotlinToolingVersion.toKotlinVersion().isAtLeast(major = 1, minor = 9, patch = 20)) {
    "A minimum Kotlin version of 1.9.20 is required to use kmpTargets"
  }

  val apple = ios || macos || tvos || watchos
  val wasm = wasmJs || wasmWasi

  if(requireAtLeastOneTarget) {
    require(android || androidNative || jvm || apple || linux || mingw || js || wasm) {
      "At least one target needs to be set to true"
    }
  }

  if(createCommonJsSourceSet) {
    createCommonJs(project)
  }

  if(android) {
    project.plugins.withId("com.android.library") {
      androidTarget {
        publishAllLibraryVariants()
      }
    }

    project.plugins.withId("com.android.application") {
      androidTarget {
        publishAllLibraryVariants()
      }
    }
  }

  if(androidNative) {
    androidNativeArm32 {
      if(binaryType == BinaryType.Executable) {
        binaries.executable()
      }
    }

    androidNativeArm64 {
      if(binaryType == BinaryType.Executable) {
        binaries.executable()
      }
    }

    androidNativeX86 {
      if(binaryType == BinaryType.Executable) {
        binaries.executable()
      }
    }

    androidNativeX64 {
      if(binaryType == BinaryType.Executable) {
        binaries.executable()
      }
    }
  }

  if(apple) {
    project.afterEvaluate {
      project.registerSourceSetDetektTask("apple", "ios", "macos", "tvos", "watchos")
    }

    if(ios) {
      val targets = listOf(
        iosX64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        },
        iosArm64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        },
        iosSimulatorArm64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        }
      )

      project.registerDetektKmpIntermediateTask(intermediateName = "ios", targets)
    }

    if(macos) {
      val targets = listOf(
        macosX64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        },
        macosArm64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        }
      )

      project.registerDetektKmpIntermediateTask(intermediateName = "macos", targets)
    }

    if(tvos) {
      val targets = listOf(
        tvosX64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        },
        tvosArm64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        },
        tvosSimulatorArm64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        }
      )

      project.registerDetektKmpIntermediateTask(intermediateName = "tvos", targets)
    }

    if(watchos) {
      val targets = listOf(
        watchosX64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        },
        watchosArm32 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        },
        watchosArm64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        },
        watchosDeviceArm64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        },
        watchosSimulatorArm64 {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        },
      )

      project.registerDetektKmpIntermediateTask(intermediateName = "watchos", targets)
    }
  }

  if(linux) {
    val targets = listOf(
      linuxX64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      },
      linuxArm64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }
    )

    project.registerDetektKmpIntermediateTask(intermediateName = "linux", targets)
  }

  if(mingw) {
    val targets = listOf(
      mingwX64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }
    )

    project.registerDetektKmpIntermediateTask(intermediateName = "mingw", targets)
  }

  if(wasmJs || wasmWasi) {
    if(wasmJs) {
      @OptIn(ExperimentalWasmDsl::class)
      wasmJs {
        if(wasmJsModuleName != null) {
          moduleName = wasmJsModuleName
        }

        if(wasmJsBrowser) {
          browser {
            if(binaryType == BinaryType.Executable) {
              binaries.executable()
            }
          }
        }

        if(wasmJsNode) {
          nodejs {
            if(binaryType == BinaryType.Executable) {
              binaries.executable()
            }
          }
        }
      }
    }

    if(wasmWasi) {
      @OptIn(ExperimentalWasmDsl::class)
      wasmWasi {
        if(binaryType == BinaryType.Executable) {
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
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        }
      }

      if(jsNode) {
        nodejs {
          if(binaryType == BinaryType.Executable) {
            binaries.executable()
          }
        }
      }
    }
  }

  if(jvm) {
    jvm()
  }
}

private fun KotlinMultiplatformExtension.createCommonJs(
  project: Project
) {
  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  applyDefaultHierarchyTemplate {
    group("commonJs") {
      withJs()
      withWasm()
    }
  }

  sourceSets.register("commonJsMain")
  sourceSets.register("commonJsTest")

  project.registerDetektKmpIntermediateTask(
    intermediateName = "commonJs",
    targets = listOfNotNull(targets.findByName("js"), targets.findByName("wasmJs"))
  )
}
