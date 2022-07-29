import com.eygraber.gradle.detekt.configureDetekt
import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
  id("io.gitlab.arturbosch.detekt")
}

configureDetekt(
  jdkVersion = libs.versions.jdk
)

@Suppress("UnstableApiUsage")
dependencies {
  detektPlugins(libs.detekt)
  detektPlugins(libs.detektEygraber.formatting)
  detektPlugins(libs.detektEygraber.style)
}
