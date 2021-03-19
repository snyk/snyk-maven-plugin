package io.snyk.snyk_maven_plugin.download;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class Installer {

    public static Path getInstallLocation(Platform platform, Optional<Path> homeDirectory, Map<String, String> env)
    throws MissingContextException {
        switch (platform) {
            case MAC_OS: {
                return homeDirectory
                    .map(home -> home.resolve("Library/Application Support/Snyk"))
                    .orElseThrow(() -> new MissingContextException("macOS needs a home directory."));
            }
            case WINDOWS: {
                return Optional.ofNullable(env.get("APPDATA"))
                    .map(Paths::get)
                    .map(appData -> appData.resolve("Snyk"))
                    .orElseThrow(() -> new MissingContextException("Windows needs APPDATA directory."));
            }
            case LINUX:
            case LINUX_ALPINE: {
                return Optional.ofNullable(env.get("XDG_DATA_HOME"))
                    .map(Paths::get)
                    .map(xdgHome -> xdgHome.resolve("snyk"))
                    .map(Optional::of)
                    .orElseGet(() -> homeDirectory.map(home -> home.resolve(".local/share/snyk")))
                    .orElseThrow(() -> new MissingContextException("Linux needs XDG_DATA_HOME or home directory."));
            }
            default: {
                throw new MissingContextException("Unsupported platform (" + platform + ").");
            }
        }
    }

    public static class MissingContextException extends RuntimeException {
        public MissingContextException(String message) {
            super(message);
        }
    }

}