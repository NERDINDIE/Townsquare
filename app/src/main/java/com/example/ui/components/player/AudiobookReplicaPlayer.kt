package com.example.ui.components.player

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

@Composable
fun AudiobookReplicaPlayer(
    audioState: AudioState,
    onTogglePlayPause: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf("English") }
    var isLanguageMenuOpen by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val languages = listOf("English", "Spanish", "French", "German", "Japanese")

    val sampleBookTitle = audioState.currentItem?.title ?: "The Architecture of Commonwealth Cities"
    val sampleAuthor = audioState.currentItem?.authorName ?: "Elena Vance"

    val replicaText = when (selectedLanguage) {
        "Spanish" -> "Capítulo 1: La arquitectura de las ciudades de la commonwealth. En el centro de la metrópolis, los adoquines de piedra y los tejados de zinc reflejan la historia de generaciones pasadas. Cada edificio cuenta una crónica de resiliencia y comunidad."
        "French" -> "Chapitre 1: L'architecture des villes du commonwealth. Au cœur de la métropole, les pavés en pierre et les toits en zinc reflètent l'histoire des générations passées. Chaque bâtiment raconte une chronique de résilience."
        "German" -> "Kapitel 1: Die Architektur der Commonwealth-Städte. Im Herzen der Metropole spiegeln Kopfsteinpflaster und Zinkdächer die Geschichte vergangener Generationen wider. Jedes Gebäude erzählt eine Chronik der Widerstandskraft."
        "Japanese" -> "第1章：コモンウェルス都市の建築。メトロポリスの中心部では、石畳と亜鉛の屋根が過去の世代の歴史を映し出しています。すべての建物がレジリエンスの物語を伝えています。"
        else -> "Chapter 1: The Architecture of Commonwealth Cities. At the heart of the metropolis, stone cobblestones and zinc rooftops reflect the history of past generations. Every building tells a chronicle of resilience, human-scale design, and community spirit."
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
                        text = "Listening with Synchronized Digital Replica Follow-Along",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarmAmber
                    )
                }
            }

            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        // Language Switcher Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("AUDIOBOOK LANGUAGE:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = NeonCyan)

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
                    modifier = Modifier.background(Color(0xFF0F172A))
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang, fontSize = 12.sp, color = if (lang == selectedLanguage) NeonCyan else Color.White) },
                            onClick = {
                                selectedLanguage = lang
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isLanguageMenuOpen = false
                            }
                        )
                    }
                }
            }
        }

        // Digital Replica Follow-Along Text Container
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF141A26),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(14.dp)) {
                item {
                    Text(
                        text = sampleBookTitle,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = WarmAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Narrated by $sampleAuthor • Translated to $selectedLanguage",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = replicaText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 20.sp
                        ),
                        color = Color.White
                    )
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
                IconButton(onClick = {}) {
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
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = Color.LightGray)
                }
            }

            Text(
                text = "04:12 / 45:00",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
