package io.snyk.snyk_maven_plugin.download;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CLIVersionsTest {

    @Test
    public void acceptsLatest() {
        assertEquals("latest", CLIVersions.sanitize("latest"));
        assertEquals(CLIVersions.LATEST_VERSION_KEYWORD, CLIVersions.sanitize(CLIVersions.LATEST_VERSION_KEYWORD));
    }

    @Test
    public void acceptsVersion() {
        assertEquals("v1.456.3", CLIVersions.sanitize("1.456.3"));
    }

    @Test
    public void throwsOnInvalidVersions() {
        assertThrows(IllegalArgumentException.class, () -> CLIVersions.sanitize("dev"));
        assertThrows(IllegalArgumentException.class, () -> CLIVersions.sanitize("1.13"));
        assertThrows(IllegalArgumentException.class, () -> CLIVersions.sanitize("1.13.0-alpha"));
    }
}
