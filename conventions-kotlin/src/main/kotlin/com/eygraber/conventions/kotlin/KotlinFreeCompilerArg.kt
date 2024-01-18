package com.eygraber.conventions.kotlin

public sealed class KotlinFreeCompilerArg(public val value: String) {
  public object AllowExpectActualClasses : KotlinFreeCompilerArg("-Xexpect-actual-classes")
  public class Unknown(value: String) : KotlinFreeCompilerArg(value)
}
