package de.pritwerk.rydatent;
import java.util.Set;
public final class Logic {
 private Logic() {}
 public static boolean shouldRun(Set<String> connected, Set<String> selected) {
  for (String address : connected) if (selected.contains(address)) return true;
  return false;
 }
 public static boolean shouldReply(String caller, String testNumber, boolean enabled, boolean connected) {
  return enabled && connected && caller != null && !caller.isBlank() && caller.equals(testNumber);
 }
}
