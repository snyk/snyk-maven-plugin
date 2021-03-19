package io.snyk.snyk_maven_plugin;

import io.snyk.snyk_maven_plugin.command.Command;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "monitor")
public class SnykMonitorMojo extends AbstractSnykMojo {

    @Override
    public Command getCommand() {
        return Command.MONITOR;
    }

}
