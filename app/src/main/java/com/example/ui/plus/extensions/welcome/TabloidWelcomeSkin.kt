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
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Newspaper
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

/**
 * Sensationalist Red-Header Tabloid Welcome Screen Extension
 */
@Composable
fun TabloidWelcomeSkin(
    feedItems: List<MediaItemEntity>,
    journalEditions: List<JournalEditionEntity>,
    bulletins: List<LocalBulletinEntity>,
    onEnterHomepage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val topHeadline = feedItems.firstOrNull()?.title ?: "EXCLUSIVE: TOWN SQUARE BREAKS ALL DISPATCH RECORDS!"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .testTag("tabloid_welcome_skin")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp)
        ) {
            // RED TABLOID BANNER MASTHEAD
            Surface(
                color = Color(0xFFFF0033),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "★ THE DAILY TABLOID DISPATCH ★",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700),
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "THE DAILY SHOCKER",
                        fontSize = 36.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "UNFILTERED CIVIC NEWS • EXCLUSIVES • DRAMA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // YELLOW ALERT BANNER
            Surface(
                color = Color(0xFFFFD700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SENSATIONAL FRONT PAGE • READ IT BEFORE IT'S REVISED!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // MASSIVE TABLOID HEADLINE
            Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                Surface(
                    color = Color.Black,
                    border = BorderStroke(2.dp, Color(0xFFFF0033)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFF0033)
                        ) {
                            Text(
                                text = "🔥 FRONT PAGE EXCLUSIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = topHeadline.uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD700),
                            lineHeight = 30.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Unbelievable dispatches unfolding across District 4 and the central plaza. Sources report massive surge in local engagement!",
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 19.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SCANDAL & GOSSIP COLUMNS
                Text(
                    text = "⚡ HOT TABLOID STORIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFF0033)
                )

                Spacer(modifier = Modifier.height(8.dp))

                feedItems.drop(1).take(3).forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E1E1E),
                        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFF0033),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("📢", fontSize = 16.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = item.authorName,
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ENTER BUTTON
        Surface(
            color = Color(0xFFFF0033),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(14.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Button(
                onClick = onEnterHomepage,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("tabloid_enter_btn")
            ) {
                Text(
                    text = "READ TODAY'S FULL TABLOID • ENTER NOW",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}
