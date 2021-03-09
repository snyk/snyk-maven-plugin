package io.snyk;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.util.List;


@Mojo(name = "test")
public class SnykTestMojo extends AbstractMojo {
    @Parameter(property = "apiToken")
    private String apiToken;

    @Parameter(property = "cli")
    private CLI cli;

    @Parameter(property = "args")
    private List<String> args;

    public void execute() throws MojoFailureException, MojoExecutionException {
        try {
            Runner.run(getLog(), cli.getExecutable(), "test");
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("command execution failed", e);
        }
    }
}
