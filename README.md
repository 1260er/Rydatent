# Rydatent – UI-Grundrahmen 0.1.0-dev.7

Kein Stable-Release.

Die technisch getestete Basis bleibt v0.1.0-dev.6. Dev.7 legt darauf den
UI-Grundrahmen fuer die weitere Entwicklung.

## Technische Basis
- Bluetooth-Multiwahl fuer Blitzer.de und Anruf-Automatik
- Blitzer.de Start/Stop ueber bekannte Intents
- 15-Sekunden-Blitzer-Watchdog mit 45-Sekunden-Retry
- CallScreeningService fuer eingehende Anrufe
- automatische Fahr-SMS mit 10-Minuten-Sperre je Rufnummer
- Foreground-Fahrmodusdienst
- 15-Sekunden-Dienst-Heartbeat und 45-Sekunden-Stale-Erkennung
- optionaler Autostart nach BOOT_COMPLETED
- Wiederherstellung nach App-Update
- lokale Diagnosehistorie

## UI-Grundrahmen
Das Design orientiert sich an ScaleLauncher und AppStow.

- identischer Light- und Dark-Hintergrund aus ScaleLauncher
- neutrale Karten- und Oberflaechenstruktur nach AppStow/ScaleLauncher
- Orange als Rydatent-Akzentfarbe
- Light- und Dark-Theme
- Startseite mit Schnellsteuerung und Status
- getrennte Seiten fuer Geraete, Automationen, Berechtigungen, Diagnose und Info
- Englisch als Default-Ressourcensprache
- deutsche Uebersetzung in values-de
- Android-App-Sprachauswahl ueber locales_config
- Adaptive Launcher Icon
- monochromes Android-13+-Icon fuer Material You
- schwarzer Icon-Hintergrund passend zur bestehenden App-Familie
- orangefarbenes Rydatent-Symbol

## Wichtig
Dev.7 soll die bestehende Automationslogik nicht funktional veraendern.
Der naechste Schritt ist ausschliesslich UI-Feinschliff und visuelle Abstimmung.

## Lokale Qualitaetspruefung
./gradlew testDebugUnitTest lintDebug assembleDebug assembleRelease

Android SDK platforms;android-37.0, Gradle 9.8.1 und AGP 9.4.0.
