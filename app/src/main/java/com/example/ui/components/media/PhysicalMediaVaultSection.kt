package com.example.ui.components.media

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhysicalMediaFormat
import com.example.data.model.PhysicalMediaItem
import com.example.data.repository.PhysicalMediaRepository
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun PhysicalMediaVaultSection(
    onPlayInPlayer: (PhysicalMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val vaultItems by PhysicalMediaRepository.vaultItems.collectAsState()
    var selectedFormatFilter by remember { mutableStateOf<PhysicalMediaFormat?>(null) }

    var showVinylStudio by remember { mutableStateOf(false) }
    var showCassetteStudio by remember { mutableStateOf(false) }
    var showBookConverter by remember { mutableStateOf(false) }
    var itemToVerify by remember { mutableStateOf<PhysicalMediaItem?>(null) }
    var itemToVerifyBeforeUpload by remember { mutableStateOf<PhysicalMediaItem?>(null) }
    var itemToTransform by remember { mutableStateOf<PhysicalMediaItem?>(null) }
    var itemToRestoreAudio by remember { mutableStateOf<PhysicalMediaItem?>(null) }
    var itemToRestoreVideo by remember { mutableStateOf<PhysicalMediaItem?>(null) }
    var itemToProofread by remember { mutableStateOf<PhysicalMediaItem?>(null) }

    var isCommunityHubOpen by remember { mutableStateOf(false) }

    val filteredItems = remember(vaultItems, selectedFormatFilter) {
        if (selectedFormatFilter == null) vaultItems
        else vaultItems.filter { it.format == selectedFormatFilter }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("physical_media_vault_section")
            .padding(12.dp)
    ) {
        // Vault Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, NeonCyan),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📦", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Personal Physical Media Vault",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Off to public sharing by default • Waves & Stream Sync",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                        }
                    }

                    IconButton(
                        onClick = { isCommunityHubOpen = !isCommunityHubOpen },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isCommunityHubOpen) NeonCyan else Color(0xFF162032))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Community Hub",
                            tint = if (isCommunityHubOpen) Color.Black else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Studio Buttons Grid
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { showVinylStudio = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("💿 Vinyl Studio", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showCassetteStudio = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📻 Tape Boombox", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { showBookConverter = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📚 Scan Book", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                // Direct Disc Upload Triggering Upload Verification Modal
                                val pendingDisc = PhysicalMediaItem(
                                    title = "Profile Disc Upload #${(10..99).random()}",
                                    creatorOrArtist = "Studio Disc Rip",
                                    format = PhysicalMediaFormat.DISC_ALBUM,
                                    tracksOrPages = listOf("Track A", "Track B", "Track C"),
                                    totalDurationOrPages = "52:10",
                                    isPrivate = true
                                )
                                itemToVerifyBeforeUpload = pendingDisc
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📀 Upload & Verify", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Community Shared Exchange View Toggle
        AnimatedVisibility(visible = isCommunityHubOpen) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131F33)),
                border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Community Physical Media Exchange",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Download community media into your Waves & Stream app. Digital fingerprints protect original uploaders from unverified resharing.",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val sampleCommunityMedia = PhysicalMediaItem(
                                title = "Classical Symphonies Vinyl Master",
                                creatorOrArtist = "Community Member @VinylMaster_88",
                                format = PhysicalMediaFormat.VINYL,
                                description = "Hi-Fi record player rip of original 1974 pressing.",
                                tracksOrPages = listOf("Movement I", "Movement II", "Movement III"),
                                isPrivate = false,
                                isCommunityShared = true
                            )
                            PhysicalMediaRepository.downloadCommunityMedia(sampleCommunityMedia)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download Sample Community Vinyl to Vault", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Format Filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedFormatFilter == null,
                    onClick = { selectedFormatFilter = null },
                    label = { Text("All Archive (${vaultItems.size})") }
                )
            }
            items(PhysicalMediaFormat.entries) { fmt ->
                val count = vaultItems.count { it.format == fmt }
                FilterChip(
                    selected = selectedFormatFilter == fmt,
                    onClick = { selectedFormatFilter = fmt },
                    label = { Text("${fmt.iconEmoji} ${fmt.label} ($count)") }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Vault Items List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
        ) {
            items(filteredItems, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162032)),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(item.format.iconEmoji, fontSize = 18.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${item.creatorOrArtist} • ${item.totalDurationOrPages}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = WarmAmber
                                    )
                                }
                            }

                            // Private vs Community Public Toggle
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (item.isPrivate) "🔒 Private" else "🌐 Community Public",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isPrivate) Color.LightGray else NeonCyan
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = !item.isPrivate,
                                    onCheckedChange = { share ->
                                        if (share && !item.fingerprint.isResharePermitted) {
                                            // Blocked: needs transformative work
                                            itemToTransform = item
                                        } else {
                                            PhysicalMediaRepository.toggleCommunitySharing(item.id, share)
                                        }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Provenance Banner
                        ProvenanceFingerprintBanner(
                            item = item,
                            onOpenTransformativeStudio = { itemToTransform = item }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                if (!item.proofOfPurchase.isVerified) {
                                    IconButton(
                                        onClick = { itemToVerify = item },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Shield, contentDescription = "Verify Purchase", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                    }
                                }

                                if (item.format == PhysicalMediaFormat.VINYL || item.format == PhysicalMediaFormat.CASSETTE_TAPE) {
                                    IconButton(
                                        onClick = { itemToRestoreAudio = item },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.GraphicEq, contentDescription = "Restore Audio", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                    }
                                }

                                if (item.format == PhysicalMediaFormat.VHS_TAPE || item.format == PhysicalMediaFormat.MOVIE_REEL || item.format == PhysicalMediaFormat.DISC_MOVIE) {
                                    IconButton(
                                        onClick = { itemToRestoreVideo = item },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.MovieFilter, contentDescription = "Restore Video/Film", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                    }
                                }

                                if (item.format == PhysicalMediaFormat.BOOK_PDF) {
                                    IconButton(
                                        onClick = { itemToProofread = item },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.FindInPage, contentDescription = "Proofread Book", tint = WarmAmber, modifier = Modifier.size(18.dp))
                                    }
                                }

                                IconButton(
                                    onClick = { itemToTransform = item },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.AutoFixHigh, contentDescription = "Transformative Work", tint = WarmAmber, modifier = Modifier.size(18.dp))
                                }
                            }

                            Button(
                                onClick = { onPlayInPlayer(item) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open in ${item.format.defaultAppTarget}", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog Triggers
    if (showVinylStudio) {
        VinylRecordCaptureStudio(
            onDismiss = { showVinylStudio = false },
            onSavedToVault = { showVinylStudio = false }
        )
    }

    if (showCassetteStudio) {
        CassetteBoomboxCaptureStudio(
            onDismiss = { showCassetteStudio = false },
            onSavedToVault = { showCassetteStudio = false }
        )
    }

    if (showBookConverter) {
        BookDocumentPdfConverter(
            onDismiss = { showBookConverter = false },
            onSavedToVault = { showBookConverter = false }
        )
    }

    itemToVerifyBeforeUpload?.let { pendingItem ->
        UploadVerificationModal(
            pendingItem = pendingItem,
            onDismiss = { itemToVerifyBeforeUpload = null },
            onUploadFinalized = { itemToVerifyBeforeUpload = null }
        )
    }

    itemToVerify?.let { item ->
        TownsquarePurchaseVerifierDialog(
            item = item,
            onDismiss = { itemToVerify = null }
        )
    }

    itemToTransform?.let { item ->
        TransformativeWorkStudioDialog(
            item = item,
            onDismiss = { itemToTransform = null }
        )
    }

    itemToRestoreAudio?.let { item ->
        AudioRestorationStudioDialog(
            item = item,
            onDismiss = { itemToRestoreAudio = null },
            onRestorationApplied = { itemToRestoreAudio = null }
        )
    }

    itemToRestoreVideo?.let { item ->
        VideoFilmRestorationStudioDialog(
            item = item,
            onDismiss = { itemToRestoreVideo = null },
            onRestorationApplied = { itemToRestoreVideo = null }
        )
    }

    itemToProofread?.let { item ->
        BookProofreaderStudioDialog(
            item = item,
            onDismiss = { itemToProofread = null },
            onProofreadSaved = { itemToProofread = null }
        )
    }
}
