package com.example.ui.plus.extensions.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEditionEntity
import com.example.data.model.LocalBulletinEntity
import com.example.data.model.MediaItemEntity
import com.example.ui.plus.extensions.Android10SoundEffects

/**
 * Japanese 2000s Keitai Flip-Phone I-Mode Mobile Web Portal Welcome Extension
 */
@Composable
fun KeitaiWelcomeSkin(
    feedItems: List<MediaItemEntity>,
    journalEditions: List<JournalEditionEntity>,
    bulletins: List<LocalBulletinEntity>,
    onEnterHomepage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val shortcuts = listOf(
        Triple("1", "Main Feed Wire", "📰"),
        Triple("2", "Plus Superapp", "✨"),
        Triple("3", "BBS Terminal", "📟"),
        Triple("4", "Bookworm Tracker", "📚"),
        Triple("5", "Lingo Polyglot", "🗣️"),
        Triple("6", "Waves Audio", "🌊")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF001524))
            .testTag("keitai_welcome_skin")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(14.dp)
                .padding(bottom = 90.dp)
        ) {
            // KEITAI CARRIER STATUS BAR
            Surface(
                color = Color(0xFF002B49),
                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SignalCellular4Bar, contentDescription = null, tint = Color(0xFF00FF66), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "DoCoMo 3G • Townsquare i-mode",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "🔋 100%",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF00FF66)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // KEITAI PORTAL BANNER
            Surface(
                color = Color(0xFF003554),
                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📲 TOWNSQUARE KEITAI NAVI",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "タウンスクエア 携帯ポータル v2.4",
                        fontSize = 11.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // MARQUEE BANNER
            Surface(
                color = Color(0xFF051923),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "▶ NEWS TICKER: Dispatches live across District 4 • Select keypad shortcut below [1-6]",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFFFFB300),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // KEYPAD SHORTCUTS LIST
            Text(
                text = "i-MODE PORTAL MENU:",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            shortcuts.forEach { shortcut ->
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF002B49),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable {
                            Android10SoundEffects.playTrackballClick()
                            onEnterHomepage()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = Color(0xFF00E5FF)
                        ) {
                            Text(
                                text = "[${shortcut.first}]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(shortcut.third, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = shortcut.second,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // ENTER BUTTON
        Surface(
            color = Color(0xFF001524).copy(alpha = 0.95f),
            border = BorderStroke(1.dp, Color(0xFF00E5FF)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(14.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Button(
                onClick = onEnterHomepage,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF002B49)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("keitai_enter_btn")
            ) {
                Text(
                    text = "OPEN i-MODE PORTAL • ENTER APP",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}
