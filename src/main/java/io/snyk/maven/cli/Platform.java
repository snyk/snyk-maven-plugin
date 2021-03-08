package io.snyk.maven.cli;

import javax.annotation.Nonnull;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

public enum Platform {
  LINUX("snyk-linux"),
  LINUX_ALPINE("snyk-alpine"),
  MAC_OS("snyk-macos"),
  WINDOWS("snyk-win.exe");

  public final String snykExecutableFileName;

  Platform(String snykExecutableFileName) {
    this.snykExecutableFileName = snykExecutableFileName;
  }

  @Nonnull
  public static Platform current() {
    return detect(System.getProperties());
  }

  @Nonnull
  private static Platform detect(@Nonnull Map<Object, Object> systemProperties) {
    String arch = ((String) systemProperties.get("os.name")).toLowerCase(Locale.ENGLISH);
    if (arch.contains("linux")) {
      return Paths.get("/etc/alpine-release").toFile().exists() ? LINUX_ALPINE : LINUX;
    } else if (arch.contains("mac os x") || arch.contains("darwin") || arch.contains("osx")) {
      return MAC_OS;
    } else if (arch.contains("windows")) {
      return WINDOWS;
    }
    throw new IllegalArgumentException(arch + " is not supported CPU type");
  }
}
