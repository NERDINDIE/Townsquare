package com.example.ui.plus.extensions

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class G1AppIcon(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val actionType: String
)

@Composable
fun Android10Skin(
    modifier: Modifier = Modifier
) {
    var isNotificationDrawerOpen by remember { mutableStateOf(false) }
    var activeAppDialog by remember { mutableStateOf<String?>(null) }
    var trackballHighlightIndex by remember { mutableIntStateOf(0) }

    val appsList = remember {
        listOf(
            G1AppIcon("Browser", Icons.Default.Language, Color(0xFF1E88E5), "browser"),
            G1AppIcon("Gmail", Icons.Default.Email, Color(0xFFD32F2F), "gmail"),
            G1AppIcon("Market", Icons.Default.ShoppingBag, Color(0xFF388E3C), "market"),
            G1AppIcon("Maps", Icons.Default.Place, Color(0xFFF57C00), "maps"),
            G1AppIcon("Dialer", Icons.Default.Phone, Color(0xFF009688), "dialer"),
            G1AppIcon("Media", Icons.Default.MusicNote, Color(0xFF7B1FA2), "media"),
            G1AppIcon("Arcade", Icons.Default.Gamepad, Color(0xFF33FF33), "arcade"),
            G1AppIcon("Settings", Icons.Default.Settings, Color(0xFF616161), "settings")
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .testTag("android_10_skin_root")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Android 1.0 Retro Top Status Bar
            Surface(
                color = Color(0xFF1E1E1E),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.SignalCellular4Bar, contentDescription = null, tint = Color(0xFFA4C639), modifier = Modifier.size(12.dp))
                        Text("3G", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFA4C639), fontFamily = FontFamily.Monospace)
                        Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                    }

                    Text("ANDROID 1.0 G1", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA4C639), letterSpacing = 1.sp)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = Color(0xFFA4C639), modifier = Modifier.size(12.dp))
                        Text("10:08 AM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // 2. Vintage Green Title Banner
            Surface(
                color = Color(0xFFA4C639),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Extension, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Android 1.0 • Townsquare OS", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 13.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = Color.Black.copy(alpha = 0.2f),
                        modifier = Modifier.clickable {
                            Android10SoundEffects.playShadeSlide()
                            isNotificationDrawerOpen = !isNotificationDrawerOpen
                        }
                    ) {
                        Text(
                            text = if (isNotificationDrawerOpen) "Close Shade" else "Shade ▼",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // 3. Android 1.0 Home Screen Canvas
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Retro Analog Clock & Search Bar Widget Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF262626),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA4C639).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOWNSQUARE WEB SEARCH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA4C639))
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFA4C639), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Search news, dispatch, channels...", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // Retro App Grid Drawer
                Text("APPLICATIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(appsList.size) { index ->
                        val item = appsList[index]
                        val isHighlighted = index == trackballHighlightIndex
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isHighlighted) Color(0xFFA4C639).copy(alpha = 0.2f) else Color(0xFF1E1E1E),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isHighlighted) Color(0xFFA4C639) else Color(0xFF333333)
                            ),
                            modifier = Modifier
                                .clickable {
                                    Android10SoundEffects.playAppLaunchSound()
                                    trackballHighlightIndex = index
                                    activeAppDialog = item.actionType
                                }
                                .testTag("g1_app_${item.actionType}")
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = item.color,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.name, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 4. Physical G1 Trackball Simulation Controller Bar
            Surface(
                color = Color(0xFF1A1A1A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        Android10SoundEffects.playTrackballClick()
                        trackballHighlightIndex = (trackballHighlightIndex - 1 + appsList.size) % appsList.size
                    }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = Color(0xFFA4C639))
                    }

                    // Physical Pearl Trackball Visual
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.Gray),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                Android10SoundEffects.playAppLaunchSound()
                                activeAppDialog = appsList[trackballHighlightIndex].actionType
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Surface(shape = CircleShape, color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.size(12.dp)) {}
                        }
                    }

                    IconButton(onClick = {
                        Android10SoundEffects.playTrackballClick()
                        trackballHighlightIndex = (trackballHighlightIndex + 1) % appsList.size
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color(0xFFA4C639))
                    }
                }
            }
        }

        // 5. Pull-Down Notifications Shade Overlay
        AnimatedVisibility(
            visible = isNotificationDrawerOpen,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.95f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ANDROID 1.0 NOTIFICATIONS SHADE", fontWeight = FontWeight.Bold, color = Color(0xFFA4C639), fontSize = 12.sp)
                        IconButton(onClick = { isNotificationDrawerOpen = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    NotificationItemRow("📰 Civic Wire", "Grand Promenade opening ribbon cutting tomorrow morning.")
                    NotificationItemRow("✉️ Encrypted Mail", "New message from Mayor's Office with attached PDF.")
                    NotificationItemRow("🎮 Arcade Leaderboard", "Your high score on Space Invaders was recorded!")
                }
            }
        }

        // 6. Retro App Launch Dialog
        if (activeAppDialog != null) {
            AlertDialog(
                onDismissRequest = { activeAppDialog = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Extension, contentDescription = null, tint = Color(0xFFA4C639))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(activeAppDialog!!.uppercase() + " APP", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Text(
                        "Launched ${activeAppDialog!!.uppercase()} in Android 1.0 G1 Emulation environment. Connection to Townsquare superapp backend established.",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { activeAppDialog = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA4C639), contentColor = Color.Black)
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
private fun NotificationItemRow(title: String, body: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF1E1E1E),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { Android10SoundEffects.playNotificationChime() }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFFA4C639), fontSize = 12.sp)
            Text(body, color = Color.White, fontSize = 11.sp)
        }
    }
}
