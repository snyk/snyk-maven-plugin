package io.snyk;

import io.snyk.snyk_maven_plugin.command.Command;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "test")
public class SnykTestMojo extends AbstractSnykMojo {

    @Override
    public Command getCommand() {
        return Command.TEST;
    }

}
