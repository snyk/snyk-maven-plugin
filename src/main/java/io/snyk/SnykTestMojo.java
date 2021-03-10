package io.snyk;

import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "test")
public class SnykTestMojo extends AbstractSnykMojo {

    @Override
    public Command getCommand() {
        return Command.TEST;
    }

}
