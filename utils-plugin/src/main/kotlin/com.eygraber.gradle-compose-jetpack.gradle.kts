import org.gradle.kotlin.dsl.withType
import com.android.build.gradle.BasePlugin as AndroidBasePlugin

plugins {
  id("com.eygraber.gradle-compose")
}

plugins.withType<AndroidBasePlugin> {
  android {
    @Suppress("UnstableApiUsage")
    buildFeatures.compose = true
  }
}
