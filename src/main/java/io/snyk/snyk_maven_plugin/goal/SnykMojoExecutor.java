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
import org.apache.maven.project.MavenProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class SnykMojoExecutor implements MojoExecutor {

    private final static int EXIT_CODE_OK = 0;
    private final static int EXIT_CODE_ACTION_NEEDED = 1;

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

        switch (exitCode) {
            case EXIT_CODE_OK:
                break;
            case EXIT_CODE_ACTION_NEEDED:
                if (!mojo.getFailOnIssues()) {
                    mojo.getLog().warn("snyk " + mojo.getCommand().commandName()
                            + " did find issues, but the plugin is configured"
                            + " to not fail in this situation.");
                    break;
                }
            default:
                throw new MojoFailureException("snyk command exited with non-zero exit code ("
                        + exitCode + "). See output for details.");
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

            String outputFilename = Optional.ofNullable(mojo.getOutputFile())
                    .filter(s -> !s.isEmpty())
                    .orElse(null);

            ProcessBuilder commandLine = CommandLine.asProcessBuilder(
                executablePath,
                mojo.getCommand(),
                mojo.getApiToken(),
                mojo.getArguments(),
                mojo.supportsColor() && null == outputFilename
            ).directory(getProjectRootDirectory());

            if (log.isDebugEnabled()) {
                log.debug("Snyk Command: "
                        + String.join(" ", commandLine.command()));
            }

            if (null == outputFilename) {
                return CommandRunner.run(commandLine::start, log::info, log::error);
            }

            File outputFile = new File(outputFilename).getAbsoluteFile();
            ensureDirExists(outputFile.getParentFile());

            try (
                    FileOutputStream outputStream = new FileOutputStream(outputFilename);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                            outputStream, StandardCharsets.UTF_8);
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)
            ) {
                LineLogger infoAndOutputCaptureLineLogger = new CommandRunner.CompositeLineLogger(
                        log::info,
                        l -> {
                            try {
                                bufferedWriter.write(l);
                                bufferedWriter.newLine();
                            }
                            catch (IOException ioe) {
                                throw new UncheckedIOException("unable to write the command output", ioe);
                            }
                        }
                );

                return CommandRunner.run(
                        commandLine::start,
                        infoAndOutputCaptureLineLogger,
                        log::error);
            }
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private File getProjectRootDirectory() {
        MavenProject project = (MavenProject) mojo.getPluginContext().get("project");

        if (project == null) {
            throw new IllegalStateException("the `project` is missing from the plugin context");
        }

        return project.getBasedir();
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

    private static void ensureDirExists(File file) {
        if (null == file) {
            // this will be the case if the file is at the top level such that there
            // is no parent directory.
            return;
        }
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("unable to create the directory [" + file.getAbsolutePath() + "]");
            }
        } else {
            if (!file.isDirectory()) {
                throw new RuntimeException("expected [" + file.getAbsolutePath() + "] to be a directory");
            }
        }
    }

}
