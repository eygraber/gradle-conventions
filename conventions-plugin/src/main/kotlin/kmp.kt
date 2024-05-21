import com.eygraber.conventions.detekt.configureDetektForMultiplatform
import com.eygraber.conventions.gradleConventionsKmpDefaultsService
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.tooling.core.toKotlinVersion

enum class BinaryType {
  Library,
  Executable,
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
    val isNodeEnabled: Boolean = true,
    val isBrowserEnabled: Boolean = false,
    val isBrowserEnabledForLibraryTests: Boolean = false,
    val isBrowserEnabledForExecutables: Boolean = true,
    val moduleName: String? = null,
  )
}

fun KotlinMultiplatformExtension.defaultKmpTargets(
  project: Project,
  webOptions: KmpTarget.WebOptions = project.gradleConventionsKmpDefaultsService.webOptions,
  binaryType: BinaryType = project.gradleConventionsKmpDefaultsService.binaryType,
  createCommonJsSourceSet: Boolean = project.gradleConventionsKmpDefaultsService.createCommonJsSourceSet,
  applyDefaultHierarchy: Boolean = true,
) {
  val defaultTargets = project.gradleConventionsKmpDefaultsService.targets
  require(defaultTargets.isNotEmpty()) {
    "defaultKmpTargets doesn't have any effect if default targets haven't been specified in the root project"
  }

  kmpTargets(
    target = defaultTargets.take(1).first(),
    project = project,
    webOptions = webOptions,
    binaryType = binaryType,
    createCommonJsSourceSet = createCommonJsSourceSet,
    ignoreDefaultTargets = false,
    applyDefaultHierarchy = applyDefaultHierarchy,
  )
}

fun KotlinMultiplatformExtension.allKmpTargets(
  project: Project,
  webOptions: KmpTarget.WebOptions = project.gradleConventionsKmpDefaultsService.webOptions,
  binaryType: BinaryType = project.gradleConventionsKmpDefaultsService.binaryType,
  createCommonJsSourceSet: Boolean = project.gradleConventionsKmpDefaultsService.createCommonJsSourceSet,
  applyDefaultHierarchy: Boolean = true,
) {
  val isBrowserEnabled = when(binaryType) {
    BinaryType.Executable -> webOptions.isBrowserEnabled || webOptions.isBrowserEnabledForExecutables
    BinaryType.Library -> webOptions.isBrowserEnabled || webOptions.isBrowserEnabledForLibraryTests
  }

  configureAllKmpTargets(
    project = project,
    jsBrowser = isBrowserEnabled,
    jsModuleName = webOptions.moduleName,
    jsNode = webOptions.isNodeEnabled,
    wasmJsBrowser = isBrowserEnabled,
    wasmJsModuleName = webOptions.moduleName,
    wasmJsNode = webOptions.isNodeEnabled,
    binaryType = binaryType,
    createCommonJsSourceSet = createCommonJsSourceSet,
    applyDefaultHierarchy = applyDefaultHierarchy,
  )
}

fun KotlinMultiplatformExtension.kmpTargets(
  target: KmpTarget,
  vararg targets: KmpTarget,
  project: Project,
  webOptions: KmpTarget.WebOptions = project.gradleConventionsKmpDefaultsService.webOptions,
  binaryType: BinaryType = project.gradleConventionsKmpDefaultsService.binaryType,
  createCommonJsSourceSet: Boolean = project.gradleConventionsKmpDefaultsService.createCommonJsSourceSet,
  ignoreDefaultTargets: Boolean = false,
  applyDefaultHierarchy: Boolean = true,
) {
  val finalTargets = when {
    ignoreDefaultTargets -> setOf(target) + setOf(*targets)
    else -> project.gradleConventionsKmpDefaultsService.targets + setOf(target) + setOf(*targets)
  }

  if(finalTargets.isNotEmpty()) {
    val isBrowserEnabled = when(binaryType) {
      BinaryType.Executable -> webOptions.isBrowserEnabled || webOptions.isBrowserEnabledForExecutables
      BinaryType.Library -> webOptions.isBrowserEnabled || webOptions.isBrowserEnabledForLibraryTests
    }

    configureKmpTargets(
      project = project,
      android = KmpTarget.Android in finalTargets,
      androidNative = KmpTarget.AndroidNative in finalTargets,
      ios = KmpTarget.Ios in finalTargets,
      jvm = KmpTarget.Jvm in finalTargets,
      js = KmpTarget.Js in finalTargets,
      jsBrowser = isBrowserEnabled,
      jsModuleName = webOptions.moduleName,
      jsNode = webOptions.isNodeEnabled,
      linux = KmpTarget.Linux in finalTargets,
      macos = KmpTarget.Macos in finalTargets,
      mingw = KmpTarget.Mingw in finalTargets,
      tvos = KmpTarget.Tvos in finalTargets,
      wasmJs = KmpTarget.WasmJs in finalTargets,
      wasmJsBrowser = isBrowserEnabled,
      wasmJsModuleName = webOptions.moduleName,
      wasmJsNode = webOptions.isNodeEnabled,
      wasmWasi = KmpTarget.WasmWasi in finalTargets,
      watchos = KmpTarget.Watchos in finalTargets,
      binaryType = binaryType,
      createCommonJsSourceSet = createCommonJsSourceSet,
      applyDefaultHierarchy = applyDefaultHierarchy,
    )
  }
}

