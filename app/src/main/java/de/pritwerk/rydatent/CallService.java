package de.pritwerk.rydatent;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telephony.SmsManager;
import android.Manifest;
import android.content.pm.PackageManager;
public class CallService extends CallScreeningService {
 @Override public void onScreenCall(Call.Details call) {
  boolean incoming=call.getCallDirection()==Call.Details.DIRECTION_INCOMING;
  String number=call.getHandle()==null?null:call.getHandle().getSchemeSpecificPart();
  boolean enabled=Prefs.get(this).getBoolean("reply_enabled",false);
  boolean can= incoming && Logic.shouldReply(number,Prefs.get(this).getString("test_number",""),enabled,Prefs.connected(this,"sms"));
  if(incoming) Prefs.note(this,"Anruf erkannt; Testregel "+(can?"passt":"passt nicht"));
  if(can && checkSelfPermission(Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED) {
   // Only one test SMS per number in 10 minutes, reject only after send attempt.
   String key="last_sms_"+number;
   long now=System.currentTimeMillis();
   if(now-Prefs.get(this).getLong(key,0)>600000L) {
    try {
     SmsManager manager=getSystemService(SmsManager.class);
     manager.sendTextMessage(number,null,"Ich fahre gerade Motorrad, ich rufe zurück.",null,null);
     Prefs.get(this).edit().putLong(key,now).apply();
     respondToCall(call,new CallResponse.Builder().setDisallowCall(true).setRejectCall(true).build()); return;
    } catch(Exception ex) {Prefs.note(this,"SMS-Fehler: "+ex.getClass().getSimpleName());}
   }
  }
  if(incoming) respondToCall(call,new CallResponse.Builder().build());
 }
}
