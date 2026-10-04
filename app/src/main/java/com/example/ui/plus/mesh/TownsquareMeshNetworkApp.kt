package com.example.ui.plus.mesh

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class MeshNode(
    val id: String,
    val name: String,
    val type: String, // GATEWAY, RELAY, CITIZEN_PEER, SENSOR
    val rssiRbm: Int,
    val hops: Int,
    val status: String,
    val posXPercent: Float,
    val posYPercent: Float
)

data class MeshPacket(
    val id: String,
    val sender: String,
    val payload: String,
    val hops: Int,
    val timestamp: String,
    val isEmergency: Boolean = false
)

@Composable
fun TownsquareMeshNetworkApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRelayEnabled by remember { mutableStateOf(true) }
    var isOfflineGossipSync by remember { mutableStateOf(true) }
    var txPowerLevel by remember { mutableStateOf("HIGH (+20 dBm)") }
    var packetBroadcastInput by remember { mutableStateOf("") }
    var packetsForwarded by remember { mutableIntStateOf(1420) }

    val nodes = remember {
        listOf(
            MeshNode("node_me", "Local Node (You)", "CITIZEN_PEER", -40, 0, "Active Host", 0.5f, 0.5f),
            MeshNode("node_civic", "Civic Center Solar Gateway", "GATEWAY", -54, 1, "Fiber Bridge Online", 0.25f, 0.28f),
            MeshNode("node_rail", "Line 3 Metro Repeater #12", "RELAY", -62, 1, "Continuous Line Power", 0.76f, 0.32f),
            MeshNode("node_bazaar", "Old Town Bazaar Node", "RELAY", -74, 2, "Battery 92%", 0.22f, 0.72f),
            MeshNode("node_sarah", "Citizen Peer: Sarah Vance", "CITIZEN_PEER", -48, 1, "Direct BLE Link", 0.72f, 0.68f),
            MeshNode("node_harbor", "Harbor Buoy Beacon #4", "SENSOR", -86, 3, "Solar Float", 0.85f, 0.88f)
        )
    }

    var packetFeed by remember {
        mutableStateOf(
            listOf(
                MeshPacket("p1", "Civic Center Solar Gateway", "Civic Dispatch #108: Water main test scheduled for 22:00", 1, "14:20", false),
                MeshPacket("p2", "Citizen Peer: Sarah Vance", "Line 3 express bypass telemetry synced to local cache", 1, "14:18", false),
                MeshPacket("p3", "Harbor Buoy Beacon #4", "Tidal sensor report: Mean high water +0.4m, wind 12 kts", 3, "14:15", false)
            )
        )
    }

    // Dynamic packet counter ticker
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            packetsForwarded += (1..4).random()
        }
    }

    fun broadcastPacket(isEmergency: Boolean = false) {
        val text = if (isEmergency) "⚠️ [EMERGENCY MESH BEACON] Citizen SOS signal broadcast from Sector 4"
        else packetBroadcastInput.ifBlank { "Decentralized peer broadcast test" }

        val pkt = MeshPacket(
            id = "pkt_${System.currentTimeMillis()}",
            sender = "You (Local Mesh Node)",
            payload = text,
            hops = 1,
            timestamp = "Just now",
            isEmergency = isEmergency
        )

        packetFeed = listOf(pkt) + packetFeed
        packetBroadcastInput = ""
        packetsForwarded += 6
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("townsquare_mesh_network_screen")
    ) {
        // Top Mesh Navigation Bar
        Surface(
            color = DarkSurfaceVariant,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("mesh_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Townsquare Decentralized Mesh", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF22C55E)))
                    }
                    Text("Off-Grid P2P Routing • Zero Internet Required", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                }

                // Emergency SOS Beacon Button
                Button(
                    onClick = { broadcastPacket(isEmergency = true) },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SOS Beacon", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // Live Mesh Metrics Strip
        Surface(
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("CONNECTED PEERS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DarkTextSecondary)
                    Text("6 Nodes Active", fontSize = 13.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                }
                Column {
                    Text("PACKETS ROUTED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DarkTextSecondary)
                    Text("$packetsForwarded Pkts", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF38BDF8))
                }
                Column {
                    Text("NETWORK RESILIENCE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DarkTextSecondary)
                    Text("99.4% (Optimal)", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF22C55E))
                }
            }
        }

        // Mesh Network Interactive Topology Canvas
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF070B14),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .padding(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val center = Offset(w * 0.5f, h * 0.5f)

                    // Draw concentric radar circles
                    drawCircle(Color(0xFF003844), radius = 40.dp.toPx(), center = center, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
                    drawCircle(Color(0xFF002830), radius = 80.dp.toPx(), center = center, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))

                    // Draw connecting mesh vectors between nodes
                    nodes.forEach { node ->
                        if (node.id != "node_me") {
                            val nodePos = Offset(w * node.posXPercent, h * node.posYPercent)
                            drawLine(
                                color = NeonCyan.copy(alpha = 0.4f),
                                start = center,
                                end = nodePos,
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                            )
                        }
                    }
                }

                // Render Nodes as Compose Overlays
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val boxW = maxWidth
                    val boxH = maxHeight
                    nodes.forEach { node ->
                        val isMe = node.id == "node_me"
                        val leftOffset = boxW * node.posXPercent - 24.dp
                        val topOffset = boxH * node.posYPercent - 24.dp

                        Surface(
                            shape = CircleShape,
                            color = if (isMe) NeonCyan else Color(0xFF1E293B),
                            border = BorderStroke(
                                1.5.dp,
                                if (node.type == "GATEWAY") Color(0xFFF59E0B) else NeonCyan
                            ),
                            modifier = Modifier
                                .offset(x = leftOffset, y = topOffset)
                                .size(if (isMe) 48.dp else 40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isMe) "YOU" else if (node.type == "GATEWAY") "GW" else if (node.type == "RELAY") "RLY" else "PEER",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isMe) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Toggles & Transmit Power Row
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Packet Relay Node", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isRelayEnabled,
                        onCheckedChange = { isRelayEnabled = it },
                        modifier = Modifier.height(20.dp),
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Gossip Sync", fontSize = 12.sp, color = DarkTextSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isOfflineGossipSync,
                        onCheckedChange = { isOfflineGossipSync = it },
                        modifier = Modifier.height(20.dp),
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Packet Traffic & Broadcast Stream
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = "Live Mesh Packet Feed",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = DarkTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(packetFeed, key = { it.id }) { pkt ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (pkt.isEmergency) Color(0xFF7F1D1D) else DarkSurface,
                        border = BorderStroke(
                            1.dp,
                            if (pkt.isEmergency) CoralRed else DarkBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(pkt.sender, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (pkt.isEmergency) Color.White else NeonCyan)
                                Text("${pkt.hops} hop • ${pkt.timestamp}", fontSize = 10.sp, color = DarkTextMuted, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(pkt.payload, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }

            // Packet Transmitter Input Bar
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = packetBroadcastInput,
                        onValueChange = { packetBroadcastInput = it },
                        placeholder = { Text("Broadcast off-grid mesh message...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { broadcastPacket(isEmergency = false) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
