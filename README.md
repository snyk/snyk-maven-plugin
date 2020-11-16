![Snyk logo](https://snyk.io/style/asset/logo/snyk-print.svg)

![Travis](https://img.shields.io/travis/snyk/snyk-maven-plugin.svg)

![Snyk Vulnerabilities](https://img.shields.io/snyk/vulnerabilities/github/snyk/snyk-maven-plugin.svg)

***

Snyk helps you find, fix and monitor for known vulnerabilities in Node.js npm, Ruby and Java dependencies, both on an ad hoc basis and as part of your CI (Build) system.

The Snyk Maven plugin tests and monitors your Maven dependencies.

## Installation

1. If you haven't done so already, head on to the [Snyk website](https://snyk.io), register and get your API token. It will be presented in your [Snyk account page](https://snyk.io/account/).

2. In your pom.xml file, add the Snyk Maven plugin:

```
<build>
    <plugins>
        <plugin>
            <groupId>io.snyk</groupId>
            <artifactId>snyk-maven-plugin</artifactId>
            <version>1.2.9</version>
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
                <apiToken>${SNYK_API_TOKEN}</apiToken>
                <failOnSeverity>medium</failOnSeverity>
                <org></org>
            </configuration>
        </plugin>
    </plugins>
</build>
```

3. As seen in the snippet above, Snyk recommends to set the **test** goal in the **test** phase of Maven; and the **monitor** goal in the **install** phase of Maven.

## Supported Maven versions

This plugin is supported by Maven version 3.1.0 and above.

## Configuration

The following are elements in the `<configuration></configuration>` section of the plugin:

- **apiToken** (mandatory): The **apiToken** is used to authenticate with the Snyk services. With the API token, the plugin can be configured with it as a system property or environment variable. The token can also be manually added to the pom.xml, although this is not the recommended method. This is mandatory configuration.
- **failOnAuthError** (optional): Setting **failOnAuthError** to true will fail the build if authentication fails. Default value is `false`
- **failOnSeverity** (optional): Setting **failOnSeverity** to any of the values (`low`, `medium` or `high`) will fail the Maven build if a severity is found at or above what was configured. This configuration is optional, and will be set to `low` if not defined. Setting it to `false` will never fail the build.
- **org** (optional): The **org** configuration element sets under which of your Snyk organisations the project will be recorded. Leaving out this configuration will record the project under your default organisation.
- **includeProvidedDependencies** (optional): The **includeProvidedDependencies** configuration element allows to include dependencies with `provided` scope. Default value is `true`.
- **skip** (optional): The **skip** configuration element allows to skip plugin's execution when setting it to `true`. Default value is `false`.

## Features

- The **test** goal presents a list of vulnerabilities in your project's dependencies, in either a developer's machine or in your CI process.
- The **monitor** goal records the state of dependencies and any vulnerabilities on snyk.io so you can be alerted when new vulnerabilities or updates/patches are disclosed that affect your repositories.
- Running `mvn snyk:test` or `mvn snyk:monitor` will run the desired goals (either **test** or **monitor**) outside the Maven build lifecycle.


## Development setup
`export SNYK_API_TOKEN="*********-****-****-****-****"`
`export SNYK_API_ENDPOINT="https://snyk.io/"`
## Get maven
`brew install maven`
### Running the build & tests
`mvn clean install -Prun-its`
