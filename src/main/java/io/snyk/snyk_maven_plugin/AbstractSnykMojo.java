package io.snyk.snyk_maven_plugin;

import io.snyk.snyk_maven_plugin.command.Command;
import io.snyk.snyk_maven_plugin.command.CommandLine;
import io.snyk.snyk_maven_plugin.command.CommandRunner;
import io.snyk.snyk_maven_plugin.download.CLIVersions;
import io.snyk.snyk_maven_plugin.download.ExecutableDownloader;
import io.snyk.snyk_maven_plugin.download.Installer;
import io.snyk.snyk_maven_plugin.download.Platform;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractSnykMojo extends AbstractMojo {

    @Parameter(property = "apiToken")
    protected String apiToken;

    @Parameter(property = "cli")
    protected CLI cli;

    @Parameter(property = "args")
    protected List<String> args;

    public void execute() throws MojoFailureException, MojoExecutionException {
        ProcessBuilder commandLine = CommandLine.asProcessBuilder(
            this.getExecutable().getAbsolutePath(),
            this.getCommand(),
            Optional.ofNullable(apiToken),
            args
        );

        CommandRunner.run(commandLine::start, getLog());
    }

    private File getExecutable() throws MojoExecutionException {
        try {
            return Optional.ofNullable(cli)
                .map(CLI::getExecutable)
                .orElseGet(() -> {
                    try {
                        String versionToDownload = Optional.ofNullable(cli)
                                .map(CLI::getVersion).orElse(CLIVersions.LATEST_VERSION_KEYWORD);
                        Platform platform = Platform.current();
                        Path targetFolder = Installer.getInstallLocation(platform, Optional.ofNullable(System.getProperty("user.home")).map(Paths::get), System.getenv());
                        return ExecutableDownloader.download(targetFolder, Platform.current(), versionToDownload);
                    } catch (IOException | MojoExecutionException e) {
                        throw new RuntimeException("failed to download executable", e);
                    }
                });
        } catch (RuntimeException e) {
            throw new MojoExecutionException("failed to get executable", e);
        }
    }

    public abstract Command getCommand();

    public static class CLI {

        @Parameter(property = "executable")
        private File executable;

        @Parameter(property = "version")
        private String version;

        public File getExecutable() {
            return executable;
        }

        public String getVersion() {
            return version;
        }

    }
}
