package io.snyk.snyk_maven_plugin.download;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExecutableDestinationTest {

    @Test
    public void worksWindows() {
        Platform platform = Platform.WINDOWS;
        Map<String, String> env = new HashMap<>();
        env.put("APPDATA", "c:\\users\\foo\\appdata");
        File destination = ExecutableDestination.getDownloadDestination(platform, Optional.empty(), env);
        assertEquals(
            toWindowsPath(destination),
            "c:\\users\\foo\\appdata\\Snyk\\snyk-win.exe"
        );
    }

    @Test
    public void windowsThrowsIfAppDataNotSet() {
        Platform platform = Platform.WINDOWS;
        Map<String, String> env = new HashMap<>(); // no APPDATA
        assertThrows(
            ExecutableDestination.MissingContextException.class,
            () -> ExecutableDestination.getDownloadDestination(platform, Optional.empty(), env)
        );
    }

    @Test
    public void worksMac() {
        Platform platform = Platform.MAC_OS;
        Optional<Path> maybeHomeDir = Optional.of(Paths.get("/Users/foo"));
        Map<String, String> env = new HashMap<>();
        File destination = ExecutableDestination.getDownloadDestination(platform, maybeHomeDir, env);
        assertEquals(
            toUnixPath(destination),
            "/Users/foo/Library/Application Support/Snyk/snyk-macos"
        );
    }

    @Test
    public void macThrowsIfAppDataNotSet() {
        Platform platform = Platform.MAC_OS;
        Optional<Path> missingHomeDir = Optional.empty(); // can't get home directory
        Map<String, String> env = new HashMap<>();
        assertThrows(
            ExecutableDestination.MissingContextException.class,
            () -> ExecutableDestination.getDownloadDestination(platform, missingHomeDir, env)
        );
    }

    @Test
    public void worksLinuxXgdSet() {
        Platform platform = Platform.LINUX;
        Map<String, String> env = new HashMap<>();
        env.put("XDG_DATA_HOME", "/user/foo/xgd");
        File destination = ExecutableDestination.getDownloadDestination(platform, Optional.empty(), env);
        assertEquals(
            toUnixPath(destination),
            "/user/foo/xgd/snyk/snyk-linux"
        );
    }

    @Test
    public void worksLinuxNotSet() {
        Platform platform = Platform.LINUX;
        Optional<Path> maybeHomeDir = Optional.of(Paths.get("/user/foo"));
        Map<String, String> env = new HashMap<>();
        File destination = ExecutableDestination.getDownloadDestination(platform, maybeHomeDir, env);
        assertEquals(
            toUnixPath(destination),
            "/user/foo/.local/share/snyk/snyk-linux"
        );
    }

    @Test
    public void linuxThrowsIfXgdNotSetAndHomeDirNotSet() {
        Platform platform = Platform.LINUX;
        Optional<Path> missingHomeDir = Optional.empty(); // can't get home directory
        Map<String, String> env = new HashMap<>(); // no XDG_DATA_HOME
        assertThrows(
            ExecutableDestination.MissingContextException.class,
            () -> ExecutableDestination.getDownloadDestination(platform, missingHomeDir, env)
        );
    }

    @Test
    public void worksAlpineLinuxXgdSet() {
        Platform platform = Platform.LINUX_ALPINE;
        Map<String, String> env = new HashMap<>();
        env.put("XDG_DATA_HOME", "/user/foo/xgd");
        File destination = ExecutableDestination.getDownloadDestination(platform, Optional.empty(), env);
        assertEquals(
            toUnixPath(destination),
            "/user/foo/xgd/snyk/snyk-alpine"
        );
    }

    @Test
    public void worksAlpineLinuxNotSet() {
        Platform platform = Platform.LINUX_ALPINE;
        Optional<Path> maybeHomeDir = Optional.of(Paths.get("/user/foo"));
        Map<String, String> env = new HashMap<>();
        File destination = ExecutableDestination.getDownloadDestination(platform, maybeHomeDir, env);
        assertEquals(
            toUnixPath(destination),
            "/user/foo/.local/share/snyk/snyk-alpine"
        );
    }

    @Test
    public void worksLinuxArm64XgdSet() {
        Platform platform = Platform.LINUX_ARM64;
        Map<String, String> env = new HashMap<>();
        env.put("XDG_DATA_HOME", "/user/foo/xgd");
        File destination = ExecutableDestination.getDownloadDestination(platform, Optional.empty(), env);
        assertEquals(
                toUnixPath(destination),
                "/user/foo/xgd/snyk/snyk-linux-arm64"
        );
    }

    @Test
    public void worksLinuxArm64NotSet() {
        Platform platform = Platform.LINUX_ARM64;
        Optional<Path> maybeHomeDir = Optional.of(Paths.get("/user/foo"));
        Map<String, String> env = new HashMap<>();
        File destination = ExecutableDestination.getDownloadDestination(platform, maybeHomeDir, env);
        assertEquals(
                toUnixPath(destination),
                "/user/foo/.local/share/snyk/snyk-linux-arm64"
        );
    }

    @Test
    public void alpineThrowsIfXgdNotSetAndHomeDirNotSet() {
        Platform platform = Platform.LINUX_ALPINE;
        Optional<Path> missingHomeDir = Optional.empty(); // can't get home directory
        Map<String, String> env = new HashMap<>(); // no XDG_DATA_HOME
        assertThrows(
            ExecutableDestination.MissingContextException.class,
            () -> ExecutableDestination.getDownloadDestination(platform, missingHomeDir, env)
        );
    }

    private String toWindowsPath(File destination) {
        return destination.getPath().replace('/', '\\');
    }

    private String toUnixPath(File destination) {
        return destination.getPath().replace('\\', '/');
    }
}
