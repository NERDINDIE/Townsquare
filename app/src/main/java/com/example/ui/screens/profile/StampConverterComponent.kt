package com.example.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.ui.theme.*

data class PostalStamp(
    val id: String,
    val title: String,
    val emojiOrIcon: String,
    val valueDenomination: String, // e.g. "50c", "$2.00", "CIVIC"
    val colorHex: Long,
    val isCustom: Boolean = false,
    val issueDate: String = "2026"
)

@Composable
fun StampConverterComponent(
    onSaveStamp: (PostalStamp) -> Unit,
    onDismiss: () -> Unit
) {
    var stampName by remember { mutableStateOf("") }
    var denomination by remember { mutableStateOf("2.00 Credits") }
    var selectedEmoji by remember { mutableStateOf("🏛️") }
    var selectedColor by remember { mutableStateOf(0xFF00D2FFL) } // NeonCyan

    val presetEmojis = listOf("🏛️", "📮", "⏱️", "🪙", "📰", "🎵", "🌲", "⛵", "⭐", "⚡", "🕊️", "🌹")
    val colorOptions = listOf(
        Pair(0xFF00D2FFL, "Neon Cyan"),
        Pair(0xFFFFB300L, "Warm Amber"),
        Pair(0xFF30D158L, "Emerald Green"),
        Pair(0xFFA855F7L, "Royal Purple"),
        Pair(0xFFFF3B30L, "Crimson Red")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📮", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Custom Stamp Converter", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Convert images or icons into collectible postal stamps for your collection vault and outbound Townsquare Inbox dispatches.",
                    fontSize = 11.sp,
                    color = DarkTextSecondary
                )

                // Live Preview Stamp Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(selectedColor).copy(alpha = 0.15f))
                        .border(2.dp, Color(selectedColor), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(selectedColor).copy(alpha = 0.3f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(selectedEmoji, fontSize = 24.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stampName.ifBlank { "Custom Townsquare Stamp" },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "TOWNSQUARE POST • $denomination",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(selectedColor)
                        )
                    }
                }

                OutlinedTextField(
                    value = stampName,
                    onValueChange = { stampName = it },
                    label = { Text("Stamp Title / Name") },
                    placeholder = { Text("e.g. Grand Canal Memorial") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = denomination,
                    onValueChange = { denomination = it },
                    label = { Text("Postal Denomination / Value") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Choose Icon / Emoji source
                Text("Select Motif / Image Source:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetEmojis.take(6).forEach { emo ->
                        val isSel = selectedEmoji == emo
                        Surface(
                            shape = CircleShape,
                            color = if (isSel) NeonCyan else DarkSurfaceElevated,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { selectedEmoji = emo },
                            border = BorderStroke(1.dp, if (isSel) NeonCyan else DarkBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(emo, fontSize = 18.sp)
                            }
                        }
                    }
                }

                // Color Theme
                Text("Postmark & Tint Color:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorOptions.forEach { (col, name) ->
                        val isSel = selectedColor == col
                        Surface(
                            shape = CircleShape,
                            color = Color(col),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { selectedColor = col },
                            border = BorderStroke(2.dp, if (isSel) Color.White else Color.Transparent)
                        ) {}
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newStamp = PostalStamp(
                        id = "stamp_${System.currentTimeMillis()}",
                        title = stampName.ifBlank { "Custom Civic Stamp" },
                        emojiOrIcon = selectedEmoji,
                        valueDenomination = denomination,
                        colorHex = selectedColor,
                        isCustom = true
                    )
                    onSaveStamp(newStamp)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
            ) {
                Text("Convert & Save Stamp", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DarkTextSecondary)
            }
        }
    )
}
