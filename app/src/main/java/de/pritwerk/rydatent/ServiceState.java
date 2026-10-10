package de.pritwerk.rydatent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

/**
 * Persistierter Heartbeat des Fahrmodus-Dienstes.
 * Er ersetzt die flüchtige Prozessvariable als Statusquelle für die Oberfläche.
 */
final class ServiceState {
    private static final String PREFS = "service_state";
    private static final String KEY_HEARTBEAT = "heartbeat";

    static final long STALE_AFTER_MS = 45_000L;

    private ServiceState() {}

    static void heartbeat(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_HEARTBEAT, SystemClock.uptimeMillis())
                .commit();
    }

    static void stopped(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_HEARTBEAT)
                .commit();
    }

    static long heartbeatMs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getLong(KEY_HEARTBEAT, 0L);
    }

    static boolean isFresh(Context context) {
        return isFreshAt(
                heartbeatMs(context),
                SystemClock.uptimeMillis());
    }

    static boolean isFreshAt(long heartbeatMs, long nowUptimeMs) {
        return heartbeatMs > 0L
                && heartbeatMs <= nowUptimeMs
                && nowUptimeMs - heartbeatMs <= STALE_AFTER_MS;
    }

    static String label(Context context, boolean wanted) {
        if (!wanted) return "nicht aktiv";
        if (isFresh(context)) return "aktiv und reagiert";
        return "reagiert nicht";
    }
}
