package io.snyk.maven;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import io.snyk.maven.cli.CommandRunner;
import org.apache.maven.plugin.MojoExecutionException;
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
  void doExecute() throws MojoExecutionException {
    // resolve snyk cli path (add method to installer)
    // prepare args
    // run
    // check exit code

    CommandRunner commandRunner = CommandRunner.newInstance(getLog());
    try {
      commandRunner.execute(Arrays.asList("snyk", "test", "--all-projects"));
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new MojoExecutionException("Could not execute Snyk CLI command", ex);
    } catch (IOException | TimeoutException ex) {
      throw new MojoExecutionException("Could not execute Snyk CLI command", ex);
    }
  }
}
