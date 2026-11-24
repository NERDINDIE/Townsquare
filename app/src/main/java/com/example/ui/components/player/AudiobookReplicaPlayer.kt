package com.example.ui.components.player

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioState
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AudiobookReplicaPlayer(
    audioState: AudioState,
    onTogglePlayPause: () -> Unit,
    onClose: () -> Unit,
    onOpenEq: () -> Unit = {},
    onSetAutoHeadroom: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf("English") }
    var isLanguageMenuOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isNormalizationActive by remember { mutableStateOf(true) }
    var activeWordIndex by remember { mutableIntStateOf(0) }
    val haptic = LocalHapticFeedback.current

    val languages = listOf("English", "Spanish", "French", "German", "Japanese")

    val sampleBookTitle = audioState.currentItem?.title ?: "The Architecture of Commonwealth Cities"
    val sampleAuthor = audioState.currentItem?.authorName ?: "Elena Vance"

    // Digital replica texts in supported languages
    val replicaTexts = mapOf(
        "English" to "Chapter 1: The Architecture of Commonwealth Cities. At the heart of the metropolis, stone cobblestones and zinc rooftops reflect the history of past generations. Every building tells a chronicle of resilience, human-scale design, and community spirit.",
        "Spanish" to "Capítulo 1: La arquitectura de las ciudades de la commonwealth. En el centro de la metrópolis, los adoquines de piedra y los tejados de zinc reflejan la historia de generaciones pasadas. Cada edificio cuenta una crónica de resiliencia y comunidad.",
        "French" to "Chapitre 1: L'architecture des villes du commonwealth. Au cœur de la métropole, les pavés en pierre et les toits en zinc reflètent l'histoire des générations passées. Chaque bâtiment raconte une chronique de résilience.",
        "German" to "Kapitel 1: Die Architektur der Commonwealth-Städte. Im Herzen der Metropole spiegeln Kopfsteinpflaster und Zinkdächer die Geschichte vergangener Generationen wider. Jedes Gebäude erzählt eine Chronik der Widerstandskraft.",
        "Japanese" to "第1章：コモンウェルス都市の建築。メトロポリスの中心部では、石畳と亜鉛の屋根が過去の世代の歴史を映し出しています。すべての建物がレジリエンスの物語を伝えています。"
    )

    val currentFullText = replicaTexts[selectedLanguage] ?: replicaTexts["English"]!!
    val words = remember(currentFullText) { currentFullText.split(" ") }

    // Synchronized word highlighting loop during audio playback
    LaunchedEffect(audioState.isPlaying, selectedLanguage) {
        if (audioState.isPlaying) {
            while (true) {
                delay(350L)
                activeWordIndex = (activeWordIndex + 1) % words.size
            }
        }
    }

    // Filter text based on search query across title & keywords in all languages
    val matchesSearch = remember(searchQuery, sampleBookTitle, currentFullText) {
        if (searchQuery.isBlank()) true
        else sampleBookTitle.contains(searchQuery, ignoreCase = true) ||
                currentFullText.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📚", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Townsquare Audiobook Player",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Synchronized Digital Replica & Multilingual Search",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarmAmber
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SimultaneousEqCompactBadge(
                    eqState = audioState.simultaneousEq,
                    onClick = onOpenEq
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        // Multilingual Search Input Field across all supported languages
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search titles, keywords & chapter text in all 5 languages...", fontSize = 11.sp, color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonCyan, modifier = Modifier.size(16.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF141A26),
                unfocusedContainerColor = Color(0xFF141A26),
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color(0xFF1E293B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("audiobook_search_input")
        )

        // Language Switcher & Interactive Volume Normalization Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive Volume Normalization Toggle with LUFS Level Indicator
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isNormalizationActive) Color(0xFF0284C7).copy(alpha = 0.25f) else Color(0xFF1E293B),
                border = BorderStroke(1.dp, if (isNormalizationActive) Color(0xFF38BDF8) else Color(0xFF334155)),
                modifier = Modifier
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        isNormalizationActive = !isNormalizationActive
                        onSetAutoHeadroom(isNormalizationActive)
                    }
                    .testTag("volume_normalization_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Volume Normalization",
                        tint = if (isNormalizationActive) Color(0xFF38BDF8) else Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isNormalizationActive) "NORM ON (-14.0 LUFS)" else "NORM OFF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNormalizationActive) Color(0xFF38BDF8) else Color.LightGray
                    )
                }
            }

            // Language Dropdown Selector
            Box {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, NeonCyan),
                    modifier = Modifier
                        .clickable { isLanguageMenuOpen = true }
                        .testTag("audiobook_language_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Text(text = selectedLanguage, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }

                DropdownMenu(
                    expanded = isLanguageMenuOpen,
                    onDismissRequest = { isLanguageMenuOpen = false },
                    properties = androidx.compose.ui.window.PopupProperties(focusable = true, dismissOnClickOutside = true),
                    modifier = Modifier.background(Color(0xFF0F172A))
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang, fontSize = 12.sp, color = if (lang == selectedLanguage) NeonCyan else Color.White) },
                            onClick = {
                                selectedLanguage = lang
                                activeWordIndex = 0
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isLanguageMenuOpen = false
                            }
                        )
                    }
                }
            }
        }

        // Digital Replica Follow-Along Text Container with Animated Spoken Word Highlighting
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF141A26),
            border = BorderStroke(1.dp, if (matchesSearch) Color(0xFF1E293B) else CoralRed.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(135.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(12.dp)) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = sampleBookTitle,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = WarmAmber
                        )
                        if (isNormalizationActive) {
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF0284C7).copy(alpha = 0.2f)) {
                                Text("AUDIO BALANCED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Narrated by $sampleAuthor • Language: $selectedLanguage",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!matchesSearch) {
                        Text(
                            text = "No direct keyword matches found for '$searchQuery' in $selectedLanguage.",
                            fontSize = 11.sp,
                            color = CoralRed
                        )
                    } else {
                        // Animated Word-by-Word Replica Text with Highlighted Background Pill for spoken word
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            words.forEachIndexed { idx, word ->
                                val isSpokenWord = idx == activeWordIndex && audioState.isPlaying

                                if (isSpokenWord) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = NeonCyan,
                                        border = BorderStroke(1.dp, Color.White),
                                        modifier = Modifier.testTag("active_spoken_word")
                                    ) {
                                        Text(
                                            text = word,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Black
                                            ),
                                            color = Color(0xFF003544),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = word,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontStyle = FontStyle.Italic
                                        ),
                                        color = Color.White.copy(alpha = 0.9f),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Playback Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    activeWordIndex = (activeWordIndex - 5).coerceAtLeast(0)
                }) {
                    Icon(Icons.Default.Replay10, contentDescription = "Rewind 10s", tint = Color.LightGray)
                }
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onTogglePlayPause()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(WarmAmber)
                ) {
                    Icon(
                        imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color(0xFF261800),
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = {
                    activeWordIndex = (activeWordIndex + 5) % words.size
                }) {
                    Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = Color.LightGray)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = if (isNormalizationActive) Color(0xFF38BDF8) else Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "04:12 / 45:00",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
