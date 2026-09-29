package com.example.ui.plus.extensions.welcome

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
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
 * 15th-Century Gutenberg Illuminated Manuscript Welcome Screen Extension
 */
@Composable
fun ManuscriptWelcomeSkin(
    feedItems: List<MediaItemEntity>,
    journalEditions: List<JournalEditionEntity>,
    bulletins: List<LocalBulletinEntity>,
    onEnterHomepage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val parchmentBg = Color(0xFFF4E8C1)
    val inkDark = Color(0xFF2A1B0E)
    val goldAccent = Color(0xFFC5A059)
    val crimsonAccent = Color(0xFF8B0000)

    val leadItem = feedItems.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(parchmentBg)
            .testTag("manuscript_welcome_skin")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
                .padding(bottom = 90.dp)
        ) {
            // ILLUMINATED HEADER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, goldAccent, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ANNO DOMINI MMXXVI • SCRIPTOTERIA TOWNSQUARE",
                    fontFamily = FontFamily.Serif,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = crimsonAccent,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "CODEX TOWNSQUARE",
                    fontFamily = FontFamily.Serif,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = inkDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Divider(color = goldAccent, thickness = 1.dp)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Liber Primus • Chronica & Dispatches of the Realm",
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    color = inkDark
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ILLUMINATED DROP CAP LEAD DISPATCH
            Surface(
                color = Color(0xFFFAF0D7),
                border = BorderStroke(1.dp, goldAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📜 LEADING CHRONICLE",
                        fontFamily = FontFamily.Serif,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = crimsonAccent
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        // Ornate Drop Cap 'T'
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = crimsonAccent,
                            border = BorderStroke(1.5.dp, goldAccent),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "T",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = goldAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = (leadItem?.title ?: "ownsquare Gazette Proclaims New Age of Civic Dispatches").drop(1),
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = inkDark,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = (leadItem?.bodyText ?: "Herein recorded are the live dispatches, letters, and public announcements for all citizens of the district.").take(300) + "...",
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        color = inkDark,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // MARGINALIA NOTES
            Surface(
                color = Color(0xFFEFE2BD),
                border = BorderStroke(1.dp, crimsonAccent.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "✍️ Marginalia Note from the Scribe:",
                        fontFamily = FontFamily.Serif,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = crimsonAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All scrolls, bookworm archives, and packet broadcasts are preserved in the central scriptorium.",
                        fontFamily = FontFamily.Serif,
                        fontSize = 11.sp,
                        color = inkDark
                    )
                }
            }
        }

        // RED WAX SEAL BUTTON
        Surface(
            color = parchmentBg.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, goldAccent),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Button(
                onClick = onEnterHomepage,
                colors = ButtonDefaults.buttonColors(containerColor = crimsonAccent, contentColor = goldAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("manuscript_enter_btn")
            ) {
                Text(
                    text = "UNSEAL MANUSCRIPT • READ CODEX",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}
