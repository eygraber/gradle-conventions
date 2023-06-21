import com.eygraber.conventions.detekt.registerDetektKmpIntermediateTask
import com.eygraber.conventions.detekt.registerSourceSetDetektTask
import com.eygraber.conventions.kotlin.kmp.createNestedSharedSourceSetForTargets
import com.eygraber.conventions.kotlin.kmp.createSharedSourceSet
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

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
  isJsLeafModule: Boolean = false,
  jsModuleName: String? = null,
  requireAtLeastOneTarget: Boolean = true
) {
  if(requireAtLeastOneTarget) {
    check(android || jvm || ios || macos || js) {
      "At least one of android, jvm, ios, macos, or js needs to be set to true"
    }
  }

  if(android) {
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

      browser {
        if(isJsLeafModule) {
          binaries.executable()
        }
      }
    }
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
