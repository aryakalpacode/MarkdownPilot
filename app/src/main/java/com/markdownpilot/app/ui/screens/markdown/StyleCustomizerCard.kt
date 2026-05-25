package com.markdownpilot.app.ui.screens.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markdownpilot.app.domain.model.DocStyleConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleCustomizerCard(
    style: DocStyleConfig,
    onStyleChange: (DocStyleConfig) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Palette, null, tint = MaterialTheme.colorScheme.primary)
                Text("Theme & Document Styles", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            // ─── THEME SELECTOR ───
            Column {
                Text("Select Style Palette", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DocStyleConfig.THEMES.forEach { theme ->
                        val isSelected = style.themeName == theme.themeName
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onStyleChange(
                                        style.copy(
                                            themeName = theme.themeName,
                                            primaryColorHex = theme.primaryColorHex,
                                            secondaryColorHex = theme.secondaryColorHex,
                                            textColorHex = theme.textColorHex,
                                            backgroundColorHex = theme.backgroundColorHex
                                        )
                                    )
                                }
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.parseColor(theme.primaryColorHex))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                        shape = CircleShape
                                    )
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(theme.themeName, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            HorizontalDivider()

            // ─── TYPOGRAPHY SELECTOR ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Typography Font", style = MaterialTheme.typography.labelMedium)
                    Text("Applies to headlines and body", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Sans-Serif", "Serif", "Monospace").forEach { font ->
                        val isSelected = style.fontFamily == font
                        ElevatedFilterChip(
                            selected = isSelected,
                            onClick = { onStyleChange(style.copy(fontFamily = font)) },
                            label = { Text(font, fontSize = 11.sp) }
                        )
                    }
                }
            }

            HorizontalDivider()

            // ─── MARGINS & SPACING ───
            Column {
                Text("Page Margins", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Compact" to 30f, "Normal" to 50f, "Wide" to 70f).forEach { (label, size) ->
                        val isSelected = style.marginSize == size
                        FilterChip(
                            selected = isSelected,
                            onClick = { onStyleChange(style.copy(marginSize = size)) },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Line Spacing", style = MaterialTheme.typography.labelMedium)
                    Text("Line gap height multiplier", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1.0f, 1.3f, 1.6f, 2.0f).forEach { space ->
                        val isSelected = style.lineSpacing == space
                        ElevatedFilterChip(
                            selected = isSelected,
                            onClick = { onStyleChange(style.copy(lineSpacing = space)) },
                            label = { Text("${space}x", fontSize = 11.sp) }
                        )
                    }
                }
            }

            HorizontalDivider()

            // ─── HEADERS & FOOTERS ───
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Include Running Header", style = MaterialTheme.typography.labelMedium)
                        Text("Add custom text at the top of pages", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = style.showHeader,
                        onCheckedChange = { onStyleChange(style.copy(showHeader = it)) }
                    )
                }
                if (style.showHeader) {
                    OutlinedTextField(
                        value = style.headerText,
                        onValueChange = { onStyleChange(style.copy(headerText = it)) },
                        label = { Text("Header Text") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Include Running Footer", style = MaterialTheme.typography.labelMedium)
                        Text("Add signature text at bottom left", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = style.showFooter,
                        onCheckedChange = { onStyleChange(style.copy(showFooter = it)) }
                    )
                }
                if (style.showFooter) {
                    OutlinedTextField(
                        value = style.footerText,
                        onValueChange = { onStyleChange(style.copy(footerText = it)) },
                        label = { Text("Footer Text") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Page Numbers", style = MaterialTheme.typography.labelMedium)
                        Text("Print 'Page X' at the bottom right", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = style.showPageNumbers,
                        onCheckedChange = { onStyleChange(style.copy(showPageNumbers = it)) }
                    )
                }
            }

            HorizontalDivider()

            // ─── EXTRA LAYOUT CONFIGS ───
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Page Breaks at Headlines", style = MaterialTheme.typography.labelMedium)
                        Text("Insert automatic page break before ## elements", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = style.enablePageBreaksBeforeH2,
                        onCheckedChange = { onStyleChange(style.copy(enablePageBreaksBeforeH2 = it)) }
                    )
                }

                Column {
                    Text("Table Grid Style", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Striped", "Grid", "HeaderColor", "Clean").forEach { tblStyle ->
                            val isSelected = style.tableStyle == tblStyle
                            FilterChip(
                                selected = isSelected,
                                onClick = { onStyleChange(style.copy(tableStyle = tblStyle)) },
                                label = { Text(tblStyle, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Color.Companion.parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        Color.DarkGray
    }
}
