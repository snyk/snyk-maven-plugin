package io.snyk.maven.common;

import javax.annotation.Nullable;
import java.util.Locale;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

/**
 * Gets information about {@link org.apache.maven.project.MavenProject}.
 */
public final class MavenProjectProperties {

  private MavenProjectProperties() {
  }

  /**
   * Get a property by the given name. Order of precedence:
   * <ol>
   *   <li>{@code -D} commandline argument</li>
   *   <li>environment variable if exists</li>
   *   <li>{@code property} defined in POM file</li>
   * </ol>
   * Returns null if nothing found.
   */
  @Nullable
  public static String getProperty(String propertyName, @Nullable MavenProject project, @Nullable MavenSession session) {
    if (session != null && session.getSystemProperties().containsKey(propertyName)) {
      return session.getSystemProperties().getProperty(propertyName);
    }

    String envKey = convertPropertyNameToEnvironmentKey(propertyName);
    if (System.getenv().containsKey(envKey)) {
      return System.getenv(envKey);
    }

    if (project != null && project.getProperties().containsKey(propertyName)) {
      return project.getProperties().getProperty(propertyName);
    }
    return null;
  }

  @Nullable
  static String convertPropertyNameToEnvironmentKey(String propertyName) {
    if (propertyName == null || propertyName.isEmpty()) {
      return null;
    }

    return propertyName.toUpperCase(Locale.ENGLISH).replace(".", "_");
  }
}
