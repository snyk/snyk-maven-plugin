package io.snyk;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "test")
public class SnykTestMojo extends AbstractMojo {
    @Parameter(property = "apiToken")
    private String apiToken;

    @Parameter(property = "cli")
    private CLI cli;

    @Parameter(property = "args")
    private List<String> args;

    public void execute() throws MojoFailureException {
//        System.out.println(System.getenv("SNYK_API_TOKEN"));
        throw new MojoFailureException("hello world");
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
