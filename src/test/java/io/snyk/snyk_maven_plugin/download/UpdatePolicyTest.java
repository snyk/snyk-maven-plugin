package io.snyk.snyk_maven_plugin.download;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdatePolicyTest {

    private static final long HOUR_MS = 1000 * 60 * 60;
    private static final long DAY_MS = HOUR_MS * 24;
    private static final long YEAR_MS = DAY_MS * 365;

    @Test
    public void shouldParseInterval() {
        assertEquals(
            6_000_000,
            UpdatePolicy.parseIntervalMinutesMs("interval:100")
        );
    }

    @Test
    public void shouldRejectNonInterval() {
        assertThrows(
            IllegalArgumentException.class,
            () -> UpdatePolicy.parseIntervalMinutesMs("not an interval")
        );
    }

    @Test
    public void shouldRejectNonNumberInterval() {
        assertThrows(
            IllegalArgumentException.class,
            () -> UpdatePolicy.parseIntervalMinutesMs("interval:hello")
        );
    }

    @Test
    public void shouldUpdateDailyAfterADay() {
        long now = 1000;
        assertTrue(UpdatePolicy.shouldUpdate("daily", now, now + DAY_MS));
    }

    @Test
    public void shouldNotUpdateDailyAfterAnHour() {
        long now = 1000;
        assertFalse(UpdatePolicy.shouldUpdate("daily", now, now + HOUR_MS));
    }

    @Test
    public void shouldUpdateAlways() {
        long now = 1000;
        assertTrue(UpdatePolicy.shouldUpdate("always", now, now));
    }

    @Test
    public void shouldUpdateNever() {
        long now = 1000;
        assertFalse(UpdatePolicy.shouldUpdate("never", now, now + YEAR_MS));
    }

    @Test
    public void shouldUpdateInterval() {
        long now = 1000;
        assertTrue(UpdatePolicy.shouldUpdate("interval:60", now, now + HOUR_MS));
    }

    @Test
    public void shouldNotUpdateInterval() {
        long now = 1000;
        assertFalse(UpdatePolicy.shouldUpdate("interval:100", now, now + HOUR_MS));
    }

}
