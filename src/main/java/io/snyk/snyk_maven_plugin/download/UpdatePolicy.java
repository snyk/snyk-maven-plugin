package io.snyk.snyk_maven_plugin.download;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class UpdatePolicy {

    public static final String DAILY = "daily";
    public static final String NEVER = "never";
    public static final String ALWAYS = "always";

    private static final String INTERVAL_LEFT = "interval";

    public static boolean shouldUpdate(String policy, long thenMs, long nowMs) {
        switch (policy) {
            case DAILY: {
                LocalDate lastModified = Instant.ofEpochMilli(thenMs).atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate now = Instant.ofEpochMilli(nowMs).atZone(ZoneId.systemDefault()).toLocalDate();
                return now.isAfter(lastModified);
            }
            case NEVER: {
                return false;
            }
            case ALWAYS: {
                return true;
            }
            default: {
                return (nowMs - thenMs) >= parseIntervalMinutesMs(policy);
            }
        }
    }

    protected static long parseIntervalMinutesMs(String policy) {
        String[] parts = policy.split(":");
        if (parts.length == 2 && parts[0].equals(INTERVAL_LEFT)) {
            long minutes = Long.parseLong(parts[1]);
            return 1000 * 60 * minutes;
        }
        throw new IllegalArgumentException("Unknown update policy provided (" + policy + ").");
    }
}
