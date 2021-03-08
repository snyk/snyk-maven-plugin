package io.snyk.maven;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.snyk.maven.cli.GitHubDownloader;
import io.snyk.maven.cli.Installer;
import io.snyk.maven.cli.Platform;
import io.snyk.maven.common.MavenProjectProperties;
import io.snyk.maven.common.PropertyNames;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static java.lang.String.format;

/**
 * Base class for all Snyk mojos with all common attributes. Snyk {@link org.apache.maven.plugin.Mojo}s
 * should extend this class and implement
 */
abstract class AbstractSnykMojo extends AbstractMojo {

  /**
   * Configuration for {@code cli} parameter.
   */
  public static class CliConfiguration {
    @Nullable
    @Parameter
    private File executable;

    @Nullable
    @Parameter
    private URL downloadUrl;
  }

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Nullable
  @Parameter(defaultValue = "${session}", readonly = true)
  private MavenSession session;

  @Nullable
  @Parameter(property = PropertyNames.TOKEN)
  private String token;

  @Nullable
  @Parameter(property = PropertyNames.ARGS)
  private List<String> args;

  @Parameter(property = PropertyNames.SKIP)
  private boolean skip;

  @Parameter
  private final CliConfiguration cli = new CliConfiguration();

  @Nullable
  String getToken() {
    String property = MavenProjectProperties.getProperty(PropertyNames.TOKEN, project, session);
    if (property != null) {
      return property;
    }
    return token;
  }

  List<String> getArgs() {
    //TODO(pavel): add list property parsing (comma-separated string to list)
    return args;
  }

  boolean isSkipped() {
    return skip;
  }

  @Nullable
  Path getCliExecutable() {
    String property = MavenProjectProperties.getProperty(PropertyNames.CLI_EXECUTABLE, project, session);
    if (property != null) {
      return Paths.get(property);
    }
    return cli.executable == null ? null : cli.executable.toPath();
  }

  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    if (isSkipped()) {
      getLog().info("Skipping security tests");
      return;
    }

    final Path cliExecutable = getCliExecutable();
    if (cliExecutable != null) {
      getLog().info(format("Using Snyk CLI installation %s", cliExecutable));
    } else {
      Installer installer = new Installer(getLog(), new GitHubDownloader(), Platform.current());
      try {
        installer.performInstallationIfNeeded();
      } catch (IOException ex) {
        throw new MojoExecutionException("Could not install Snyk CLI", ex);
      }
    }

    doExecute();
  }

  abstract void doExecute() throws MojoExecutionException, MojoFailureException;
}
