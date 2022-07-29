import com.eygraber.gradle.kotlin.configureKgp
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

val libs = the<LibrariesForLibs>()

configureKgp(
  jdkVersion = libs.versions.jdk,
  explicitApiMode = ExplicitApiMode.Strict
)
