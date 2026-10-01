package com.example.ui.plus.extensions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.CustomExtensionManifest
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.MediaSuperappViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareExtensionsApp(
    onBack: () -> Unit,
    viewModel: MediaSuperappViewModel = viewModel()
) {
    val appSettings by viewModel.appSettings.collectAsState()
    val customExtensions by viewModel.customExtensions.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Installed & Toggles, 1 = Custom Extension Builder

    // Interactive Fullscreen Skin Previews
    var activeTestingSkinId by remember { mutableStateOf<String?>(null) }

    // Custom Extension Builder State
    var builderName by remember { mutableStateOf("My Custom Extension") }
    var builderAuthor by remember { mutableStateOf("Townsquare Creator") }
    var builderVersion by remember { mutableStateOf("1.0") }
    var builderDescription by remember { mutableStateOf("A customized skin override with custom colors & layout engine") }
    var builderCategory by remember { mutableStateOf("APP_SKIN") }
    var builderPrimaryHex by remember { mutableStateOf("#00D2FF") }
    var builderSecondaryHex by remember { mutableStateOf("#FF9500") }
    var builderBgHex by remember { mutableStateOf("#0A0E17") }
    var builderSurfaceHex by remember { mutableStateOf("#131C2E") }
    var builderFontStyle by remember { mutableStateOf("MONOSPACE") }
    var builderLayoutType by remember { mutableStateOf("TERMINAL") }
    var builderEnableSoundFx by remember { mutableStateOf(true) }
    var builderEnableCrtScanlines by remember { mutableStateOf(false) }
    var builderEnableTopTicker by remember { mutableStateOf(true) }
    var builderEnableNavKeypad by remember { mutableStateOf(false) }
    var builderWelcomeMotto by remember { mutableStateOf("Townsquare Special Edition") }

    var showJsonExportDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    // Intercept back button to exit active skin preview
    BackHandler(enabled = activeTestingSkinId != null) {
        activeTestingSkinId = null
    }

    if (activeTestingSkinId != null) {
        Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
            TopAppBar(
                title = { Text(text = "Skin Preview: $activeTestingSkinId", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { activeTestingSkinId = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close Preview", tint = NeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
            Box(modifier = Modifier.weight(1f)) {
                when (activeTestingSkinId) {
                    "GEEK_LIVE" -> GeekLiveSkin(modifier = Modifier.fillMaxSize())
                    "FANDOM_TIMES" -> FandomTimesSkin(modifier = Modifier.fillMaxSize())
                    "CLI_DOS" -> CliDosSkin(modifier = Modifier.fillMaxSize())
                    "METRO_WIN8" -> MetroWin8Skin(modifier = Modifier.fillMaxSize())
                    "ANDROID_10" -> Android10Skin(modifier = Modifier.fillMaxSize())
                    "SYMBOS_AMBER" -> SymbOSAmberSkin(modifier = Modifier.fillMaxSize())
                    "SYMBIAN_OS" -> SymbianOSSkin(modifier = Modifier.fillMaxSize())
                    else -> {
                        // Custom Extension Live View
                        val customManifest = customExtensions.find { it.id == activeTestingSkinId }
                            ?: CustomExtensionManifest(
                                id = "preview",
                                name = builderName,
                                author = builderAuthor,
                                version = builderVersion,
                                description = builderDescription,
                                primaryColorHex = builderPrimaryHex,
                                secondaryColorHex = builderSecondaryHex,
                                backgroundColorHex = builderBgHex,
                                surfaceColorHex = builderSurfaceHex,
                                fontStyle = builderFontStyle,
                                layoutType = builderLayoutType,
                                enableSoundFx = builderEnableSoundFx,
                                enableCrtScanlines = builderEnableCrtScanlines,
                                enableTopTicker = builderEnableTopTicker,
                                enableNavKeypad = builderEnableNavKeypad,
                                welcomeMotto = builderWelcomeMotto
                            )

                        CustomExtensionLivePreview(
                            manifest = customManifest,
                            onClose = { activeTestingSkinId = null }
                        )
                    }
                }
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        TopAppBar(
            title = {
                Column {
                    Text(text = "Extensions Studio & Builder", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = "Manage preinstalled skins or build custom extensions", fontSize = 11.sp, color = Color.Gray)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        // Top Studio Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = NeonCyan
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Installed Skins & Toggles", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("🛠️ Skin Builder", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("🎼 Ringtone Composer", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> InstalledExtensionsTab(
                    settings = appSettings,
                    customExtensions = customExtensions,
                    onTestRunSkin = { skinId -> activeTestingSkinId = skinId },
                    onSetMasterOverride = { override -> viewModel.updateOverrideBaseAppInterface(override) },
                    onSetActiveSkin = { skinId ->
                        viewModel.updateActiveAppSkinId(skinId)
                        viewModel.updateOverrideBaseAppInterface(skinId != null)
                    },
                    onRemoveCustomExtension = { id -> viewModel.removeCustomExtension(id) }
                )
                1 -> ExtensionBuilderStudioTab(
                    name = builderName,
                    author = builderAuthor,
                    version = builderVersion,
                    description = builderDescription,
                    category = builderCategory,
                    primaryHex = builderPrimaryHex,
                    secondaryHex = builderSecondaryHex,
                    bgHex = builderBgHex,
                    surfaceHex = builderSurfaceHex,
                    fontStyle = builderFontStyle,
                    layoutType = builderLayoutType,
                    enableSoundFx = builderEnableSoundFx,
                    enableCrtScanlines = builderEnableCrtScanlines,
                    enableTopTicker = builderEnableTopTicker,
                    enableNavKeypad = builderEnableNavKeypad,
                    welcomeMotto = builderWelcomeMotto,
                    onUpdateName = { builderName = it },
                    onUpdateAuthor = { builderAuthor = it },
                    onUpdateVersion = { builderVersion = it },
                    onUpdateDescription = { builderDescription = it },
                    onUpdateCategory = { builderCategory = it },
                    onUpdatePrimaryHex = { builderPrimaryHex = it },
                    onUpdateSecondaryHex = { builderSecondaryHex = it },
                    onUpdateBgHex = { builderBgHex = it },
                    onUpdateSurfaceHex = { builderSurfaceHex = it },
                    onUpdateFontStyle = { builderFontStyle = it },
                    onUpdateLayoutType = { builderLayoutType = it },
                    onUpdateEnableSoundFx = { builderEnableSoundFx = it },
                    onUpdateEnableCrtScanlines = { builderEnableCrtScanlines = it },
                    onUpdateEnableTopTicker = { builderEnableTopTicker = it },
                    onUpdateEnableNavKeypad = { builderEnableNavKeypad = it },
                    onUpdateWelcomeMotto = { builderWelcomeMotto = it },
                    onTestRun = { activeTestingSkinId = "CUSTOM_PREVIEW" },
                    onSaveAndActivate = {
                        val newExt = CustomExtensionManifest(
                            id = "ext_${System.currentTimeMillis()}",
                            name = builderName,
                            author = builderAuthor,
                            version = builderVersion,
                            description = builderDescription,
                            category = builderCategory,
                            primaryColorHex = builderPrimaryHex,
                            secondaryColorHex = builderSecondaryHex,
                            backgroundColorHex = builderBgHex,
                            surfaceColorHex = builderSurfaceHex,
                            fontStyle = builderFontStyle,
                            layoutType = builderLayoutType,
                            enableSoundFx = builderEnableSoundFx,
                            enableCrtScanlines = builderEnableCrtScanlines,
                            enableTopTicker = builderEnableTopTicker,
                            enableNavKeypad = builderEnableNavKeypad,
                            welcomeMotto = builderWelcomeMotto
                        )
                        viewModel.addCustomExtension(newExt)
                        viewModel.updateActiveAppSkinId(newExt.id)
                        viewModel.updateOverrideBaseAppInterface(true)
                        selectedTab = 0
                    },
                    onExportJson = { showJsonExportDialog = true }
                )
                2 -> TownsquareRingtoneComposer(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    if (showJsonExportDialog) {
        val jsonManifest = """
            {
              "id": "ext_${builderName.lowercase().replace(" ", "_")}",
              "name": "$builderName",
              "author": "$builderAuthor",
              "version": "$builderVersion",
              "description": "$builderDescription",
              "category": "$builderCategory",
              "colors": {
                "primary": "$builderPrimaryHex",
                "secondary": "$builderSecondaryHex",
                "background": "$builderBgHex",
                "surface": "$builderSurfaceHex"
              },
              "typography": "$builderFontStyle",
              "layout": "$builderLayoutType",
              "features": {
                "soundFx": $builderEnableSoundFx,
                "crtScanlines": $builderEnableCrtScanlines,
                "topTicker": $builderEnableTopTicker,
                "navKeypad": $builderEnableNavKeypad
              },
              "welcomeMotto": "$builderWelcomeMotto"
            }
        """.trimIndent()

        AlertDialog(
            onDismissRequest = { showJsonExportDialog = false },
            title = { Text("Export Extension JSON Manifest") },
            text = {
                Column {
                    Text("Copy this manifest to share your custom skin extension:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Text(
                            text = jsonManifest,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = NeonCyan,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(jsonManifest))
                        showJsonExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Copy JSON")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJsonExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun InstalledExtensionsTab(
    settings: com.example.data.model.AppSettings?,
    customExtensions: List<CustomExtensionManifest>,
    onTestRunSkin: (String) -> Unit,
    onSetMasterOverride: (Boolean) -> Unit,
    onSetActiveSkin: (String?) -> Unit,
    onRemoveCustomExtension: (String) -> Unit
) {
    val masterOverride = settings?.overrideBaseAppInterface ?: false
    val activeSkinId = settings?.activeAppSkinId

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Active Status Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (masterOverride && activeSkinId != null) NeonCyan.copy(alpha = 0.15f) else Color(0xFF1E293B),
            border = BorderStroke(1.dp, if (masterOverride && activeSkinId != null) NeonCyan else Color.Gray.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (masterOverride && activeSkinId != null) "Active App Skin Override" else "Default Base Interface Active",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (masterOverride && activeSkinId != null) "Current Skin: $activeSkinId" else "Base Townsquare Superapp layout active",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (masterOverride && activeSkinId != null) NeonCyan else Color.LightGray
                    )
                }

                Switch(
                    checked = masterOverride,
                    onCheckedChange = { onSetMasterOverride(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF003544),
                        checkedTrackColor = NeonCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "PREINSTALLED SKIN EXTENSIONS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = NeonCyan,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val preinstalled = listOf(
            ExtensionCardData("GEEK_LIVE", "Geek Live (Alpha)", "Legacy gaming, anime & newsblog update tickers", Icons.Default.SportsEsports, NeonCyan),
            ExtensionCardData("FANDOM_TIMES", "The Fandom Times Live", "Interactive portal skin with red-brown layout", Icons.Default.Newspaper, Color(0xFFD2042D)),
            ExtensionCardData("CLI_DOS", "MS-DOS Terminal v2.86", "Functional green phosphor CRT command shell", Icons.Default.Terminal, Color(0xFF33FF33)),
            ExtensionCardData("METRO_WIN8", "Windows 8 Metro Start", "Flat live tile start screen & diagnostics", Icons.Default.Gamepad, Color(0xFF38BDF8)),
            ExtensionCardData("ANDROID_10", "Android 1.0 G1 Retro", "Nostalgic 2008 Android G1 & trackball", Icons.Default.Phonelink, Color(0xFFA4C639)),
            ExtensionCardData("SYMBOS_AMBER", "SymbOS Amber CRT", "8-bit multi-tasking desktop with amber phosphor glow", Icons.Default.DesktopWindows, Color(0xFFFFB000)),
            ExtensionCardData("SYMBIAN_OS", "Symbian OS Series 60", "Classic Nokia S60 mobile interface with 3G active standby", Icons.Default.PhoneAndroid, Color(0xFF00D2FF))
        )

        preinstalled.forEach { item ->
            val isActive = activeSkinId == item.id && masterOverride
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("extension_card_${item.id}"),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, if (isActive) item.color else Color.Gray.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                                Text(item.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }

                        if (isActive) {
                            Surface(shape = RoundedCornerShape(6.dp), color = item.color) {
                                Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { onTestRunSkin(item.id) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, item.color)
                        ) {
                            Text("Launch / Preview", color = item.color, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (isActive) {
                                    onSetActiveSkin(null)
                                } else {
                                    onSetActiveSkin(item.id)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isActive) Color.DarkGray else item.color,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isActive) "Deactivate" else "Set as App Skin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (customExtensions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "CUSTOM USER EXTENSIONS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = WarmAmber,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            customExtensions.forEach { custom ->
                val isActive = activeSkinId == custom.id && masterOverride
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18140B)),
                    border = BorderStroke(1.dp, if (isActive) WarmAmber else Color.Gray.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("${custom.name} v${custom.version}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                                    Text("By ${custom.author} • ${custom.description}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                            IconButton(onClick = { onRemoveCustomExtension(custom.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { onTestRunSkin(custom.id) },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, WarmAmber)
                            ) {
                                Text("Preview", color = WarmAmber, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (isActive) {
                                        onSetActiveSkin(null)
                                    } else {
                                        onSetActiveSkin(custom.id)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isActive) Color.DarkGray else WarmAmber,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isActive) "Deactivate" else "Set as App Skin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private data class ExtensionCardData(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExtensionBuilderStudioTab(
    name: String,
    author: String,
    version: String,
    description: String,
    category: String,
    primaryHex: String,
    secondaryHex: String,
    bgHex: String,
    surfaceHex: String,
    fontStyle: String,
    layoutType: String,
    enableSoundFx: Boolean,
    enableCrtScanlines: Boolean,
    enableTopTicker: Boolean,
    enableNavKeypad: Boolean,
    welcomeMotto: String,
    onUpdateName: (String) -> Unit,
    onUpdateAuthor: (String) -> Unit,
    onUpdateVersion: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateCategory: (String) -> Unit,
    onUpdatePrimaryHex: (String) -> Unit,
    onUpdateSecondaryHex: (String) -> Unit,
    onUpdateBgHex: (String) -> Unit,
    onUpdateSurfaceHex: (String) -> Unit,
    onUpdateFontStyle: (String) -> Unit,
    onUpdateLayoutType: (String) -> Unit,
    onUpdateEnableSoundFx: (Boolean) -> Unit,
    onUpdateEnableCrtScanlines: (Boolean) -> Unit,
    onUpdateEnableTopTicker: (Boolean) -> Unit,
    onUpdateEnableNavKeypad: (Boolean) -> Unit,
    onUpdateWelcomeMotto: (String) -> Unit,
    onTestRun: () -> Unit,
    onSaveAndActivate: () -> Unit,
    onExportJson: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "DESIGN CUSTOM SKIN EXTENSION",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = WarmAmber,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Basic Info
        OutlinedTextField(
            value = name,
            onValueChange = onUpdateName,
            label = { Text("Extension Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = Color.Gray)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = author,
                onValueChange = onUpdateAuthor,
                label = { Text("Author") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = Color.Gray)
            )
            OutlinedTextField(
                value = version,
                onValueChange = onUpdateVersion,
                label = { Text("Version") },
                modifier = Modifier.width(100.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = Color.Gray)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onUpdateDescription,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = Color.Gray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Color Palettes
        Text(
            text = "ONE-TAP PALETTE PRESETS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = NeonCyan,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val palettes = listOf(
            PalettePreset("Cyber Neon", "#00D2FF", "#FF007F", "#080B10", "#121926"),
            PalettePreset("Amber Retrowave", "#FF9500", "#FFD600", "#1A1208", "#2A1E0E"),
            PalettePreset("Matrix Terminal", "#33FF33", "#00AA00", "#051005", "#0D200D"),
            PalettePreset("Gutenberg Codex", "#C8AD7F", "#8B5A2B", "#231F19", "#342E25"),
            PalettePreset("Y2K Hot Pink", "#FF007F", "#00E5FF", "#100518", "#220A30")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            palettes.forEach { pal ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(android.graphics.Color.parseColor(pal.bgHex)),
                    border = BorderStroke(1.dp, Color(android.graphics.Color.parseColor(pal.primaryHex))),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onUpdatePrimaryHex(pal.primaryHex)
                            onUpdateSecondaryHex(pal.secondaryHex)
                            onUpdateBgHex(pal.bgHex)
                            onUpdateSurfaceHex(pal.surfaceHex)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(14.dp).background(Color(android.graphics.Color.parseColor(pal.primaryHex)), CircleShape))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(pal.name, fontSize = 9.sp, color = Color.White, maxLines = 1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Typography Selector
        Text("TYPOGRAPHY ENGINE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 6.dp)) {
            listOf("MONOSPACE", "SERIF", "SANS", "PIXEL").forEach { font ->
                val isSel = fontStyle == font
                FilterChip(
                    selected = isSel,
                    onClick = { onUpdateFontStyle(font) },
                    label = { Text(font, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Layout Engine Selector
        Text("LAYOUT ENGINE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 6.dp)) {
            listOf("TERMINAL", "TILES", "BROADSHEET", "MAGAZINE", "MOBILE_3G").forEach { layout ->
                val isSel = layoutType == layout
                FilterChip(
                    selected = isSel,
                    onClick = { onUpdateLayoutType(layout) },
                    label = { Text(layout, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Feature Switches
        Text("FUNCTIONAL HOOKS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
        SwitchRow("Enable Retro Audio Sound FX", enableSoundFx, onUpdateEnableSoundFx)
        SwitchRow("Enable CRT Phosphor Scanlines", enableCrtScanlines, onUpdateEnableCrtScanlines)
        SwitchRow("Enable Top Rolling Wire Ticker", enableTopTicker, onUpdateEnableTopTicker)
        SwitchRow("Enable Directional Nav Keypad Bar", enableNavKeypad, onUpdateEnableNavKeypad)

        Spacer(modifier = Modifier.height(16.dp))

        // LIVE PREVIEW CARD
        Text("LIVE EXTENSION PREVIEW", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = WarmAmber)
        Spacer(modifier = Modifier.height(6.dp))

        val parseColorSafe = { hex: String, fallback: Color ->
            try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { fallback }
        }

        val primaryCol = parseColorSafe(primaryHex, NeonCyan)
        val bgCol = parseColorSafe(bgHex, DarkBg)
        val surfaceCol = parseColorSafe(surfaceHex, DarkSurface)

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = bgCol,
            border = BorderStroke(1.5.dp, primaryCol),
            modifier = Modifier.fillMaxWidth().height(160.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(name, color = primaryCol, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("v$version", color = Color.Gray, fontSize = 10.sp)
                }

                if (enableTopTicker) {
                    Surface(color = primaryCol.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("⚡ LIVE WIRE TICKER: Custom extension wire connected...", color = primaryCol, fontSize = 10.sp, modifier = Modifier.padding(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(color = surfaceCol, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(welcomeMotto, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Layout: $layoutType • Font: $fontStyle", color = Color.LightGray, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onTestRun,
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, NeonCyan)
            ) {
                Text("Test Run", color = NeonCyan)
            }

            Button(
                onClick = onSaveAndActivate,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800))
            ) {
                Text("Save & Activate", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onExportJson, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Icon(Icons.Default.Code, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Export JSON Manifest", color = NeonCyan, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private data class PalettePreset(
    val name: String,
    val primaryHex: String,
    val secondaryHex: String,
    val bgHex: String,
    val surfaceHex: String
)

@Composable
private fun SwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 12.sp, color = Color.White)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
        )
    }
}

@Composable
fun CustomExtensionLivePreview(
    manifest: CustomExtensionManifest,
    onClose: () -> Unit
) {
    val parseColorSafe = { hex: String, fallback: Color ->
        try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { fallback }
    }

    val primaryCol = parseColorSafe(manifest.primaryColorHex, NeonCyan)
    val secondaryCol = parseColorSafe(manifest.secondaryColorHex, WarmAmber)
    val bgCol = parseColorSafe(manifest.backgroundColorHex, DarkBg)
    val surfaceCol = parseColorSafe(manifest.surfaceColorHex, DarkSurface)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgCol)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(manifest.name, color = primaryCol, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Created by ${manifest.author} • v${manifest.version}", color = Color.Gray, fontSize = 12.sp)
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (manifest.enableTopTicker) {
            Surface(
                color = primaryCol.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, primaryCol),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚡ WIRE TICKER: [${manifest.name}] ${manifest.welcomeMotto} — Real-time stream connected.",
                    color = primaryCol,
                    fontSize = 11.sp,
                    fontFamily = if (manifest.fontStyle == "MONOSPACE") FontFamily.Monospace else FontFamily.Default,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Surface(
            color = surfaceCol,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, secondaryCol),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("CUSTOM EXTENSION INTERFACE", color = secondaryCol, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = manifest.description,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontFamily = if (manifest.fontStyle == "MONOSPACE") FontFamily.Monospace else FontFamily.Default
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = bgCol,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, primaryCol.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("● Live Wire Feed Item 1", color = primaryCol, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Editorial dispatch rendered inside ${manifest.name} layout engine (${manifest.layoutType}).", color = Color.LightGray, fontSize = 11.sp)
                    }
                }

                Surface(
                    color = bgCol,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, primaryCol.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("● Live Wire Feed Item 2", color = primaryCol, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Custom extension active as full application skin override.", color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = primaryCol, contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Exit Skin Preview", fontWeight = FontWeight.Bold)
        }
    }
}
