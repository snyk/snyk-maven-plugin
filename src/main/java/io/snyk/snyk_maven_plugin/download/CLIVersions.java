package io.snyk.snyk_maven_plugin.download;

import org.apache.maven.plugin.MojoExecutionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLIVersions {
    public static final String LATEST_VERSION_KEYWORD = "latest";

    // is it latest or 1.456.0
    static final Pattern versionRegex = Pattern.compile("^" + LATEST_VERSION_KEYWORD + "|(?:\\d+\\.){2}\\d+$");

    private static boolean isValidCLIVersion(String versionToCheck) {
        Matcher versionMatcher = CLIVersions.versionRegex.matcher(versionToCheck);
        return versionMatcher.matches();
    }

    public static String sanitize(String version) throws MojoExecutionException {
        if (!isValidCLIVersion(version)) {
            throw new MojoExecutionException("Invalid Snyk CLI version. It should be a valid semver e.g. 1.489.0");
        }

        // Add "v" prefix e.g. v1.456.0 if needed
        return version.equals(LATEST_VERSION_KEYWORD) ? version : "v" + version;
    }
}
