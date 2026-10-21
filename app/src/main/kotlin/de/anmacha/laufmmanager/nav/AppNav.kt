// Navigation der App als einfacher Zustand (aktueller Bereich + Reiter) statt Back-Stack: Reiterwechsel und
// Wechsel der Betriebsart sind damit immer eindeutig. Zurück: erst auf den ersten Reiter, dann beendet Android die App.
package de.anmacha.laufmmanager.nav

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import de.anmacha.laufmmanager.golive.GoLiveViewModel
import de.anmacha.laufmmanager.golive.LautFmLoginScreen
import de.anmacha.laufmmanager.golive.LiveScreen
import de.anmacha.laufmmanager.golive.MusicScreen
import de.anmacha.laufmmanager.golive.SetupScreen
import de.anmacha.laufmmanager.radioadmin.RaManageScreen
import de.anmacha.laufmmanager.radioadmin.RaOverviewScreen
import de.anmacha.laufmmanager.radioadmin.RaProgramScreen
import de.anmacha.laufmmanager.radioadmin.RaScheduleScreen
import de.anmacha.laufmmanager.radioadmin.RaStatsScreen
import de.anmacha.laufmmanager.radioadmin.RadioadminViewModel

/** Bereiche der App, jeder gehört zu einer Betriebsart. */
private enum class Screen(val mode: Mode) {
    LIVE(Mode.GOLIVE), MUSIC(Mode.GOLIVE), SETUP(Mode.GOLIVE),
    RA_OVERVIEW(Mode.RADIOADMIN), RA_PROGRAM(Mode.RADIOADMIN), RA_SCHEDULE(Mode.RADIOADMIN), RA_STATS(Mode.RADIOADMIN), RA_MANAGE(Mode.RADIOADMIN),
}

private data class Tab(val screen: Screen, val label: String, val icon: ImageVector)

private val liveTabs = listOf(
    Tab(Screen.LIVE, "Live", Icons.Filled.Mic),
    Tab(Screen.MUSIC, "Musik", Icons.Filled.LibraryMusic),
    Tab(Screen.SETUP, "Sender", Icons.Filled.Settings),
)
private val adminTabs = listOf(
    Tab(Screen.RA_OVERVIEW, "Übersicht", Icons.Filled.Dashboard),
    Tab(Screen.RA_PROGRAM, "Programm", Icons.Filled.LibraryMusic),
    Tab(Screen.RA_SCHEDULE, "Sendeplan", Icons.Filled.CalendarMonth),
    Tab(Screen.RA_STATS, "Statistik", Icons.Filled.BarChart),
    Tab(Screen.RA_MANAGE, "Verwalten", Icons.Filled.Tune),
)

private fun tabsOf(mode: Mode): List<Tab> = when (mode) {
    Mode.GOLIVE -> liveTabs
    Mode.RADIOADMIN -> adminTabs
}

@Composable
fun AppNav() {
    val context = LocalContext.current
    val modeStore = remember { ModeStore(context) }
    // Start in der zuletzt benutzten Betriebsart, beim ersten Start in der Sender-Admin
    var screenName by rememberSaveable { mutableStateOf(tabsOf(modeStore.get() ?: Mode.RADIOADMIN).first().screen.name) }
    val screen = Screen.valueOf(screenName)
    val live: GoLiveViewModel = viewModel()
    val admin: RadioadminViewModel = viewModel()
    val liveUi by live.ui.collectAsState()
    val showLogin = liveUi.lautfm.showLogin
    val mode = screen.mode
    val tabs = tabsOf(mode)

    fun go(s: Screen) { screenName = s.name }

    fun enter(m: Mode) {
        modeStore.set(m)
        go(tabsOf(m).first().screen)
    }

    // Zurück: Anmeldung schließen, sonst auf den ersten Reiter; dort beendet Android die App wie gewohnt
    BackHandler(enabled = showLogin) { live.closeLautFmLogin() }
    BackHandler(enabled = !showLogin && screen != tabs.first().screen) { go(tabs.first().screen) }

    if (showLogin) {
        LautFmLoginScreen(onToken = live::connectLautFm, onClose = live::closeLautFmLogin)
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = { ModeHeader(mode, liveUi.running) { enter(it) } },
        bottomBar = {
            NavigationBar {
                tabs.forEach { t ->
                    NavigationBarItem(
                        selected = screen == t.screen,
                        onClick = { go(t.screen) },
                        icon = { Icon(t.icon, t.label) },
                        label = { Text(t.label) },
                    )
                }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (screen) {
                Screen.LIVE -> LiveScreen(live) { go(Screen.SETUP) }
                Screen.MUSIC -> MusicScreen(live)
                Screen.SETUP -> SetupScreen(live)
                Screen.RA_OVERVIEW -> RaOverviewScreen(admin) { enter(Mode.GOLIVE) }
                Screen.RA_PROGRAM -> RaProgramScreen(admin)
                Screen.RA_SCHEDULE -> RaScheduleScreen(admin)
                Screen.RA_STATS -> RaStatsScreen(admin)
                Screen.RA_MANAGE -> RaManageScreen(admin) { enter(Mode.GOLIVE) }
            }
        }
    }
}
