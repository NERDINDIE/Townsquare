package com.example.ui.plus.phone

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

enum class VoicemailCategory(val label: String, val color: Color) {
    ALL("All Messages", NeonCyan),
    URGENT("Urgent Action", CoralRed),
    CIVIC("Civic Desk", WarmAmber),
    PERSONAL("Personal", MintTeal),
    REDIRECTED("Redirected Calls", RadiantPurple)
}

data class RichVisualVoicemail(
    val id: String,
    val callerName: String,
    val phoneNumber: String,
    val timestamp: String,
    val durationSeconds: Int,
    val transcript: String,
    val category: VoicemailCategory,
    val redirectReason: String, // e.g. "Redirected: Line Busy"
    val actionItems: List<String> = emptyList(),
    val isRead: Boolean = false,
    val isStarred: Boolean = false
)

@Composable
fun VisualVoicemailComponent(
    onCallBack: (String, String) -> Unit,
    onSendSms: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var selectedCategory by remember { mutableStateOf(VoicemailCategory.ALL) }
    var playingId by remember { mutableStateOf<String?>(null) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var isGreetingStudioOpen by remember { mutableStateOf(false) }
    var currentGreetingText by remember {
        mutableStateOf("You've reached Townsquare Direct Line. Please leave your name and dispatch details after the tone.")
    }

    // Call redirection configuration settings
    var isRedirectWhenBusy by remember { mutableStateOf(true) }
    var isRedirectWhenUnanswered by remember { mutableStateOf(true) }
    var isRedirectSpamImmediately by remember { mutableStateOf(true) }
    var showRedirectionRulesDialog by remember { mutableStateOf(false) }

    // Seed visual voicemails
    var voicemails by remember {
        mutableStateOf(
            listOf(
                RichVisualVoicemail(
                    id = "vm-1",
                    callerName = "Elena Rostova",
                    phoneNumber = "+1 (555) 723-1194",
                    timestamp = "Today, 10:14 AM",
                    durationSeconds = 48,
                    transcript = "Hey, verified the shipping manifests down at Pier 4. The clocktower historical archive documents were intact after all. Call me back at 555-723-1194 before the noon editorial meeting!",
                    category = VoicemailCategory.URGENT,
                    redirectReason = "Redirected: Line Busy (Active Call)",
                    actionItems = listOf("Verify shipping manifests", "Call back before 12:00 PM"),
                    isRead = false,
                    isStarred = true
                ),
                RichVisualVoicemail(
                    id = "vm-2",
                    callerName = "Mayor's Civic Office",
                    phoneNumber = "+1 (555) 019-2831",
                    timestamp = "Today, 08:30 AM",
                    durationSeconds = 62,
                    transcript = "Good morning. Mayor Vance requested advance notes on the Waterfront Promenade ribbon-cutting for Thursday morning at 10 AM. Please send the media team roster.",
                    category = VoicemailCategory.CIVIC,
                    redirectReason = "Redirected: Unanswered (20s delay)",
                    actionItems = listOf("Submit media roster for Thursday ribbon cutting"),
                    isRead = true,
                    isStarred = false
                ),
                RichVisualVoicemail(
                    id = "vm-3",
                    callerName = "Harbor Weather Station",
                    phoneNumber = "+1 (555) 882-9410",
                    timestamp = "Yesterday, 06:31 PM",
                    durationSeconds = 34,
                    transcript = "Harbor barometer has dropped 6 millibars in the last hour. Offshore squall line expected near the breakwater around 9 PM. Small craft advisory in effect.",
                    category = VoicemailCategory.CIVIC,
                    redirectReason = "Redirected: Direct to Voicemail",
                    actionItems = listOf("Broadcast squall advisory"),
                    isRead = true,
                    isStarred = false
                ),
                RichVisualVoicemail(
                    id = "vm-4",
                    callerName = "Suspected Telemarketer",
                    phoneNumber = "+1 (800) 412-9901",
                    timestamp = "Yesterday, 03:15 PM",
                    durationSeconds = 21,
                    transcript = "Important notice regarding your commercial vehicle warranty... press 1 now to speak with a specialist.",
                    category = VoicemailCategory.REDIRECTED,
                    redirectReason = "Redirected: Automated Robocall Deflector Shield",
                    actionItems = listOf("Blocked by Robocall Shield"),
                    isRead = true,
                    isStarred = false
                )
            )
        )
    }

    // Playback loop
    LaunchedEffect(playingId) {
        if (playingId != null) {
            playbackProgress = 0f
            while (isActive && playingId != null) {
                delay(150L)
                playbackProgress += 0.035f
                if (playbackProgress >= 1f) {
                    playingId = null
                    playbackProgress = 0f
                    break
                }
            }
        }
    }

    val filteredVoicemails = voicemails.filter {
        selectedCategory == VoicemailCategory.ALL || it.category == selectedCategory
    }

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
        // Redirection Hub Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth().testTag("redirection_management_banner")
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
                                color = RadiantPurple.copy(alpha = 0.2f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneForwarded,
                                        contentDescription = null,
                                        tint = RadiantPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Smart Call Redirection",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Redirected calls automatically transcribed & indexed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = { showRedirectionRulesDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = RadiantPurple.copy(alpha = 0.2f), contentColor = RadiantPurple),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RadiantPurple.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("manage_redirects_button")
                        ) {
                            Text("Rules (3)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Greeting: \"${currentGreetingText.take(45)}...\"",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { isGreetingStudioOpen = true },
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Greeting", fontSize = 11.sp, color = NeonCyan)
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VoicemailCategory.entries.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) cat.color else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) cat.color else DarkBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat.label.take(10),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF003544) else Color.White,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Voicemails List with Rich Audio Waveform and Action Items
        if (filteredVoicemails.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No visual voicemails in this filter.", color = DarkTextSecondary)
                }
            }
        } else {
            items(filteredVoicemails, key = { it.id }) { vm ->
                val isPlaying = playingId == vm.id

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (!vm.isRead) Color(0xFF131D2D) else DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (!vm.isRead) NeonCyan.copy(alpha = 0.5f) else DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("visual_voicemail_card_${vm.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!vm.isRead) {
                                    Surface(shape = CircleShape, color = NeonCyan, modifier = Modifier.size(8.dp)) {}
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = vm.callerName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = vm.timestamp, style = MaterialTheme.typography.labelSmall, color = DarkTextMuted)
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        voicemails = voicemails.map {
                                            if (it.id == vm.id) it.copy(isStarred = !it.isStarred) else it
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (vm.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Star",
                                        tint = if (vm.isStarred) WarmAmber else DarkTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Redirection Reason Badge
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = vm.phoneNumber, style = MaterialTheme.typography.labelSmall, color = DarkTextSecondary)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = vm.category.color.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = vm.redirectReason,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = vm.category.color,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Waveform Player Control
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (isPlaying) {
                                            playingId = null
                                        } else {
                                            playingId = vm.id
                                            voicemails = voicemails.map {
                                                if (it.id == vm.id) it.copy(isRead = true) else it
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(34.dp).testTag("play_vm_${vm.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Simulated Waveform Bar
                                LinearProgressIndicator(
                                    progress = { if (isPlaying) playbackProgress else 0f },
                                    color = NeonCyan,
                                    trackColor = DarkBorder,
                                    modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                                )

                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${vm.durationSeconds}s",
                                    fontSize = 11.sp,
                                    color = DarkTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Speech-to-text Transcript
                        Text(
                            text = "TRANSCRIPT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 9.sp),
                            color = NeonCyan
                        )
                        Text(
                            text = "\"${vm.transcript}\"",
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                            color = Color.White,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        // Highlighted Action Items
                        if (vm.actionItems.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                vm.actionItems.forEach { action ->
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = WarmAmber.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "📌 $action",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = WarmAmber,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = DarkBorder)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Buttons: Call Back, SMS, Copy, Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { clipboardManager.setText(AnnotatedString(vm.transcript)) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = DarkTextMuted, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = {
                                        voicemails = voicemails.filterNot { it.id == vm.id }
                                        if (playingId == vm.id) playingId = null
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = CoralRed, modifier = Modifier.size(16.dp))
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { onSendSms(vm.phoneNumber) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reply SMS", fontSize = 11.sp, color = NeonCyan)
                                }

                                Button(
                                    onClick = { onCallBack(vm.callerName, vm.phoneNumber) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("callback_vm_${vm.id}")
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call Back", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Call Redirection Rules Config Dialog
    if (showRedirectionRulesDialog) {
            AlertDialog(
                onDismissRequest = { showRedirectionRulesDialog = false },
                title = { Text("Call Forwarding & Redirection Rules", fontWeight = FontWeight.Bold, color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Forward when Busy", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("Send to Visual Voicemail immediately if on another call", fontSize = 11.sp, color = DarkTextSecondary)
                            }
                            Switch(checked = isRedirectWhenBusy, onCheckedChange = { isRedirectWhenBusy = it })
                        }
                        HorizontalDivider(color = DarkBorder)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Forward when Unanswered", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("Redirect to Visual Voicemail after 20 seconds", fontSize = 11.sp, color = DarkTextSecondary)
                            }
                            Switch(checked = isRedirectWhenUnanswered, onCheckedChange = { isRedirectWhenUnanswered = it })
                        }
                        HorizontalDivider(color = DarkBorder)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Auto-Redirect Unknown Spammers", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("Shield incoming robocalls & log transcripts silently", fontSize = 11.sp, color = DarkTextSecondary)
                            }
                            Switch(checked = isRedirectSpamImmediately, onCheckedChange = { isRedirectSpamImmediately = it })
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showRedirectionRulesDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                    ) {
                        Text("Apply Rules", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Custom Greeting Studio Dialog
        if (isGreetingStudioOpen) {
            var tempGreeting by remember { mutableStateOf(currentGreetingText) }
            AlertDialog(
                onDismissRequest = { isGreetingStudioOpen = false },
                title = { Text("Visual Voicemail Greeting Studio", fontWeight = FontWeight.Bold, color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Personalize what callers hear before their message is redirected to visual transcript:", fontSize = 12.sp, color = DarkTextSecondary)
                        OutlinedTextField(
                            value = tempGreeting,
                            onValueChange = { tempGreeting = it },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NeonCyan.copy(alpha = 0.2f),
                                modifier = Modifier.clickable {
                                    tempGreeting = "In editorial meeting until 3 PM. Please leave your dispatch details."
                                }
                            ) {
                                Text("Editorial Preset", fontSize = 10.sp, color = NeonCyan, modifier = Modifier.padding(6.dp))
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MintTeal.copy(alpha = 0.2f),
                                modifier = Modifier.clickable {
                                    tempGreeting = "Out on field reporting. Calls redirected to visual inbox."
                                }
                            ) {
                                Text("Field Reporter", fontSize = 10.sp, color = MintTeal, modifier = Modifier.padding(6.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            currentGreetingText = tempGreeting
                            isGreetingStudioOpen = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                    ) {
                        Text("Save Greeting", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isGreetingStudioOpen = false }) {
                        Text("Cancel", color = DarkTextSecondary)
                    }
                }
            )
        }
    }
}
