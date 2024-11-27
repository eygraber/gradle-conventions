package com.eygraber.conventions

public object Env {
  public val isGitHubActions: Boolean get() = System.getenv("CI") == "true"
  public val isCI: Boolean get() = isGitHubActions
}
