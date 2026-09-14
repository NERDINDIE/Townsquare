package com.example.ui.components

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FacsimileBroadsheet
import com.example.data.model.FacsimileBroadsheetRepository
import com.example.widget.TownsquareFacsimileWidgetProvider
import java.util.Calendar

@Composable
fun FacsimileBroadsheetWidget(
    modifier: Modifier = Modifier,
    onTearSlip: (FacsimileBroadsheet) -> Unit = {},
    onOpenFullDispatch: (FacsimileBroadsheet) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val initialHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    var currentHour by remember { mutableIntStateOf(initialHour) }
    val edition = remember(currentHour) { FacsimileBroadsheetRepository.getEditionForHour(currentHour) }

    var isPrintingAnimation by remember { mutableStateOf(false) }
    var isTornSaved by remember { mutableStateOf(false) }
    var pinSuccessMessage by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "thermal_printhead")
    val scanOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_offset"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9F7F1) // Vintage thermal paper ivory
        ),
        border = BorderStroke(1.5.dp, Color(0xFFC7BBA2)),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("facsimile_broadsheet_widget")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Perforated Tear Edge (Top)
            PerforatedPaperEdge()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Masthead Teleprinter Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF8B5E14),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📠", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "TOWNSQUARE FACSIMILE WIRE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = Color(0xFF5C4724)
                            )
                            Text(
                                text = edition.carrierSignalKhz,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = Color(0xFF87775D)
                            )
                        }
                    }

                    // Hourly Edition Tag & Controls
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFECE6D8),
                        border = BorderStroke(1.dp, Color(0xFFC8BEA8))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    currentHour = if (currentHour > 0) currentHour - 1 else 23
                                    isTornSaved = false
                                },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Previous Hour",
                                    tint = Color(0xFF423B2D),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = String.format("%02d:00 HRS", currentHour),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = Color(0xFF262017)
                            )

                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    currentHour = (currentHour + 1) % 24
                                    isTornSaved = false
                                },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next Hour",
                                    tint = Color(0xFF423B2D),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Wire Metadata Line
                Text(
                    text = "${edition.editionCode} • ${edition.timestampFormatted}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF7A6B53)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Weather and Barometer Box
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1EBDD),
                    border = BorderStroke(1.dp, Color(0xFFDACFBB)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BARO: ${edition.barometer}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF473E2E)
                        )
                        Text(
                            text = "TEMP: ${edition.temperature}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF473E2E)
                        )
                        Text(
                            text = "TELEGRAPH 100 WPM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            ),
                            color = Color(0xFF7E725C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Thermal Wire Scanning Line (Simulated)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color(0xFFE2D7C2))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.25f)
                            .height(2.dp)
                            .align(Alignment.CenterStart)
                            .padding(start = (scanOffset * 220).dp)
                            .background(Color(0xFFE56A32))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Main Lead Headline (Ink stamped style)
                Text(
                    text = edition.headline,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.2.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 20.sp
                    ),
                    color = Color(0xFF181510)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = edition.subheadline,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    ),
                    color = Color(0xFF3F372A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Hourly Wire Bulletins
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3ECE0), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    edition.items.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "[${item.timeTag}] ${item.category}: ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = Color(0xFF7D4E12)
                            )
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = Color(0xFF2B251B),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions: Tear & Save Slip, Pin Home Widget, Read Full Dispatch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tear & Save Slip Button
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isTornSaved = true
                            onTearSlip(edition)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTornSaved) Color(0xFF2E6342) else Color(0xFF4A3E2C),
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("facsimile_tear_slip_button")
                    ) {
                        Icon(
                            imageVector = if (isTornSaved) Icons.Default.Check else Icons.Default.ContentCut,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTornSaved) "SLIP SAVED" else "TEAR SLIP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Pin to Home Screen
                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                pinFacsimileWidget(context)
                                pinSuccessMessage = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF9E8E75)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF453A2A)
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("facsimile_pin_widget_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF7D4E12)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PIN WIDGET",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        // Cycle to current hour
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                                isTornSaved = false
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFE9E2D2), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Current Hour",
                                tint = Color(0xFF453A2A),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Pin Toast feedback
                AnimatedVisibility(visible = pinSuccessMessage) {
                    Text(
                        text = "✓ Facsimile Broadsheet Home Widget pinned to your launcher",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF2E6342),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            // Perforated Tear Edge (Bottom)
            PerforatedPaperEdge()
        }
    }
}

@Composable
private fun PerforatedPaperEdge(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFFEAE3D4))
    ) {
        val step = 10.dp.toPx()
        val halfStep = step / 2f
        val path = Path().apply {
            moveTo(0f, 0f)
            var currentX = 0f
            while (currentX < size.width) {
                lineTo(currentX + halfStep, size.height)
                lineTo(currentX + step, 0f)
                currentX += step
            }
            close()
        }
        drawPath(path, Color(0xFFC7BBA2))
    }
}

private fun pinFacsimileWidget(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(context, TownsquareFacsimileWidgetProvider::class.java)
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            val pinnedWidgetCallbackIntent = Intent(context, TownsquareFacsimileWidgetProvider::class.java)
            val successCallback = PendingIntent.getBroadcast(
                context,
                302,
                pinnedWidgetCallbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
        }
    }
}

@Composable
fun FacsimileBroadsheetDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    onTearSlip: (FacsimileBroadsheet) -> Unit = {},
    onOpenFullDispatch: (FacsimileBroadsheet) -> Unit = {}
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header bar with Close
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFC7BBA2),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = null,
                                    tint = Color(0xFF332B1E),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TOWNSQUARE FACSIMILE WIRE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            ),
                            color = Color(0xFFEFE8DB)
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .testTag("facsimile_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Facsimile",
                            tint = Color.White
                        )
                    }
                }

                // The thermal broadsheet slip
                FacsimileBroadsheetWidget(
                    onTearSlip = onTearSlip,
                    onOpenFullDispatch = onOpenFullDispatch
                )
            }
        }
    }
}
