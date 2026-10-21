// Betriebsart der App: GO LIVE (Handy sendet selbst an laut.fm) oder SENDER-ADMIN (laut.fm Radioadmin). Die letzte Wahl
// bleibt gespeichert, der Umschalter ist in beiden Betriebsarten dauerhaft sichtbar.
package de.anmacha.laufmmanager.nav

import android.content.Context

enum class Mode { GOLIVE, RADIOADMIN }

class ModeStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("laufmmanager_ui", Context.MODE_PRIVATE)

    fun get(): Mode? = runCatching { Mode.valueOf(prefs.getString("mode", "") ?: "") }.getOrNull()
    fun set(m: Mode) = prefs.edit().putString("mode", m.name).apply()
}
