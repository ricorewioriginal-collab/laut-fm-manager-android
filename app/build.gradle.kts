import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// Interner Schlüssel (nur für diese interne App, nie öffentlich). Für Store-Apps der AnMaCha-Familie gelten eigene Schlüssel,
// verwaltet im Admin-Bereich „App-Signaturen“ des Control Centers.
val signProps = Properties().apply { val f = rootProject.file("signing/signing.properties"); if (f.exists()) f.inputStream().use { load(it) } }

android {
    namespace = "de.anmacha.laufmmanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.anmacha.laufmmanager"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (signProps.containsKey("storeFile")) create("intern") {
            storeFile = rootProject.file(signProps.getProperty("storeFile")); storePassword = signProps.getProperty("storePassword")
            keyAlias = signProps.getProperty("keyAlias"); keyPassword = signProps.getProperty("keyPassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfigs.findByName("intern")?.let { signingConfig = it }
        }
        debug { signingConfigs.findByName("intern")?.let { signingConfig = it } }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Reine Handy-Engine (Mixer, MP3-Encoder, Icecast-Quelle, Android-Schicht) liegt außerhalb von
    // app/src, damit sie unabhängig von der App auch mit engine/test.sh testbar bleibt (eine einzige
    // Quelle statt einer Kopie, die bei Änderungen auseinanderlaufen könnte).
    sourceSets {
        getByName("main") {
            java.srcDirs("../engine/src", "../native")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.security.crypto)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    // MP3-Encoder für den Handy-Sender (LAME als reines Java, LGPL 2.1+)
    implementation("de.sciss:jump3r:1.0.5")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
}
