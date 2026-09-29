package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BroadcastProgram(
    val id: String,
    val timeSlot: String,
    val title: String,
    val presenter: String,
    val category: String,
    val isLiveNow: Boolean = false,
    val synopsis: String,
    val durationMinutes: Int = 60
)

data class StationChannelSchedule(
    val channelNumber: Int,
    val callsign: String,
    val name: String,
    val emoji: String,
    val programs: List<BroadcastProgram>
)

@Composable
fun BroadcastScheduleDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    onTuneChannel: (Int) -> Unit = {}
) {
    if (!isOpen) return

    var currentTimeString by remember { mutableStateOf("") }
    var currentDateString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss")
        val dateFmt = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
        while (isActive) {
            val now = LocalDateTime.now()
            currentTimeString = now.format(timeFmt)
            currentDateString = now.format(dateFmt)
            delay(1000L)
        }
    }

    val channelSchedules = remember {
        listOf(
            StationChannelSchedule(
                channelNumber = 1,
                callsign = "TCTV-1",
                name = "First Programme",
                emoji = "🏛️",
                programs = listOf(
                    BroadcastProgram("101", "06:00 - 09:00", "Townsquare Breakfast", "Elena Vance", "News & Civic", false, "Early morning bulletin, metro transit overview, and meteorological briefing."),
                    BroadcastProgram("102", "09:00 - 12:00", "Morning Civic Forum", "Marcus Holloway", "Civic Debate", false, "Community delegates discuss regional infrastructure initiatives and municipal budget proposals."),
                    BroadcastProgram("103", "12:00 - 13:00", "The Midday Broadsheet Hour", "Aria Sterling", "National Wire", false, "Live teletext wire updates and in-depth investigative reports from the field."),
                    BroadcastProgram("104", "13:00 - 16:30", "Parliamentary & Assembly Live", "David Chen", "Government", false, "Gavel-to-gavel live proceedings from the Municipal Chamber."),
                    BroadcastProgram("105", "16:30 - 18:00", "Townsquare Telegraph", "Sophia Martinez", "Investigative", false, "Deep-dive investigative photojournalism and economic trends."),
                    BroadcastProgram("106", "18:00 - 19:30", "Evening National Dispatch", "Julian Vance", "Main News", true, "The station's flagship evening broadcast with satellite correspondents."),
                    BroadcastProgram("107", "19:30 - 21:00", "Townsquare Editorial Roundtable", "Claire Beaumont", "Analysis", false, "Senior correspondents critique major state dispatches and legislative filings."),
                    BroadcastProgram("108", "21:00 - 23:00", "Cinema & Civic Documentary", "Guest Curators", "Culture", false, "Archival documentary restorations highlighting local industrial heritage."),
                    BroadcastProgram("109", "23:00 - 06:00", "Late Night Wire & Teletext Loop", "Automated Station MUX", "Late Wire", false, "Continuous overnight teletext wire, ambient audio, and emergency test signal.")
                )
            ),
            StationChannelSchedule(
                channelNumber = 2,
                callsign = "TCTV-2",
                name = "24/7 Civic Wire",
                emoji = "⚡",
                programs = listOf(
                    BroadcastProgram("201", "00:00 - 24:00", "Continuous Breaking Wire Feed", "Automated News Desk", "Wire", true, "Non-stop breaking bulletins, real-time civic dispatches, and emergency alerts.")
                )
            ),
            StationChannelSchedule(
                channelNumber = 3,
                callsign = "TCTV-3",
                name = "Arts & Broadsheet",
                emoji = "🎨",
                programs = listOf(
                    BroadcastProgram("301", "10:00 - 12:00", "Literary Review Hour", "Hannah Cole", "Arts", false, "Critiques of contemporary press publications and historical reprints."),
                    BroadcastProgram("302", "12:00 - 15:00", "Symphony & Soundstage", "Townsquare Philharmonic", "Music", true, "High-fidelity broadcast of classical orchestral performances and acoustic showcases."),
                    BroadcastProgram("303", "15:00 - 18:00", "Printmakers & Typography", "Artisan Guild", "Documentary", false, "Exploration of letterpress printing, broadsheet design, and typography.")
                )
            ),
            StationChannelSchedule(
                channelNumber = 4,
                callsign = "TCTV-4",
                name = "Science & Ecology",
                emoji = "🔬",
                programs = listOf(
                    BroadcastProgram("401", "08:00 - 12:00", "Earth Observation & Climate", "Dr. Raymond Shaw", "Science", false, "Satellite meteorological analysis and ecological conservation dispatches."),
                    BroadcastProgram("402", "12:00 - 18:00", "Technology & Innovation Today", "Devon Park", "Tech", true, "Cutting-edge computational science, renewable energy systems, and research.")
                )
            ),
            StationChannelSchedule(
                channelNumber = 5,
                callsign = "TCTV-5",
                name = "Regional Community",
                emoji = "🏘️",
                programs = listOf(
                    BroadcastProgram("501", "07:00 - 12:00", "Valley & County Dispatches", "Local Reporters", "Community", false, "Hyperlocal events, rural farm reports, and township meetings."),
                    BroadcastProgram("502", "12:00 - 19:00", "Townsquare Community Spotlight", "Civic Volunteers", "Community", true, "Profiles of neighborhood organizers, volunteers, and local artisans.")
                )
            ),
            StationChannelSchedule(
                channelNumber = 6,
                callsign = "TCTV-6",
                name = "Youth & Academy",
                emoji = "🎓",
                programs = listOf(
                    BroadcastProgram("601", "08:00 - 12:00", "Morning Discovery Academy", "Educator Collective", "Education", false, "Educational curricula, literacy adventures, and historical mysteries."),
                    BroadcastProgram("602", "12:00 - 17:00", "Junior Science Lab", "Dr. Maya Lin", "Education", true, "Interactive science demonstrations and student experiment showcases.")
                )
            )
        )
    }

    var selectedChannelIndex by remember { mutableIntStateOf(0) }
    val activeSchedule = channelSchedules[selectedChannelIndex]

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("broadcast_schedule_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NeonCyan.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Station Clock & Broadcast Guide",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Townsquare Master Transmission Center",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                        }
                    }

                    IconButton(onClick = onClose, modifier = Modifier.testTag("schedule_dialog_close")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Station Master Clock Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF001F2B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF30D158), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SYNCHRONIZED MASTER CLOCK",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = NeonCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentTimeString.isNotEmpty()) currentTimeString else "12:00:00",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 2.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = if (currentDateString.isNotEmpty()) currentDateString else "Saturday, September 28, 2026",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NeonCyan.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                            ) {
                                Text(
                                    text = "MUX 108.5 MHz",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "STATION NTP SYNCED",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Channel Selectors
                Text(
                    text = "SELECT BROADCAST CHANNEL",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(channelSchedules.size) { idx ->
                        val ch = channelSchedules[idx]
                        val isSelected = selectedChannelIndex == idx
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) NeonCyan else DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else DarkBorder
                            ),
                            modifier = Modifier.clickable { selectedChannelIndex = idx }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = ch.emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = ch.callsign,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color(0xFF003544) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "CH ${ch.channelNumber}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = if (isSelected) Color(0xFF003544).copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Channel Header & Tune In Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${activeSchedule.callsign} — ${activeSchedule.name}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Daily 24-Hour Broadcast Timetable",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            onTuneChannel(activeSchedule.channelNumber)
                            onClose()
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = NeonCyan,
                            contentColor = Color(0xFF003544)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Watch CH ${activeSchedule.channelNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Schedule List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(activeSchedule.programs) { program ->
                        ProgramScheduleCard(program = program)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgramScheduleCard(program: BroadcastProgram) {
    var isReminderActive by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (program.isLiveNow) Color(0xFF00222F) else DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (program.isLiveNow) NeonCyan else DarkBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = program.timeSlot,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (program.isLiveNow) NeonCyan else WarmAmber
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = program.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (program.isLiveNow) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF3B30)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color.White, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ON AIR NOW",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = Color.White
                            )
                        }
                    }
                } else {
                    IconButton(
                        onClick = { isReminderActive = !isReminderActive },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isReminderActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = "Set Reminder",
                            tint = if (isReminderActive) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = program.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Presenter: ${program.presenter}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = program.synopsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
