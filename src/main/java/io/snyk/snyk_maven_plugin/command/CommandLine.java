package io.snyk.snyk_maven_plugin.command;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CommandLine {

    String INTEGRATION_NAME = "MAVEN_PLUGIN";

    Process start() throws IOException;

    static ProcessBuilder asProcessBuilder(
        String cliExecutablePath,
        Command command,
        Optional<String> apiToken,
        List<String> args,
        boolean color
    ) {
        Stream<String> baseParts = Stream.of(
            cliExecutablePath,
            command.commandName(),
            "--integration-name=" + INTEGRATION_NAME
        );

        Stream<String> normalisedArgs = args.stream()
            .map(String::trim)
            .filter(arg -> !arg.startsWith("--integration-name"));

        List<String> parts = Stream.concat(baseParts, normalisedArgs)
            .collect(Collectors.toList());

        ProcessBuilder pb = new ProcessBuilder(parts);

        if (color) {
            pb.environment().put("FORCE_COLOR", "true");
        }

        apiToken.ifPresent((t) -> {
            pb.environment().put("SNYK_TOKEN", t);
        });

        return pb;
    }
}
