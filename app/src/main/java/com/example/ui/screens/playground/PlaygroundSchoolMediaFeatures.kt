package com.example.ui.screens.playground

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
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class SchoolRadioTrack(
    val title: String,
    val host: String,
    val timeSlot: String,
    val description: String,
    val iconEmoji: String
)

data class SchoolPublicationSubmission(
    val id: String,
    val title: String,
    val author: String,
    val grade: String,
    val category: String, // Story, Comic, Science, Poetry, Sports
    val bodyText: String,
    val status: String, // Published, In Review, Featured
    val likesCount: Int = 12,
    val iconEmoji: String = "📝"
)

@Composable
fun PlaygroundSchoolMediaFeatures(
    studentName: String,
    schoolName: String,
    modifier: Modifier = Modifier
) {
    var isRadioPlaying by remember { mutableStateOf(true) }
    var radioVolume by remember { mutableFloatStateOf(0.8f) }
    var radioTicker by remember { mutableIntStateOf(0) }

    // Content Submission Form State
    var isSubmitDialogOpen by remember { mutableStateOf(false) }
    var subTitle by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("Science Project") }
    var subBody by remember { mutableStateOf("") }
    var submissionSuccessToast by remember { mutableStateOf<String?>(null) }
    var viewingSubmission by remember { mutableStateOf<SchoolPublicationSubmission?>(null) }

    val radioShows = remember {
        listOf(
            SchoolRadioTrack("Morning Bell & Recess Jokes", "Leo & The Morning Crew", "LIVE NOW (08:30)", "Daily morning trivia, lost & found, and recess weather report!", "📻"),
            SchoolRadioTrack("Science Fair Inventors Club", "Maya (5th Grade)", "10:15 AM", "Interviewing students building baking soda volcanoes and solar ovens.", "🔬"),
            SchoolRadioTrack("Library Story Hour Podcast", "Mr. Henderson & Mia", "13:00 PM", "Chapter 3 read-aloud of The Secret of Cedar Woods.", "📚"),
            SchoolRadioTrack("After-School Soccer & Games", "Coach Dave", "15:30 PM", "Recap of the 4th-grade penalty shootout victory!", "⚽")
        )
    }

    var publications by remember {
        mutableStateOf(
            listOf(
                SchoolPublicationSubmission(
                    id = "sub_1",
                    title = "How I Built a Robot That Sweeps Up Eraser Dust",
                    author = "Leo",
                    grade = "4th Grade",
                    category = "Science Project",
                    bodyText = "I used three AA batteries, a tiny motor from an old toy car, and sponge bristles. It clean up pencils shavings really fast!",
                    status = "Featured Front Page",
                    likesCount = 38,
                    iconEmoji = "🤖"
                ),
                SchoolPublicationSubmission(
                    id = "sub_2",
                    title = "The Squirrel Who Wanted to Learn Multiplication",
                    author = "Mia",
                    grade = "2nd Grade",
                    category = "Story",
                    bodyText = "One morning during recess, a squirrel sat on the classroom window sill and tapped its tail 4 times 3 times, which is 12 acorns!",
                    status = "Published in Gazette",
                    likesCount = 29,
                    iconEmoji = "🐿️"
                ),
                SchoolPublicationSubmission(
                    id = "sub_3",
                    title = "Comic: The Cafeteria Mystery Pizza",
                    author = "Sammy & Toby",
                    grade = "4th Grade",
                    category = "Comic Strip",
                    bodyText = "Episode 4: The detective lunchbox finds out why Friday's cheese pizza is extra stretchy!",
                    status = "Published in Gazette",
                    likesCount = 45,
                    iconEmoji = "🍕"
                )
            )
        )
    }

    // Dynamic wave animation ticker
    LaunchedEffect(isRadioPlaying) {
        while (isRadioPlaying) {
            delay(800)
            radioTicker++
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("playground_school_media_features"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. SCHOOL RADIO STATION CARD
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF1E1B4B),
            border = BorderStroke(1.5.dp, Color(0xFF6366F1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Radio Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF4F46E5),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📻", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("$schoolName Radio", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF22C55E)) {
                                    Text("88.5 FM ON AIR", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text("Student Broadcasts, Morning Announcements & Recess Jams", fontSize = 11.sp, color = Color(0xFFC7D2FE))
                        }
                    }

                    // Play/Pause button
                    IconButton(
                        onClick = { isRadioPlaying = !isRadioPlaying },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6366F1))
                    ) {
                        Icon(
                            imageVector = if (isRadioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Toggle Radio",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Now Playing Info Box with Animated Waveform
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F0E2A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("NOW PLAYING", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFA5B4FC))
                            Text("LIVE FROM GYM STUDIO", fontSize = 9.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(radioShows[0].title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Host: ${radioShows[0].host}", fontSize = 11.sp, color = Color(0xFFCBD5E1))

                        Spacer(modifier = Modifier.height(8.dp))

                        // Waveform bars
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val baseHeights = listOf(8, 14, 20, 10, 18, 6, 22, 12, 16, 9, 20, 14, 8)
                            baseHeights.forEachIndexed { idx, h ->
                                val dynamicH = if (isRadioPlaying) {
                                    ((h + (radioTicker * (idx + 1) * 5) % 16).coerceIn(4, 22)).dp
                                } else 4.dp
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(dynamicH)
                                        .background(Color(0xFF818CF8), RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Upcoming Radio Schedule Carousel
                Text("Today's Broadcast Schedule:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC7D2FE))
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(radioShows.drop(1)) { show ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF312E81),
                            modifier = Modifier.width(200.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(show.iconEmoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(show.timeSlot, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                }
                                Text(show.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                                Text("Host: ${show.host}", fontSize = 9.sp, color = Color(0xFFC7D2FE), maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        // 2. SUBMIT TO SCHOOL PUBLICATIONS CARD
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
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
                            color = WarmAmber.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📰", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("School Gazette Publications", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text("Submit your stories, drawings, and science projects!", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                        }
                    }

                    // Submit CTA Button
                    Button(
                        onClick = { isSubmitDialogOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF332000)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Submit Work", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }

                if (submissionSuccessToast != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF064E3B),
                        border = BorderStroke(1.dp, Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(submissionSuccessToast!!, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = { submissionSuccessToast = null }, modifier = Modifier.size(18.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Student Published Works (${publications.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))

                // Publications List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    publications.forEach { pub ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewingSubmission = pub }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(pub.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(pub.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White, modifier = Modifier.weight(1f))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = WarmAmber.copy(alpha = 0.2f)
                                        ) {
                                            Text(pub.status, fontSize = 8.sp, fontWeight = FontWeight.Black, color = WarmAmber, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("By ${pub.author} (${pub.grade}) • ${pub.category}", fontSize = 10.sp, color = NeonCyan)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(pub.bodyText, fontSize = 11.sp, color = DarkTextSecondary, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // View Submission Details Modal
    if (viewingSubmission != null) {
        val pub = viewingSubmission!!
        AlertDialog(
            onDismissRequest = { viewingSubmission = null },
            containerColor = Color(0xFF0F172A),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(pub.iconEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(pub.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Author: ${pub.author} • ${pub.grade} • ${pub.category}", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(pub.bodyText, fontSize = 13.sp, color = Color.White, modifier = Modifier.padding(12.dp))
                    }
                    Text("❤️ ${pub.likesCount} classmates cheered this publication!", fontSize = 11.sp, color = WarmAmber, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewingSubmission = null }) {
                    Text("Close", color = NeonCyan)
                }
            }
        )
    }

    // Submit Content Dialog
    if (isSubmitDialogOpen) {
        AlertDialog(
            onDismissRequest = { isSubmitDialogOpen = false },
            containerColor = Color(0xFF0F172A),
            title = { Text("Submit Work to School Gazette", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subTitle,
                        onValueChange = { subTitle = it },
                        label = { Text("Title of Your Work") },
                        placeholder = { Text("e.g. My Visit to the Canal Bridge") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Select Category:", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("Science Project", "Story", "Poetry", "Comic Strip", "Sports Report")) { cat ->
                            val isSel = subCategory == cat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) WarmAmber else Color(0xFF1E293B),
                                modifier = Modifier.clickable { subCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = subBody,
                        onValueChange = { subBody = it },
                        label = { Text("Write your story or report here:") },
                        modifier = Modifier.fillMaxWidth().height(110.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newPub = SchoolPublicationSubmission(
                            id = "sub_${System.currentTimeMillis()}",
                            title = subTitle.ifBlank { "My Creative Playground Story" },
                            author = studentName,
                            grade = "Student Work",
                            category = subCategory,
                            bodyText = subBody.ifBlank { "Great story submitted by a student!" },
                            status = "Under Review",
                            likesCount = 1,
                            iconEmoji = if (subCategory.contains("Science")) "🔬" else if (subCategory.contains("Comic")) "🎨" else "📝"
                        )
                        publications = listOf(newPub) + publications
                        submissionSuccessToast = "Work submitted! Mrs. Jenkins & student editors are reviewing it."
                        isSubmitDialogOpen = false
                        subTitle = ""
                        subBody = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF332000))
                ) {
                    Text("Submit to Gazette", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isSubmitDialogOpen = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
