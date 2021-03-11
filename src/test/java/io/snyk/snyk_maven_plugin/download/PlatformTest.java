package io.snyk.snyk_maven_plugin.download;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlatformTest {

    @Test
    public void resolvesPlatformMacOs() {
        assertEquals(Platform.MAC_OS, Platform.detect("mac os x"));
        assertEquals(Platform.MAC_OS, Platform.detect("darwin"));
    }

    @Test
    public void resolvesPlatformLinux() {
        assertEquals(Platform.LINUX, Platform.detect("linux"));
    }

    @Test
    public void resolvesPlatformWindows() {
        assertEquals(Platform.WINDOWS, Platform.detect("windows"));
    }

    @Test
    public void throwsOnUnrecognizedPlatform() {
        assertThrows(IllegalArgumentException.class, () -> Platform.detect("nes64"));
    }

}
