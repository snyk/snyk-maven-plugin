package io.snyk.snyk_maven_plugin.goal;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class ComposedMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoFailureException, MojoExecutionException {
        getExecutor().execute();
    }

    public abstract MojoExecutor getExecutor();

}
