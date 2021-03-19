package io.snyk.snyk_maven_plugin.goal;

import io.snyk.snyk_maven_plugin.command.CommandLine;
import io.snyk.snyk_maven_plugin.command.CommandRunner;
import io.snyk.snyk_maven_plugin.download.ExecutableDownloader;
import io.snyk.snyk_maven_plugin.download.Platform;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.nio.file.Path;

import static io.snyk.snyk_maven_plugin.download.Installer.getInstallLocation;

public class SnykMojoExecutor implements MojoExecutor {

    private final SnykMojo mojo;

    public SnykMojoExecutor(SnykMojo mojo) {
        this.mojo = mojo;
    }

    @Override
    public void execute() throws MojoFailureException, MojoExecutionException {
        if (mojo.shouldSkip()) {
            mojo.getLog().info("snyk " + mojo.getCommand().commandName() + " skipped");
            return;
        }

        int exitCode = executeCommand();
        if (exitCode != 0) {
            throw new MojoFailureException("snyk command exited with non-zero exit code (" + exitCode + "). See output for details.");
        }
    }

    private int executeCommand() throws MojoExecutionException {
        try {
            ProcessBuilder commandLine = CommandLine.asProcessBuilder(
                mojo.getExecutable().orElseGet(this::downloadExecutable).getAbsolutePath(),
                mojo.getCommand(),
                mojo.getApiToken(),
                mojo.getArguments(),
                mojo.supportsColor()
            );
            Log log = mojo.getLog();
            return CommandRunner.run(commandLine::start, log::info, log::error);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private File downloadExecutable() {
        Platform platform = mojo.getPlatform();
        String version = mojo.getDownloadVersion();
        Path directory = getInstallLocation(
            platform,
            mojo.getHomeDirectory(),
            mojo.getEnvironmentVariables()
        );
        return ExecutableDownloader.download(directory, platform, version);
    }
}
