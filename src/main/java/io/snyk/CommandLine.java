package io.snyk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface CommandLine {

    Process start() throws IOException;

    static ProcessBuilder asProcessBuilder(
        String cliExecutablePath,
        Command command,
        Optional<String> apiToken,
        List<String> args
    ) {
        ProcessBuilder pb = new ProcessBuilder(new ArrayList<String>() {{
            add(cliExecutablePath);
            add(command.commandName());
            addAll(args);
        }});

        apiToken.ifPresent((t) -> {
            pb.environment().put("SNYK_TOKEN", t);
        });

        return pb;
    }
}
