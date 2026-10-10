package de.pritwerk.rydatent;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

/** A low-work 15-second check, only while selected Bluetooth is recorded connected. */
public final class BlitzerWatchService extends Service {
    private static final String CHANNEL = "rydatent_drive";
    private static final int NOTIFICATION_ID = 1203;
    static final long CHECK_INTERVAL_MS = 15_000L;
    static final long RETRY_INTERVAL_MS = 45_000L;
    private static volatile boolean active;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable checkTask = this::checkAndReschedule;
    private int previousStatus = 2; // 2=not checked, -1=unknown, 0=absent, 1=present
    private long nextRetryElapsed;

    /** Called only on genuine state/configuration changes, not at every 15s tick. */
    static void sync(Context context) {
        boolean shouldWatch = Logic.shouldWatchBlitzer(
                Prefs.get(context).getBoolean("blitzer_enabled", false),
                Prefs.connected(context, "blitzer"));
        Intent service = new Intent(context, BlitzerWatchService.class);
        if (!shouldWatch) {
            context.stopService(service);
            return;
        }
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            Prefs.note(context, "Fahrmodus nicht gestartet: Bluetooth-Berechtigung fehlt");
            return;
        }
        if (active) return;
        try {
            context.startForegroundService(service);
        } catch (RuntimeException exception) {
            // Android 12+ may forbid foreground service starts from a background receiver.
            Prefs.note(context, "Fahrmodusstart von Android blockiert: "
                    + exception.getClass().getSimpleName());
        }
    }

    static boolean running() {
        return active;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (nm != null) {
            nm.createNotificationChannel(new NotificationChannel(
                    CHANNEL, "Rydatent Fahrmodus", NotificationManager.IMPORTANCE_LOW));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!wanted()) {
            stopSelf();
            return START_NOT_STICKY;
        }
        try {
            Intent open = new Intent(this, MainActivity.class);
            PendingIntent tap = PendingIntent.getActivity(this, 0, open,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            Notification note = new Notification.Builder(this, CHANNEL)
                    .setSmallIcon(android.R.drawable.ic_menu_compass)
                    .setContentTitle("Fahrmodus aktiv")
                    .setOngoing(true)
                    .setContentIntent(tap)
                    .build();
            startForeground(NOTIFICATION_ID, note,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
        } catch (RuntimeException exception) {
            Prefs.note(this, "Fahrmodus nicht möglich: "
                    + exception.getClass().getSimpleName());
            stopSelf();
            return START_NOT_STICKY;
        }
        if (!active) {
            active = true;
            Prefs.note(this, "Fahrmodus gestartet: Benachrichtigung alle 15 Sekunden prüfen");
            // The Bluetooth receiver already sends the start intent. Give Blitzer.de
            // 15 seconds to create its notification before evaluating absence.
            handler.postDelayed(checkTask, CHECK_INTERVAL_MS);
        }
        return START_STICKY;
    }

    private boolean wanted() {
        return Logic.shouldWatchBlitzer(
                Prefs.get(this).getBoolean("blitzer_enabled", false),
                Prefs.connected(this, "blitzer"));
    }

    private void checkAndReschedule() {
        if (!wanted()) {
            Prefs.note(this, "Fahrmodus beendet: kein ausgewähltes Bluetooth mehr aktiv");
            stopSelf();
            return;
        }

        Boolean present = BlitzerListener.blitzerNotificationPresent();
        long now = SystemClock.elapsedRealtime();
        int status = present == null ? -1 : (present ? 1 : 0);
        if (status != previousStatus) {
            if (status == -1) {
                Prefs.note(this, "Blitzer-Status unbekannt: Benachrichtigungszugriff prüfen");
            } else if (status == 1) {
                Prefs.note(this, "Blitzer-Benachrichtigung vorhanden");
            } else {
                Prefs.note(this, "Blitzer-Benachrichtigung fehlt");
            }
            previousStatus = status;
        }

        if (status == 1) {
            nextRetryElapsed = 0L; // A later missing notification can be restarted immediately.
        } else if (status == 0) {
            if (Logic.shouldRestartMissingNotification(
                    Prefs.get(this).getBoolean("blitzer_enabled", false),
                    Prefs.connected(this, "blitzer"), false, now, nextRetryElapsed)) {
                nextRetryElapsed = now + RETRY_INTERVAL_MS;
                Prefs.note(this, "Blitzer-Wache: Start-Intent wegen fehlender Benachrichtigung");
                Blitzer.command(this, true);
            }
        }
        handler.postDelayed(checkTask, CHECK_INTERVAL_MS);
    }

    @Override
    public void onDestroy() {
        active = false;
        handler.removeCallbacks(checkTask);
        Prefs.note(this, "Fahrmodus angehalten");
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
