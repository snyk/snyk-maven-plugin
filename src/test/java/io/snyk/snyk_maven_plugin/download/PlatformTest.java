package io.snyk.snyk_maven_plugin.download;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlatformTest {

    @Test
    public void shouldSupportMacOS() {
        assertEquals(Platform.MAC_OS, Platform.detect("mac os x", "x86_64"));
        assertEquals(Platform.MAC_OS, Platform.detect("darwin", "x86_64"));
    }

    @Test
    public void shouldSupportLinux() {
        assertEquals(Platform.LINUX, Platform.detect("linux", "x86_64"));
    }

    @Test
    public void shouldSupportLinuxArm64() {
        assertEquals(Platform.LINUX_ARM64, Platform.detect("linux", "aarch64"));
    }

    @Test
    public void shouldSupportWindows() {
        assertEquals(Platform.WINDOWS, Platform.detect("windows", "x86_64"));
    }

    @Test
    public void shouldNotSupportUnknownPlatforms() {
        assertThrows(IllegalArgumentException.class, () -> Platform.detect("nes64", "ppc64"));
    }

    @Test
    public void shouldIgnoreLetterCasing() {
        assertEquals(Platform.MAC_OS, Platform.detect("MaC Os X", "x86_64"));
    }

}
