# Rydatent – Machbarkeitsprototyp 0.1.0-dev.4

Kein Stable-Release. Die Testoberflaeche bleibt bewusst technisch.

## Funktionsumfang
- Bluetooth-Multiwahl je Funktion und bekannte Start-/Stopp-Intents fuer Blitzer.de PRO unveraendert.
- **Blitzer-Wache dev.3:** Bei aktivierter Bluetooth-Automatik und mindestens einer
  als verbunden erfassten ausgewählten Bluetooth-Verbindung laeuft ein Android-
  Vordergrunddienst mit sichtbarer Statusbenachrichtigung. Er fragt **alle 15 Sekunden**
  ueber den zuvor freigegebenen NotificationListenerService ab, ob wenigstens eine
  Benachrichtigung von `de.blitzer.plus` vorhanden ist. Ist sie nicht vorhanden,
  sendet er einen Start-Intent. Bei weiterhin fehlender Benachrichtigung wiederholt
  er den Versuch im Abstand von mindestens 45 Sekunden; das verhindert Start-Spam.
  Nach dem Ende der letzten passenden Verbindung stoppt der Vordergrunddienst.
  Wenn der Benutzer die Bluetooth-Automatik oder Geraeteauswahl aendert, wird der
  Dienst ebenfalls angepasst. Die urspruengliche Bluetooth-Start-/Stopp-Logik bleibt.
- Diagnoseprotokoll schreibt Zustandsaenderungen (vorhanden/fehlt/unbekannt) und
  Restartversuche, **nicht** jede der 15-Sekunden-Abfragen (weniger I/O und Akku).
- Anruf-Test nur fuer exakt eingetragene Testnummer bei aktivierter Testfunktion und
  ausgewaehltem verbundenem Bluetooth: **Anruf abweisen unabhaengig vom SMS-Sendestatus**;
  SMS hoechstens einmal pro Nummer innerhalb von 10 Minuten anstossen.
- Lokales Diagnoseprotokoll mit bis zu 30 Zeitstempel-Eintraegen, ohne Rufnummern.
- Keine Verarbeitung eingehender SMS oder Messenger-Nachrichten.

## Wichtige Grenzen
- Rydatent braucht **Bluetooth-Berechtigung** und **Benachrichtigungszugriff**;
  fuer eine sichtbare Fahrmodus-Benachrichtigung zudem `POST_NOTIFICATIONS`.
  Fehlt der Benachrichtigungszugriff, startet Rydatent **nicht blind** neu.
- Eine Benachrichtigung ist nur das vereinbarte Indiz fuer eine aktive Blitzer-App,
  kein Nachweis, dass die Warnfunktion technisch arbeitet. Eine andere noch sichtbare
  Blitzer.de-Benachrichtigung kann einen Neustart verhindern.
- Auf Android 12+ darf ein Hintergrund-Broadcast nicht immer einen Vordergrunddienst
  starten. Ein solcher Fehler wird im Diagnoseprotokoll angezeigt. Bei bestehender
  Verbindung kann der Dienst durch Oeffnen von Rydatent erneut gestartet werden.
  Wenn das beim Bluetooth-Verbinden auf GrapheneOS reproduzierbar scheitert,
  brauchen wir einen Android-konformen Trigger (z. B. Companion Device Manager).
- 15 Sekunden sind das **gewuenschte Prüfintervall im aktiven Dienst**, keine
  Echtzeitgarantie bei Android-Energiespar- oder Prozessbeschraenkungen.
- Der Bluetooth-Zustand beruht noch auf ACL-Ereignissen; eine unabhaengige
  Synchronisation nach Reboot oder verpassten Ereignissen fehlt.
- Die CallScreeningService-Rolle sowie READ_CONTACTS fuer gespeicherte Kontakte
  muessen vorhanden sein. Die bisher getestete Anruf-/SMS-Logik bleibt unveraendert.
- SMS nutzt die Standard-SIM; SIM-1-Auswahl und echte Zustellbestaetigung fehlen.
- dev.4 zeigt Hintergrundbeschraenkung und Akkuoptimierungs-Ausnahme getrennt an.
  Der Nutzer kann die Android-App-Einstellungen zum Anpassen oeffnen.
- CI kontrolliert Tests, Lint, Builds und den Dev-Signaturfingerabdruck und
  veroeffentlicht bei exakt passendem, bereits gepushtem Dev-Tag die APK
  direkt als GitHub-Prerelease. Kein lokaler APK-Download erforderlich.

## Reproduzierbarer Test
1. Dev.3 ueber Obtainium installieren und Blitzer-Wache per
   Benachrichtigungszugriff erlauben. Bluetooth-/Benachrichtigungsrechte pruefen.
2. Mit ausgewaehltem Bluetooth verbinden: Blitzer startet; die Rydatent-
   Benachrichtigung "Fahrmodus aktiv" erscheint und das Protokoll zeigt
   den Start. Blitzer soll eine eigene Benachrichtigung erzeugen.
3. Blitzer.de manuell schliessen: innerhalb des naechsten 15-Sekunden-Takts
   soll bei fehlender Blitzer-Benachrichtigung ein Startversuch erfolgen.
4. Mehrfaches Schliessen pruefen. Ist die Blitzer-Benachrichtigung laenger
   nicht verfuegbar, darf kein sekundenweises Start-Spamming auftreten.
5. Ausgewaehlte Bluetooth-Verbindung trennen: Blitzer wird beendet, die
   Rydatent-Fahrmodus-Benachrichtigung verschwindet; keine Neustarts mehr.
6. Mit gesperrtem Bildschirm erneut verbinden und den Fahrmodus pruefen.
   Bei Fehler zuerst das Diagnoseprotokoll auswerten.
7. Anruf-Regression: Zwei Anrufe derselben Testnummer binnen 10 Minuten:
   beide abweisen, nur eine Test-SMS anstossen.

## Lokale Qualitaetspruefung
`./gradlew testDebugUnitTest lintDebug assembleDebug assembleRelease`

Android SDK `platforms;android-37.0`, Gradle 9.8.1 und AGP 9.4.0 im Projekt.
