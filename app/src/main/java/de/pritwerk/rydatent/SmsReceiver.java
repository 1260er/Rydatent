package de.pritwerk.rydatent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class SmsReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c, Intent i) {
  if("android.provider.Telephony.SMS_RECEIVED".equals(i.getAction()))
   Prefs.note(c,"Eingehende SMS erkannt (keine automatische Antwort)");
 }
}
