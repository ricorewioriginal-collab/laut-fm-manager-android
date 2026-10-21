// laut.fm Manager: native Kotlin/Compose-App (kein WebView-Wrapper). Die reine Handy-Engine (Mixer, MP3-Encoder,
// Icecast-Quelle) liegt in engine/src bzw. native/ und wird vom :app-Modul als Quellverzeichnis eingebunden.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
