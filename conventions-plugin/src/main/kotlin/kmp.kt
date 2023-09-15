import com.eygraber.conventions.detekt.registerDetektKmpIntermediateTask
import com.eygraber.conventions.detekt.registerSourceSetDetektTask
import com.eygraber.conventions.kotlin.kmp.createNestedSharedSourceSetForTargets
import com.eygraber.conventions.kotlin.kmp.createSharedSourceSet
import com.eygraber.conventions.kotlin.kmp.jsMain
import com.eygraber.conventions.kotlin.kmp.jsTest
import com.eygraber.conventions.kotlin.kmp.wasmMain
import com.eygraber.conventions.kotlin.kmp.wasmTest
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.tooling.core.toKotlinVersion

fun KotlinMultiplatformExtension.kmpTargets(
  project: Project,
  android: Boolean = false,
  jvm: Boolean = false,
  ios: Boolean = false,
  macos: Boolean = false,
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
  if(project.kotlinToolingVersion.toKotlinVersion().isAtLeast(major = 1, minor = 9)) {
    kmpTargets19(
      project = project,
      android = android,
      jvm = jvm,
      ios = ios,
      macos = macos,
      wasm = wasm,
      isWasmLeafModule = isWasmLeafModule,
      wasmModuleName = wasmModuleName,
      js = js,
      jsBrowser = jsBrowser,
      jsNode = jsNode,
      isJsLeafModule = isJsLeafModule,
      jsModuleName = jsModuleName,
      requireAtLeastOneTarget = requireAtLeastOneTarget,
      useDefaultTargetHierarchy = useDefaultTargetHierarchy
    )
  }
  else {
    kmpTargetsPre19(
      project = project,
      android = android,
      jvm = jvm,
      ios = ios,
      macos = macos,
      wasm = wasm,
      isWasmLeafModule = isWasmLeafModule,
      wasmModuleName = wasmModuleName,
      js = js,
      jsBrowser = jsBrowser,
      jsNode = jsNode,
      isJsLeafModule = isJsLeafModule,
      jsModuleName = jsModuleName,
      createJsWasmSourceSetIfApplicable = createJsWasmSourceSetIfApplicable,
      requireAtLeastOneTarget = requireAtLeastOneTarget
    )
  }
}

private fun KotlinMultiplatformExtension.kmpTargets19(
  project: Project,
  android: Boolean = false,
  jvm: Boolean = false,
  ios: Boolean = false,
  macos: Boolean = false,
  wasm: Boolean = false,
  isWasmLeafModule: Boolean = false,
  wasmModuleName: String? = null,
  js: Boolean = false,
  jsBrowser: Boolean = true,
  jsNode: Boolean = true,
  isJsLeafModule: Boolean = false,
  jsModuleName: String? = null,
  requireAtLeastOneTarget: Boolean = true,
  useDefaultTargetHierarchy: Boolean = true
) {
  if(requireAtLeastOneTarget) {
    check(android || jvm || ios || macos || js || wasm) {
      "At least one of android, jvm, ios, macos, js, or wasm needs to be set to true"
    }
  }

  if(useDefaultTargetHierarchy) {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    targetHierarchy.default {
      group("jsWasm") {
        withJs()
        withWasm()
      }
    }
  }

  if(android) {
    androidTarget {
      publishAllLibraryVariants()
    }
  }

  if(ios || macos) {
    project.afterEvaluate {
      project.registerSourceSetDetektTask("apple", "ios", "macos")
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

private fun KotlinMultiplatformExtension.kmpTargetsPre19(
  project: Project,
  android: Boolean = false,
  jvm: Boolean = false,
  ios: Boolean = false,
  macos: Boolean = false,
  wasm: Boolean = false,
  isWasmLeafModule: Boolean = false,
  wasmModuleName: String? = null,
  js: Boolean = false,
  jsBrowser: Boolean = true,
  jsNode: Boolean = true,
  isJsLeafModule: Boolean = false,
  jsModuleName: String? = null,
  createJsWasmSourceSetIfApplicable: Boolean = true,
  requireAtLeastOneTarget: Boolean = true
) {
  if(requireAtLeastOneTarget) {
    check(android || jvm || ios || macos || js || wasm) {
      "At least one of android, jvm, ios, macos, js, or wasm needs to be set to true"
    }
  }

  if(android) {
    @Suppress("DEPRECATION")
    android {
      publishAllLibraryVariants()
    }
  }

  if(ios || macos) {
    createSharedSourceSet(
      project = project,
      name = "apple"
    )
    project.registerSourceSetDetektTask("apple", "ios", "macos")

    if(ios) {
      val targets = listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
      )

      createNestedAppleSharedSourceSet(
        project = project,
        name = "ios",
        targets = targets
      )
    }

    if(macos) {
      val targets = listOf(
        macosX64(),
        macosArm64()
      )

      createNestedAppleSharedSourceSet(
        project = project,
        name = "macos",
        targets = targets
      )
    }
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

  if(createJsWasmSourceSetIfApplicable && js && wasm) {
    createSharedSourceSet(
      project = project,
      name = "jsWasm"
    )

    sourceSets.jsMain.get().dependsOn(sourceSets.getByName("jsWasmMain"))
    sourceSets.jsTest.get().dependsOn(sourceSets.getByName("jsWasmTest"))
    sourceSets.wasmMain.get().dependsOn(sourceSets.getByName("jsWasmMain"))
    sourceSets.wasmTest.get().dependsOn(sourceSets.getByName("jsWasmTest"))

    project.registerDetektKmpIntermediateTask(
      intermediateName = "jsWasm",
      targets = listOf(targets.getByName("js"), targets.getByName("wasm"))
    )
  }

  if(jvm) {
    jvm()
  }
}

private fun KotlinMultiplatformExtension.createNestedAppleSharedSourceSet(
  project: Project,
  name: String,
  targets: List<KotlinNativeTarget>
) {
  createNestedSharedSourceSetForTargets(
    project = project,
    name = name,
    targets = targets,
    parentSourceSetName = "apple"
  ) { target ->
    target.compilations.all {
      compilerOptions.options.freeCompilerArgs.addAll(
        listOf("-linker-options", "-application_extension")
      )
    }
  }

  project.registerDetektKmpIntermediateTask(name, targets)
}
