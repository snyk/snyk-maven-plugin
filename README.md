![Snyk logo](https://snyk.io/style/asset/logo/snyk-print.svg)

![Travis](https://img.shields.io/travis/snyk/snyk-maven-plugin.svg)

![Snyk Maven Plugin Vulnerabilities](https://img.shields.io/snyk/vulnerabilities/github/snyk/snyk-maven-plugin.svg)

---

# Snyk Maven Plugin

Official [Snyk](https://snyk.io) Maven plugin tests and monitors your Maven dependencies.

## Installation

1. [Get Snyk API token](https://snyk.co/ucT6J).

2. In your pom.xml file, add the Snyk Maven plugin:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>io.snyk</groupId>
            <artifactId>snyk-maven-plugin</artifactId>
            <version>2.0.0</version>
            <executions>
                <execution>
                    <id>snyk-test</id>
                    <phase>test</phase>
                    <goals>
                        <goal>test</goal>
                    </goals>
                </execution>
                <execution>
                    <id>snyk-monitor</id>
                    <phase>install</phase>
                    <goals>
                        <goal>monitor</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <apiToken>${env.SNYK_TOKEN}</apiToken>
                <args>
                    <arg>--all-projects</arg>
                </args>
            </configuration>
        </plugin>
    </plugins>
</build>
```

3. We recommend to set the **test** goal in the **test** phase of Maven; and the **monitor** goal in the **install** phase of Maven.

## Configuration

The following are elements in the `<configuration></configuration>` section of the plugin:

- **apiToken**: used to authenticate with the Snyk services. Snyk API token must be available either 1) in the pom.xml 2) in an environment as `SNYK_TOKEN` 3) in user storage after independently running CLI's `snyk auth` command.
- **skip** (optional): The **skip** configuration element allows to skip plugin's execution when setting it to `true`. Default value is `false`.

### Configuring Snyk CLI in Maven plugin

Snyk Maven plugin is using [Snyk CLI](https://github.com/snyk/snyk) to scan your projects. This means that you may pass CLI flags with the `<args>`. [Snyk CLI reference with list of flags and arguments](https://support.snyk.io/hc/en-us/articles/360003812578-CLI-reference)

Example configuration:

```xml
<configuration>
    <apiToken>${env.SNYK_TOKEN}</apiToken>
    <args>
        <arg>--severity-threshold=critical</arg>
        <arg>--scan-all-unmanaged</arg>
        <arg>--json</arg>
    </args>
</configuration>
```

There are also additional `<cli>` configurations available:

- **executable** (optional): path to the Snyk CLI executable that should be used. If not provided, plugin will download the CLI from Snyk.io
- **version** (optional): specify a version of CLI to use. For example: `<version>1.489.0</version>`. If not provided, plugin will download the latest version
- **updatePolicy** (optional): you may specify update policy to fetch the CLI. Allowed values: `daily`, `never` or `interval:$HOURS`. Defaults to the recommended `daily` checks.
- **downloadUrl** (optional): specify an URL from which to download the CLI. For example: `<downloadUrl>https://snyk-cli-mirror.local/cli/</downloadUrl>`. It should follow the same format as Snyk's download URL: `$downloadUrl/$CLI_VERSION/$CLI_FILE`. Where `$CLI_VERSION` is `latest` or a version (see the `<version>` above) and `$CLI_FILE` is one of the OS-specific filenames: `snyk-linux`, `snyk-macos`, `snyk-alpine` or `snyk-win.exe`.

```xml
<configuration>
    <apiToken>${env.SNYK_TOKEN}</apiToken>
    <cli>
        <executable>~/.local/share/snyk/snyk-linux</executable>
    </cli>
</configuration>
```

## Features

- The **test** goal presents a list of vulnerabilities in your project's dependencies, in either a developer's machine or in your CI process.
- The **monitor** goal records the state of dependencies and any vulnerabilities on snyk.io so you can be alerted when new vulnerabilities or updates/patches are disclosed that affect your repositories.
- Running `mvn snyk:test` or `mvn snyk:monitor` will run the desired goals (either **test** or **monitor**) outside the Maven build lifecycle.

## Migrating from Snyk Maven Plugin v1

All plugin options from v1 were moved to the `<args>` object, to keep them in line with the CLI usage. See the mapping:

- `org` => `<arg>--org=my-org-name</arg>`
- `failOnSeverity` => `<arg>--severity-threshold=low|medium|high</arg>`
- `failOnAuthError` => Not implemented
- `includeProvidedDependencies` => Not implemented

Also checkoout the [Snyk CLI reference with list of flags and arguments](https://support.snyk.io/hc/en-us/articles/360003812578-CLI-reference).

## Supported Maven versions

This plugin is supported by Maven version 3.1.0 and above.

## Local development setup

`export SNYK_API_TOKEN="*********-****-****-****-****"`

### Get maven

`brew install maven`

### Running the build & tests

`mvn clean install -Prun-its`
