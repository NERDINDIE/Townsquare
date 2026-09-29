package com.example.ui.plus.extensions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareExtensionsApp(onBack: () -> Unit) {
    var isGeekLiveActive by remember { mutableStateOf(false) }
    var isFandomTimesActive by remember { mutableStateOf(false) }
    var isCliDosActive by remember { mutableStateOf(false) }
    var isMetroWin8Active by remember { mutableStateOf(false) }

    // Intercept back button to exit active skin instead of closing Extensions entirely
    BackHandler(enabled = isGeekLiveActive || isFandomTimesActive || isCliDosActive || isMetroWin8Active) {
        isGeekLiveActive = false
        isFandomTimesActive = false
        isCliDosActive = false
        isMetroWin8Active = false
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        TopAppBar(
            title = {
                Text(
                    text = if (isGeekLiveActive) "Geek Live Skin" 
                           else if (isFandomTimesActive) "The Fandom Times Live" 
                           else if (isCliDosActive) "MS-DOS Terminal v2.86" 
                           else if (isMetroWin8Active) "Windows 8 Metro Start" 
                           else "Extensions Builder",
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        if (isGeekLiveActive || isFandomTimesActive || isCliDosActive || isMetroWin8Active) {
                            isGeekLiveActive = false
                            isFandomTimesActive = false
                            isCliDosActive = false
                            isMetroWin8Active = false
                        } else {
                            onBack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NeonCyan
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface),
            actions = {
                if (isGeekLiveActive || isFandomTimesActive || isCliDosActive || isMetroWin8Active) {
                    IconButton(
                        onClick = {
                            isGeekLiveActive = false
                            isFandomTimesActive = false
                            isCliDosActive = false
                            isMetroWin8Active = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Skin",
                            tint = Color.White
                        )
                    }
                }
            }
        )
        
        Box(modifier = Modifier.weight(1f)) {
            if (isGeekLiveActive) {
                GeekLiveSkin(modifier = Modifier.fillMaxSize())
            } else if (isFandomTimesActive) {
                FandomTimesSkin(modifier = Modifier.fillMaxSize())
            } else if (isCliDosActive) {
                CliDosSkin(modifier = Modifier.fillMaxSize())
            } else if (isMetroWin8Active) {
                MetroWin8Skin(modifier = Modifier.fillMaxSize())
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Customize Your Townsquare Superapp Experience",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.LightGray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Text(
                        text = "PREINSTALLED SKIN EXTENSIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = NeonCyan,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // 1. Geek Live Skin
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isGeekLiveActive = true }
                            .testTag("geek_live_extension_card"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Extension,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Geek Live (Alpha)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                Text("Legacy alpha portal skin with gaming, retro, anime sections, and live newsblog update tickers.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Fandom Times Skin
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isFandomTimesActive = true }
                            .testTag("fandom_times_extension_card"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Extension,
                                contentDescription = null,
                                tint = Color(0xFFD2042D),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("The Fandom Times Live", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                Text("Interactive portal skin with toggling side navigation, secure login, search bar, and custom red-brown themed news tiles.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. CLI/DOS Command Terminal Skin
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCliDosActive = true }
                            .testTag("cli_dos_extension_card"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Terminal,
                                contentDescription = null,
                                tint = Color(0xFF33FF33),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("MS-DOS Terminal v2.86", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                Text("Independent functional command shell. Run queries to request news, weather, civil ID, audio feeds, and custom phosphor CRT colors.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Windows 8 Metro Skin
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isMetroWin8Active = true }
                            .testTag("metro_win8_extension_card"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Gamepad,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Windows 8 Metro Start Screen", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                Text("Flat live tile start screen. Interactive panels display active audio, mail alerts, and click overlays with complete diagnostic logs.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
