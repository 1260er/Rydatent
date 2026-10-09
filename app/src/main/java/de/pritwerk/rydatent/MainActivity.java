package de.pritwerk.rydatent;
import android.Manifest;
import android.app.Activity;
import android.app.role.RoleManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import java.util.*;
public class MainActivity extends Activity {
 LinearLayout layout;
 @Override protected void onCreate(Bundle b){super.onCreate(b); draw();}
 private void title(String t){TextView v=new TextView(this);v.setText(t);v.setTextSize(17);v.setPadding(12,18,12,8);layout.addView(v);}
 private void button(String t,Runnable r){Button b=new Button(this);b.setText(t);layout.addView(b);b.setOnClickListener(v->r.run());}
 private void checkbox(String label, String key){CheckBox c=new CheckBox(this); c.setText(label);c.setChecked(Prefs.get(this).getBoolean(key,false));layout.addView(c);c.setOnCheckedChangeListener((x,v)->Prefs.get(this).edit().putBoolean(key,v).apply());}
 private void draw(){ScrollView sc=new ScrollView(this);layout=new LinearLayout(this);layout.setOrientation(1);layout.setPadding(18,12,18,12);sc.addView(layout);setContentView(sc);
 title("Rydatent 0.1.0 – isolierter Machbarkeitstest");
 title("1. Berechtigungen");button("Bluetooth / Kontakte / SMS anfordern",()->requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS,Manifest.permission.RECEIVE_SMS},10));
 button("Anruffilter-Rolle anfordern",()->{RoleManager rm=getSystemService(RoleManager.class);if(rm!=null && rm.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING))startActivityForResult(rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING),22);else Toast.makeText(this,"Rolle nicht verfügbar",Toast.LENGTH_LONG).show();});
 title("2. Gekoppelte Bluetooth-Geräte");
 if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)!=PackageManager.PERMISSION_GRANTED){title("Bitte zuerst Bluetooth-Berechtigung erteilen.");}
 else { BluetoothManager bm=getSystemService(BluetoothManager.class);BluetoothAdapter a=bm==null?null:bm.getAdapter();Set<BluetoothDevice> paired=a==null?Set.of():a.getBondedDevices();
 if(paired.isEmpty())title("Keine gekoppelten Geräte erkannt.");
 for(BluetoothDevice d:paired){String addr=d.getAddress();title(d.getName()+" ("+addr+")");choice("Blitzer.de", "blitzer",addr);choice("Anruf/SMS-Test", "sms",addr);}
 }
 title("3. Blitzer.de");checkbox("Bluetooth-Automatik aktivieren", "blitzer_enabled");button("Start-Intent manuell testen",()->{Blitzer.command(this,true);draw();});button("Stop-Intent manuell testen",()->{Blitzer.command(this,false);draw();});
 title("4. Anruf-Test (nur freigegebene Nummer!)");EditText number=new EditText(this);number.setHint("Exakte Testnummer mit Vorwahl");number.setInputType(InputType.TYPE_CLASS_PHONE);number.setText(Prefs.get(this).getString("test_number",""));layout.addView(number);
 button("Testnummer speichern",()->{Prefs.get(this).edit().putString("test_number",number.getText().toString().trim()).apply();Toast.makeText(this,"Testnummer gespeichert",Toast.LENGTH_SHORT).show();});
 checkbox("SMS und Anrufabweisung für Testnummer aktivieren", "reply_enabled");
 title("Test-SMS nur bei ausgewähltem Bluetooth-Gerät, exakt übereinstimmender Nummer und höchstens einmal alle 10 Minuten. SIM-Auswahl noch nicht implementiert.");
 title("5. Status");title(Prefs.get(this).getString("last","Noch kein Ereignis"));button("Anzeige aktualisieren",this::draw);
 }
 private void choice(String label,String type,String addr){CheckBox c=new CheckBox(this);c.setText(label);c.setChecked(Prefs.chosen(this,type).contains(addr));layout.addView(c);c.setOnCheckedChangeListener((b,yes)->{Set<String> s=Prefs.chosen(this,type);if(yes)s.add(addr);else s.remove(addr);Prefs.get(this).edit().putStringSet("choose_"+type,s).apply();});}
}
