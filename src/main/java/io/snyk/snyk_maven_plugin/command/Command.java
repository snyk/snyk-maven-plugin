package io.snyk.snyk_maven_plugin.command;

public enum Command {

    CODE_TEST("code", "test"),
    CONTAINER_TEST("container", "test"),
    TEST("test"),
    MONITOR("monitor"),
    VERSION("version");

    private final String[] commandParameters;

    Command(String... commandParameters) {
        this.commandParameters = commandParameters;
    }

    public String[] commandParameters() {
        return commandParameters;
    }

    public String commandName() {
        return String.join(" ", commandParameters());
    }

}
