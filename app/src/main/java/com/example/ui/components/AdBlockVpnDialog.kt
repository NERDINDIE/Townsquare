package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class VpnNode(
    val id: String,
    val name: String,
    val location: String,
    val pingMs: Int,
    val securityProtocol: String // "WireGuard Mesh", "OpenVPN AES-256", "Quantum Tunnel"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdBlockVpnDialog(
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // State
    var isAdBlockEnabled by remember { mutableStateOf(true) }
    var isVpnConnected by remember { mutableStateOf(true) }
    var isConnectingVpn by remember { mutableStateOf(false) }
    var killSwitchEnabled by remember { mutableStateOf(true) }
    var malwareProtection by remember { mutableStateOf(true) }

    // Stats State
    var adsBlockedCount by remember { mutableIntStateOf(1482) }
    var trackersBlockedCount by remember { mutableIntStateOf(892) }
    var dataSavedMb by remember { mutableFloatStateOf(142.8f) }

    val nodes = listOf(
        VpnNode("NODE-1", "Townsquare Node Alpha", "Local Civic Gateway", 12, "WireGuard Mesh"),
        VpnNode("NODE-2", "Townsquare Node Beta", "Encrypted Regional Relay", 24, "AES-256 Tunnel"),
        VpnNode("NODE-3", "Secure Mesh Gamma", "Off-Grid Community Node", 45, "Quantum Shield"),
        VpnNode("NODE-4", "Privacy Tunnel Delta", "High Anonymity Node", 68, "Tor-Mesh Bridge")
    )

    var selectedNodeIndex by remember { mutableIntStateOf(0) }
    var statusLog by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .padding(10.dp)
                .testTag("adblock_vpn_dialog"),
            colors = CardDefaults.cardColors(containerColor = DarkBg),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MintTeal.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isVpnConnected) MintTeal.copy(alpha = 0.2f) else CoralRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isVpnConnected) Icons.Default.VpnLock else Icons.Default.LockOpen,
                                contentDescription = "VPN Shield",
                                tint = if (isVpnConnected) MintTeal else CoralRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TOWNSQUARE SHIELD",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MintTeal
                                ) {
                                    Text(
                                        text = "ADBLOCK + VPN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DarkBg,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Built-in ad removal & encrypted mesh network tunnel",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // VPN Connection Control Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isVpnConnected) MintTeal.copy(alpha = 0.12f) else DarkCardBg
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isVpnConnected) MintTeal else Color.Gray.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = if (isVpnConnected) "ENCRYPTED VPN ACTIVE" else "VPN DISCONNECTED",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isVpnConnected) MintTeal else CoralRed
                                            )
                                        )
                                        Text(
                                            text = if (isVpnConnected) "Virtual IP: 10.244.18.99 (AES-256 Encrypted)" else "Your real IP address is exposed",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                                        )
                                    }

                                    Switch(
                                        checked = isVpnConnected,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                isConnectingVpn = true
                                                coroutineScope.launch {
                                                    delay(1200L)
                                                    isVpnConnected = true
                                                    isConnectingVpn = false
                                                    statusLog = "Connected to ${nodes[selectedNodeIndex].name}"
                                                }
                                            } else {
                                                isVpnConnected = false
                                                statusLog = "VPN Tunnel Disconnected"
                                            }
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = DarkBg,
                                            checkedTrackColor = MintTeal
                                        ),
                                        modifier = Modifier.testTag("vpn_toggle_switch")
                                    )
                                }

                                if (isConnectingVpn) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = MintTeal,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Establishing secure handshakes...", fontSize = 12.sp, color = MintTeal)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Selected Relay:", fontSize = 10.sp, color = Color.Gray)
                                        Text(nodes[selectedNodeIndex].name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Latency / Protocol:", fontSize = 10.sp, color = Color.Gray)
                                        Text("${nodes[selectedNodeIndex].pingMs} ms • ${nodes[selectedNodeIndex].securityProtocol}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    }
                                }
                            }
                        }
                    }

                    // Node Selector List
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "SELECT MESH VPN NODE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MintTeal,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                nodes.forEachIndexed { idx, node ->
                                    val isSel = selectedNodeIndex == idx
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) MintTeal.copy(alpha = 0.2f) else Color.Transparent)
                                            .clickable { selectedNodeIndex = idx }
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = node.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSel) MintTeal else Color.White
                                                )
                                            )
                                            Text(
                                                text = "${node.location} • ${node.securityProtocol}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${node.pingMs} ms",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    color = if (node.pingMs < 30) MintTeal else WarmAmber
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            RadioButton(
                                                selected = isSel,
                                                onClick = { selectedNodeIndex = idx },
                                                colors = RadioButtonDefaults.colors(selectedColor = MintTeal)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // AdBlocker Security Shield Settings & Telemetry
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "AD & TRACKER BLOCKER",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = NeonCyan,
                                                letterSpacing = 1.sp
                                            )
                                        )
                                        Text(
                                            text = "DNS filtering & cosmetic ad stripping",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                        )
                                    }

                                    Switch(
                                        checked = isAdBlockEnabled,
                                        onCheckedChange = { isAdBlockEnabled = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = DarkBg,
                                            checkedTrackColor = NeonCyan
                                        ),
                                        modifier = Modifier.testTag("adblock_toggle_switch")
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Telemetry Counters
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$adsBlockedCount", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 18.sp)
                                        Text("Ads Blocked", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$trackersBlockedCount", fontWeight = FontWeight.Bold, color = WarmAmber, fontSize = 18.sp)
                                        Text("Trackers Neutralized", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${String.format("%.1f", dataSavedMb)} MB", fontWeight = FontWeight.Bold, color = MintTeal, fontSize = 18.sp)
                                        Text("Data Saved", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    // Advanced Security Toggles
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "ADVANCED PRIVACY RULES",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Network Kill Switch", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
                                        Text("Block internet if VPN disconnects unexpectedly", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 10.sp))
                                    }
                                    Switch(
                                        checked = killSwitchEnabled,
                                        onCheckedChange = { killSwitchEnabled = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = DarkBg, checkedTrackColor = MintTeal)
                                    )
                                }

                                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Malicious Domain Filter", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
                                        Text("Block phishing and malware telemetry sites", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 10.sp))
                                    }
                                    Switch(
                                        checked = malwareProtection,
                                        onCheckedChange = { malwareProtection = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = DarkBg, checkedTrackColor = MintTeal)
                                    )
                                }
                            }
                        }
                    }
                }

                if (statusLog != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = statusLog!!,
                        style = MaterialTheme.typography.bodySmall.copy(color = MintTeal, fontFamily = FontFamily.Monospace)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        adsBlockedCount += (1..5).random()
                        trackersBlockedCount += (1..3).random()
                        dataSavedMb += 1.2f
                        statusLog = "DNS Filter Rules Flushed & Whitelist Updated"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("flush_adblock_rules_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MintTeal, contentColor = DarkBg),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("FLUSH PRIVACY FILTERS & OPTIMIZE", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
