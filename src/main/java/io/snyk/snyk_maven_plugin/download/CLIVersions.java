package io.snyk.snyk_maven_plugin.download;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLIVersions {
    public static final String LATEST_VERSION_KEYWORD = "latest";

    static final Pattern versionRegex = Pattern.compile("^(?:\\d+\\.){2}\\d+$");

    private static boolean isValidCLIVersion(String versionToCheck) {
        Matcher versionMatcher = CLIVersions.versionRegex.matcher(versionToCheck);
        return versionMatcher.matches();
    }

    public static String sanitize(String version) {
        if (!isValidCLIVersion(version)) {
            throw new IllegalArgumentException("Invalid Snyk CLI version. It should be a valid semver e.g. 1.489.0");
        }
        return getCDNVersion(version);
    }

    public static String getCDNVersion(String version) {
        return "v" + version;
    }
}
