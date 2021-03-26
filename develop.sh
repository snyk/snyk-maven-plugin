#! /usr/bin/env bash
set -euo pipefail

expected_java_version="1.8"
expected_maven_version="3"
test_maven_version="3.6.3"

jdk_help() {
  echo "  Instructions for macOS:"
  echo "    brew install openjdk@8"
  echo "    export JAVA_HOME=\"\$(brew --prefix)/opt/openjdk@8/libexec/openjdk.jdk/Contents/Home\""
}

failure() {
  echo
  echo "Failure... See above for help setting things up."
  exit 1
}

if [[ -z "${JAVA_HOME-}" ]]; then
  echo
  echo "JAVA_HOME pointing to JDK 1.8 is needed."
  jdk_help
  exit 1
fi

java_version="$("${JAVA_HOME}/bin/javac" -version 2>&1 | cut -d ' ' -f2)"
if [[ -z "$(echo "${java_version}" | grep -e "^${expected_java_version}")" ]]; then
  echo "Need JDK ${expected_java_version} but found ${java_version}."
  jdk_help
  failure
fi

env_errors=""
if [[ -z "${SNYK_TEST_TOKEN-}" ]]; then
  env_errors="${env_errors}  SNYK_TEST_TOKEN - A Snyk API Token used for acceptance tests.\n"
  env_errors="${env_errors}    export SNYK_TEST_TOKEN='<your-api-token>'\n"
fi

if [[ -z "${SNYK_CLI_EXECUTABLE-}" ]]; then
  env_errors="${env_errors}  SNYK_CLI_EXECUTABLE - Path to your Snyk CLI executable for acceptance tests.\n"
  env_errors="${env_errors}    export SNYK_CLI_EXECUTABLE='/usr/local/bin/snyk'\n"
fi

if [[ -z "${SNYK_DOWNLOAD_DESTINATION-}" ]]; then
  env_errors="${env_errors}  SNYK_DOWNLOAD_DESTINATION - Relative path to where acceptance tests place the executable.\n"
  env_errors="${env_errors}    export SNYK_DOWNLOAD_DESTINATION='downloads/snyk'\n"
fi

if [[ "${env_errors}" ]]; then
  echo
  echo "Missing environment variables:"
  echo -n "${env_errors}"
  failure
fi

maven_version="$(mvn -version 2>&1 | head -n1 | cut -d ' ' -f3)"
if [[ -z "$(echo "${maven_version}" | grep -e "^${expected_maven_version}")" ]]; then
  echo "Need Maven ${expected_maven_version} but found ${maven_version}."
  echo "  Instructions for macOS:"
  echo "    brew install maven"
  failure
fi

set +e
wrapper_out="$(mvn -N io.takari:maven:0.7.7:wrapper -Dmaven="${test_maven_version}")"
wrapper_exit_code="$?"
set -e
if [[ "${wrapper_exit_code}" != "0" ]]; then
  echo
  echo "${wrapper_out}"
  echo
  echo 'Failed to install Maven Wrapper. See output above.'
  failure
fi

echo
echo "Success! You're ready to develop."
