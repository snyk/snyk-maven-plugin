package io.snyk.snyk_maven_plugin.goal;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public interface MojoExecutor {
    void execute() throws MojoFailureException, MojoExecutionException;
}
