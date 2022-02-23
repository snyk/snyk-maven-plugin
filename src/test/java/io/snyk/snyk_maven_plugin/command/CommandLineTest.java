package io.snyk.snyk_maven_plugin.command;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.snyk.snyk_maven_plugin.command.CommandLine.INTEGRATION_NAME;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandLineTest {

    @Test
    public void shouldIncludePathCommandNameIntegrationName() {
        ProcessBuilder pb = CommandLine.asProcessBuilder(
            "/path/to/cli",
            Command.TEST,
            Optional.empty(),
            Optional.empty(),
            emptyList(),
            false
        );

        assertEquals(
            asList(
                "/path/to/cli",
                "test",
                "--integration-name=" + INTEGRATION_NAME
            ),
            pb.command()
        );
    }

    @Test
    public void shouldIncludeTrimmedArguments() {
        ProcessBuilder pb = CommandLine.asProcessBuilder(
            "/path/to/cli",
            Command.TEST,
            Optional.empty(),
            Optional.empty(),
            asList(
                "--print-deps",
                "  --all-projects  ",
                "--json-file-output=out.json  "
            ),
            false
        );

        assertEquals(
            asList(
                "/path/to/cli",
                "test",
                "--integration-name=" + INTEGRATION_NAME,
                "--print-deps",
                "--all-projects",
                "--json-file-output=out.json"
            ),
            pb.command()
        );
    }

    @Test
    public void shouldNotChangeIntegrationName() {
        ProcessBuilder pb = CommandLine.asProcessBuilder(
            "/path/to/cli",
            Command.TEST,
            Optional.empty(),
            Optional.empty(),
            singletonList("--integration-name=this-is-not-ok"),
            false
        );

        assertEquals(
            asList(
                "/path/to/cli",
                "test",
                "--integration-name=" + INTEGRATION_NAME
            ),
            pb.command()
        );
    }

    @Test
    public void shouldNotModifyEnvironmentByDefault() {
        ProcessBuilder pb = CommandLine.asProcessBuilder(
            "/path/to/cli",
            Command.TEST,
            Optional.empty(),
            Optional.empty(),
            emptyList(),
            false
        );
        assertEquals(
            System.getenv(),
            pb.environment()
        );
    }

    @Test
    public void shouldIncludeAPIToken() {
        ProcessBuilder pb = CommandLine.asProcessBuilder(
            "/path/to/cli",
            Command.TEST,
            Optional.of("fake-token"),
            Optional.empty(),
            emptyList(),
            false
        );

        assertEquals(
            "fake-token",
            pb.environment().get("SNYK_TOKEN")
        );
    }

    @Test
    public void shouldForceColorWhenEnabled() {
        ProcessBuilder pb = CommandLine.asProcessBuilder(
            "/path/to/cli",
            Command.TEST,
            Optional.empty(),
            Optional.empty(),
            emptyList(),
            true
        );
        assertEquals(
            "true",
            pb.environment().get("FORCE_COLOR")
        );
    }

}
