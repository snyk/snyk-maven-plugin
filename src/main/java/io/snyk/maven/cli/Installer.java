package io.snyk.maven.cli;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.maven.plugin.logging.Log;

public class Installer {

  private static final String LAST_UPDATE_MARKER_FILE = ".lastUpdate";

  private final Log log;
  private final Downloader downloader;
  private final Platform platform;

  public Installer(Log log, Downloader downloader, Platform platform) {
    this.log = log;
    this.downloader = downloader;
    this.platform = platform;
  }

  public void performInstallationIfNeeded() throws IOException {
    Path cliInstallationDirectory = preferredLocation();

    if (isUpToDate(cliInstallationDirectory)) {
      log.info("Snyk CLI installation is up-to-date");
      return;
    }

    log.info("Installing Snyk CLI (latest) to " + cliInstallationDirectory.resolve(platform.snykExecutableFileName));
    downloader.download(cliInstallationDirectory.resolve(platform.snykExecutableFileName), platform);

    log.debug("Updating the marker file with new installation timestamp");
    Path marker = cliInstallationDirectory.resolve(LAST_UPDATE_MARKER_FILE);
    Files.write(marker, String.valueOf(Instant.now().toEpochMilli()).getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Find OS-specific location to install Snyk CLI.
   */
  Path preferredLocation() throws IOException {
    //TODO(pavel): handle different OS-specific locations (windows, linux, macos)
    Path installDirectory = Paths.get(System.getProperty("user.home")).resolve("Library/Application Support/Snyk");
    if (Files.notExists(installDirectory)) {
      try {
        log.debug("Installation directory does not exist, creating: " + installDirectory.toAbsolutePath());
        installDirectory = Files.createDirectories(installDirectory);
      } catch (IOException ex) {
        throw new IOException("Could not create installation directory for Snyk CLI", ex);
      }
    }
    return installDirectory;
  }

  boolean isUpToDate(@Nonnull Path installationDirectory) {
    Path marker = installationDirectory.resolve(LAST_UPDATE_MARKER_FILE);
    if (Files.notExists(marker)) {
      return false;
    }

    long timestampFromFile;
    try {
      List<String> lines = Files.readAllLines(marker, StandardCharsets.UTF_8);
      String content = lines.get(0).replace("\r", "").replace("\n", "");
      timestampFromFile = Long.parseLong(content);
    } catch (Exception ex) {
      log.debug("Marker file with timestamp is corrupt or cannot be read, so the timestamp will be reset to 0");
      timestampFromFile = 0;
    }
    long timestampNow = Instant.now().toEpochMilli();

    long timestampDiff = timestampNow - timestampFromFile;
    if (timestampDiff <= 0) {
      return true;
    }
    //TODO(pavel): handle updatePolicy parameter later, at the moment 24 hours hard-coded.
    long updatePolicy = TimeUnit.HOURS.toMillis(24);
    return timestampDiff < updatePolicy;
  }
}
