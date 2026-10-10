package de.pritwerk.rydatent;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Experimental watchdog: the disappearance of the ongoing Blitzer.de PRO
 * notification is only a signal to re-check, not proof that the app stopped.
 * The user must explicitly grant notification access.
 */
public class BlitzerListener extends NotificationListenerService {
    private static final String BLITZER_PACKAGE = "de.blitzer.plus";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean observedOngoing;
    private boolean connectedToListener;
    private long lastAttemptElapsed = -60_000L;

    @Override
    public void onListenerConnected() {
        connectedToListener = true;
        observedOngoing = hasOngoingBlitzerNotification();
        Prefs.note(this, observedOngoing
                ? "Blitzer-Wache aktiv; laufende Benachrichtigung erkannt"
                : "Blitzer-Wache aktiv; noch keine laufende Benachrichtigung erkannt");
    }

    @Override
    public void onListenerDisconnected() {
        connectedToListener = false;
        handler.removeCallbacksAndMessages(null);
        Prefs.note(this, "Blitzer-Wache getrennt; automatische Erkennung pausiert");
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        connectedToListener = false;
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (isOngoingBlitzer(sbn) && !observedOngoing) {
            observedOngoing = true;
            Prefs.note(this, "Blitzer-Wache: laufende Benachrichtigung erkannt");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (!isOngoingBlitzer(sbn) || !observedOngoing) return;
        Prefs.note(this, "Blitzer-Wache: laufende Benachrichtigung verschwunden");
        handler.removeCallbacksAndMessages(null);
        // Kurz warten: die App kann ihre Benachrichtigung gerade nur ersetzen.
        handler.postDelayed(this::checkAndRestart, 3_000L);
    }

    private boolean isOngoingBlitzer(StatusBarNotification sbn) {
        return sbn != null && BLITZER_PACKAGE.equals(sbn.getPackageName())
                && sbn.isOngoing();
    }

    private boolean hasOngoingBlitzerNotification() {
        if (!connectedToListener) return false;
        try {
            StatusBarNotification[] list = getActiveNotifications();
            if (list != null) {
                for (StatusBarNotification notification : list) {
                    if (isOngoingBlitzer(notification)) return true;
                }
            }
        } catch (SecurityException ex) {
            Prefs.note(this, "Blitzer-Wache: Benachrichtigungszugriff fehlt");
            return true; // kein Neustart ohne vertrauenswuerdigen Status
        }
        return false;
    }

    private void checkAndRestart() {
        if (!connectedToListener) return;
        boolean enabled = Prefs.get(this).getBoolean("blitzer_enabled", false);
        boolean selectedBtConnected = Prefs.connected(this, "blitzer");
        boolean notificationPresent = hasOngoingBlitzerNotification();
        long now = SystemClock.elapsedRealtime();
        long elapsed = now - lastAttemptElapsed;
        if (Logic.mayRestartBlitzer(enabled, selectedBtConnected,
                observedOngoing, notificationPresent, elapsed)) {
            lastAttemptElapsed = now;
            Prefs.note(this, "Blitzer-Wache: Neustart bei aktiver BT-Auswahl versucht");
            Blitzer.command(this, true);
        } else {
            Prefs.note(this, "Blitzer-Wache: kein Neustart (BT/Automatik/Benachrichtigung/Sperre)");
        }
    }
}
