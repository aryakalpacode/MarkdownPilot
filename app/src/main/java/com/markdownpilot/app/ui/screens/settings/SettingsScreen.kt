package com.markdownpilot.app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markdownpilot.app.data.local.dao.FileDao
import com.markdownpilot.app.data.repository.PrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: PrefsRepository,
    private val fileDao: FileDao
) : ViewModel() {
    val defaultFormat = prefs.defaultFormat.stateIn(viewModelScope, SharingStarted.Lazily, "pdf")

    fun setDefaultFormat(f: String) { viewModelScope.launch { prefs.setDefaultFormat(f) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: SettingsViewModel = hiltViewModel(), onBack: () -> Unit) {
    val fmt by vm.defaultFormat.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, "Back") } }
        )
    }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState())) {
            
            // Default format
            Text(
                text = "Preferences", 
                Modifier.padding(16.dp, 12.dp), 
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            ListItem(
                headlineContent = { Text("Default Output Format") },
                supportingContent = {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("pdf", "docx", "html").forEach { f ->
                            FilterChip(
                                selected = fmt == f, 
                                onClick = { vm.setDefaultFormat(f) },
                                label = { Text(f.uppercase()) }
                            )
                        }
                    }
                },
                leadingContent = { Icon(Icons.Default.Tune, null, tint = MaterialTheme.colorScheme.primary) }
            )

            HorizontalDivider(Modifier.padding(16.dp))

            // About
            Text(
                text = "About", 
                Modifier.padding(16.dp, 12.dp), 
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            ListItem(
                headlineContent = { Text("Version") },
                supportingContent = { Text("1.0.0 (Stable)") },
                leadingContent = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) }
            )
            
            ListItem(
                headlineContent = { Text("Engine") },
                supportingContent = { Text("Offline Vector Canvas & POI XWPF") },
                leadingContent = { Icon(Icons.Default.Memory, null, tint = MaterialTheme.colorScheme.secondary) }
            )

            ListItem(
                headlineContent = { Text("License") },
                supportingContent = { Text("Open Source (Apache 2.0)") },
                leadingContent = { Icon(Icons.Default.Policy, null, tint = MaterialTheme.colorScheme.tertiary) }
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}
