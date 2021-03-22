<img src="https://snyk.io/style/asset/logo/snyk-print.svg" alt="Snyk Logo" style="float:right" />

# Snyk Maven Plugin

![Vulnerabilities](https://img.shields.io/snyk/vulnerabilities/github/snyk/snyk-maven-plugin.svg)
![Maven Release](https://img.shields.io/maven-central/v/io.snyk/snyk-maven-plugin)

Tests and monitors your Maven dependencies. This plugin is officially maintained
by [Snyk](https://snyk.io).

## Installation

1. [Get your Snyk API token](https://snyk.co/ucT6J).

2. Add the Snyk Maven Plugin to your `pom.xml` and configure it as needed.

```xml
<!-- Example Plugin Configuration -->
<build>
  <plugins>
    <plugin>
      <groupId>io.snyk</groupId>
      <artifactId>snyk-maven-plugin</artifactId>
      <version>2.0.0</version>
      <executions>
        <execution>
          <id>snyk-test</id>
          <goals>
            <goal>test</goal>
          </goals>
        </execution>
        <execution>
          <id>snyk-monitor</id>
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

## Supported Versions

- Java 8 and above.
- Maven 3.2.5 and above.

## Goals

You can specify the following `goals`. You can run these outside your Maven
lifecycle using `mvn snyk:<goal>`.

### `test`

Presents a list of vulnerabilities in your project's dependencies, in either a
developer's machine or in your CI process.

### `monitor`

Records the state of dependencies and any vulnerabilities
on [snyk.io](https://snyk.io) so you can be alerted when new vulnerabilities or
updates/patches are disclosed that affect your repositories.

## Configuration

You can configure the following parameters inside the `<configuration>` section.
All parameters are optional.

### `apiToken` \[string\]

You must provide a Snyk API token to access Snyk's services. You can do so by:

- Providing this `apiToken` in your configuration.
- Providing a `SNYK_TOKEN` environment variable
- Authenticating via `snyk auth` using Snyk CLI before using this plugin.

### `skip` \[boolean\]

Default: `false`

Skip this plugin's execution. You can also use `-Dsnyk.skip` to toggle this
behaviour.

### `args` \[array\<string\>\]

This plugin uses [Snyk CLI](https://github.com/snyk/snyk) so you can pass any
supported arguments using `<args>`. See the example below.

For a list of supported arguments,
see [Snyk CLI Reference](https://support.snyk.io/hc/en-us/articles/360003812578-CLI-reference)
.

```xml
<!-- Example Arguments Configuration -->
<configuration>
  <args>
    <arg>--severity-threshold=critical</arg>
    <arg>--scan-all-unmanaged</arg>
    <arg>--json</arg>
  </args>
</configuration>
```

### `cli` \[object\]

Lets you configure the Snyk CLI that's used by this plugin.

```xml
<!-- Example CLI Configuration -->
<configuration>
  <cli>
    <!-- Place CLI Configuration Here -->
  </cli>
</configuration>
```

#### `executable` \[string\]

Path to a Snyk CLI binary. When provided, this plugin won't automatically
download the CLI.

Example: `~/.local/share/snyk/snyk-linux`

#### `version` \[string\]

Default: `latest`, Accepts: `latest` or an exact version such as `1.149.0`.

The version of the CLI to download.

## Migrating from Snyk Maven Plugin v1

All plugin options from v1 were moved to the `<args>` object, to keep them in
line with the CLI usage. See the mapping:

- `org` => `<arg>--org=my-org-name</arg>`
- `failOnSeverity` => `<arg>--severity-threshold=low|medium|high</arg>`
- `failOnAuthError` => Use `<skip>true</skip>` to skip plugin execution.
- `includeProvidedDependencies` => `provided` dependencies are always included.

For a list of support arguments, see [Configuration](#args-arraystring)
