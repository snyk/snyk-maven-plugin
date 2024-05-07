package io.snyk.snyk_maven_plugin.download;

import java.nio.file.Paths;
import java.util.Locale;

public enum Platform {
    LINUX("snyk-linux"),
    LINUX_ARM64("snyk-linux-arm64"),
    LINUX_ALPINE("snyk-alpine"),
    MAC_OS("snyk-macos"),
    WINDOWS("snyk-win.exe");

    public final String snykExecutableFileName;

    Platform(String snykExecutableFileName) {
        this.snykExecutableFileName = snykExecutableFileName;
    }

    public static Platform current() {
        return detect(System.getProperty("os.name"), System.getProperty("os.arch"));
    }

    protected static Platform detect(String osName, String arch) {
        String osNameLower = osName.toLowerCase(Locale.ENGLISH);
        if (osNameLower.contains("linux")) {
            if (Paths.get("/etc/alpine-release").toFile().exists()) {
                switch (arch) {
                    case "x86_64":
                        return LINUX_ALPINE;
                    default:
                        throw new IllegalArgumentException("linux alpine " + arch + " is not supported.");
                }
            }
            switch (arch) {
                case "x86_64":
                    return LINUX;
                case "aarch64":
                    return LINUX_ARM64;
                default:
                    throw new IllegalArgumentException("linux " + arch + " is not supported.");
            }
        } else if (osNameLower.contains("mac os x") || osNameLower.contains("darwin") || osNameLower.contains("osx")) {
            // Mac M1/M2 (Arm64) will happily run x86 under Rosetta2, no need to check arch
            return MAC_OS;
        } else if (osNameLower.contains("windows") && arch.equals("x86_64")) {
            return WINDOWS;
        }
        throw new IllegalArgumentException(osNameLower + "(" + arch + ") is not supported.");
    }
}
