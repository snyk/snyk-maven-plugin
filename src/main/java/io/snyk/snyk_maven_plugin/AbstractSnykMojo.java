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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public abstract class AbstractSnykMojo extends AbstractMojo {

    @Parameter(property = "apiToken")
    protected String apiToken;

    @Parameter(property = "cli")
    protected CLI cli;

    @Parameter(property = "args")
    protected List<String> args;

    public void execute() throws MojoFailureException, MojoExecutionException {
        int exitCode = executeCommand();
        if (exitCode != 0) {
            throw new MojoFailureException("snyk command exited with non-zero exit code (" + exitCode + "). See output for details.");
        }
    }

    public int executeCommand() throws MojoExecutionException {
        try {
            ProcessBuilder commandLine = CommandLine.asProcessBuilder(
                getExecutable().getAbsolutePath(),
                getCommand(),
                Optional.ofNullable(apiToken),
                args
            );
            Log log = getLog();
            return CommandRunner.run(commandLine::start, log::info, log::error);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private File getExecutable() {
        return Optional.ofNullable(cli)
            .map(CLI::getExecutable)
            .orElseGet(this::downloadExecutable);
    }

    private File downloadExecutable() {
        Platform platform = Platform.current();
        String version = Optional.ofNullable(cli)
            .map(CLI::getVersion)
            .orElse(CLIVersions.LATEST_VERSION_KEYWORD);
        Path destination = Installer.getInstallLocation(
            platform,
            Optional.ofNullable(System.getProperty("user.home")).map(Paths::get),
            System.getenv()
        );
        return ExecutableDownloader.download(destination, platform, version);
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
