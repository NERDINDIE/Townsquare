package com.example.ui.plus.phone

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PagerMessage(
    val id: String,
    val lineName: String,
    val lineFrequency: String,
    val priority: String, // "CRITICAL", "HIGH", "STANDARD"
    val templateName: String,
    val body: String,
    val locationTag: String,
    val timestamp: String,
    val status: String // "DELIVERED", "ACKNOWLEDGED", "DISPATCHED"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyPagerComponent(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // Emergency Line Options
    val lines = listOf(
        Pair("Redline 911 Direct", "154.280 MHz - Priority 0"),
        Pair("Civil Defense & Hazard", "162.550 MHz - NOAA Direct"),
        Pair("Community SOS Mesh", "99.900 MHz - Local Citizen Line"),
        Pair("Fire & Rescue Dispatch", "153.890 MHz - Emergency Duty"),
        Pair("Medical Evac & Trauma", "462.950 MHz - Townsquare Health")
    )

    var selectedLineIndex by remember { mutableIntStateOf(0) }
    var selectedPriority by remember { mutableStateOf("CRITICAL SOS") }
    var customText by remember { mutableStateOf("") }
    var attachLocation by remember { mutableStateOf(true) }
    var isSending by remember { mutableStateOf(false) }
    var pagerBeepActive by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Sent Pager Log State
    var pagerLog by remember {
        mutableStateOf(
            listOf(
                PagerMessage(
                    id = "PAG-9081",
                    lineName = "Redline 911 Direct",
                    lineFrequency = "154.280 MHz",
                    priority = "CRITICAL SOS",
                    templateName = "Medical Emergency",
                    body = "Urgent: Oxygen tank request at Citizen Block 12, Unit 4B.",
                    locationTag = "37.7749° N, 122.4194° W (Townsquare North)",
                    timestamp = "10 mins ago",
                    status = "ACKNOWLEDGED BY DISPATCH"
                ),
                PagerMessage(
                    id = "PAG-8812",
                    lineName = "Community SOS Mesh",
                    lineFrequency = "99.900 MHz",
                    priority = "HIGH",
                    templateName = "Power Outage Alert",
                    body = "Main generator failure on District 3 line. Backup batteries active.",
                    locationTag = "37.7721° N, 122.4201° W",
                    timestamp = "1 hour ago",
                    status = "DELIVERED"
                )
            )
        )
    }

    val presetTemplates = listOf(
        "🆘 Medical Emergency: Urgent Assistance Needed",
        "🚨 Civil Defense: Power Grid Outage / Line Down",
        "🌊 Flash Flood Alert: Community Shelter Advised",
        "🔒 Security SOS: Suspicious Intrusion Detected",
        "✅ All Clear: Status Restored to Normal"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
    ) {
        // Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = CoralRed.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CoralRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CellTower,
                        contentDescription = "Pager Line",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EMERGENCY PAGER LINE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CoralRed,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Direct high-priority dispatch channel • Active 24/7",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                    )
                }
                if (pagerBeepActive) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Beeping",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Line Selection Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SELECT SPECIAL EMERGENCY LINE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        lines.forEachIndexed { idx, line ->
                            val isSelected = (idx == selectedLineIndex)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { selectedLineIndex = idx }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = line.first,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) NeonCyan else Color.White
                                        )
                                    )
                                    Text(
                                        text = line.second,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.Gray
                                        )
                                    )
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedLineIndex = idx },
                                    colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Preset Templates
            item {
                Text(
                    text = "QUICK EMERGENCY TEMPLATES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    presetTemplates.forEach { template ->
                        AssistChip(
                            onClick = { customText = template },
                            label = { Text(template, fontSize = 12.sp, color = Color.White) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = null,
                                    tint = CoralRed,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(containerColor = DarkCardBg)
                        )
                    }
                }
            }

            // Message Composer Field
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "PAGER MESSAGE CONTENT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customText,
                            onValueChange = { customText = it },
                            placeholder = { Text("Enter emergency broadcast alert details...", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = attachLocation,
                                    onCheckedChange = { attachLocation = it },
                                    colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                                )
                                Text("Attach GPS Coordinates", style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray))
                            }

                            // Priority Selector
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("HIGH", "CRITICAL SOS").forEach { prio ->
                                    val isPrioSel = selectedPriority == prio
                                    FilterChip(
                                        selected = isPrioSel,
                                        onClick = { selectedPriority = prio },
                                        label = { Text(prio, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CoralRed,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dispatch Pager Button
                        Button(
                            onClick = {
                                if (customText.isBlank()) return@Button
                                isSending = true
                                pagerBeepActive = true
                                statusMessage = "Transmitting Pager Alert Tone over ${lines[selectedLineIndex].second}..."

                                coroutineScope.launch {
                                    delay(1500L)
                                    pagerBeepActive = false
                                    statusMessage = "Pager Alert Dispatched to Emergency Nodes!"
                                    val newMsg = PagerMessage(
                                        id = "PAG-${(1000..9999).random()}",
                                        lineName = lines[selectedLineIndex].first,
                                        lineFrequency = lines[selectedLineIndex].second,
                                        priority = selectedPriority,
                                        templateName = "Citizen Alert",
                                        body = customText,
                                        locationTag = if (attachLocation) "37.7749° N, 122.4194° W (GPS Verified)" else "Location Withheld",
                                        timestamp = "Just Now",
                                        status = "DISPATCHED"
                                    )
                                    pagerLog = listOf(newMsg) + pagerLog
                                    customText = ""
                                    isSending = false
                                    delay(3000L)
                                    statusMessage = null
                                }
                            },
                            enabled = customText.isNotBlank() && !isSending,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("send_emergency_pager_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isSending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("TRANSMITTING PAGER SIGNAL...")
                            } else {
                                Icon(Icons.Default.CellTower, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("DISPATCH EMERGENCY PAGER ALERT", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (statusMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = statusMessage!!,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }
            }

            // Pager History Log
            item {
                Text(
                    text = "DISPATCH HISTORY LOG",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                )
            }

            items(pagerLog) { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardBg.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = msg.priority,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (msg.priority.contains("CRITICAL")) CoralRed else WarmAmber
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg.lineName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                            Text(
                                text = msg.timestamp,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = msg.body,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "📍 ${msg.locationTag}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "STATUS: ${msg.status}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
