# Rydatent – Scenic UI foundation 0.1.0-dev.8

Kein Stable-Release.

Dev.8 baut auf dem technisch validierten Stand v0.1.0-dev.7 auf und richtet
den UI-Grundrahmen neu aus.

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

## Design-Grundrahmen
- helles Strassen-/Sonnenuntergangs-Theme als App-Hintergrund
- Bedienoberflaeche wieder neutral, Orange nur im Motiv/Icon
- Notch / Safe-Area sauber beruecksichtigt
- adaptive Launcher-Icons mit Material-You-Monochromvariante
- ueberarbeitete Startseite und Kartenoptik
- Englisch als Default, Deutsch zusaetzlich

## Wichtig
Dev.8 veraendert nicht bewusst die zugrunde liegende Automationslogik.
Schwerpunkt dieses Schritts ist der visuelle Rahmen fuer den spaeteren
UI-Feinschliff.

## Lokale Qualitaetspruefung
./gradlew testDebugUnitTest lintDebug assembleDebug assembleRelease

Android SDK platforms;android-37.0, Gradle 9.8.1 und AGP 9.4.0.
