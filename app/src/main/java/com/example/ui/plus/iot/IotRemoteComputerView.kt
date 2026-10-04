package com.example.ui.plus.iot

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

@Composable
fun IotRemoteComputerView(
    modifier: Modifier = Modifier
) {
    var terminalInput by remember { mutableStateOf("") }
    var terminalLogs by remember {
        mutableStateOf(
            listOf(
                "Townsquare Remote Node v4.12.0-lts (x86_64-linux-civic)",
                "System uptime: 18 days, 4 hours, 22 minutes",
                "Connected to remote workstation: node-alpha.townsquare.local [10.0.4.15]",
                "All daemons operational: mesh-relay, broadsheet-indexer, epg-streamer",
                "Type 'help', 'top', 'status', 'disk', 'reboot', or 'clear' below."
            )
        )
    }

    var cpuLoad by remember { mutableIntStateOf(24) }
    var ramUsedGb by remember { mutableFloatStateOf(8.4f) }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Terminal, 1: Remote GUI VNC, 2: System Health

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            cpuLoad = (18 + Math.random() * 20).toInt()
            ramUsedGb = 8.2f + (Math.random() * 0.6f).toFloat()
        }
    }

    fun handleCommand(cmd: String) {
        val trimmed = cmd.trim().lowercase()
        val newLogs = terminalLogs.toMutableList()
        newLogs.add("user@townsquare:~$ $cmd")

        when {
            trimmed == "clear" -> {
                terminalLogs = listOf("Terminal cleared.")
                terminalInput = ""
                return
            }
            trimmed == "help" -> {
                newLogs.add("Available commands:")
                newLogs.add("  status  - Print active system services & mesh connectivity")
                newLogs.add("  top     - Display running processes and thread allocation")
                newLogs.add("  disk    - View connected NVMe and SD storage mounts")
                newLogs.add("  sensors - Thermal sensors and fan RPM readouts")
                newLogs.add("  ping    - Ping Townsquare municipal gateway")
                newLogs.add("  clear   - Clear terminal screen buffer")
            }
            trimmed == "status" -> {
                newLogs.add("[OK] meshd.service: Active (running) - 14 peer hops")
                newLogs.add("[OK] civic-erp-sync: Synchronized at 10:14:02")
                newLogs.add("[OK] broadsheet-pdf-renderer: Idle (ready)")
            }
            trimmed == "top" -> {
                newLogs.add("PID  USER     PR  NI  VIRT   RES   %CPU  %MEM  COMMAND")
                newLogs.add("1042 civic    20   0  4.2G  1.1G   14.2   3.4  townsquare-daemon")
                newLogs.add("1088 mesh     20   0  1.8G  450M    5.1   1.4  mesh-router")
                newLogs.add("2011 stream   20   0  2.4G  800M    3.8   2.5  epg-cable-mux")
            }
            trimmed == "disk" -> {
                newLogs.add("Filesystem      Size  Used Avail Use% Mounted on")
                newLogs.add("/dev/nvme0n1p2  953G  210G  743G  22% /")
                newLogs.add("/dev/sda1       2.0T  850G  1.1T  43% /mnt/media_vault")
            }
            trimmed == "sensors" -> {
                newLogs.add("Package id 0:  +41.0°C (high = +84.0°C, crit = +100.0°C)")
                newLogs.add("Core 0:        +39.0°C")
                newLogs.add("Chassis Fan:   1,240 RPM (Quiet Profile)")
            }
            trimmed.startsWith("ping") -> {
                newLogs.add("PING gateway.townsquare.local (10.0.0.1) 56(84) bytes of data.")
                newLogs.add("64 bytes from 10.0.0.1: icmp_seq=1 ttl=64 time=0.482 ms")
                newLogs.add("64 bytes from 10.0.0.1: icmp_seq=2 ttl=64 time=0.512 ms")
            }
            trimmed.isNotBlank() -> {
                newLogs.add("bash: $trimmed: command not found. Type 'help' for options.")
            }
        }
        terminalLogs = newLogs
        terminalInput = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(14.dp)
            .testTag("iot_remote_computer_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Workstation Header Bar
        Surface(
            color = DarkSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Computer, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Remote Workstation (VNC/SSH)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Text("node-alpha.townsquare.local • Secure Tunnel (TLS 1.3)", fontSize = 10.sp, color = DarkTextSecondary)
                    }
                }

                // Power actions
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = { handleCommand("status") },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = NeonCyan.copy(alpha = 0.2f), contentColor = NeonCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    FilledTonalButton(
                        onClick = { handleCommand("sensors") },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = WarmAmber.copy(alpha = 0.2f), contentColor = WarmAmber),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Sensors", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Sub Tabs (Terminal Shell vs GUI Desktop vs Hardware Vitals)
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = DarkSurface,
            contentColor = NeonCyan
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("⚡ Interactive Shell", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("🖥️ Remote Desktop VNC", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("📊 Resource Diagnostics", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        // Content Area
        Box(modifier = Modifier.weight(1f)) {
            when (activeTab) {
                0 -> {
                    // Terminal Shell
                    Surface(
                        color = Color(0xFF030712),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF1F2937)),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                items(terminalLogs) { line ->
                                    Text(
                                        text = line,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (line.startsWith("user@")) NeonCyan else if (line.startsWith("[OK]")) Color(0xFF4ADE80) else Color(0xFFE5E7EB)
                                    )
                                }
                            }

                            // Command input line
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("user@townsquare:~$ ", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = NeonCyan, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = terminalInput,
                                    onValueChange = { terminalInput = it },
                                    modifier = Modifier.weight(1f).testTag("remote_cmd_input"),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = Color(0xFF374151),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(onClick = { handleCommand(terminalInput) }) {
                                    Icon(Icons.Default.Send, contentDescription = "Run", tint = NeonCyan)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Simulated Remote Desktop GUI
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                            // Desktop Top Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Applications", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Places", fontSize = 11.sp, color = DarkTextSecondary)
                                }
                                Text("1080p @ 60 FPS (Latency: 12ms)", fontSize = 10.sp, color = Color(0xFF22C55E), fontFamily = FontFamily.Monospace)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Simulated Window: Townsquare Broadsheet CMS Studio
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarkSurface,
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().weight(1f)
                            ) {
                                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Townsquare Desktop Editor Pro v3.2", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WarmAmber))
                                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CoralRed))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("File: /srv/broadsheet/editions/october_waterfront.dispatch", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = DarkTextSecondary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        "1  // Townsquare Municipal Broadsheet Dispatch\n2  const editionDate = '2026-10-04';\n3  const headline = 'Waterfront Promenade Grand Opening';\n4  const authors = ['Sarah Vance', 'Marcus Chen'];\n5  print('Status: Press run verified & cached.');",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF93C5FD),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Resource Diagnostics
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("CPU Load ($cpuLoad%)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { cpuLoad / 100f },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = if (cpuLoad > 70) CoralRed else NeonCyan,
                                    trackColor = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("AMD Ryzen Embedded 8-Core Processor @ 3.4 GHz", fontSize = 11.sp, color = DarkTextSecondary)
                            }
                        }

                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("RAM Usage (${String.format("%.1f", ramUsedGb)} / 32.0 GB)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { ramUsedGb / 32f },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = Color(0xFF38BDF8),
                                    trackColor = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("DDR5-5600 ECC Memory · 23.6 GB Available for Caching", fontSize = 11.sp, color = DarkTextSecondary)
                            }
                        }

                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Mesh Tunnel Bandwidth", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                    Text("RX: 142.8 MB/s · TX: 48.2 MB/s", fontSize = 11.sp, color = Color(0xFF4ADE80), fontFamily = FontFamily.Monospace)
                                }
                                Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
