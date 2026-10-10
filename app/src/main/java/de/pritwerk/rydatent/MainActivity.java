package de.pritwerk.rydatent;
import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.role.RoleManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.ComponentName;
import android.provider.Settings;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.net.Uri;
import android.view.View;
import android.widget.*;
import java.util.*;
public class MainActivity extends Activity {
 LinearLayout layout;
 @Override protected void onCreate(Bundle b){super.onCreate(b); Prefs.get(this).edit().remove("test_number").apply();}
 @Override protected void onResume(){super.onResume(); BlitzerWatchService.sync(this); draw();}
 private void title(String t){TextView v=new TextView(this);v.setText(t);v.setTextSize(17);v.setPadding(12,18,12,8);layout.addView(v);}
 private void button(String t,Runnable r){Button b=new Button(this);b.setText(t);layout.addView(b);b.setOnClickListener(v->r.run());}
 private void checkbox(String label, String key){CheckBox c=new CheckBox(this); c.setText(label);c.setChecked(Prefs.get(this).getBoolean(key,false));layout.addView(c);c.setOnCheckedChangeListener((x,v)->{
  Prefs.get(this).edit().putBoolean(key,v).apply();
  if("blitzer_enabled".equals(key)) BlitzerWatchService.sync(this);
 });}
 private void draw(){ScrollView sc=new ScrollView(this);layout=new LinearLayout(this);layout.setOrientation(LinearLayout.VERTICAL);layout.setPadding(18,12,18,12);sc.addView(layout);setContentView(sc);
 title("Rydatent 0.1.0-dev.5 – Entwicklungskandidat");
 title("1. Berechtigungen");button("Bluetooth / Kontakte / SMS-Versand anfordern",()->requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS},10));
 button("Anruffilter-Rolle anfordern",()->{RoleManager rm=getSystemService(RoleManager.class);if(rm!=null && rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING))Toast.makeText(this,"Anruffilter bereits aktiv",Toast.LENGTH_SHORT).show();else if(rm!=null && rm.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING))startActivityForResult(rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING),22);else Toast.makeText(this,"Rolle nicht verfügbar",Toast.LENGTH_LONG).show();});
 // Android restriction indicators; neither guarantees scheduling.
 ActivityManager am=getSystemService(ActivityManager.class);
 PowerManager pm=getSystemService(PowerManager.class);
 boolean restricted=am!=null && am.isBackgroundRestricted();
 boolean exempt=pm!=null && pm.isIgnoringBatteryOptimizations(getPackageName());
 title("Hintergrundausführung: "+(am==null?"unbekannt":restricted?"EINGESCHRÄNKT":"nicht eingeschränkt"));
 title("Akkuoptimierung: "+(pm==null?"unbekannt":exempt?"ausgenommen":"nicht ausgenommen"));
 title("Für den Fahrmodus Hintergrundnutzung erlauben und Akku auf Uneingeschränkt setzen.");
 button("Rydatent-App-Info / Akku-Einstellungen öffnen",()->{
  Intent i=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.fromParts("package",getPackageName(),null));
  try { startActivity(i); }
  catch (RuntimeException e) { Toast.makeText(this,"App-Einstellungen nicht verfügbar",Toast.LENGTH_LONG).show(); }
 });
 title("2. Gekoppelte Bluetooth-Geräte");
 if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)!=PackageManager.PERMISSION_GRANTED){title("Bitte zuerst Bluetooth-Berechtigung erteilen.");}
 else { BluetoothManager bm=getSystemService(BluetoothManager.class);BluetoothAdapter a=bm==null?null:bm.getAdapter();Set<BluetoothDevice> paired=a==null?Set.of():a.getBondedDevices();
 if(paired.isEmpty())title("Keine gekoppelten Geräte erkannt.");
 for(BluetoothDevice d:paired){String addr=d.getAddress();title(d.getName()+" ("+addr+")");choice("Blitzer.de", "blitzer",addr);choice("Anruf-Automatik", "sms",addr);}
 }
 title("3. Blitzer.de");checkbox("Bluetooth-Automatik aktivieren", "blitzer_enabled");button("Start-Intent manuell testen",()->{Blitzer.command(this,true);draw();});button("Stop-Intent manuell testen",()->{Blitzer.command(this,false);draw();});
 title("Fahrmodus: prueft bei verbundenem ausgewaehltem Bluetooth alle 15 Sekunden die Blitzer.de-Benachrichtigung.");
 button("Benachrichtigungszugriff erteilen",()->startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
 button("Fahrmodus-Benachrichtigung erlauben",()->requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},11));
 title("4. Anruf-Automatik");
 checkbox("Anrufabweisung und automatische SMS aktivieren", "reply_enabled");
 title("Bei ausgewähltem Bluetooth-Gerät wird jeder eingehende Anruf abgewiesen. Für Anrufe mit Rufnummer wird höchstens einmal je Nummer innerhalb von 10 Minuten eine SMS gesendet. Unterdrückte Rufnummern können abgewiesen, aber nicht per SMS beantwortet werden. SIM-Auswahl noch nicht implementiert.");
 title("5. Status");title(Prefs.get(this).getString("last","Noch kein Ereignis"));
 title("Bluetooth-Berechtigung: "+(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)==PackageManager.PERMISSION_GRANTED?"erteilt":"FEHLT"));
 title("Kontakte für Anruffilter: "+(checkSelfPermission(Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED?"erteilt":"FEHLT"));
 title("SMS-Versand: "+(checkSelfPermission(Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED?"erteilt":"FEHLT"));
 title("Fahrmodus-Benachrichtigung: "+(checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED?"erteilt":"FEHLT"));
 RoleManager role=getSystemService(RoleManager.class);
 title("Anruffilter: "+(role!=null && role.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)?"aktiv":"nicht aktiv"));
 title("Anruf-Bluetooth verbunden: "+(Prefs.connected(this,"sms")?"ja":"nein"));
 title("Blitzer-Bluetooth verbunden: "+(Prefs.connected(this,"blitzer")?"ja":"nein"));
 NotificationManager nm=getSystemService(NotificationManager.class);
 boolean access=nm!=null && nm.isNotificationListenerAccessGranted(new ComponentName(this,BlitzerListener.class));
 title("Blitzer-Wache / Benachrichtigungszugriff: "+(access?"freigegeben":"nicht freigegeben"));
 title("Fahrmodus-Dienst: "+(BlitzerWatchService.running()?"aktiv":"nicht aktiv"));
 button("Anzeige aktualisieren",this::draw);
 title("6. Diagnoseprotokoll (neueste zuerst)");
 title(Prefs.get(this).getString("event_history","Noch keine Ereignisse"));
 button("Protokoll loeschen",()->{Prefs.clearHistory(this);draw();});
 }
 private void choice(String label,String type,String addr){CheckBox c=new CheckBox(this);c.setText(label);c.setChecked(Prefs.chosen(this,type).contains(addr));layout.addView(c);c.setOnCheckedChangeListener((b,yes)->{Set<String> s=Prefs.chosen(this,type);if(yes)s.add(addr);else s.remove(addr);Prefs.get(this).edit().putStringSet("choose_"+type,s).apply();
  if("blitzer".equals(type)) BlitzerWatchService.sync(this);
 });}
}
