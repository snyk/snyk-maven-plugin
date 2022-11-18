# Contributing

## Contributor Agreement
A pull-request will only be considered for merging into the upstream codebase after you have signed our [contributor agreement](https://gist.github.com/snyksec/201fc2fd188b4a68973998ec30b57686#file-snyk-oss-contributor-agreement-md), assigning us the rights to the contributed code and granting you a license to use it in return. If you submit a pull request, you will be prompted to review and sign the agreement with one click (we use [CLA assistant](https://cla-assistant.io/)).

## Commit messages

Commit messages must follow the [Angular-style](https://github.com/angular/angular.js/blob/master/CONTRIBUTING.md#commit-message-format) commit format (but excluding the scope).

i.e:

```text
fix: minified scripts being removed

Also includes tests
```

This will allow for the automatic changelog to generate correctly.

### Commit types

Must be one of the following:

* **feat**: A new feature
* **fix**: A bug fix
* **docs**: Documentation only changes
* **test**: Adding missing tests
* **chore**: Changes to the build process or auxiliary tools and libraries such as documentation generation
* **refactor**: A code change that neither fixes a bug nor adds a feature
* **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
* **perf**: A code change that improves performance

To release a major you need to add `BREAKING CHANGE: ` to the start of the body and the detail of the breaking change.

## Local Build

During the build of this plugin, a number of tests will run in the host platform's environment.  To get these tests to run from a developer setting requires some environment variables to be set;

### `SNYK_DOWNLOAD_DESTINATION`

This value is a file path where the test is able to download the Snyk binary to for use in the test.  An example might be `downloads/snyk`.

### `SNYK_TEST_TOKEN`

A Snyk token that can be used in the execution of the test.  You can obtain a token for your Snyk user under the "Account Settings" page in the Snyk Web UI.

### `SNYK_CLI_EXECUTABLE`

The path where the Snyk tool would ordinarily be found on the system.  An example would be `/usr/local/bin/snyk`.


## Release

To release the plugin from your local machine, perform the following steps.

When releasing the first time, you need to import the GPG key and add Maven repository credentials to your local machine. If you have done this, you can skip to step 4.
1. Download GPG keys for Maven Central from the password manager.
2. Run `gpg --import maven-master.gpg` and `gpg --import maven-signing.gpg` for the downloaded keys.
3. Populate your `~/.m2/settings.xml` with the following content, replacing ID and password using "Sonatype JIRA" credentials from the password manager:
```xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>your-jira-id</username>
      <password>your-jira-pwd</password>
    </server>
  </servers>
</settings>
```
4. Run `mvn versions:set -DnewVersion=1.2.3` to set `pom.xml` to the desired version to be released.
5. Ensure the build and tests pass, then trigger release using `mvn clean deploy -P release`.
6. Upon successful completion of Step 5, navigate to [Sonatype](https://oss.sonatype.org)
5. Click on [Staging Repositories](https://oss.sonatype.org/#)
6. Select `iosnyk-xxxx`
7. Click on `Close`. If this not available, perform Step 10.
8. Wait for the Close activity to finish (takes about 10 min)
9. Select `iosnyk-xxxx` staging repository again
10. Click on `Release` (takes about 10 min)

The released version should be available in the [Released repository](https://repo.maven.apache.org/maven2/io/snyk/snyk-maven-plugin/) now. It can take some time to update [Maven Central Repository](https://central.sonatype.dev/artifact/io.snyk/snyk-maven-plugin/2.2.0/versions).

11. Add new release in GitHub manually to create new tag and have better visibility.

If you have questions, consult [the official documentation](https://central.sonatype.org/publish/publish-maven) for publishing information.