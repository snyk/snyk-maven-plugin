package io.snyk.snyk_maven_plugin.download;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlatformTest {

    @Test
    public void shouldSupportMacOS() {
        assertEquals(Platform.MAC_OS, Platform.detect("mac os x"));
        assertEquals(Platform.MAC_OS, Platform.detect("darwin"));
    }

    @Test
    public void shouldSupportLinux() {
        assertEquals(Platform.LINUX, Platform.detect("linux"));
    }

    @Test
    public void shouldSupportWindows() {
        assertEquals(Platform.WINDOWS, Platform.detect("windows"));
    }

    @Test
    public void shouldNotSupportUnknownPlatforms() {
        assertThrows(IllegalArgumentException.class, () -> Platform.detect("nes64"));
    }

    @Test
    public void shouldIgnoreLetterCasing() {
        assertEquals(Platform.MAC_OS, Platform.detect("MaC Os X"));
    }

}
