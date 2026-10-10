package de.pritwerk.rydatent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telephony.SmsManager;

public class CallService extends CallScreeningService {
    private static final String SMS_TEXT = "Ich fahre gerade Motorrad, ich rufe zurück.";

    @Override
    public void onScreenCall(Call.Details call) {
        if (call.getCallDirection() != Call.Details.DIRECTION_INCOMING) {
            return;
        }

        String number = call.getHandle() == null
                ? null : call.getHandle().getSchemeSpecificPart();
        boolean enabled = Prefs.get(this).getBoolean("reply_enabled", false);
        boolean connected = Prefs.connected(this, "sms");
        String testNumber = Prefs.get(this).getString("test_number", "");
        boolean reject = Logic.shouldReply(number, testNumber, enabled, connected);

        String reason;
        if (!enabled) reason = "Testregel deaktiviert";
        else if (!connected) reason = "kein ausgewähltes Anruf-Bluetooth erkannt";
        else if (number == null || number.isBlank()) reason = "keine Rufnummer";
        else if (!number.equals(testNumber)) reason = "Testnummer abweichend";
        else reason = "Testregel passt";
        Prefs.note(this, "Anruf geprüft: " + reason);

        // Die Abweisung muss innerhalb von 5 Sekunden erfolgen. Insbesondere
        // darf sie NICHT vom SMS-Versand oder von dessen Wiederholsperre abhängen.
        CallResponse response = reject
                ? new CallResponse.Builder().setDisallowCall(true).setRejectCall(true).build()
                : new CallResponse.Builder().build();
        respondToCall(call, response);
        Prefs.note(this, reject ? "Anrufabweisung angefordert" : "Anruf zugelassen");

        if (!reject) return;
        if (checkSelfPermission(Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Prefs.note(this, "Anruf abgewiesen; SEND_SMS-Berechtigung fehlt");
            return;
        }
        long now = System.currentTimeMillis();
        String key = "last_sms_" + number;
        long previous = Prefs.get(this).getLong(key, 0L);
        if (!Logic.smsCooldownElapsed(previous, now)) {
            Prefs.note(this, "SMS gesperrt (10 Minuten); Anrufabweisung bleibt aktiv");
            return;
        }
        try {
            SmsManager sms = getSystemService(SmsManager.class);
            if (sms == null) {
                Prefs.note(this, "SMS-Dienst nicht verfügbar");
                return;
            }
            // Rückgabe bedeutet nur, dass der Versand angestoßen wurde.
            sms.sendTextMessage(number, null, SMS_TEXT, null, null);
            Prefs.get(this).edit().putLong(key, now).apply();
            Prefs.note(this, "SMS angestoßen; Zustellung nicht bestätigt");
        } catch (Exception ex) {
            Prefs.note(this, "SMS-Fehler: " + ex.getClass().getSimpleName());
        }
    }
}
