package com.example.ui.plus.iot

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Universal Bluetooth Device Manager View
 * Manages any connected Bluetooth hardware regardless of device type
 * (Audio, Smartwatches, Automotive, Peripherals, Smart Home, Beacons).
 * Includes QR Code Scanner button & viewfinder dialog for instant pairing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IotBluetoothManagerView(
    modifier: Modifier = Modifier
) {
    // Initial rich catalog of connected and paired Bluetooth devices
    var devices by remember {
        mutableStateOf(
            listOf(
                BluetoothDeviceEntity(
                    id = "bt_1",
                    name = "Townsquare Studio Monitor Buds",
                    macAddress = "7C:9E:BD:41:8A:10",
                    type = BluetoothDeviceType.AUDIO_HEADPHONES,
                    connectionState = BluetoothConnectionState.CONNECTED,
                    batteryLevel = 92,
                    signalRssi = -42,
                    firmwareVersion = "v3.1.0",
                    codecOrProfile = "LDAC 990kbps (Audiophile)",
                    isAutoConnectEnabled = true,
                    isLowLatencyMode = false,
                    isNotificationSyncEnabled = true,
                    isAudioForwardingEnabled = true
                ),
                BluetoothDeviceEntity(
                    id = "bt_2",
                    name = "Townsquare Horizon Chrono Watch",
                    macAddress = "34:81:C4:F2:19:6B",
                    type = BluetoothDeviceType.SMARTWATCH,
                    connectionState = BluetoothConnectionState.CONNECTED,
                    batteryLevel = 74,
                    signalRssi = -55,
                    firmwareVersion = "v2.8.4",
                    codecOrProfile = "BLE 5.3 GATT Sync",
                    isAutoConnectEnabled = true,
                    isLowLatencyMode = true,
                    isNotificationSyncEnabled = true,
                    isAudioForwardingEnabled = false
                ),
                BluetoothDeviceEntity(
                    id = "bt_3",
                    name = "Municipal Transit Car Console",
                    macAddress = "AA:BB:CC:88:99:00",
                    type = BluetoothDeviceType.CAR_AUDIO_OBD,
                    connectionState = BluetoothConnectionState.CONNECTED,
                    batteryLevel = null, // Vehicle powered
                    signalRssi = -38,
                    firmwareVersion = "v5.0.12",
                    codecOrProfile = "aptX HD & CANbus Telemetry",
                    isAutoConnectEnabled = true,
                    isLowLatencyMode = false,
                    isNotificationSyncEnabled = true,
                    isAudioForwardingEnabled = true
                ),
                BluetoothDeviceEntity(
                    id = "bt_4",
                    name = "Slimline Ergonomic Field Keyboard",
                    macAddress = "E0:D5:5E:23:44:11",
                    type = BluetoothDeviceType.KEYBOARD_MOUSE,
                    connectionState = BluetoothConnectionState.DISCONNECTED,
                    batteryLevel = 58,
                    signalRssi = -76,
                    firmwareVersion = "v1.0.8",
                    codecOrProfile = "Bluetooth HID Profile",
                    isAutoConnectEnabled = false,
                    isLowLatencyMode = true,
                    isNotificationSyncEnabled = false,
                    isAudioForwardingEnabled = false,
                    lastConnectedTime = "Yesterday at 17:40"
                ),
                BluetoothDeviceEntity(
                    id = "bt_5",
                    name = "Townsquare Civic Air Quality Beacon",
                    macAddress = "50:02:91:AA:FF:33",
                    type = BluetoothDeviceType.IOT_SENSOR_BEACON,
                    connectionState = BluetoothConnectionState.CONNECTED,
                    batteryLevel = 100,
                    signalRssi = -60,
                    firmwareVersion = "v1.4.2",
                    codecOrProfile = "Environmental Telemetry Broadcast",
                    isAutoConnectEnabled = true,
                    isLowLatencyMode = false,
                    isNotificationSyncEnabled = false,
                    isAudioForwardingEnabled = false
                )
            )
        )
    }

    var selectedTypeFilter by remember { mutableStateOf<BluetoothDeviceType?>(null) }
    var selectedDeviceForManage by remember { mutableStateOf<BluetoothDeviceEntity?>(null) }
    var isScanDialogOpen by remember { mutableStateOf(false) }
    var isQrScannerDialogOpen by remember { mutableStateOf(false) }
    var statusToastMessage by remember { mutableStateOf<String?>(null) }

    // Nearby discovered devices during scanning
    val nearbyScanPool = remember {
        listOf(
            BluetoothScanItem("scan_1", "Civic Park Weather Node #4", "6C:29:90:11:22:33", BluetoothDeviceType.IOT_SENSOR_BEACON, -65),
            BluetoothScanItem("scan_2", "Pro Acoustic Wireless Soundbar", "90:B6:86:77:88:99", BluetoothDeviceType.AUDIO_SPEAKER, -48),
            BluetoothScanItem("scan_3", "Townsquare Dual-Trigger Gamepad", "12:34:56:78:9A:BC", BluetoothDeviceType.GAME_CONTROLLER, -55),
            BluetoothScanItem("scan_4", "Garmin Civic Pulse Ring", "FE:DC:BA:98:76:54", BluetoothDeviceType.FITNESS_BAND, -72)
        )
    }

    // Filtered list
    val displayedDevices = remember(devices, selectedTypeFilter) {
        if (selectedTypeFilter == null) devices
        else devices.filter { it.type == selectedTypeFilter }
    }

    val connectedCount = devices.count { it.connectionState == BluetoothConnectionState.CONNECTED }

    // ---------------------------------------------------------------------
    // QR Code Scanner Viewfinder Dialog
    // ---------------------------------------------------------------------
    if (isQrScannerDialogOpen) {
        Dialog(onDismissRequest = { isQrScannerDialogOpen = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF070C14)),
                border = BorderStroke(2.dp, NeonCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("bluetooth_qr_scanner_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Scan Device QR Code",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        IconButton(onClick = { isQrScannerDialogOpen = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Align device pairing QR code inside the viewfinder window below",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Viewfinder Box Simulation
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF020617))
                            .border(2.dp, NeonCyan, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simulated QR graphic background
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = NeonCyan.copy(alpha = 0.4f), modifier = Modifier.size(90.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Awaiting Optical QR Lock...", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = NeonCyan)
                        }

                        // Scanning Laser Beam Animation Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(2.dp)
                                .background(MintTeal)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Or tap a sample QR code below to instantly simulate scanning:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sample QR Code Quick-Scan Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("Townsquare Pro ANC Pods", BluetoothDeviceType.AUDIO_HEADPHONES, "BT-PODS-991"),
                            Triple("Civic Smart Fitness Ring", BluetoothDeviceType.FITNESS_BAND, "RING-CIVIC-404"),
                            Triple("Metropolitan Hi-Fi Speaker", BluetoothDeviceType.AUDIO_SPEAKER, "SPK-METRO-882")
                        ).forEach { (name, type, mac) ->
                            Button(
                                onClick = {
                                    val newDev = BluetoothDeviceEntity(
                                        id = "qr_${System.currentTimeMillis()}",
                                        name = name,
                                        macAddress = mac,
                                        type = type,
                                        connectionState = BluetoothConnectionState.CONNECTED,
                                        batteryLevel = 95,
                                        signalRssi = -35,
                                        firmwareVersion = "v2.0.1",
                                        codecOrProfile = "Instant QR Pairing"
                                    )
                                    devices = devices + newDev
                                    statusToastMessage = "✨ Successfully paired '$name' via QR code!"
                                    isQrScannerDialogOpen = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(type.iconEmoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("Scan & Pair ⚡", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    // Device Management Settings Modal
    // ---------------------------------------------------------------------
    if (selectedDeviceForManage != null) {
        val target = selectedDeviceForManage!!
        var editName by remember(target) { mutableStateOf(target.name) }
        var isAutoConnect by remember(target) { mutableStateOf(target.isAutoConnectEnabled) }
        var isLowLatency by remember(target) { mutableStateOf(target.isLowLatencyMode) }
        var isNotificationSync by remember(target) { mutableStateOf(target.isNotificationSyncEnabled) }
        var isAudioForward by remember(target) { mutableStateOf(target.isAudioForwardingEnabled) }

        Dialog(onDismissRequest = { selectedDeviceForManage = null }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, target.type.categoryColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("bluetooth_device_settings_dialog")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(target.type.iconEmoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Device Settings",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = target.type.label,
                                    fontSize = 11.sp,
                                    color = target.type.categoryColor
                                )
                            }
                        }
                        IconButton(onClick = { selectedDeviceForManage = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Rename Field
                    Text("DEVICE NAME / ALIAS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = target.type.categoryColor,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Hardware Specs Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("MAC Address:", fontSize = 11.sp, color = Color.Gray)
                                Text(target.macAddress, fontSize = 11.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Firmware:", fontSize = 11.sp, color = Color.Gray)
                                Text(target.firmwareVersion, fontSize = 11.sp, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Protocol / Codec:", fontSize = 11.sp, color = Color.Gray)
                                Text(target.codecOrProfile, fontSize = 11.sp, color = target.type.categoryColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Toggle Settings
                    Text("MANAGEMENT OPTIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Auto-Connect Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto-Reconnect", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            Text("Connect automatically within range", fontSize = 10.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isAutoConnect,
                            onCheckedChange = { isAutoConnect = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = target.type.categoryColor)
                        )
                    }

                    // Low-Latency Gaming / Sync Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Low-Latency Link", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            Text("Prioritize 10ms response over range", fontSize = 10.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isLowLatency,
                            onCheckedChange = { isLowLatency = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = target.type.categoryColor)
                        )
                    }

                    // Notification & Audio Forwarding
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Sync Townsquare Alerts", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            Text("Forward live dispatches & bulletins", fontSize = 10.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isNotificationSync,
                            onCheckedChange = { isNotificationSync = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = target.type.categoryColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Actions: Save Changes & Unpair
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                devices = devices.filter { it.id != target.id }
                                statusToastMessage = "Removed '${target.name}' from paired Bluetooth devices"
                                selectedDeviceForManage = null
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                            border = BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Forget", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                devices = devices.map {
                                    if (it.id == target.id) {
                                        it.copy(
                                            name = editName.ifBlank { target.name },
                                            isAutoConnectEnabled = isAutoConnect,
                                            isLowLatencyMode = isLowLatency,
                                            isNotificationSyncEnabled = isNotificationSync,
                                            isAudioForwardingEnabled = isAudioForward
                                        )
                                    } else it
                                }
                                statusToastMessage = "Updated settings for '${editName}'"
                                selectedDeviceForManage = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = target.type.categoryColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save", fontSize = 12.sp, color = Color(0xFF002233), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    // Radar Scan Dialog for Adding New Devices
    // ---------------------------------------------------------------------
    if (isScanDialogOpen) {
        var isScanningRadar by remember { mutableStateOf(true) }
        var discoveredItems by remember { mutableStateOf(nearbyScanPool) }

        LaunchedEffect(isScanningRadar) {
            if (isScanningRadar) {
                delay(2500L)
                isScanningRadar = false
            }
        }

        Dialog(onDismissRequest = { isScanDialogOpen = false }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, NeonCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("bluetooth_scan_dialog")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BluetoothSearching, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pair New Device",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        IconButton(onClick = { isScanDialogOpen = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isScanningRadar) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Scanning 2.4GHz Bluetooth spectrum...", fontSize = 12.sp, color = Color.LightGray)
                        }
                    } else {
                        Text(
                            text = "AVAILABLE NEARBY DEVICES (${discoveredItems.size})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.heightIn(max = 240.dp)
                        ) {
                            items(discoveredItems) { scanned ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF1E293B))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Text(scanned.type.iconEmoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(scanned.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text("${scanned.type.label} • ${scanned.rssi} dBm", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            val newDev = BluetoothDeviceEntity(
                                                id = "paired_${System.currentTimeMillis()}",
                                                name = scanned.name,
                                                macAddress = scanned.macAddress,
                                                type = scanned.type,
                                                connectionState = BluetoothConnectionState.CONNECTED,
                                                batteryLevel = (60..100).random(),
                                                signalRssi = scanned.rssi,
                                                firmwareVersion = "v1.0.0",
                                                codecOrProfile = "Standard BLE Profile"
                                            )
                                            devices = devices + newDev
                                            statusToastMessage = "Paired and connected to '${scanned.name}'"
                                            isScanDialogOpen = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Pair", fontSize = 11.sp, color = Color(0xFF003442), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { isScanningRadar = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Re-scan Spectrum", color = NeonCyan, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    // Main UI Layout
    // ---------------------------------------------------------------------
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("iot_bluetooth_manager_screen")
    ) {
        // Hero Overview Card with QR Scanner and Add Device buttons
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bluetooth, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Universal Bluetooth Hub",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Text(
                            text = "$connectedCount of ${devices.size} devices active",
                            style = MaterialTheme.typography.bodySmall,
                            color = MintTeal
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { isQrScannerDialogOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp),
                            border = BorderStroke(1.dp, NeonCyan)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan QR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }

                        Button(
                            onClick = { isScanDialogOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF003544), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF003544))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                val isSel = selectedTypeFilter == null
                FilterChip(
                    selected = isSel,
                    onClick = { selectedTypeFilter = null },
                    label = { Text("All Devices (${devices.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan.copy(alpha = 0.25f),
                        selectedLabelColor = NeonCyan
                    )
                )
            }
            items(BluetoothDeviceType.values()) { type ->
                val isSel = selectedTypeFilter == type
                val count = devices.count { it.type == type }
                if (count > 0 || isSel) {
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedTypeFilter = if (isSel) null else type },
                        label = { Text("${type.iconEmoji} ${type.label} ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = type.categoryColor.copy(alpha = 0.25f),
                            selectedLabelColor = type.categoryColor
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (statusToastMessage != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MintTeal.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, MintTeal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(statusToastMessage ?: "", color = MintTeal, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = { statusToastMessage = null }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = MintTeal, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Device Cards List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(displayedDevices) { device ->
                BluetoothDeviceCard(
                    device = device,
                    onToggleConnection = {
                        devices = devices.map {
                            if (it.id == device.id) {
                                val newState = if (it.connectionState == BluetoothConnectionState.CONNECTED)
                                    BluetoothConnectionState.DISCONNECTED
                                else BluetoothConnectionState.CONNECTED
                                it.copy(connectionState = newState)
                            } else it
                        }
                    },
                    onManage = { selectedDeviceForManage = device }
                )
            }
        }
    }
}

/**
 * Individual Bluetooth Device Card with Battery, Signal Gauge & Action Controls
 */
@Composable
private fun BluetoothDeviceCard(
    device: BluetoothDeviceEntity,
    onToggleConnection: () -> Unit,
    onManage: () -> Unit
) {
    val isConnected = device.connectionState == BluetoothConnectionState.CONNECTED

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) Color(0xFF131D2E) else Color(0xFF0F172A)
        ),
        border = BorderStroke(
            1.dp,
            if (isConnected) device.type.categoryColor.copy(alpha = 0.5f) else Color(0xFF334155)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Type Avatar + Title + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = device.type.categoryColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, device.type.categoryColor),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(device.type.iconEmoji, fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = device.type.label,
                                fontSize = 10.sp,
                                color = device.type.categoryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${device.macAddress}",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Connection status chip
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = device.connectionState.badgeColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, device.connectionState.badgeColor)
                ) {
                    Text(
                        text = device.connectionState.label,
                        color = device.connectionState.badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Row: Live Battery & Signal Strength Gauge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A0F1D))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Battery Meter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (device.batteryLevel != null) {
                        val bat = device.batteryLevel
                        val batColor = when {
                            bat > 50 -> MintTeal
                            bat > 20 -> WarmAmber
                            else -> Color(0xFFEF4444)
                        }
                        Icon(
                            imageVector = when {
                                bat > 80 -> Icons.Default.BatteryFull
                                bat > 40 -> Icons.Default.Battery5Bar
                                else -> Icons.Default.BatteryAlert
                            },
                            contentDescription = "Battery",
                            tint = batColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$bat% Battery",
                            fontSize = 11.sp,
                            color = batColor,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(Icons.Default.Power, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("External / Direct Power", fontSize = 11.sp, color = Color.LightGray)
                    }
                }

                // Signal Strength / RSSI Gauge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${device.signalRssi} dBm",
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Action Row: Connect Toggle & Manage Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = device.codecOrProfile,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onToggleConnection,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isConnected) Color.LightGray else MintTeal
                        )
                    ) {
                        Text(if (isConnected) "Disconnect" else "Connect", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onManage,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = device.type.categoryColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Manage", fontSize = 11.sp, color = device.type.categoryColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
