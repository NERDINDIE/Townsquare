package com.example.ui.plus.mail

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ImContact(
    val id: String,
    val name: String,
    val handle: String,
    val role: String,
    val avatarEmoji: String,
    val avatarBg: Color,
    val isOnline: Boolean,
    val statusText: String,
    val unreadCount: Int = 0
)

data class ImMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val status: ImMessageStatus = ImMessageStatus.READ,
    val attachmentUrl: String? = null,
    val isAudioVoiceNote: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    val reactionEmoji: String? = null
)

enum class ImMessageStatus {
    SENDING, SENT, DELIVERED, READ
}

object ImSeedData {
    fun getInitialContacts(): List<ImContact> = listOf(
        ImContact(
            id = "sarah_metro",
            name = "Sarah Vance",
            handle = "@vance_metro",
            role = "Metro Transit Director",
            avatarEmoji = "🚇",
            avatarBg = Color(0xFF0284C7),
            isOnline = true,
            statusText = "Monitoring Line 3 signal maintenance",
            unreadCount = 2
        ),
        ImContact(
            id = "marcus_broadsheet",
            name = "Marcus Chen",
            handle = "@chen_editor",
            role = "Lead Broadsheet Editor",
            avatarEmoji = "📰",
            avatarBg = Color(0xFFE11D48),
            isOnline = true,
            statusText = "Preparing Evening Dispatch layout"
        ),
        ImContact(
            id = "elena_arts",
            name = "Elena Rostova",
            handle = "@elena_curator",
            role = "Townsquare Antiquities Curator",
            avatarEmoji = "🏺",
            avatarBg = Color(0xFFD97706),
            isOnline = false,
            statusText = "In archive vault · Back at 16:00"
        ),
        ImContact(
            id = "dr_kofi",
            name = "Dr. Kofi Mensah",
            handle = "@mensah_civic",
            role = "Public Health Commissioner",
            avatarEmoji = "🏥",
            avatarBg = Color(0xFF059669),
            isOnline = true,
            statusText = "Water fountain filter audit complete"
        ),
        ImContact(
            id = "tara_market",
            name = "Tara Bellamy",
            handle = "@bellamy_goods",
            role = "Artisan Marketplace Guildmaster",
            avatarEmoji = "🛍️",
            avatarBg = Color(0xFF7C3AED),
            isOnline = false,
            statusText = "Away at Weekend Farmers Pavilion"
        )
    )

    fun getInitialMessages(contactId: String): List<ImMessage> = when (contactId) {
        "sarah_metro" -> listOf(
            ImMessage("m1", "sarah_metro", "Good afternoon citizen! Line 3 express bypass is now officially energized.", "14:12", false),
            ImMessage("m2", "sarah_metro", "Commuters should see travel times from North Ridge to Civic Square reduced by 8 minutes.", "14:13", false),
            ImMessage("m3", "me", "That's fantastic news! Will the tram frequency remain every 6 minutes?", "14:15", true, ImMessageStatus.READ),
            ImMessage("m4", "sarah_metro", "Yes! Peak frequency will be every 4 minutes during the festival weekend.", "14:16", false, reactionEmoji = "👏"),
            ImMessage("m5", "sarah_metro", "Here is the updated signaling schematic for the downtown junction.", "14:17", false, attachmentUrl = "schematic_line3.png")
        )
        "marcus_broadsheet" -> listOf(
            ImMessage("mb1", "marcus_broadsheet", "Hey there! We saw your dispatch draft in the Letters inbox.", "12:05", false),
            ImMessage("mb2", "marcus_broadsheet", "The photography of the botanical trellises is stunning. We'd love to run it on page A3.", "12:06", false),
            ImMessage("mb3", "me", "Thank you Marcus! Feel free to use the high-res crop.", "12:10", true, ImMessageStatus.READ)
        )
        else -> listOf(
            ImMessage("gen1", contactId, "Hello from the Townsquare municipal network. Messages here are end-to-end encrypted across our civic mesh.", "10:00", false),
            ImMessage("gen2", "me", "Confirmed, connected and synced.", "10:02", true, ImMessageStatus.READ)
        )
    }
}