fun KotlinMultiplatformExtension.configureAllKmpTargets(
  project: Project,
  jsBrowser: Boolean = true,
  jsNode: Boolean = true,
  jsModuleName: String? = null,
  wasmJsBrowser: Boolean = true,
  wasmJsNode: Boolean = true,
  wasmJsModuleName: String? = null,
  binaryType: BinaryType = BinaryType.Library,
  createCommonJsSourceSet: Boolean = true,
  applyDefaultHierarchy: Boolean = true,
) {
  configureKmpTargets(
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
    requireAtLeastOneTarget = true,
    applyDefaultHierarchy = applyDefaultHierarchy,
  )
}

fun KotlinMultiplatformExtension.configureKmpTargets(
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
  requireAtLeastOneTarget: Boolean = true,
  applyDefaultHierarchy: Boolean = true,
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

  if(applyDefaultHierarchy) {
    applyDefaultHierarchyTemplate()
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
    if(ios) {
      iosX64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }

      iosArm64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }

      iosSimulatorArm64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }
    }

    if(macos) {
      macosX64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }

      macosArm64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }
    }

    if(tvos) {
      tvosX64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }

      tvosArm64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }

      tvosSimulatorArm64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }
    }

    if(watchos) {
      watchosX64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }

      watchosArm32 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }

      watchosArm64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }

      watchosDeviceArm64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }

      watchosSimulatorArm64 {
        if(binaryType == BinaryType.Executable) {
          binaries.executable()
        }
      }
    }
  }

  if(linux) {
    linuxX64 {
      if(binaryType == BinaryType.Executable) {
        binaries.executable()
      }
    }

    linuxArm64 {
      if(binaryType == BinaryType.Executable) {
        binaries.executable()
      }
    }
  }

  if(mingw) {
    mingwX64 {
      if(binaryType == BinaryType.Executable) {
        binaries.executable()
      }
    }
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

  if(createCommonJsSourceSet) {
    createJsHierarchyGroups(
      isBrowserEnabled = jsBrowser,
    )
  }

  project.afterEvaluate {
    project.configureDetektForMultiplatform(
      targets = targets,
      sourceSets = sourceSets,
    )
  }
}

private fun KotlinMultiplatformExtension.createJsHierarchyGroups(
  isBrowserEnabled: Boolean,
) {
  val js = targets.findByName("js")
  val wasmJs = targets.findByName("wasmJs")

  if(js != null || wasmJs != null) {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
      common {
        group("commonJs") {
          withJs()
          withWasmJs()
        }

        if(isBrowserEnabled) {
          group("web") {
            withJs()
            withWasmJs()
          }
        }
      }
    }
  }
}

/**
 * Provides the existing [commonJsMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.commonJsMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("commonJsMain")

/**
 * Provides the existing [commonJsTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.commonJsTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("commonJsTest")

/**
 * Provides the existing [webMain][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.webMain: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("webMain")

/**
 * Provides the existing [webTest][KotlinSourceSet] element.
 */
public val NamedDomainObjectContainer<KotlinSourceSet>.webTest: NamedDomainObjectProvider<KotlinSourceSet>
  get() = named<KotlinSourceSet>("webTest")
