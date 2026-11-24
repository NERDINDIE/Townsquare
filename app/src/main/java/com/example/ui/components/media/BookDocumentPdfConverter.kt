package com.example.ui.components.media

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.data.model.DigitalFingerprint
import com.example.data.model.PhysicalMediaFormat
import com.example.data.model.PhysicalMediaItem
import com.example.data.model.ProofOfPurchase
import com.example.data.repository.PhysicalMediaRepository
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDocumentPdfConverter(
    onDismiss: () -> Unit,
    onSavedToVault: (PhysicalMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var bookTitle by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var scannedPagesCount by remember { mutableIntStateOf(120) }
    var isConverting by remember { mutableStateOf(false) }
    var conversionProgress by remember { mutableFloatStateOf(0f) }
    var extractAudiobookText by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("book_document_converter_dialog")
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📚", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Book PDF & Text Converter",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Physical Document Text & Image Digitizer",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input fields
                OutlinedTextField(
                    value = bookTitle,
                    onValueChange = { bookTitle = it },
                    label = { Text("Physical Book / Document Title") },
                    placeholder = { Text("e.g. Dune: Expanded Hardcover Edition") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = NeonCyan
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = authorName,
                    onValueChange = { authorName = it },
                    label = { Text("Author / Publisher") },
                    placeholder = { Text("e.g. Frank Herbert") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = NeonCyan
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Page count slider
                Text(
                    text = "Scanned Pages: $scannedPagesCount Pages",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )
                Slider(
                    value = scannedPagesCount.toFloat(),
                    onValueChange = { scannedPagesCount = it.toInt() },
                    valueRange = 10f..800f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("OCR Audiobook Replica Text", fontSize = 12.sp, color = Color.LightGray)
                    Switch(
                        checked = extractAudiobookText,
                        onCheckedChange = { extractAudiobookText = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }

                if (isConverting) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { conversionProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = NeonCyan,
                        trackColor = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Converting text & images... ${(conversionProgress * 100).toInt()}%",
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Button(
                    onClick = {
                        isConverting = true
                        coroutineScope.launch {
                            for (i in 1..10) {
                                delay(100L)
                                conversionProgress = i / 10f
                            }
                            isConverting = false

                            val newItem = PhysicalMediaItem(
                                title = bookTitle.ifBlank { "Scanned Physical Document" },
                                creatorOrArtist = authorName.ifBlank { "Unknown Author" },
                                format = PhysicalMediaFormat.BOOK_PDF,
                                description = "PDF Document & OCR Text Replica ($scannedPagesCount pages).",
                                tracksOrPages = listOf("Chapter 1: Intro", "Chapter 2: Main Text", "Chapter 3: Epilogue"),
                                totalDurationOrPages = "$scannedPagesCount Pages",
                                isPrivate = true,
                                proofOfPurchase = ProofOfPurchase(
                                    isVerified = true,
                                    storeName = "PDF Book Converter"
                                ),
                                fingerprint = DigitalFingerprint(
                                    originalUploaderId = "usr_me",
                                    originalUploaderName = "My Vault",
                                    isDownloadedFromElsewhere = false
                                )
                            )
                            PhysicalMediaRepository.addItem(newItem)
                            onSavedToVault(newItem)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Convert Document & Add to Vault", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
