package io.snyk.snyk_maven_plugin.download;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PlatformTest {

    @Test
    public void resolvesPlatformMacOs() {
        Map<Object, Object> systemPropertiesMacOsX = new HashMap<>();
        systemPropertiesMacOsX.put("os.name", "mac os x");
        Platform p1 = Platform.detect(systemPropertiesMacOsX);
        assertEquals(p1, Platform.MAC_OS);

        Map<Object, Object> systemPropertiesDarwin = new HashMap<>();
        systemPropertiesDarwin.put("os.name", "darwin");
        Platform p = Platform.detect(systemPropertiesDarwin);
        assertEquals(p, Platform.MAC_OS);
    }

    @Test
    public void resolvesPlatformLinux() {
        Map<Object, Object> systemProperties = new HashMap<>();
        systemProperties.put("os.name", "linux");
        Platform p = Platform.detect(systemProperties);
        assertEquals(p, Platform.LINUX);
    }

    @Test
    public void resolvesPlatformWindows() {
        Map<Object, Object> systemProperties = new HashMap<>();
        systemProperties.put("os.name", "windows");
        Platform p = Platform.detect(systemProperties);
        assertEquals(p, Platform.WINDOWS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnUnrecognizedPlatform() {
        Map<Object, Object> systemProperties = new HashMap<>();
        systemProperties.put("os.name", "nes64");

//      TODO: better to use assertThrows
        Platform p = Platform.detect(systemProperties);
    }

}
