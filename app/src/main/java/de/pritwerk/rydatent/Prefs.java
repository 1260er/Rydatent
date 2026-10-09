package de.pritwerk.rydatent;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;
final class Prefs {
 static SharedPreferences get(Context c) {return c.getSharedPreferences("test", Context.MODE_PRIVATE);}
 static Set<String> chosen(Context c, String type) {return new HashSet<>(get(c).getStringSet("choose_"+type, Set.of()));}
 static Set<String> active(Context c) {return new HashSet<>(get(c).getStringSet("connected",Set.of()));}
 static boolean connected(Context c,String type) {return Logic.shouldRun(active(c), chosen(c,type));}
 static void note(Context c,String text) {get(c).edit().putString("last",text).apply();}
 static void state(Context c,String address,boolean on) {
  Set<String> active=active(c); if(on) active.add(address); else active.remove(address);
  get(c).edit().putStringSet("connected",active).apply();
 }
}
