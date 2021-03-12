package io.snyk.snyk_maven_plugin.download;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InstallerTest {

    @Test
    public void worksWindows() {
        Platform platform = Platform.WINDOWS;
        Map<String, String> env = new HashMap<>();
        env.put("APPDATA", "c:\\users\\foo\\appdata");
        Path destinationDirectory = Installer.getInstallLocation(platform, Optional.empty(), env);
        assertEquals(
                destinationDirectory.toString().replace('/', '\\'),  // replace so that this test works on Mac/Linux
                "c:\\users\\foo\\appdata\\Snyk");
    }

    @Test
    public void windowsThrowsIfAppDataNotSet() {
        Platform platform = Platform.WINDOWS;
        Map<String, String> env = new HashMap<>(); // no APPDATA
        assertThrows(Installer.MissingContextException.class, () -> Installer.getInstallLocation(platform, Optional.empty(), env));
    }

    @Test
    public void worksMac() {
        Platform platform = Platform.MAC_OS;
        Optional<Path> maybeHomeDir = Optional.of(Paths.get("/Users/foo"));
        Map<String, String> env = new HashMap<>();
        Path destinationDirectory = Installer.getInstallLocation(platform, maybeHomeDir, env);
        assertEquals(
                destinationDirectory.toString().replace('\\', '/'), // replace so that this test works on Windows
                "/Users/foo/Library/Application Support/Snyk");
    }

    @Test
    public void macThrowsIfAppDataNotSet() {
        Platform platform = Platform.MAC_OS;
        Optional<Path> missingHomeDir = Optional.empty(); // can't get home directory
        Map<String, String> env = new HashMap<>();
        assertThrows(Installer.MissingContextException.class, () -> Installer.getInstallLocation(platform, missingHomeDir, env));
    }

    @Test
    public void worksLinuxXgdSet() {
        Platform platform = Platform.LINUX;
        Map<String, String> env = new HashMap<>();
        env.put("XDG_DATA_HOME", "/user/foo/xgd");
        Path destinationDirectory = Installer.getInstallLocation(platform, Optional.empty(), env);
        assertEquals(
                destinationDirectory.toString().replace('\\', '/'), // replace so that this test works on Windows
                "/user/foo/xgd/snyk");
    }

    @Test
    public void worksLinuxNotSet() {
        Platform platform = Platform.LINUX;
        Optional<Path> maybeHomeDir = Optional.of(Paths.get("/user/foo"));
        Map<String, String> env = new HashMap<>();
        Path destinationDirectory = Installer.getInstallLocation(platform, maybeHomeDir, env);
        assertEquals(
                destinationDirectory.toString().replace('\\', '/'), // replace so that this test works on Windows
                "/user/foo/.local/share/snyk");
    }

    @Test
    public void linuxThrowsIfXgdNotSetAndHomeDirNotSet() {
        Platform platform = Platform.LINUX;
        Optional<Path> missingHomeDir = Optional.empty(); // can't get home directory
        Map<String, String> env = new HashMap<>(); // no XDG_DATA_HOME
        assertThrows(Installer.MissingContextException.class, () -> Installer.getInstallLocation(platform, missingHomeDir, env));
    }

    @Test
    public void worksAlpineLinuxXgdSet() {
        Platform platform = Platform.LINUX_ALPINE;
        Map<String, String> env = new HashMap<>();
        env.put("XDG_DATA_HOME", "/user/foo/xgd");
        Path destinationDirectory = Installer.getInstallLocation(platform, Optional.empty(), env);
        assertEquals(
                destinationDirectory.toString().replace('\\', '/'), // replace so that this test works on Windows
                "/user/foo/xgd/snyk");
    }

    @Test
    public void worksAlpineLinuxNotSet() {
        Platform platform = Platform.LINUX_ALPINE;
        Optional<Path> maybeHomeDir = Optional.of(Paths.get("/user/foo"));
        Map<String, String> env = new HashMap<>();
        Path destinationDirectory = Installer.getInstallLocation(platform, maybeHomeDir, env);
        assertEquals(
                destinationDirectory.toString().replace('\\', '/'), // replace so that this test works on Windows
                "/user/foo/.local/share/snyk");
    }

    @Test
    public void aplineThrowsIfXgdNotSetAndHomeDirNotSet() {
        Platform platform = Platform.LINUX_ALPINE;
        Optional<Path> missingHomeDir = Optional.empty(); // can't get home directory
        Map<String, String> env = new HashMap<>(); // no XDG_DATA_HOME
        assertThrows(Installer.MissingContextException.class, () -> Installer.getInstallLocation(platform, missingHomeDir, env));
    }

}
