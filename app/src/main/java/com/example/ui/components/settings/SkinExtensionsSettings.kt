package com.example.ui.components.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.CustomExtensionManifest
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun SkinExtensionsSettings(
    settings: AppSettings?,
    customExtensions: List<CustomExtensionManifest> = emptyList(),
    onUpdateActiveAppSkin: (String?) -> Unit,
    onUpdateActiveWelcomeSkin: (String) -> Unit,
    onUpdateOverrideBaseAppInterface: (Boolean) -> Unit,
    onUpdateRetroTerminalMode: (Boolean) -> Unit,
    onUpdateKeitai3GOverlay: (Boolean) -> Unit,
    onUpdateManuscriptParchmentTheme: (Boolean) -> Unit,
    onUpdateMetroTilesView: (Boolean) -> Unit,
    onUpdateGeekLiveTickerHeader: (Boolean) -> Unit,
    onOpenExtensionBuilderInPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val overrideEnabled = settings?.overrideBaseAppInterface ?: false
    val activeAppSkin = settings?.activeAppSkinId
    val activeWelcomeSkin = settings?.activeWelcomeSkinId ?: "BROADSHEET"

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier.fillMaxWidth().testTag("skin_extensions_settings_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Extension,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Skin Extensions & Overrides",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Preinstalled & custom interface overrides",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NeonCyan.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "EXTENSIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Master Override Toggle Switch
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (overrideEnabled) NeonCyan.copy(alpha = 0.12f) else Color(0xFF161E2E),
                border = BorderStroke(1.dp, if (overrideEnabled) NeonCyan else DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Master App Interface Override",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (overrideEnabled) "Selected skin extension overrides base app UI" else "Base app interface active",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (overrideEnabled) NeonCyan else Color.Gray
                        )
                    }
                    Switch(
                        checked = overrideEnabled,
                        onCheckedChange = { onUpdateOverrideBaseAppInterface(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF003544),
                            checkedTrackColor = NeonCyan
                        ),
                        modifier = Modifier.testTag("master_skin_override_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "PREINSTALLED APP SKIN EXTENSIONS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                color = NeonCyan,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Skin Selector List
            val preinstalledAppSkins = listOf(
                SkinOption(null, "Base Townsquare UI", "Default superapp interface", Icons.Default.Dashboard, Color(0xFF00D2FF)),
                SkinOption("GEEK_LIVE", "Geek Live Skin (Alpha)", "Legacy gaming, anime & newsblog tickers", Icons.Default.SportsEsports, Color(0xFF00D2FF)),
                SkinOption("FANDOM_TIMES", "The Fandom Times Live", "Interactive portal skin with red-brown layout", Icons.Default.Newspaper, Color(0xFFD2042D)),
                SkinOption("CLI_DOS", "MS-DOS Terminal v2.86", "Functional green phosphor command shell", Icons.Default.Terminal, Color(0xFF33FF33)),
                SkinOption("METRO_WIN8", "Windows 8 Metro Start", "Flat live tile dashboard & diagnostics", Icons.Default.Gamepad, Color(0xFF38BDF8)),
                SkinOption("ANDROID_10", "Android 1.0 G1 Retro", "Nostalgic 2008 Android G1 & trackball", Icons.Default.Phonelink, Color(0xFFA4C639)),
                SkinOption("SYMBOS_AMBER", "SymbOS Amber CRT", "8-bit multi-tasking desktop with amber phosphor glow", Icons.Default.DesktopWindows, Color(0xFFFFB000)),
                SkinOption("SYMBIAN_OS", "Symbian OS Series 60", "Classic Nokia S60 mobile interface with 3G active standby", Icons.Default.PhoneAndroid, Color(0xFF00D2FF))
            )

            preinstalledAppSkins.forEach { option ->
                val isSelected = activeAppSkin == option.id
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) option.tint.copy(alpha = 0.18f) else Color(0xFF131C2E),
                    border = BorderStroke(1.dp, if (isSelected) option.tint else DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable {
                            onUpdateActiveAppSkin(option.id)
                            if (option.id != null) {
                                onUpdateOverrideBaseAppInterface(true)
                            }
                        }
                        .testTag("skin_select_${option.id ?: "base"}")
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                onUpdateActiveAppSkin(option.id)
                                if (option.id != null) {
                                    onUpdateOverrideBaseAppInterface(true)
                                }
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = option.tint)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            tint = option.tint,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = option.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }

            // Render Custom Created Extensions if any
            if (customExtensions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "CUSTOM USER EXTENSIONS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                    color = WarmAmber,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                customExtensions.forEach { custom ->
                    val isSelected = activeAppSkin == custom.id
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) WarmAmber.copy(alpha = 0.18f) else Color(0xFF18140B),
                        border = BorderStroke(1.dp, if (isSelected) WarmAmber else DarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                onUpdateActiveAppSkin(custom.id)
                                onUpdateOverrideBaseAppInterface(true)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onUpdateActiveAppSkin(custom.id)
                                    onUpdateOverrideBaseAppInterface(true)
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = WarmAmber)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = WarmAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${custom.name} v${custom.version}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = custom.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Welcome Screen Skin Override Selector
            Text(
                text = "WELCOME SCREEN EXTENSION OVERRIDE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                color = NeonCyan,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val welcomeSkinList = listOf(
                "BROADSHEET" to "📰 Broadsheet Press",
                "TABLOID" to "🔥 Red Tabloid",
                "MAGAZINE" to "🖼️ Glossy Magazine",
                "DASHBOARD" to "📊 Ops Dashboard",
                "KEITAI" to "📲 Keitai i-Mode",
                "MANUSCRIPT" to "📜 Gutenberg Codex",
                "Y2K_DESKTOP" to "💻 Y2K Desktop"
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                welcomeSkinList.forEach { (skinId, label) ->
                    val isSelected = activeWelcomeSkin == skinId
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color(0xFF131C2E),
                        border = if (isSelected) BorderStroke(1.dp, NeonCyan) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdateActiveWelcomeSkin(skinId) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                color = if (isSelected) NeonCyan else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Functional Feature Overrides
            Text(
                text = "FUNCTIONAL INTERFACE OVERRIDES",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                color = NeonCyan,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ToggleRow("Retro Phosphor Terminal Mode", "Enforces green CRT phosphor text across wire feeds", settings?.enableRetroTerminalMode ?: false, onUpdateRetroTerminalMode)
            ToggleRow("Keitai 3G Carrier & Keypad Overlay", "Displays retro signal bar & physical keypad controls", settings?.enableKeitai3GOverlay ?: false, onUpdateKeitai3GOverlay)
            ToggleRow("Manuscript Parchment Paper Theme", "Gutenberg codex aged paper & serif typography", settings?.enableManuscriptParchmentTheme ?: false, onUpdateManuscriptParchmentTheme)
            ToggleRow("Metro Windows 8 Flat Tiles Stream", "Transforms feed cards into interactive live tiles", settings?.enableMetroTilesView ?: false, onUpdateMetroTilesView)
            ToggleRow("Geek Live Rolling Ticker Header", "Top bar anime/gaming live news updates ticker", settings?.enableGeekLiveTickerHeader ?: false, onUpdateGeekLiveTickerHeader)

            Spacer(modifier = Modifier.height(16.dp))

            // Open Extension Builder Studio Button
            Button(
                onClick = onOpenExtensionBuilderInPlus,
                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("open_extension_builder_from_settings_btn")
            ) {
                Icon(imageVector = Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Extension Builder Studio in Plus", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

private data class SkinOption(
    val id: String?,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tint: Color
)

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(text = subtitle, fontSize = 10.sp, color = Color.Gray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF003544),
                checkedTrackColor = NeonCyan
            )
        )
    }
}
