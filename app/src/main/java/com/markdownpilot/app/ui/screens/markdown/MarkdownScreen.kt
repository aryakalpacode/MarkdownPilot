package com.markdownpilot.app.ui.screens.markdown

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.markdownpilot.app.data.local.entity.GeneratedFileEntity
import com.markdownpilot.app.domain.model.DocFormat
import com.markdownpilot.app.domain.model.DocStyleConfig
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownScreen(
    vm: MarkdownViewModel = hiltViewModel(),
    onSettings: () -> Unit
) {
    val markdown by vm.markdownText.collectAsState()
    val style by vm.styleConfig.collectAsState()
    val format by vm.selectedFormat.collectAsState()
    val conversion by vm.conversionState.collectAsState()
    val files by vm.recentFiles.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showStylePanel by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Article, "MD Icon", Modifier.size(28.dp), MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("MarkdownPilot", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            // ─── TABS ───
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.EditDocument, null) },
                    text = { Text("Workshop") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.FolderZip, null) },
                    text = { Text("My Documents (${files.size})") }
                )
            }

            // ─── CONTENT DECISION ───
            Box(modifier = Modifier.weight(1f)) {
                if (selectedTab == 0) {
                    // WORKSHOP TAB
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // ─── TEMPLATES LOADERS ───
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Load A Sample Template",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = { showStylePanel = !showStylePanel }) {
                                    Icon(Icons.Default.Tune, null, Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (showStylePanel) "Hide Styles" else "Show Styles", fontSize = 12.sp)
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SuggestionChip(
                                    onClick = { vm.loadSample(MarkdownViewModel.DEFAULT_SAMPLE) },
                                    label = { Text("📄 Sales Report") }
                                )
                                SuggestionChip(
                                    onClick = { vm.loadSample(MarkdownViewModel.RESUME_SAMPLE) },
                                    label = { Text("👤 Resume / CV") }
                                )
                                SuggestionChip(
                                    onClick = { vm.loadSample(MarkdownViewModel.TECH_DOC_SAMPLE) },
                                    label = { Text("⚙️ Tech Integration") }
                                )
                            }
                        }

                        // ─── STYLE PANEL ───
                        AnimatedVisibility(
                            visible = showStylePanel,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            StyleCustomizerCard(
                                style = style,
                                onStyleChange = vm::updateStyle
                            )
                        }

                        // ─── EDITOR ───
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Write or Paste Markdown",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${markdown.lines().size} lines",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = markdown,
                                onValueChange = vm::updateMarkdown,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 280.dp, max = 550.dp),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                ),
                                placeholder = { Text("Start typing markdown here... Use # for headers, - for lists, etc.") },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // ─── EXPORT FORMAT CHIPS ───
                        Column {
                            Text(
                                text = "Choose Export Format",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(DocFormat.PDF, DocFormat.DOCX, DocFormat.HTML, DocFormat.ZIP, DocFormat.MD, DocFormat.CSV).forEach { fmt ->
                                    FilterChip(
                                        selected = format == fmt,
                                        onClick = { vm.selectFormat(fmt) },
                                        label = { Text(fmt.label) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = when (fmt) {
                                                    DocFormat.PDF -> Icons.Default.PictureAsPdf
                                                    DocFormat.DOCX -> Icons.Default.Article
                                                    DocFormat.HTML -> Icons.Default.Language
                                                    DocFormat.ZIP -> Icons.Default.FolderZip
                                                    DocFormat.CSV -> Icons.Default.Storage
                                                    else -> Icons.Default.InsertDriveFile
                                                },
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        // ─── ACTION BUTTON ───
                        Button(
                            onClick = { vm.convert() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Construction, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Convert Markdown Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(Modifier.height(40.dp))
                    }
                } else {
                    // MY DOCUMENTS / HISTORY TAB
                    MyDocumentsTab(
                        files = files,
                        onOpen = vm::openFile,
                        onShare = vm::shareFile,
                        onDelete = vm::deleteFile
                    )
                }
            }
        }

        // ─── CONVERSION STATES OVERLAYS / DIALOGS ───
        when (val state = conversion) {
            is ConversionState.Converting -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(16.dp))
                            Text("Formatting & Rendering...", fontWeight = FontWeight.SemiBold)
                            Text("Generating your beautiful file locally", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            is ConversionState.Success -> {
                AlertDialog(
                    onDismissRequest = { vm.resetState() },
                    icon = { Icon(Icons.Default.CheckCircle, "Success", tint = Color(0xFF10B981), modifier = Modifier.size(40.dp)) },
                    title = { Text("Success!", textAlign = TextAlign.Center) },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(state.fileName, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(4.dp))
                            Text("Saved locally in device storage", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { vm.openFile(state.filePath) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.OpenInNew, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Open Document")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { vm.shareFile(state.filePath) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Share File")
                        }
                    }
                )
            }
            is ConversionState.Error -> {
                AlertDialog(
                    onDismissRequest = { vm.resetState() },
                    icon = { Icon(Icons.Default.ErrorOutline, "Error", tint = MaterialTheme.colorScheme.error) },
                    title = { Text("Generation Failed") },
                    text = { Text(state.message) },
                    confirmButton = {
                        TextButton(onClick = { vm.resetState() }) {
                            Text("Dismiss")
                        }
                    }
                )
            }
            else -> {}
        }
    }
}

@Composable
fun MyDocumentsTab(
    files: List<GeneratedFileEntity>,
    onOpen: (String) -> Unit,
    onShare: (String) -> Unit,
    onDelete: (GeneratedFileEntity) -> Unit
) {
    if (files.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(0.3f)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "No Converted Documents Yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Any document you convert in the Workshop will appear here for easy management.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(files, key = { it.id }) { file ->
                FileHistoryCard(file, onOpen, onShare, onDelete)
            }
        }
    }
}

@Composable
fun FileHistoryCard(
    file: GeneratedFileEntity,
    onOpen: (String) -> Unit,
    onShare: (String) -> Unit,
    onDelete: (GeneratedFileEntity) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color.LightGray.copy(0.5f), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            val icon = when (file.format.lowercase()) {
                "pdf" -> Icons.Default.PictureAsPdf
                "docx" -> Icons.Default.Article
                "html" -> Icons.Default.Language
                "zip" -> Icons.Default.FolderZip
                "csv" -> Icons.Default.Storage
                else -> Icons.Default.InsertDriveFile
            }
            val iconColor = when (file.format.lowercase()) {
                "pdf" -> Color(0xFFEF4444)
                "docx" -> Color(0xFF3B82F6)
                "html" -> Color(0xFF10B981)
                "zip" -> Color(0xFFF59E0B)
                "csv" -> Color(0xFF8B5CF6)
                else -> Color.Gray
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(22.dp), tint = iconColor)
            }

            Spacer(Modifier.width(12.dp))

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.fileName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatBytes(file.sizeBytes),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(Modifier.size(3.dp).background(Color.LightGray, CircleShape))
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(file.createdAt)),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { onOpen(file.filePath) }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.OpenInNew, "Open", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = { onShare(file.filePath) }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Share, "Share", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = { onDelete(file) }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.DeleteOutline, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format(Locale.getDefault(), "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
