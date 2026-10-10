# Rydatent – Entwicklungskandidat 0.1.0-dev.6

Kein Stable-Release. Die Testoberflaeche bleibt bewusst technisch.

## Funktionsumfang
- Bluetooth-Multiwahl je Funktion und bekannte Start-/Stopp-Intents fuer Blitzer.de PRO.
- Die Anruf-Automatik weist bei aktivierter Funktion und ausgewaehltem verbundenem
  Bluetooth jeden eingehenden Anruf ab. Wenn eine Rufnummer vorhanden ist, wird
  hoechstens einmal pro Nummer innerhalb von 10 Minuten die Fahr-SMS angestossen.
- Keine Verarbeitung eingehender SMS oder Messenger-Nachrichten.
- Der Fahrmodus laeuft als Android-Foreground-Service, sobald mindestens eine aktive
  Fahrfunktion ein ausgewaehltes verbundenes Bluetooth-Geraet verwendet.
- Die sichtbare Rydatent-Dauerbenachrichtigung lautet nur "Fahrmodus aktiv".
- Solange der Fahrmodus-Dienst laeuft, schreibt er alle 15 Sekunden einen Heartbeat.
  Ein Heartbeat, der aelter als 45 Sekunden ist, wird als nicht mehr reagierender
  Dienst bewertet.
- Bei aktiver Blitzer-Automatik prueft derselbe 15-Sekunden-Takt ueber den
  NotificationListenerService, ob eine Benachrichtigung von de.blitzer.plus vorhanden
  ist. Fehlt sie, wird Blitzer.de neu gestartet. Weitere Startversuche werden auf
  mindestens 45 Sekunden begrenzt.
- Der Schalter "Nach Geraeteneustart automatisch aktivieren" bestimmt, ob die
  Fahr-Automatik nach BOOT_COMPLETED sofort wieder scharf ist. Ist der Schalter aus,
  aktiviert das einmalige Oeffnen von Rydatent die Automatik fuer die aktuelle Sitzung.
- Nach einem App-Update wird ein zuvor aktiver Laufzeitzustand beibehalten und der
  Fahrmodus-Dienst bei Bedarf erneut angefordert.
- Das Diagnoseprotokoll speichert Zustandsaenderungen, aber nicht jeden Heartbeat.

## Berechtigungen
- BLUETOOTH_CONNECT: gekoppelte und verbundene Fahrgeraete sowie connectedDevice-Dienst.
- READ_CONTACTS: erforderlich, damit CallScreeningService auch gespeicherte Kontakte
  zur Anrufpruefung erhaelt.
- SEND_SMS: automatische Fahrantwort.
- POST_NOTIFICATIONS: sichtbare Fahrmodus-Benachrichtigung.
- FOREGROUND_SERVICE und FOREGROUND_SERVICE_CONNECTED_DEVICE: Fahrmodus-Dienst.
- RECEIVE_BOOT_COMPLETED: optionale automatische Aktivierung nach Geraeteneustart.
  Dafuer gibt es keinen separaten Laufzeit-Berechtigungsdialog.
- CallScreeningService-Rolle: Anrufe pruefen und abweisen.
- Benachrichtigungszugriff: aktive Blitzer.de-Benachrichtigung pruefen.
- Es werden weiterhin keine Berechtigungen fuer eingehende SMS, direkten Anrufaufbau
  oder allgemeinen Telefonstatus angefordert.

## Zuverlaessigkeit und Grenzen
- START_STICKY bleibt fuer normale Android-Prozesswiederherstellung aktiv.
- Der persistierte Heartbeat ersetzt die bisherige rein prozesslokale Statusanzeige.
- Beim Geraeteneustart wird die gespeicherte Liste verbundener Bluetooth-Geraete
  absichtlich geloescht, damit kein Zustand aus der vorherigen Sitzung als aktuell gilt.
- Danach basiert die Verbindungserkennung weiterhin auf neuen ACL-Verbindungsereignissen.
  Ein bereits vor BOOT_COMPLETED aufgebauter Bluetooth-Link kann deshalb weiterhin eine
  spaetere direkte Verbindungssynchronisation erforderlich machen.
- Ein vom Benutzer erzwungener App-Stopp wird nicht umgangen.
- Android kann Starts eines Foreground-Service aus bestimmten Hintergrundsituationen
  blockieren. Solche Fehler werden im Diagnoseprotokoll festgehalten.
- Eine sichtbare Blitzer.de-Benachrichtigung ist nur das vereinbarte Laufzeit-Indiz und
  kein technischer Nachweis, dass die Warnfunktion selbst korrekt arbeitet.
- SMS nutzt derzeit die Standard-SIM. Eine feste SIM-1-Auswahl und echte
  Zustellbestaetigung sind noch nicht implementiert.

## Smoke-Test fuer die Entwicklungsbasis
1. Dev.6 ueber Obtainium installieren und Rydatent einmal oeffnen.
2. Bluetooth, Kontakte, SMS-Versand, Call-Screening-Rolle,
   Benachrichtigungszugriff und Fahrmodus-Benachrichtigung kontrollieren.
3. Autostart einschalten.
4. Mit dem ausgewaehlten Blitzer-Bluetooth verbinden. Blitzer.de muss starten und
   die Rydatent-Benachrichtigung muss ausschliesslich "Fahrmodus aktiv" anzeigen.
5. Nach mindestens 15 Sekunden die Statusanzeige aktualisieren. Der Fahrmodus-Dienst
   muss "aktiv und reagiert" melden.
6. Blitzer.de manuell schliessen. Innerhalb des naechsten Prueftakts muss bei fehlender
   Blitzer-Benachrichtigung ein Startversuch erfolgen. Wiederholungen duerfen nicht
   sekundenweise gespammt werden.
7. Verbindung trennen. Wenn keine andere Fahrfunktion ein passendes Bluetooth-Geraet
   verwendet, muss die Fahrmodus-Benachrichtigung verschwinden.
8. Mit dem ausgewaehlten Motorrad-Bluetooth verbinden. Der Fahrmodus-Dienst muss auch
   fuer die Anruf-Automatik aktiv sein.
9. Zwei Anrufe derselben Nummer innerhalb von 10 Minuten: beide abweisen, nur eine SMS.
10. Von einer zweiten Nummer anrufen: Anruf abweisen und fuer diese Nummer eine SMS.
11. Geraet mit eingeschaltetem Autostart neu starten. Rydatent danach nicht oeffnen.
    Anschliessend ein ausgewaehltes Bluetooth-Geraet verbinden und pruefen, dass die
    entsprechende Automatik und der Fahrmodus-Dienst starten.
12. Optional Autostart ausschalten und neu starten. Vor dem Oeffnen von Rydatent darf
    die Fahr-Automatik nicht aktiv werden; nach dem Oeffnen gilt sie wieder fuer die
    aktuelle Sitzung.

Besteht dieser Smoke-Test, ist dev.6 die vorgesehene Entwicklungsbasis.

## Lokale Qualitaetspruefung
./gradlew testDebugUnitTest lintDebug assembleDebug assembleRelease

Android SDK platforms;android-37.0, Gradle 9.8.1 und AGP 9.4.0 im Projekt.
