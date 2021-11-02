package io.snyk.snyk_maven_plugin.goal;

import io.snyk.snyk_maven_plugin.command.Command;
import io.snyk.snyk_maven_plugin.command.CommandLine;
import io.snyk.snyk_maven_plugin.command.CommandRunner;
import io.snyk.snyk_maven_plugin.command.CommandRunner.LineLogger;
import io.snyk.snyk_maven_plugin.download.ExecutableDownloader;
import io.snyk.snyk_maven_plugin.download.FileDownloader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

            String executablePath = mojo.getExecutable()
                .orElseGet(this::downloadExecutable)
                .getAbsolutePath();

            log.info("Snyk Executable Path: " + executablePath);
            log.info("Snyk CLI Version:     " + getVersion(executablePath));

            ProcessBuilder commandLine = CommandLine.asProcessBuilder(
                executablePath,
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

    private String getVersion(String executablePath) {
        ProcessBuilder versionCommandLine = CommandLine.asProcessBuilder(
            executablePath,
            Command.VERSION,
            Optional.empty(),
            emptyList(),
            false
        );
        List<String> stdout = new ArrayList<>();
        LineLogger ignore = line -> {};
        CommandRunner.run(versionCommandLine::start, stdout::add, ignore);
        return String.join("", stdout).trim();
    }

    private File downloadExecutable() {
        return ExecutableDownloader.ensure(
            mojo.getDownloadUrl(),
            mojo.getDownloadDestination(),
            mojo.getUpdatePolicy(),
            FileDownloader::downloadFile
        );
    }

}
