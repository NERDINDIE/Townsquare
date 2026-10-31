package com.example.ui.plus.iot

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlobalSandwichMenu
import com.example.ui.components.SandwichMenuItem
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalDateTime

/**
 * Environments and companion tools available in the Townsquare IoT Companion.
 */
enum class IotDeviceEnvironment(
    val title: String,
    val iconEmoji: String,
    val description: String,
    val accentColor: Color
) {
    BLUETOOTH_MANAGER("Bluetooth Hub", "📱", "Manage connected devices of any type", Color(0xFF00D2FF)),
    SMARTWATCH("Smartwatch OS", "⌚", "Titanium bezel, AMOLED watch faces & complications", Color(0xFF10B981)),
    CAR_DASHBOARD("Car Dashboard", "🚗", "Speedometer, engine gauges & car media", Color(0xFFEF4444)),
    AUTOMOTIVE_CLUSTER("In-Car Cluster", "🚘", "Heads-up speed & turn-by-turn guidance", Color(0xFF00D2FF)),
    MP3_PLAYER("Vintage MP3", "🎵", "Retro monochrome LCD & tactile click wheel", Color(0xFFFFB300)),
    EINK_HOME_DISPLAY("E-Ink Bedside", "🏡", "High-contrast daily civic bulletin & climate", Color(0xFF4A463B)),
    REMOTE_COMPUTER("Workstation", "🖥️", "Remote Linux terminal & server telemetry", Color(0xFFA855F7))
}

/**
 * Modernized Townsquare IoT Companion App
 * - Compact title header with collapsible sandwich menu (powered by GlobalSandwichMenu)
 * - Standalone modular simulation components (Smartwatch, E-Ink, Automotive Cluster, MP3, Computer, Car)
 * - Universal Bluetooth device management for any connected peripheral
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareIotCompanionApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeEnvironment by remember { mutableStateOf(IotDeviceEnvironment.BLUETOOTH_MANAGER) }
    var isSandwichMenuOpen by remember { mutableStateOf(false) }
    var liveTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            liveTime = LocalDateTime.now()
            delay(1000L)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("townsquare_iot_companion_root")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // COMPACT TITLE HEADER WITH SANDWICH MENU
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("iot_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                        }

                        // Collapsible Sandwich Menu Icon Button
                        IconButton(
                            onClick = { isSandwichMenuOpen = !isSandwichMenuOpen },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSandwichMenuOpen) NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                                .testTag("iot_sandwich_menu_button")
                        ) {
                            Icon(
                                imageVector = if (isSandwichMenuOpen) Icons.Default.Close else Icons.Default.Menu,
                                contentDescription = "Toggle Device Menu",
                                tint = if (isSandwichMenuOpen) NeonCyan else Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "IoT Companion",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = activeEnvironment.accentColor.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, activeEnvironment.accentColor)
                                ) {
                                    Text(
                                        text = "${activeEnvironment.iconEmoji} ${activeEnvironment.title}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = activeEnvironment.accentColor,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Connected hardware hub & multi-device simulation",
                                fontSize = 10.sp,
                                color = DarkTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Active Hardware Status Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, MintTeal)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(MintTeal)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "BT Online",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintTeal
                            )
                        }
                    }
                }
            }

            // COLLAPSIBLE SANDWICH MENU DRAWER (Using GlobalSandwichMenu)
            GlobalSandwichMenu(
                isOpen = isSandwichMenuOpen,
                title = "COMPANION ENVIRONMENTS & HARDWARE",
                items = IotDeviceEnvironment.values().map { env ->
                    SandwichMenuItem(
                        id = env.name,
                        title = env.title,
                        description = env.description,
                        iconEmoji = env.iconEmoji,
                        accentColor = env.accentColor,
                        isSelected = activeEnvironment == env
                    )
                },
                onSelectItem = { menuItem ->
                    activeEnvironment = IotDeviceEnvironment.valueOf(menuItem.id)
                    isSandwichMenuOpen = false
                }
            )

            // ACTIVE ENVIRONMENT COMPONENT VIEW
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeEnvironment) {
                    IotDeviceEnvironment.BLUETOOTH_MANAGER -> {
                        IotBluetoothManagerView()
                    }
                    IotDeviceEnvironment.SMARTWATCH -> {
                        IotSmartwatchView(liveTime = liveTime)
                    }
                    IotDeviceEnvironment.CAR_DASHBOARD -> {
                        IotCarDashboardView(liveTime = liveTime)
                    }
                    IotDeviceEnvironment.AUTOMOTIVE_CLUSTER -> {
                        IotAutomotiveClusterView(liveTime = liveTime)
                    }
                    IotDeviceEnvironment.MP3_PLAYER -> {
                        IotMp3PlayerView()
                    }
                    IotDeviceEnvironment.EINK_HOME_DISPLAY -> {
                        IotEInkDisplayView(liveTime = liveTime)
                    }
                    IotDeviceEnvironment.REMOTE_COMPUTER -> {
                        IotRemoteComputerView()
                    }
                }
            }
        }
    }
}
