# Rydatent – Machbarkeitsprototyp 0.1.0

Dieses Projekt ist **kein Stable-Release**. Die Oberfläche ist bewusst technisch.

## Funktionen
- Auswahl mehrerer bereits gekoppelter Bluetooth-Geräte, getrennt für Blitzer.de und Anruf/SMS.
- Empfangen von ACL-Verbindungs-/Trennungsereignissen. Schaltet Blitzer.de per Hersteller-Broadcast bei Zustandswechsel; initial deaktiviert.
- Manuelle Start-/Stopp-Testbefehle für `de.blitzer.plus`.
- Android-CallScreeningService; nur mit aktivierter Rolle und READ_CONTACTS auch für gespeicherte Kontakte.
- Automatische Test-SMS und Abweisung **ausschließlich** für eine exakt eingetragene Testnummer, bei ausgewähltem verbundenem Bluetooth-Gerät und bei aktivierter Testfunktion.
- Eingehende SMS nur als Ereignis erfassen, **keine Antworten**.

## Grenzen
- Test-SMS nutzt Androids SMS-Standard-Subscription; Auswahl von SIM 1 fehlt.
- Android darf Hintergrundstarts beschränken; Test am gesperrten Gerät ist notwendig.
- Bluetooth-Zustand wird ausschließlich aus empfangenen ACL-Events gepflegt; bei erstmaliger Installation/Neustart kann der Zustand zunächst unbekannt sein.
- Die 5-Minuten-Prüfung aus MacroDroid, Messenger-Auswertung, Benachrichtigungen, Gerätestart-Wiederherstellung und sicherer Protokollverlauf fehlen.
- Logeinträge sind nur letzter Status; es werden keine Rufnummern in Logs geschrieben.
- Es wird nicht automatisch ein GitHub-Repository oder ein Release angelegt.

## Lokale Prüfung
- JDK 17, Android SDK 36, Gradle 8.13+ nötig.
- In Android Studio öffnen, Gradle synchronisieren, `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `assembleRelease` ausführen.
- Eine Release-APK ist zunächst **nicht signiert**; für Testsignierung wird ein gesondert gesicherter Keystore benötigt.
- Zuerst Bluetooth und Blitzer-Befehle testen, danach mit einer eigenen Zweitnummer die Anrufrolle testen. MacroDroid parallel für dieselben Geräte deaktivieren, wenn gezielt getestet wird.

## Recherche
- https://www.blitzer.de/questions/wie-richte-ich-den-automatischen-start-stopp-per-tasker-ein/
- https://developer.android.com/reference/android/telecom/CallScreeningService
- https://developer.android.com/reference/android/bluetooth/BluetoothDevice
- https://developer.android.com/reference/android/telephony/SmsManager
