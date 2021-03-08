package io.snyk.maven.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.snyk.maven.common.MavenProjectProperties.convertPropertyNameToEnvironmentKey;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MavenProjectPropertiesTest {

  @Test
  @DisplayName("convert with null or empty should return null")
  void convertPropertyNameToEnvironmentKey_invalidInput() {
    assertAll("invalid input for convert",
              () -> assertNull(convertPropertyNameToEnvironmentKey(null)),
              () -> assertNull(convertPropertyNameToEnvironmentKey(""))
    );
  }

  @Test
  @DisplayName("convert should uppercase and replace dots with underscore")
  void convertPropertyNameToEnvironmentKey_uppercaseAndUnderscore() {
    assertAll("valid input for convert",
              () -> {
                String output = convertPropertyNameToEnvironmentKey("snyk.cli.executable");
                assertEquals("SNYK_CLI_EXECUTABLE", output);
              },
              () -> {
                String output = convertPropertyNameToEnvironmentKey("SNYK_TOKEN");
                assertEquals("SNYK_TOKEN", output);
              },
              () -> {
                String output = convertPropertyNameToEnvironmentKey("SnYk_EnDpOiNt");
                assertEquals("SNYK_ENDPOINT", output);
              });
  }
}
