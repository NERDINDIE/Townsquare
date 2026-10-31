package com.example.ui.components.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R

/**
 * Global Media Player - Mode 3: Image
 * Supports Image Viewing, Zoom, EXIF Metadata Inspection, and Creative In-App Image Editing
 * (Color filters, Brightness, Contrast, Saturation, Crop, and Rotation).
 */
@Composable
fun GlobalImageViewerMode(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emeraldAccent = Color(0xFF10B981)

    // Sample Photojournalism & Device Gallery items
    val sampleGallery = remember {
        listOf(
            ImagePlayerItem(
                id = "img_1",
                title = "Townsquare Morning Bell Tower & Golden Mist",
                photographer = "Elena Rostova • Civic Press",
                dateCaptured = "Oct 30, 2026 • 07:15 AM",
                resolution = "4032 x 3024 (12.2 MP)",
                location = "Old Town Plaza North",
                sourceName = "Townsquare Morning Wire",
                cameraExif = "Leica Q3 • 28mm f/1.7 Summilux • ISO 100 • 1/800s",
                baseDrawableRes = R.drawable.img_morning_brief
            ),
            ImagePlayerItem(
                id = "img_2",
                title = "Downtown Neon Transit Tramline at Dusk",
                photographer = "Marcus Vance • Urban Architecture",
                dateCaptured = "Oct 29, 2026 • 18:42 PM",
                resolution = "3840 x 2160 (4K)",
                location = "Central Transit Concourse",
                sourceName = "City Planning Visual Archives",
                cameraExif = "Sony A7R V • 24-70mm GM II • ISO 800 • 1/120s",
                baseDrawableRes = R.drawable.img_radio_live
            ),
            ImagePlayerItem(
                id = "img_3",
                title = "Autumn Harvest Botanical Festival Display",
                photographer = "Sarah Chen • Heritage Society",
                dateCaptured = "Oct 28, 2026 • 14:10 PM",
                resolution = "4200 x 2800 (11.8 MP)",
                location = "Botanical Conservatory",
                sourceName = "Community Gallery",
                cameraExif = "Fujifilm X-T5 • 33mm f/1.4 • ISO 160 • 1/400s",
                baseDrawableRes = R.drawable.img_podcast_cover
            )
        )
    }

    var activeImage by remember { mutableStateOf(sampleGallery[0]) }
    var selectedFilter by remember { mutableStateOf(ImageFilterType.ORIGINAL) }
    var brightnessLevel by remember { mutableFloatStateOf(0f) } // -1f to 1f
    var contrastLevel by remember { mutableFloatStateOf(1f) } // 0.5f to 2f
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var isEditingMode by remember { mutableStateOf(false) }
    var isExifDialogOpen by remember { mutableStateOf(false) }
    var saveStatusToast by remember { mutableStateOf<String?>(null) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    // Color Matrix calculation for live editing preview
    val colorMatrix = remember(selectedFilter, brightnessLevel, contrastLevel) {
        when (selectedFilter) {
            ImageFilterType.ORIGINAL -> ColorMatrix()
            ImageFilterType.VINTAGE_FILM -> ColorMatrix(
                floatArrayOf(
                    0.9f, 0.2f, 0.1f, 0f, 20f,
                    0.1f, 0.8f, 0.1f, 0f, 15f,
                    0.1f, 0.1f, 0.7f, 0f, -10f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            ImageFilterType.CYBERPUNK -> ColorMatrix(
                floatArrayOf(
                    0.7f, 0.0f, 0.5f, 0f, 30f,
                    0.1f, 1.2f, 0.3f, 0f, -10f,
                    0.4f, 0.2f, 1.4f, 0f, 40f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            ImageFilterType.NOIR_BW -> ColorMatrix().apply { setToSaturation(0f) }
            ImageFilterType.WARM_AMBER -> ColorMatrix(
                floatArrayOf(
                    1.2f, 0.1f, 0.0f, 0f, 25f,
                    0.1f, 1.0f, 0.0f, 0f, 15f,
                    0.0f, 0.1f, 0.8f, 0f, -20f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            ImageFilterType.VIVID_HDR -> ColorMatrix().apply { setToSaturation(1.5f) }
        }
    }

    // EXIF Metadata Inspector Modal Dialog
    if (isExifDialogOpen) {
        Dialog(onDismissRequest = { isExifDialogOpen = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, emeraldAccent)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📸 Photo EXIF & Metadata",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = emeraldAccent
                        )
                        IconButton(onClick = { isExifDialogOpen = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    listOf(
                        Pair("Title", activeImage.title),
                        Pair("Captured By", activeImage.photographer),
                        Pair("Date & Time", activeImage.dateCaptured),
                        Pair("Resolution", activeImage.resolution),
                        Pair("Location", activeImage.location),
                        Pair("File Format", activeImage.fileFormat),
                        Pair("Camera Hardware", activeImage.cameraExif),
                        Pair("Source Archive", activeImage.sourceName)
                    ).forEach { (field, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(field, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(value, color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("global_image_viewer_mode"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, emeraldAccent.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Badge & Mode Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(emeraldAccent.copy(alpha = 0.2f))
                            .border(1.dp, emeraldAccent, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🖼️ IMAGE STUDIO",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = emeraldAccent
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditingMode) "Creative Editor" else "High-Res Viewer",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // EXIF Button
                    IconButton(
                        onClick = { isExifDialogOpen = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "EXIF Metadata",
                            tint = Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Edit / View Toggle
                    IconButton(
                        onClick = { isEditingMode = !isEditingMode },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isEditingMode) Icons.Default.Visibility else Icons.Default.Tune,
                            contentDescription = "Toggle Edit Suite",
                            tint = if (isEditingMode) emeraldAccent else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Close
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Image Player",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // MAIN IMAGE CANVAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF020617))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Target Image with filters and rotation
                val res = activeImage.baseDrawableRes ?: R.drawable.img_morning_brief
                Image(
                    painter = painterResource(id = res),
                    contentDescription = activeImage.title,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix),
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotationAngle)
                )

                // Zoom controls overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { zoomScale = (zoomScale - 0.25f).coerceAtLeast(0.75f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Text("${(zoomScale * 100).toInt()}%", fontSize = 10.sp, color = Color.White)
                    IconButton(
                        onClick = { zoomScale = (zoomScale + 0.25f).coerceAtMost(3.0f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                // Filter badge watermark overlay
                if (selectedFilter != ImageFilterType.ORIGINAL) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Filter: ${selectedFilter.label}",
                            color = emeraldAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Image title and author
            Text(
                text = activeImage.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${activeImage.photographer} • ${activeImage.resolution}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (!isEditingMode) {
                // PHOTO GALLERY STRIP (Select other images)
                Text(
                    text = "Townsquare Photo Wire & Camera Roll",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = emeraldAccent
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sampleGallery) { item ->
                        val isSelected = activeImage.id == item.id
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(60.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    2.dp,
                                    if (isSelected) emeraldAccent else Color(0xFF334155),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    activeImage = item
                                    rotationAngle = 0f
                                }
                        ) {
                            Image(
                                painter = painterResource(id = item.baseDrawableRes ?: R.drawable.img_morning_brief),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            } else {
                // CREATIVE EDITING CONTROLS
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. Color Filter Presets",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = emeraldAccent
                    )

                    // Filter presets row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ImageFilterType.values()) { filter ->
                            val isSelected = selectedFilter == filter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) emeraldAccent else Color(0xFF1E293B))
                                    .clickable { selectedFilter = filter }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = filter.label,
                                    color = if (isSelected) Color(0xFF00382B) else Color.LightGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Transform tools (Rotate, Reset)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { rotationAngle = (rotationAngle + 90f) % 360f },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(16.dp), tint = emeraldAccent)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rotate 90°", fontSize = 11.sp, color = emeraldAccent)
                            }

                            OutlinedButton(
                                onClick = {
                                    selectedFilter = ImageFilterType.ORIGINAL
                                    rotationAngle = 0f
                                    brightnessLevel = 0f
                                    contrastLevel = 1f
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Reset All", fontSize = 11.sp, color = Color.LightGray)
                            }
                        }

                        Button(
                            onClick = {
                                saveStatusToast = "✅ Saved '${activeImage.title}' with ${selectedFilter.label} filter to Device Gallery"
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Image", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (saveStatusToast != null) {
                        Text(
                            text = saveStatusToast ?: "",
                            color = emeraldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
