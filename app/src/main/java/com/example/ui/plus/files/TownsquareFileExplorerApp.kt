package com.example.ui.plus.files

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

data class ExplorerFile(
    val id: String,
    val name: String,
    val parentPath: String,
    val isDirectory: Boolean,
    val sizeText: String,
    val dateModified: String,
    val extension: String,
    val iconEmoji: String,
    val previewContent: String = ""
)

@Composable
fun TownsquareFileExplorerApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentVolume by remember { mutableStateOf("Internal Storage") }
    var currentPath by remember { mutableStateOf("/Townsquare") }
    var searchQuery by remember { mutableStateOf("") }
    var viewingFile by remember { mutableStateOf<ExplorerFile?>(null) }
    var isNewFolderOpen by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    var allFiles by remember {
        mutableStateOf(
            listOf(
                // Root folders
                ExplorerFile("f_dir_1", "broadsheets", "/Townsquare", true, "4 items", "Today", "DIR", "📁"),
                ExplorerFile("f_dir_2", "podcasts_audio", "/Townsquare", true, "3 items", "Today", "DIR", "📁"),
                ExplorerFile("f_dir_3", "camera_ar_snaps", "/Townsquare", true, "2 items", "Yesterday", "DIR", "📁"),
                ExplorerFile("f_dir_4", "dvr_broadcasts", "/Townsquare", true, "2 items", "Yesterday", "DIR", "📁"),
                ExplorerFile("f_dir_5", "citizen_notes", "/Townsquare", true, "3 items", "3 days ago", "DIR", "📁"),

                // Inside /Townsquare/broadsheets
                ExplorerFile("f_bs_1", "oct_04_morning_dispatch.pdf", "/Townsquare/broadsheets", false, "34.2 MB", "Today 06:00", "PDF", "📰", "Townsquare Broadsheet - Morning Edition (Oct 4, 2026)\nFront Page: Waterfront Promenade Ribbon Cutting ceremony attracts thousands.\nLine 3 Metro transit bypass completed ahead of schedule.\nWeather: Clear crisp autumn skies, 68°F."),
                ExplorerFile("f_bs_2", "oct_03_evening_gazette.pdf", "/Townsquare/broadsheets", false, "28.5 MB", "Yesterday 18:00", "PDF", "📰", "Townsquare Evening Gazette (Oct 3, 2026)\nEditorial Column: The Future of Municipal Renewable Microgrids.\nArts & Curiosities: Secret antique glassware found in Old Town Bazaar."),
                ExplorerFile("f_bs_3", "press_editorial_charter.txt", "/Townsquare/broadsheets", false, "12 KB", "2026-09-20", "TXT", "📄", "Townsquare Editorial Charter v2.4\nIndependent, non-partisan municipal journalism committed to public transit, historic preservation, and community reporting."),

                // Inside /Townsquare/podcasts_audio
                ExplorerFile("f_pod_1", "weatherman_ep08_autumn.mp3", "/Townsquare/podcasts_audio", false, "18.4 MB", "Today 08:30", "MP3", "🎙️", "Audio File: Weatherman Podcast Episode 8\nHost: Melancholic Barista\nDuration: 04:12\nBitrate: 320 kbps Stereo"),
                ExplorerFile("f_pod_2", "oak_creek_school_radio.mp3", "/Townsquare/podcasts_audio", false, "12.1 MB", "Today 09:00", "MP3", "📻", "Audio File: Oak Creek School Radio 88.5 FM\nHost: Leo & Morning Crew\nRecess jokes & Science fair interview."),

                // Inside /Townsquare/camera_ar_snaps
                ExplorerFile("f_cam_1", "ar_geo_pin_promenade.jpg", "/Townsquare/camera_ar_snaps", false, "4.2 MB", "Today 10:14", "JPG", "🖼️", "AR Snapshot: Harbor Promenade Pier 3\nSpatial Telemetry: HDG 142° • ALT 34m\nCivic Geo-Pin: Waterfront Promenade (920m NE)"),
                ExplorerFile("f_cam_2", "thermal_hud_junction.jpg", "/Townsquare/camera_ar_snaps", false, "3.8 MB", "Yesterday 21:00", "JPG", "🖼️", "FLIR Thermal Night HUD Capture\nAmbient: 21.4°C • IR Gain: +18dB\nObject: Line 3 Rail Power Substation"),

                // Inside /Townsquare/dvr_broadcasts
                ExplorerFile("f_dvr_1", "central_tv_civic_news.mp4", "/Townsquare/dvr_broadcasts", false, "180.0 MB", "Yesterday 20:30", "MP4", "🎬", "DVR Recording: Townsquare Central Television News 24\nDuration: 30:00\nResolution: 1080p 60fps"),

                // Inside /Townsquare/citizen_notes
                ExplorerFile("f_note_1", "metro_schedule_notes.txt", "/Townsquare/citizen_notes", false, "4 KB", "Today 11:20", "TXT", "📝", "Citizen Note: Remember to validate Metro Card at North Gate.\nExpress tram departs at :05 and :35 every hour."),
                ExplorerFile("f_note_2", "marketplace_ledger_export.csv", "/Townsquare/citizen_notes", false, "18 KB", "2026-10-01", "CSV", "📊", "ID,Date,Item,Amount,Credits\n101,2026-10-01,Artisan Coffee,1,18\n102,2026-10-02,Broadsheet Press,1,2")
            )
        )
    }

    val volumes = listOf("Internal Storage", "Removable SD Card", "Civic Cloud Vault")

    // Filter files for current directory
    val filesInDir = allFiles.filter {
        it.parentPath == currentPath &&
        (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("townsquare_file_explorer_screen")
    ) {
        // Explorer Header Bar
        Surface(
            color = DarkSurfaceVariant,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (currentPath != "/Townsquare") {
                                    currentPath = currentPath.substringBeforeLast("/")
                                    if (currentPath.isEmpty()) currentPath = "/Townsquare"
                                } else {
                                    onBack()
                                }
                            },
                            modifier = Modifier.testTag("file_explorer_back_button")
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Townsquare File Explorer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text("Local Storage, Broadsheet PDFs & Media Vault", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                        }
                    }

                    // New Folder Button
                    IconButton(onClick = { isNewFolderOpen = true }) {
                        Icon(imageVector = Icons.Default.CreateNewFolder, contentDescription = "New Folder", tint = NeonCyan)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Storage Volumes Carousel
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(volumes) { vol ->
                        val isSelected = currentVolume == vol
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color(0xFF1E293B),
                            border = BorderStroke(1.dp, if (isSelected) NeonCyan else Color.Transparent),
                            modifier = Modifier.clickable { currentVolume = vol }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (vol.contains("SD")) Icons.Default.SdCard else if (vol.contains("Cloud")) Icons.Default.Cloud else Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = if (isSelected) NeonCyan else Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(vol, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) NeonCyan else Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Storage Usage Meter Bar
        Surface(
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Storage: 14.8 GB Used / 64.0 GB Total", fontSize = 11.sp, color = DarkTextSecondary)
                    Text("77% Free", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { 14.8f / 64.0f },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = NeonCyan,
                    trackColor = Color(0xFF1E293B)
                )
            }
        }

        // Path Breadcrumb & Search Bar
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentPath,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter files in this directory...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }

        if (toastMessage != null) {
            Surface(
                color = Color(0xFF064E3B),
                border = BorderStroke(1.dp, Color(0xFF10B981)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(toastMessage!!, fontSize = 11.sp, color = Color.White, modifier = Modifier.weight(1f))
                    IconButton(onClick = { toastMessage = null }, modifier = Modifier.size(16.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray)
                    }
                }
            }
        }

        // Files and Folders List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filesInDir.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📁", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("This folder is empty", style = MaterialTheme.typography.titleSmall, color = DarkTextSecondary)
                    }
                }
            } else {
                items(filesInDir, key = { it.id }) { file ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurface,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (file.isDirectory) {
                                    currentPath = "${file.parentPath}/${file.name}"
                                } else {
                                    viewingFile = file
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(file.iconEmoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(file.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White, maxLines = 1)
                                Text("${file.sizeText} • ${file.dateModified}", fontSize = 11.sp, color = DarkTextSecondary)
                            }

                            // Actions
                            if (!file.isDirectory) {
                                IconButton(
                                    onClick = {
                                        allFiles = allFiles.filter { it.id != file.id }
                                        toastMessage = "Deleted '${file.name}'"
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = CoralRed, modifier = Modifier.size(16.dp))
                                }
                            } else {
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DarkTextMuted)
                            }
                        }
                    }
                }
            }
        }
    }

    // View File Modal
    if (viewingFile != null) {
        val f = viewingFile!!
        AlertDialog(
            onDismissRequest = { viewingFile = null },
            containerColor = Color(0xFF0F172A),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(f.iconEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(f.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Type: ${f.extension} • Size: ${f.sizeText} • Modified: ${f.dateModified}", fontSize = 11.sp, color = NeonCyan)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    ) {
                        Text(
                            text = f.previewContent.ifBlank { "Binary file preview is not displayed in plain text." },
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewingFile = null }) {
                    Text("Close", color = NeonCyan)
                }
            }
        )
    }

    // New Folder Dialog
    if (isNewFolderOpen) {
        AlertDialog(
            onDismissRequest = { isNewFolderOpen = false },
            containerColor = Color(0xFF0F172A),
            title = { Text("Create New Folder", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text("Folder Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFolderName.isNotBlank()) {
                            val newDir = ExplorerFile(
                                id = "dir_${System.currentTimeMillis()}",
                                name = newFolderName.trim().lowercase(),
                                parentPath = currentPath,
                                isDirectory = true,
                                sizeText = "0 items",
                                dateModified = "Just now",
                                extension = "DIR",
                                iconEmoji = "📁"
                            )
                            allFiles = allFiles + newDir
                            toastMessage = "Folder '$newFolderName' created in $currentPath"
                            newFolderName = ""
                            isNewFolderOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Create", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isNewFolderOpen = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
