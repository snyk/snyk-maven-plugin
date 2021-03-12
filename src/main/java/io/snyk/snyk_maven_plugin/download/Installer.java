package io.snyk.snyk_maven_plugin.download;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class Installer {

    public static Path getInstallLocation(Platform platform, Optional<Path> homeDirectory, Map<String, String> env) throws MissingContextException {
        Path p;
        if (platform == Platform.MAC_OS) {
            if (homeDirectory.isPresent()) {
                p = homeDirectory.get().resolve("Library/Application Support/Snyk");
            } else {
                throw new MissingContextException("homeDirectory is empty but must be set");
            }
        } else if (platform == Platform.WINDOWS) {
            if (env.containsKey("APPDATA")) {
                p = Paths.get(env.getOrDefault("APPDATA", "")).resolve("Snyk");
            } else {
                throw new MissingContextException("APPDATA env var not set");
            }
        } else if (platform == Platform.LINUX || platform == Platform.LINUX_ALPINE) {
            Optional<String> maybeXgdDataHome = Optional.ofNullable(env.get("XDG_DATA_HOME"));
            if (maybeXgdDataHome.isPresent()) {
                p = Paths.get(maybeXgdDataHome.get()).resolve("snyk");
            } else {
                if (homeDirectory.isPresent()) {
                    p = homeDirectory.get().resolve(".local/share/snyk");
                } else {
                    throw new MissingContextException("homeDirectory is empty but must be set");
                }
            }
        } else {
            throw new IllegalStateException("all possible cases accounted for");
        }

        return p;
    }

    public static class MissingContextException extends RuntimeException {
        public  MissingContextException(String message) {
            super(message);
        }
    }

}
