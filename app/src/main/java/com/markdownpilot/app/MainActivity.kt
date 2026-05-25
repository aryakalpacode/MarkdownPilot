package com.markdownpilot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.markdownpilot.app.ui.screens.markdown.MarkdownScreen
import com.markdownpilot.app.ui.screens.settings.SettingsScreen
import com.markdownpilot.app.ui.theme.MarkdownPilotTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MarkdownPilotTheme {
                val nav = rememberNavController()
                NavHost(nav, startDestination = "markdown") {
                    composable("markdown") {
                        MarkdownScreen(onSettings = { nav.navigate("settings") })
                    }
                    composable("settings") {
                        SettingsScreen(onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}
