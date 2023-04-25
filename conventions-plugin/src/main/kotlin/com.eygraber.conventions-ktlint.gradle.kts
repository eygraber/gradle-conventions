import com.eygraber.conventions.gradleConventionsDefaultsService
import com.eygraber.conventions.gradleConventionsExtension
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

plugins {
  id("org.jlleitschuh.gradle.ktlint")
}

val ext = gradleConventionsExtension
val ktlintDefaults = gradleConventionsDefaultsService.ktlint

ext.ktlint.version = ktlintDefaults.version
ext.ktlint.relative = ktlintDefaults.relative
ext.ktlint.verbose = ktlintDefaults.verbose
ext.ktlint.debug = ktlintDefaults.debug
ext.ktlint.android = ktlintDefaults.android
ext.ktlint.outputToConsole = ktlintDefaults.outputToConsole
ext.ktlint.coloredOutput = ktlintDefaults.coloredOutput
ext.ktlint.outputColorName = ktlintDefaults.outputColorName
ext.ktlint.ignoreFailures = ktlintDefaults.ignoreFailures
ext.ktlint.workerMaxHeapSize = ktlintDefaults.workerMaxHeapSize
ext.ktlint.scriptFileTrees.addAll(ktlintDefaults.scriptFileTrees)
ext.ktlint.includes.addAll(ktlintDefaults.includes)
ext.ktlint.excludes.addAll(ktlintDefaults.excludes)

ext.awaitKtlintConfigured {
  ktlint {
    if(ext.ktlint.version != null) {
      version.set(ext.ktlint.version)
    }

    debug.set(ext.ktlint.debug)
    verbose.set(ext.ktlint.verbose)
    android.set(ext.ktlint.android)
    outputToConsole.set(ext.ktlint.outputToConsole)

    if(ext.ktlint.outputColorName != null) {
      outputColorName.set(ext.ktlint.outputColorName)
    }

    ignoreFailures.set(ext.ktlint.ignoreFailures)

    kotlinScriptAdditionalPaths {
      for(include in ext.ktlint.scriptFileTrees) {
        include(fileTree(include))
      }
    }

    filter {
      for(exclude in ext.ktlint.excludes) {
        exclude(exclude)
      }
      for(include in ext.ktlint.includes) {
        include(include)
      }
    }

    dependencies {
      for(dependency in ext.ktlint.ktlintRulesetDependencies) {
        add("ktlintRuleset", dependency)
      }
    }
  }

  if(ext.ktlint.workerMaxHeapSize != null) {
    tasks.withType(BaseKtLintCheckTask::class.java).configureEach {
      workerMaxHeapSize.set(ext.ktlint.workerMaxHeapSize)
    }
  }
}
