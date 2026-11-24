package com.example.ui.plus.waves

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItemEntity
import com.example.ui.theme.*

@Composable
fun WavesRadioTicker(
    radioStations: List<MediaItemEntity>,
    onSelectStation: (MediaItemEntity) -> Unit,
    onOpenRadioList: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeStationIndex by remember { mutableIntStateOf(0) }
    val currentStation = radioStations.getOrNull(activeStationIndex) ?: radioStations.firstOrNull()
    val haptic = LocalHapticFeedback.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onOpenRadioList()
            }
            .testTag("waves_radio_ticker")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Live FM Broadcast Channel Ticker
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = NeonCyan.copy(alpha = 0.2f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("📻", fontSize = 22.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentStation?.title ?: "Townsquare Central Radio",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = "Frequency: ${currentStation?.stationFrequency ?: "88.5 MHz"} • Tap to tune on Global Player",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan,
                            maxLines = 1
                        )
                    }
                }

                FilledTonalButton(
                    onClick = {
                        if (currentStation != null) onSelectStation(currentStation)
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Radio, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tune Radio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
