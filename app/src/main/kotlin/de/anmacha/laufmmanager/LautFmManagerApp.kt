package de.anmacha.laufmmanager

import android.app.Application
import de.anmacha.laufmmanager.live.LautFmSession

class LautFmManagerApp : Application() {
    /** gemeinsame laut.fm-Sitzung für Go Live und Sender-Admin */
    val lautSession: LautFmSession by lazy { LautFmSession(this) }
}
