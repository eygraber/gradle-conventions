package com.eygraber.conventions

public object Env {
  public val isGitHubActions: Boolean = System.getenv("CI") == "true"
  public val isCI: Boolean = isGitHubActions
}
