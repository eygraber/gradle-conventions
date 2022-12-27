package com.eygraber.gradle.kotlin

public sealed class KotlinOptIn(public val value: String) {
  public object ExperimentalCoroutines : KotlinOptIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
  public object ExperimentalTime : KotlinOptIn("kotlin.time.ExperimentalTime")
  public object FlowPreview : KotlinOptIn("kotlinx.coroutines.FlowPreview")
  public object JsExport : KotlinOptIn("kotlin.js.ExperimentalJsExport")
  public object RequiresOptIn : KotlinOptIn("kotlin.RequiresOptIn")
  public class Unknown(value: String) : KotlinOptIn(value)
}
