package io.snyk.snyk_maven_plugin.goal;

import io.snyk.snyk_maven_plugin.command.Command;
import io.snyk.snyk_maven_plugin.download.*;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.utils.logging.MessageUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

public abstract class SnykMojo extends ComposedMojo {

    @Parameter
    private String apiToken;

    @Parameter
    private List<String> args;

    @Parameter(property = "snyk.skip")
    private boolean skip;

    @Parameter
    private CLI cli;

    public static class CLI {

        @Parameter
        private File executable;

        @Parameter
        private String version;

        @Parameter
        private String updatePolicy;

        @Parameter
        private File downloadDestination;

    }

    private final boolean color;
    private final Platform platform;
    private final MojoExecutor executor;

    protected SnykMojo() {
        color = MessageUtils.isColorEnabled();
        platform = Platform.current();
        executor = new SnykMojoExecutor(this);
    }

    public List<String> getArguments() {
        return Optional.ofNullable(args).orElse(emptyList());
    }

    public Optional<String> getApiToken() {
        return Optional.ofNullable(apiToken);
    }

    public Optional<File> getExecutable() {
        return Optional.ofNullable(cli)
            .map(cli -> cli.executable);
    }

    public String getDownloadVersion() {
        return Optional.ofNullable(cli)
            .map(cli -> cli.version)
            .map(CLIVersions::sanitize)
            .orElse(CLIVersions.LATEST_VERSION_KEYWORD);
    }

    public boolean shouldSkip() {
        return skip;
    }

    public boolean supportsColor() {
        return color;
    }

    public File getDownloadDestination() {


        return Optional.ofNullable(cli).map(cli -> cli.downloadDestination).orElseGet(() -> {
            Map<String, String> environmentVariables = System.getenv();
            Optional<Path> homeDirectory = Optional.ofNullable(System.getProperty("user.home")).map(Paths::get);
            return ExecutableDestination.getDownloadDestination(
                platform,
                homeDirectory,
                environmentVariables
            );
        });
    }

    public URL getDownloadUrl() {
        return ExecutableDownloader.getDownloadUrl(platform, getDownloadVersion());
    }

    public String getUpdatePolicy() {
        if (!getDownloadVersion().equals(CLIVersions.LATEST_VERSION_KEYWORD)) {
            return UpdatePolicy.ALWAYS;
        }
        return Optional.ofNullable(cli)
            .map(cli -> cli.updatePolicy)
            .orElse(UpdatePolicy.DAILY);
    }

    @Override
    public MojoExecutor getExecutor() {
        return executor;
    }

    public abstract Command getCommand();

}
