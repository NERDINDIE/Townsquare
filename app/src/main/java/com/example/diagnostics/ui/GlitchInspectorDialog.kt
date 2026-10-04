package com.example.diagnostics.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.diagnostics.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlitchInspectorDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedFilterCategory by remember { mutableStateOf<GlitchCategory?>(null) }
    var selectedReportForDetails by remember { mutableStateOf<GlitchReport?>(null) }
    var showCopiedToast by remember { mutableStateOf(false) }

    val filteredReports = remember(SessionErrorHandler.glitchReports, selectedFilterCategory) {
        if (selectedFilterCategory == null) {
            SessionErrorHandler.glitchReports
        } else {
            SessionErrorHandler.glitchReports.filter { it.category == selectedFilterCategory }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("glitch_inspector_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0C131D),
            border = BorderStroke(1.5.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 1. Top Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🩺", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "System Diagnostics & Glitch Watchdog",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (BackgroundGlitchWatchdog.isRunning) Color(0xFF00C853) else Color(0xFFFF5252)
                                ) {
                                    Text(
                                        text = if (BackgroundGlitchWatchdog.isRunning) "DAEMON ACTIVE" else "IDLE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Real-time background freeze, jank & bug interception",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Real-Time System Vitals Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF141F2E),
                    border = BorderStroke(1.dp, Color(0xFF22354E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Vital 1: Stability Index
                            Column {
                                Text("SYSTEM STABILITY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "%.1f%%".format(SessionErrorHandler.systemStabilityScore),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (SessionErrorHandler.systemStabilityScore > 95f) Color(0xFF00E676) else WarmAmber
                                )
                            }

                            // Vital 2: UI Latency (Heartbeat)
                            Column {
                                Text("UI LATENCY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${BackgroundGlitchWatchdog.currentUiLatencyMs}ms",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (BackgroundGlitchWatchdog.currentUiLatencyMs < 16) NeonCyan else Color(0xFFFF5252)
                                )
                            }

                            // Vital 3: Render FPS
                            Column {
                                Text("TARGET FPS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${BackgroundGlitchWatchdog.currentFps} FPS",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (BackgroundGlitchWatchdog.currentFps >= 55) Color(0xFF00E676) else WarmAmber
                                )
                            }

                            // Vital 4: Heap Usage
                            Column {
                                Text("HEAP ALLOC", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${BackgroundGlitchWatchdog.usedHeapMb}MB / ${BackgroundGlitchWatchdog.maxHeapMb}MB",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Heap Progress Bar
                        val heapRatio = if (BackgroundGlitchWatchdog.maxHeapMb > 0) {
                            BackgroundGlitchWatchdog.usedHeapMb.toFloat() / BackgroundGlitchWatchdog.maxHeapMb.toFloat()
                        } else 0.1f
                        LinearProgressIndicator(
                            progress = { heapRatio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (heapRatio > 0.8f) Color(0xFFFF5252) else NeonCyan,
                            trackColor = Color(0xFF22354E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Quick Simulation & Diagnostic Tools
                Text(
                    text = "DIAGNOSTIC TEST SIMULATOR & ACTIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Button(
                            onClick = { SessionErrorHandler.simulateTestGlitch("ANR") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B2D16)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("⏳ Test ANR Freeze", fontSize = 11.sp, color = WarmAmber, fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Button(
                            onClick = { SessionErrorHandler.simulateTestGlitch("MEMORY") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E1A2E)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("⚠️ Test RAM Spike", fontSize = 11.sp, color = Color(0xFFFF80AB), fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Button(
                            onClick = { SessionErrorHandler.simulateTestGlitch("EXCEPTION") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF381818)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("🔴 Test Exception", fontSize = 11.sp, color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                System.gc()
                                SessionErrorHandler.logEvent(
                                    severity = DiagnosticSeverity.INFO,
                                    category = GlitchCategory.MEMORY_PRESSURE,
                                    tag = "ManualTrim",
                                    message = "User initiated proactive memory garbage collection and cache trim."
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF143026)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("🧹 Flush Memory & GC", fontSize = 11.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Button(
                            onClick = { SessionErrorHandler.clearLogs() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222B38)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("🗑️ Clear Log", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilterCategory == null,
                            onClick = { selectedFilterCategory = null },
                            label = { Text("All (${SessionErrorHandler.glitchReports.size})", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = Color(0xFF003040)
                            )
                        )
                    }

                    items(GlitchCategory.values()) { cat ->
                        val count = SessionErrorHandler.glitchReports.count { it.category == cat }
                        val isSel = selectedFilterCategory == cat
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedFilterCategory = if (isSel) null else cat },
                            label = { Text("${cat.title} ($count)", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                                selectedLabelColor = NeonCyan
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 5. Intercepted Glitch Events Stream
                if (filteredReports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("✨", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No Glitches or Anomalies Detected", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text("UI thread running smoothly with 0 active freeze events.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredReports, key = { it.id }) { report ->
                            val isExpanded = selectedReportForDetails?.id == report.id
                            val severityColor = when (report.severity) {
                                DiagnosticSeverity.FATAL -> Color(0xFFFF1744)
                                DiagnosticSeverity.ERROR -> Color(0xFFFF5252)
                                DiagnosticSeverity.ANR -> Color(0xFFFF9100)
                                DiagnosticSeverity.GLITCH -> WarmAmber
                                DiagnosticSeverity.WARNING -> Color(0xFFFFD600)
                                DiagnosticSeverity.INFO -> NeonCyan
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF111A26),
                                border = BorderStroke(1.dp, if (isExpanded) severityColor else Color(0xFF1E2D3E)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedReportForDetails = if (isExpanded) null else report
                                    }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(report.severity.badgeEmoji, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "[${report.severity.label}] ${report.tag}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = severityColor
                                            )
                                        }
                                        Text(
                                            text = report.formattedTime,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = report.message,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        lineHeight = 16.sp,
                                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    // Context Badges Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("📱 ${report.activeScreen}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("•", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("🧵 ${report.threadName}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("•", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("💾 ${report.memoryUsageMb}MB RAM", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    // Expandable Stack Trace View
                                    if (isExpanded && report.stackTrace.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF080D14),
                                            border = BorderStroke(1.dp, Color(0xFF22354E)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("STACK TRACE DEOBFUSCATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                                    Text(
                                                        text = "Copy",
                                                        fontSize = 10.sp,
                                                        color = NeonCyan,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.clickable {
                                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                            val clip = ClipData.newPlainText("Glitch Trace", report.stackTrace)
                                                            clipboard.setPrimaryClip(clip)
                                                            showCopiedToast = true
                                                        }
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = report.stackTrace,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = Color(0xFFE0E0E0),
                                                    lineHeight = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
