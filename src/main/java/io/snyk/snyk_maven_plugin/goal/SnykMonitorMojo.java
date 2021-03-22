package io.snyk.snyk_maven_plugin.goal;

import io.snyk.snyk_maven_plugin.command.Command;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "monitor", defaultPhase = LifecyclePhase.INSTALL)
public class SnykMonitorMojo extends SnykMojo {

    @Override
    public Command getCommand() {
        return Command.MONITOR;
    }

}
