package com.example.ui.plus.extensions.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEditionEntity
import com.example.data.model.LocalBulletinEntity
import com.example.data.model.MediaItemEntity
import com.example.ui.plus.extensions.Android10SoundEffects

/**
 * Y2K Retro Windows 98 CRT Desktop Environment Welcome Screen Extension
 */
@Composable
fun Y2KDesktopWelcomeSkin(
    feedItems: List<MediaItemEntity>,
    journalEditions: List<JournalEditionEntity>,
    bulletins: List<LocalBulletinEntity>,
    onEnterHomepage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val desktopTeal = Color(0xFF008080)
    val winGray = Color(0xFFC0C0C0)
    val winTitleBlue = Color(0xFF000080)

    val leadItem = feedItems.firstOrNull()

    val desktopIcons = listOf(
        Pair("My Computer", "💻"),
        Pair("Townsquare Net", "🌐"),
        Pair("Daily Wire.exe", "📰"),
        Pair("BBS Modem", "📟"),
        Pair("Recycle Bin", "🗑️")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(desktopTeal)
            .testTag("y2k_desktop_welcome_skin")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(12.dp)
                .padding(bottom = 60.dp)
        ) {
            // DESKTOP ICONS GRID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                desktopIcons.forEach { icon ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                Android10SoundEffects.playTrackballClick()
                                onEnterHomepage()
                            }
                            .padding(4.dp)
                    ) {
                        Text(icon.second, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(color = Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(2.dp)) {
                            Text(
                                text = icon.first,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // RETRO FLOATING NEWS WINDOW
            Surface(
                color = winGray,
                border = BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // WINDOW TITLE BAR
                    Surface(
                        color = winTitleBlue,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Computer, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Townsquare_Gazette_v2026.exe",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Surface(
                                color = winGray,
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { onEnterHomepage() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }

                    // WINDOW BODY
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "📰 LEADING NEWS DISPATCH:",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF000080)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = leadItem?.title ?: "Townsquare Press Wire Online",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = (leadItem?.bodyText ?: "Welcome to the Townsquare Y2K Desktop Edition. Click below to launch the full superapp.").take(240) + "...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onEnterHomepage,
                            colors = ButtonDefaults.buttonColors(containerColor = winGray, contentColor = Color.Black),
                            shape = RoundedCornerShape(2.dp),
                            border = BorderStroke(1.dp, Color.Black),
                            modifier = Modifier.fillMaxWidth().testTag("y2k_window_launch_btn")
                        ) {
                            Text(
                                text = "LAUNCH SUPERAPP [ENTER]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // RETRO TASKBAR
        Surface(
            color = winGray,
            border = BorderStroke(1.dp, Color.White),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(42.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onEnterHomepage,
                    colors = ButtonDefaults.buttonColors(containerColor = winGray, contentColor = Color.Black),
                    shape = RoundedCornerShape(2.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("🪟 Start", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Surface(
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.Gray),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = "10:26 AM",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
