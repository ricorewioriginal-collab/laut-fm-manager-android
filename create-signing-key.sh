#!/usr/bin/env bash
# Erzeugt einmalig einen eigenen (internen) Signaturschlüssel für diese App: signing/anmacha-intern.p12 + signing/signing.properties.
# Wichtig: Den Schlüssel sicher aufbewahren (er wird NICHT committet). Spätere Versionen müssen mit demselben Schlüssel signiert werden,
# sonst muss die App auf dem Handy einmal deinstalliert werden. Den Schlüssel kannst du im Control Center unter Admin → Server → App-Signaturen ablegen.
set -e
cd "$(dirname "$0")"; mkdir -p signing
[ -f signing/anmacha-intern.p12 ] && { echo "Schlüssel existiert schon: signing/anmacha-intern.p12"; exit 0; }
PW="$(openssl rand -hex 12)"
keytool -genkeypair -keystore signing/anmacha-intern.p12 -storetype PKCS12 -alias laufm-manager -keyalg RSA -keysize 4096 -validity 36500 \
  -storepass "$PW" -keypass "$PW" -dname "CN=laut.fm Manager (intern), O=AnMaCha, C=DE"
printf 'storeFile=signing/anmacha-intern.p12\nstorePassword=%s\nkeyAlias=laufm-manager\nkeyPassword=%s\n' "$PW" "$PW" > signing/signing.properties
chmod 600 signing/signing.properties
echo "Fertig. Passwort steht in signing/signing.properties (nicht committen!)."
