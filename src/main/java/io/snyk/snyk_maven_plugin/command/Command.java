package io.snyk.snyk_maven_plugin.command;

public enum Command {

    TEST("test"),
    MONITOR("monitor"),
    VERSION("version");

    private final String commandName;

    Command(String commandName) {
        this.commandName = commandName;
    }

    public String commandName() {
        return commandName;
    }

}
