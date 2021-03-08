package io.snyk.maven;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Runs Snyk CLI {@code test} command.
 */
@Mojo(
  name = SnykTestMojo.GOAL_NAME,
  defaultPhase = LifecyclePhase.TEST,
  threadSafe = true
)
public class SnykTestMojo extends AbstractSnykMojo {

  static final String GOAL_NAME = "test";

  @Override
  void doExecute() {
    getLog().info("");

    getLog().info("snyk.token: " + getToken());
    getLog().info("snyk.args: " + getArgs());
    getLog().info("snyk.cli.executable: " + getCliExecutable());

    getLog().info("");
  }
}
