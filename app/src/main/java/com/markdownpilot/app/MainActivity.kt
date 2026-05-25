package com.markdownpilot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.markdownpilot.app.data.repository.PrefsRepository
import com.markdownpilot.app.ui.screens.home.HomeScreen
import com.markdownpilot.app.ui.screens.markdown.MarkdownScreen
import com.markdownpilot.app.ui.screens.settings.SettingsScreen
import com.markdownpilot.app.ui.screens.setup.SetupScreen
import com.markdownpilot.app.ui.theme.MarkdownPilotTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var prefs: PrefsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val setupDone = runBlocking { prefs.setupDone.firstOrNull() ?: false }

        setContent {
            MarkdownPilotTheme {
                val nav = rememberNavController()
                NavHost(nav, startDestination = if (setupDone) "home" else "setup") {
                    composable("setup") {
                        SetupScreen(onDone = { nav.navigate("home") { popUpTo("setup") { inclusive = true } } })
                    }
                    composable("home") {
                        HomeScreen(
                            onSettings = { nav.navigate("settings") },
                            onMarkdownWorkshop = { nav.navigate("markdown") }
                        )
                    }
                    composable("markdown") {
                        MarkdownScreen(onBack = { nav.popBackStack() })
                    }
                    composable("settings") {
                        SettingsScreen(onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}