@Composable
fun MailboxInstantMessenger(
    onBackToPostalMail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contacts = remember { ImSeedData.getInitialContacts() }
    var selectedContact by remember { mutableStateOf<ImContact?>(contacts.firstOrNull()) }
    var contactMessagesMap by remember {
        mutableStateOf(
            contacts.associate { it.id to ImSeedData.getInitialMessages(it.id) }
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var activeAttachment by remember { mutableStateOf<String?>(null) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Auto-scroll on new messages
    val currentMessages = selectedContact?.let { contactMessagesMap[it.id] } ?: emptyList()
    LaunchedEffect(currentMessages.size) {
        if (currentMessages.isNotEmpty()) {
            listState.animateScrollToItem(currentMessages.size - 1)
        }
    }

    fun sendMessage() {
        val contact = selectedContact ?: return
        if (inputText.isBlank() && activeAttachment == null && !isRecordingVoice) return

        val newMsg = ImMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderId = "me",
            text = if (isRecordingVoice) "🎙️ Voice dispatch note (0:14)" else inputText.trim(),
            timestamp = "Just now",
            isFromMe = true,
            status = ImMessageStatus.DELIVERED,
            attachmentUrl = activeAttachment,
            isAudioVoiceNote = isRecordingVoice,
            voiceDurationSeconds = if (isRecordingVoice) 14 else 0
        )

        contactMessagesMap = contactMessagesMap.toMutableMap().apply {
            val list = (this[contact.id] ?: emptyList()) + newMsg
            put(contact.id, list)
        }

        inputText = ""
        activeAttachment = null
        isRecordingVoice = false

        // Simulate reply from contact
        coroutineScope.launch {
            delay(1200)
            isTyping = true
            delay(1800)
            isTyping = false

            val replyText = when (contact.id) {
                "sarah_metro" -> "Got your dispatch! Transit dispatch operators are monitoring live telemetry right now."
                "marcus_broadsheet" -> "Noted for the next press run. Looking forward to your next story."
                "dr_kofi" -> "Received and logged into the civic council health portal."
                else -> "Message received loud and clear on Townsquare secure mesh."
            }

            val replyMsg = ImMessage(
                id = "reply_${System.currentTimeMillis()}",
                senderId = contact.id,
                text = replyText,
                timestamp = "Just now",
                isFromMe = false,
                status = ImMessageStatus.READ
            )

            contactMessagesMap = contactMessagesMap.toMutableMap().apply {
                val list = (this[contact.id] ?: emptyList()) + replyMsg
                put(contact.id, list)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // IM Header Bar
        Surface(
            color = DarkSurfaceVariant,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToPostalMail,
                    modifier = Modifier.testTag("im_back_to_mail")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Postal Mail",
                        tint = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Townsquare Instant Messenger",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                    }
                    Text(
                        text = "Encrypted Civic IM & Direct Dispatch Channel",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextSecondary
                    )
                }

                // Switch to Postal Mode Button
                FilledTonalButton(
                    onClick = onBackToPostalMail,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NeonCyan.copy(alpha = 0.15f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Postal Mail", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Horizontal Contacts Scroller
        Surface(
            color = DarkSurface,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, DarkBorder.copy(alpha = 0.5f))
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(contacts) { contact ->
                    val isSelected = selectedContact?.id == contact.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) NeonCyan.copy(alpha = 0.15f) else Color.Transparent,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) NeonCyan else Color.Transparent
                        ),
                        modifier = Modifier
                            .clickable { selectedContact = contact }
                            .testTag("im_contact_${contact.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(contact.avatarBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(contact.avatarEmoji, fontSize = 16.sp)
                                }
                                if (contact.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF22C55E))
                                            .align(Alignment.BottomEnd)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = contact.name,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) NeonCyan else Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (contact.isOnline) "Online" else "Away",
                                    fontSize = 10.sp,
                                    color = if (contact.isOnline) Color(0xFF4ADE80) else DarkTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Chat Surface
        val contact = selectedContact
        if (contact != null) {
            // Selected Contact Sub-header with Role
            Surface(
                color = DarkBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${contact.name} (${contact.role})",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = contact.statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(onClick = { /* simulated audio call */ }) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Audio Call", tint = NeonCyan, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { /* simulated video call */ }) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video Call", tint = WarmAmber, modifier = Modifier.size(20.dp))
                    }
                }
            }

            HorizontalDivider(color = DarkBorder, thickness = 1.dp)

            // Message Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    // Date indicator
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceVariant,
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Text(
                                text = "Today · End-to-End Encrypted",
                                fontSize = 11.sp,
                                color = DarkTextMuted,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                items(currentMessages) { message ->
                    ImMessageBubble(message = message, contact = contact)
                }

                if (isTyping) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        ) {
                            Text(
                                text = "${contact.name} is typing...",
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = NeonCyan,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }

            // Quick Emojis Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val quickEmojis = listOf("👍", "🗞️", "🚇", "👏", "☕", "📍", "❤️", "⚡")
                items(quickEmojis) { emoji ->
                    Surface(
                        shape = CircleShape,
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.clickable {
                            inputText += emoji
                        }
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Active Attachment Banner
            if (activeAttachment != null) {
                Surface(
                    color = DarkSurfaceVariant,
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = activeAttachment!!, style = MaterialTheme.typography.bodySmall, color = Color.White, modifier = Modifier.weight(1f))
                        IconButton(onClick = { activeAttachment = null }, modifier = Modifier.size(20.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = CoralRed, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment button
                    IconButton(
                        onClick = {
                            activeAttachment = "townsquare_dispatch_photo_${System.currentTimeMillis() % 1000}.jpg"
                        },
                        modifier = Modifier.testTag("im_attach_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach File",
                            tint = if (activeAttachment != null) NeonCyan else DarkTextSecondary
                        )
                    }

                    // Voice note toggle
                    IconButton(
                        onClick = {
                            isRecordingVoice = !isRecordingVoice
                        },
                        modifier = Modifier.testTag("im_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Dispatch",
                            tint = if (isRecordingVoice) CoralRed else DarkTextSecondary
                        )
                    }

                    // Text Field
                    OutlinedTextField(
                        value = if (isRecordingVoice) "🔴 Recording voice note..." else inputText,
                        onValueChange = { if (!isRecordingVoice) inputText = it },
                        placeholder = { Text("Write a message to ${contact.name}...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("im_input_field"),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        readOnly = isRecordingVoice
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    IconButton(
                        onClick = { sendMessage() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(NeonCyan, Color(0xFF0077B6)))
                            )
                            .testTag("im_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImMessageBubble(
    message: ImMessage,
    contact: ImContact
) {
    val isMe = message.isFromMe

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isMe) 14.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 14.dp
            ),
            color = if (isMe) Color(0xFF0284C7) else DarkSurfaceVariant,
            border = BorderStroke(
                1.dp,
                if (isMe) Color(0xFF38BDF8) else DarkBorder
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (!isMe) {
                    Text(
                        text = contact.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                if (message.attachmentUrl != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurface,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(message.attachmentUrl, fontSize = 11.sp, color = Color.White, maxLines = 1)
                                Text("Attached Civic Media · Tap to view", fontSize = 9.sp, color = DarkTextMuted)
                            }
                        }
                    }
                }

                if (message.isAudioVoiceNote) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Voice Memo (${message.voiceDurationSeconds}s)", fontSize = 13.sp, color = Color.White)
                    }
                } else {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        fontSize = 10.sp,
                        color = if (isMe) Color(0xFFBAE6FD) else DarkTextMuted
                    )

                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        val statusIcon = when (message.status) {
                            ImMessageStatus.SENDING -> "⏳"
                            ImMessageStatus.SENT -> "✓"
                            ImMessageStatus.DELIVERED -> "✓✓"
                            ImMessageStatus.READ -> "✓✓"
                        }
                        Text(
                            text = statusIcon,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (message.status == ImMessageStatus.READ) NeonCyan else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        if (message.reactionEmoji != null) {
            Surface(
                shape = CircleShape,
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .offset(y = (-8).dp, x = if (isMe) (-6).dp else 6.dp)
            ) {
                Text(
                    text = message.reactionEmoji,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
