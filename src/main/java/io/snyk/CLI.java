package io.snyk;

import org.apache.maven.plugins.annotations.Parameter;

public class CLI {
    @Parameter(property = "executable")
    private String executable;

    public String getExecutable() {
        return executable;
    }
}
