package io.snyk;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

@Mojo(name = "test")
public class SnykTestMojo extends AbstractMojo {
    @Parameter(property = "apiToken")
    private String apiToken;

    @Parameter(property = "cli")
    private CLI cli;

    @Parameter(property = "args")
    private List<String> args;

    public void execute() {
        final Log log = getLog();
        log.info("for known issues, no vulnerable paths found.");
        /*
            1. validate plugin options
                - apiKey
                - cli
                    - executable
                - args
                    - arg
                    ...
            2. if required fetch & validate CLI
                - it'll be easier if we have static.snyk.io/cli/latest/snyk-win.exe
            3. execute CLI
         */
    }
}
