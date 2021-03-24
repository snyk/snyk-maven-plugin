package io.snyk.snyk_maven_plugin.goal;

import io.snyk.snyk_maven_plugin.command.Command;
import io.snyk.snyk_maven_plugin.command.CommandLine;
import io.snyk.snyk_maven_plugin.command.CommandRunner;
import io.snyk.snyk_maven_plugin.download.ExecutableDownloader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.Optional;

import static java.util.Collections.emptyList;

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
            Log log = mojo.getLog();

            String snykExecutablePath = mojo.getExecutable().orElseGet(this::downloadExecutable).getAbsolutePath();
            log.info("snyk executable path: " + snykExecutablePath);

            ProcessBuilder versionCommandLine = CommandLine.asProcessBuilder(
                snykExecutablePath,
                Command.VERSION,
                Optional.empty(),
                emptyList(),
                mojo.supportsColor()
            );
            log.info("Snyk CLI version:");
            CommandRunner.run(versionCommandLine::start, log::info, log::error);

            ProcessBuilder commandLine = CommandLine.asProcessBuilder(
                snykExecutablePath,
                mojo.getCommand(),
                mojo.getApiToken(),
                mojo.getArguments(),
                mojo.supportsColor()
            );
            return CommandRunner.run(commandLine::start, log::info, log::error);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private File downloadExecutable() {
        return ExecutableDownloader.download(
            mojo.getDownloadUrl(),
            mojo.getDownloadDestination(),
            mojo.getUpdatePolicy()
        );
    }
}
