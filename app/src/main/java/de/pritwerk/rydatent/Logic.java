package de.pritwerk.rydatent;

import java.util.Set;

public final class Logic {
    private Logic() {}

    public static boolean shouldRun(Set<String> connected, Set<String> selected) {
        for (String address : connected) {
            if (selected.contains(address)) return true;
        }
        return false;
    }

    public static boolean shouldReply(String caller, String testNumber,
                                      boolean enabled, boolean connected) {
        return enabled && connected && caller != null && !caller.isBlank()
                && caller.equals(testNumber);
    }

    public static boolean smsCooldownElapsed(long previous, long now) {
        return previous == 0L || now - previous >= 600_000L;
    }

    public static boolean shouldWatchBlitzer(boolean enabled, boolean connected) {
        return enabled && connected;
    }

    public static boolean shouldRestartMissingNotification(boolean enabled,
            boolean connected, boolean notificationPresent, long nowElapsed, long retryAfterElapsed) {
        return shouldWatchBlitzer(enabled, connected)
                && !notificationPresent && nowElapsed >= retryAfterElapsed;
    }
}
