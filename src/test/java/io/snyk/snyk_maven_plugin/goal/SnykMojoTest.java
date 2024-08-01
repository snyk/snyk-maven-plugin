package io.snyk.snyk_maven_plugin.goal;

import io.snyk.snyk_maven_plugin.command.Command;
import io.snyk.snyk_maven_plugin.download.CLIVersions;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnykMojoTest {

    @Test
    public void shouldDefaultToNoArguments() {
        SnykMojo mojo = createMojo();
        assertEquals(emptyList(), mojo.getArguments());
    }

    @Test
    public void shouldDefaultToNoExecutable() {
        SnykMojo mojo = createMojo();
        assertEquals(Optional.empty(), mojo.getExecutable());
    }

    @Test
    public void shouldDefaultToLatestVersion() {
        SnykMojo mojo = createMojo();
        assertEquals(CLIVersions.STABLE_VERSION_KEYWORD, mojo.getDownloadVersion());
    }

    @Test
    public void shouldGetDownloadUrls() {
        String expectedPrimaryUrl = "https://downloads.snyk.io";
        String expectedSecondaryUrl = "https://static.snyk.io";
        List<String> expectedUrl = Arrays.asList(expectedPrimaryUrl, expectedSecondaryUrl);

        SnykMojo mojo = createMojo();

        List<URL> actualUrls = mojo.getDownloadUrls();

        for (int i = 0; i < actualUrls.size(); i++) {
            boolean containsExpectedUrl = actualUrls.get(i).toString().contains(expectedUrl.get(i));
            assertTrue(containsExpectedUrl);
        }
    }

    private SnykMojo createMojo() {
        return new SnykMojoForTesting();
    }

    private static class SnykMojoForTesting extends SnykMojo {

        @Override
        public Command getCommand() {
            return Command.TEST;
        }

    }

}
