package io.snyk.snyk_maven_plugin.download;

import java.nio.file.Paths;
import java.util.Locale;

public enum Platform {
    LINUX("snyk-linux"),
    LINUX_ALPINE("snyk-alpine"),
    MAC_OS("snyk-macos"),
    WINDOWS("snyk-win.exe");

    public final String snykExecutableFileName;

    Platform(String snykExecutableFileName) {
        this.snykExecutableFileName = snykExecutableFileName;
    }

    public static Platform current() {
        return detect(System.getProperty("os.name"));
    }

    protected static Platform detect(String osName) {
        String osNameLower = osName.toLowerCase(Locale.ENGLISH);
        if (osNameLower.contains("linux")) {
            return Paths.get("/etc/alpine-release").toFile().exists() ? LINUX_ALPINE : LINUX;
        } else if (osNameLower.contains("mac os x") || osNameLower.contains("darwin") || osNameLower.contains("osx")) {
            return MAC_OS;
        } else if (osNameLower.contains("windows")) {
            return WINDOWS;
        }
        throw new IllegalArgumentException(osNameLower + " is not supported.");
    }
}
