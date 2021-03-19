package io.snyk.snyk_maven_plugin.goal;

import io.snyk.snyk_maven_plugin.command.Command;
import io.snyk.snyk_maven_plugin.download.CLIVersions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(CLIVersions.LATEST_VERSION_KEYWORD, mojo.getDownloadVersion());
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
