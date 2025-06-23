package com.eygraber.conventions.kotlin

public sealed class KotlinFreeCompilerArg(public val value: String) {
  public object AllowExpectActualClasses : KotlinFreeCompilerArg("-Xexpect-actual-classes")
  public class Unknown(value: String) : KotlinFreeCompilerArg(value)
}

public enum class WarningLevel(val value: String) {
  Disabled("disabled"),
  Warning("warning"),
  Error("error"),
}

public data class KotlinWarningLevel(
  public val diagnosticName: String,
  public val level: WarningLevel,
) : KotlinFreeCompilerArg("-Xwarning-level=$diagnosticName:${level.value}")
