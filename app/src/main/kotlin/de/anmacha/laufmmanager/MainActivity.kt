package de.anmacha.laufmmanager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.anmacha.laufmmanager.live.LautFmClient
import de.anmacha.laufmmanager.nav.AppNav
import de.anmacha.laufmmanager.ui.theme.LautFmManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleLink(intent)
        setContent {
            LautFmManagerTheme {
                AppNav()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleLink(intent)
    }

    /** Rückkehr von der laut.fm-Anmeldung im Browser: Token aus der Adresse übernehmen. */
    private fun handleLink(intent: Intent?) {
        val token = LautFmClient.tokenFromRedirect(intent?.dataString ?: return) ?: return
        (application as LautFmManagerApp).lautSession.acceptBrowserToken(token)
        intent.data = null // nur einmal auswerten (z. B. nach Drehen des Geräts)
    }
}
