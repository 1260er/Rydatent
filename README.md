# Rydatent – Machbarkeitsprototyp 0.1.0-dev.2

Kein Stable-Release. Die Testoberflaeche bleibt bewusst technisch.

## Funktionsumfang
- Bluetooth-Multiwahl je Funktion und bekannte Start-/Stopp-Intents fuer Blitzer.de PRO unveraendert.
- Experimentelle **Blitzer-Wache**: Nach aktivierter Benachrichtigungszugriffs-Berechtigung
  beobachtet Rydatent die **laufende** Benachrichtigung von `de.blitzer.plus`. Wird diese
  entfernt, prueft die App nach 3 Sekunden, ob sie noch fehlt, ob die Blitzer-Automatik
  eingeschaltet und ein ausgewaehltes Bluetooth-Geraet als verbunden erfasst ist.
  Dann wird der Start-Intent erneut gesendet (mindestens 60 Sekunden zwischen Versuchen).
- Anruf-Test nur fuer exakt eingetragene Testnummer bei aktivierter Testfunktion und
  ausgewaehltem verbundenem Bluetooth: **Anruf abweisen unabhaengig vom SMS-Sendestatus**;
  SMS hoechstens einmal pro Nummer innerhalb von 10 Minuten anstossen.
- Lokales Diagnoseprotokoll mit bis zu 30 Zeitstempel-Eintraegen, ohne Rufnummern.
- Keine Verarbeitung eingehender SMS oder Messenger-Nachrichten.

## Wichtige Grenzen
- Fuer die Blitzer-Wache muss in Android **Benachrichtigungszugriff** fuer Rydatent
  aktiviert werden. Die Wache funktioniert nur, **nachdem eine laufende Blitzer.de-
  Benachrichtigung tatsaechlich beobachtet wurde**. Eine fehlende Benachrichtigung
  ist kein hundertprozentiger Nachweis fuer eine geschlossene App.
- Noch keine regelmaessige 5-Minuten-Hintergrundpruefung, keine garantierte
  Selbsterholung nach Prozess-Ende / Neustart oder wenn Blitzer.de keine laufende
  Benachrichtigung anzeigt. Im ersten Geraetetest validieren wir dieses Signal.
- Bluetooth-Zustand wird weiterhin anhand empfangener ACL-Ereignisse gehalten,
  noch keine eigene Abfrage nach Neustart oder bei verpassten Ereignissen.
- Die CallScreeningService-Rolle sowie READ_CONTACTS fuer gespeicherte Kontakte
  muessen vorhanden sein. Bei fehlender/unterdrueckter Nummer oder anderer
  Rufnummer greift die Testregel nicht.
- SMS nutzt die Standard-SIM; SIM-1-Auswahl und echte SMS-Zustellbestaetigung fehlen.
- Es findet keine automatische Release-Erstellung statt; die bestehenden GitHub-
  Actions erzeugen eine signierte Dev-Debug-APK als Artifact.

## Reproduzierbarer Test
1. Bestehende Dev-Version auf dem Telefon installiert lassen und diese Version
   mit demselben Development-Keystore installieren.
2. Bluetooth- und Anrufberechtigungen, Anruffilterrolle pruefen. Blitzer-Wache
   ueber Benachrichtigungszugriff freigeben.
3. Zwei Anrufe derselben Testnummer innerhalb von 10 Minuten durchfuehren:
   beide sollen abgewiesen werden; nur beim ersten SMS-Versuch.
4. Mit ausgewähltem Bluetooth verbinden und Blitzer.de starten. Pruefen, dass
   im Diagnoseprotokoll die laufende Blitzer.de-Benachrichtigung erkannt wird.
   Blitzer.de manuell beenden: nur nach vorheriger Benachrichtigungserkennung
   einen Neustartversuch erwarten.
5. Bluetooth trennen: Blitzer.de beenden und keinen Wiederstart ausloesen.
6. Diagnoseprotokoll nach dem Test kontrollieren, bevor UI-Design besprochen wird.

## Lokale Qualitaetspruefung
`./gradlew testDebugUnitTest lintDebug assembleDebug assembleRelease`

Android SDK `platforms;android-37.0`, Gradle 9.8.1 und AGP 9.4.0 im Projekt.
