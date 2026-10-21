#!/usr/bin/env bash
# Baut die interne App „laut.fm Manager“: ./build-apk.sh   → dist/LautFmManager.apk (+ .json)
# Voraussetzungen: JDK 17+, Android SDK (ANDROID_HOME), Internet für Gradle.
set -e
cd "$(dirname "$0")"
[ -f signing/signing.properties ] || ./create-signing-key.sh
./gradlew assembleRelease
mkdir -p dist; cp app/build/outputs/apk/release/app-release.apk dist/LautFmManager.apk
SHA=$(sha256sum dist/LautFmManager.apk | cut -d' ' -f1); SIZE=$(stat -c%s dist/LautFmManager.apk)
VN=$(grep -o 'versionName = "[^"]*"' app/build.gradle.kts | cut -d'"' -f2); VC=$(grep -o 'versionCode = [0-9]*' app/build.gradle.kts | grep -o '[0-9]*$')
printf '{\n "version":"%s",\n "version_code":%s,\n "package":"de.anmacha.laufmmanager",\n "min_android":"8.0",\n "size_bytes":%s,\n "sha256":"%s",\n "signing":"intern",\n "built_at":"%s",\n "abi":"universal"\n}\n' "$VN" "$VC" "$SIZE" "$SHA" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" > dist/LautFmManager.json
echo "Fertig: dist/LautFmManager.apk ($SIZE Bytes, SHA-256 $SHA)"
