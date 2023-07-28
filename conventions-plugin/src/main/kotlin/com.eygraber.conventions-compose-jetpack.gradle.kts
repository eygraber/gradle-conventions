import com.eygraber.conventions.gradleConventionsExtension
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.android.build.gradle.BasePlugin as AndroidBasePlugin

plugins {
  id("com.eygraber.conventions-compose")
}

plugins.withType<AndroidBasePlugin> {
  android {
    buildFeatures.compose = true
  }
}

gradleConventionsExtension.awaitComposeConfigured {
  if(enableAndroidCompilerMetrics) {
    val output = project.layout.buildDirectory.dir("compose_metrics").get().asFile

    tasks.withType<KotlinCompile>().configureEach {
      compilerOptions.freeCompilerArgs.addAll(
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$output",
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$output"
      )
    }
  }
}
