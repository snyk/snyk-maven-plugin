package io.snyk.snyk_maven_plugin.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        List<String> parts = new ArrayList<>();

        parts.add(cliExecutablePath);
        Collections.addAll(parts, command.commandParameters());
        parts.add("--integration-name=" + INTEGRATION_NAME);

        args.stream()
            .map(String::trim)
            .filter(arg -> !arg.isEmpty())
            .filter(arg -> !arg.startsWith("--integration-name"))
                .forEach(parts::add);

        ProcessBuilder pb = new ProcessBuilder(parts);

        if (color) {
            pb.environment().put("FORCE_COLOR", "true");
        }

        apiToken.ifPresent((t) -> pb.environment().put("SNYK_TOKEN", t));

        return pb;
    }
}
