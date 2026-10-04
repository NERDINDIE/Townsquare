package com.example.server.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.server.TownsquareServerManager
import com.example.ui.theme.*

/**
 * Townsquare OS Backend Server Console & REST API Hub Dialog.
 * Allows users and developers to manage the embedded HTTP server, view real-time request logs,
 * test remote control endpoints, and inspect server telemetry.
 */
@Composable
fun TownsquareServerConsoleDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var selectedPort by remember { mutableIntStateOf(TownsquareServerManager.serverPort) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Dns,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Townsquare Backend Server",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Native Android HTTP & REST Gateway",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Server Status Hero Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (TownsquareServerManager.isServerRunning) DarkSurface else DarkBg
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (TownsquareServerManager.isServerRunning) Color(0xFF10B981).copy(alpha = 0.5f) else Color.DarkGray
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (TownsquareServerManager.isServerRunning) Color(0xFF10B981) else Color.Red)
                                )
                                Text(
                                    text = if (TownsquareServerManager.isServerRunning) "SERVER ONLINE" else "SERVER OFFLINE",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (TownsquareServerManager.isServerRunning) Color(0xFF10B981) else Color.Red
                                    )
                                )
                            }

                            FilledTonalButton(
                                onClick = {
                                    TownsquareServerManager.toggleServer(context)
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (TownsquareServerManager.isServerRunning) Color(0xFFEF4444).copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f),
                                    contentColor = if (TownsquareServerManager.isServerRunning) Color(0xFFEF4444) else NeonCyan
                                )
                            ) {
                                Icon(
                                    imageVector = if (TownsquareServerManager.isServerRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (TownsquareServerManager.isServerRunning) "Stop Server" else "Start Server")
                            }
                        }

                        if (TownsquareServerManager.isServerRunning) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkCardBg)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = TownsquareServerManager.serverUrl,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Server URL", TownsquareServerManager.serverUrl))
                                        Toast.makeText(context, "URL copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonCyan, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(
                        label = "Requests",
                        value = "${TownsquareServerManager.totalRequestsServed}",
                        icon = Icons.Default.Http,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "Port",
                        value = "${TownsquareServerManager.serverPort}",
                        icon = Icons.Default.Router,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "Protocol",
                        value = "HTTP/1.1",
                        icon = Icons.Default.Language,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Endpoints & Remote Actions
                Text(
                    text = "API Endpoints & Remote Control",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EndpointChip(method = "GET", path = "/api/status")
                    EndpointChip(method = "GET", path = "/api/news")
                    EndpointChip(method = "POST", path = "/api/remote/control")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Request Logs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Server Request Log (${TownsquareServerManager.requestLogs.size})",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                    )
                    if (TownsquareServerManager.requestLogs.isNotEmpty()) {
                        TextButton(
                            onClick = { TownsquareServerManager.clearLogs() },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Clear", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (TownsquareServerManager.requestLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (TownsquareServerManager.isServerRunning) "Waiting for incoming HTTP requests from LAN / Web companion..." else "Start the server above to listen for client connections.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(TownsquareServerManager.requestLogs) { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = log.method,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (log.method == "POST") WarmAmber else NeonCyan
                                        )
                                    )
                                    Text(
                                        text = log.path,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White
                                        )
                                    )
                                }
                                Text(
                                    text = "${log.clientIp} • ${log.formattedTime}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                Text(label, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp))
            }
            Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
        }
    }
}

@Composable
private fun EndpointChip(method: String, path: String) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = method,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (method == "POST") WarmAmber else NeonCyan,
                    fontSize = 10.sp
                )
            )
            Text(
                text = path,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}
