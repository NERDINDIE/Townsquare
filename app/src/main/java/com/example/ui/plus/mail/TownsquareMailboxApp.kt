package com.example.ui.plus.mail

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.plus.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareMailboxApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var emails by remember { mutableStateOf(TownsquarePlusSeed.generateInitialEmails()) }
    var currentFolder by remember { mutableStateOf(MailFolder.INBOX) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Active Reading Email
    var activeEmail by remember { mutableStateOf<EmailItem?>(null) }
    
    // Compose Dialog State
    var isComposeOpen by remember { mutableStateOf(false) }
    var composeTo by remember { mutableStateOf("") }
    var composeSubject by remember { mutableStateOf("") }
    var composeBody by remember { mutableStateOf("") }
    var composeAttachmentName by remember { mutableStateOf<String?>(null) }

    val folders = listOf(
        Pair(MailFolder.INBOX, "Inbox"),
        Pair(MailFolder.STARRED, "Starred"),
        Pair(MailFolder.SENT, "Sent"),
        Pair(MailFolder.DRAFTS, "Drafts"),
        Pair(MailFolder.ARCHIVE, "Archive"),
        Pair(MailFolder.TRASH, "Trash"),
        Pair(MailFolder.ENVELOPES, "Envelopes ✉️"),
        Pair(MailFolder.POSTCARDS, "Postcards 🏞️"),
        Pair(MailFolder.DATING_DMS, "Dating DMs 💌"),
        Pair(MailFolder.STAMPS, "Collectible Stamps 🏆")
    )

    fun sendEmail() {
        val newEmail = EmailItem(
            senderName = "You (Townsquare Citizen)",
            senderEmail = "citizen@townsquare.media",
            recipientEmail = composeTo.ifBlank { "editor@townsquare.media" },
            subject = composeSubject.ifBlank { "(No Subject)" },
            snippet = composeBody.take(120),
            body = composeBody,
            timestamp = "Just now",
            folder = MailFolder.SENT,
            isUnread = false,
            isStarred = false,
            hasAttachments = composeAttachmentName != null,
            attachments = if (composeAttachmentName != null) {
                listOf(MailAttachment(composeAttachmentName!!, "1.2 MB", "pdf"))
            } else emptyList(),
            avatarColorHex = 0xFF00D2FF
        )
        emails = listOf(newEmail) + emails
        isComposeOpen = false
        composeTo = ""
        composeSubject = ""
        composeBody = ""
        composeAttachmentName = null
    }

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        if (activeEmail != null) {
            // Email Detail View
            EmailDetailView(
                email = activeEmail!!,
                onBack = { activeEmail = null },
                onToggleStar = { emailId ->
                    emails = emails.map { if (it.id == emailId) it.copy(isStarred = !it.isStarred) else it }
                    activeEmail = activeEmail?.copy(isStarred = !activeEmail!!.isStarred)
                },
                onArchive = { emailId ->
                    emails = emails.map { if (it.id == emailId) it.copy(folder = MailFolder.ARCHIVE) else it }
                    activeEmail = null
                },
                onDelete = { emailId ->
                    emails = emails.map { if (it.id == emailId) it.copy(folder = MailFolder.TRASH) else it }
                    activeEmail = null
                },
                onReply = { email ->
                    composeTo = email.senderEmail
                    composeSubject = "Re: ${email.subject}"
                    composeBody = "\n\n--- On ${email.timestamp}, ${email.senderName} wrote:\n${email.body}"
                    isComposeOpen = true
                }
            )
        } else {
            // Email List View
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                Surface(
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack, modifier = Modifier.testTag("mailbox_back_button")) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = NeonCyan
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = WarmAmber.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Mail,
                                        contentDescription = null,
                                        tint = WarmAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Townsquare Mailbox",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = WarmAmber
                                    ) {
                                        Text(
                                            text = "PLUS",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF261800),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "editor@townsquare.media • Encrypted Dispatch",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                composeTo = ""
                                composeSubject = ""
                                composeBody = ""
                                composeAttachmentName = null
                                isComposeOpen = true
                            },
                            modifier = Modifier.testTag("mailbox_compose_top_button")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NeonCyan.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Edit, contentDescription = "Compose", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                // Folder Pills Carousel
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(folders) { (folder, label) ->
                        val isSelected = currentFolder == folder
                        val count = when (folder) {
                            MailFolder.INBOX -> emails.count { it.folder == MailFolder.INBOX && it.isUnread }
                            MailFolder.STARRED -> emails.count { it.isStarred }
                            else -> emails.count { it.folder == folder }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) NeonCyan else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else DarkBorder
                            ),
                            modifier = Modifier.clickable { currentFolder = folder }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF003544) else Color.White
                                )
                                if (count > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) Color(0xFF003544) else NeonCyan.copy(alpha = 0.25f),
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$count",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) NeonCyan else Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Search Bar
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = DarkTextMuted)
                                }
                            }
                        },
                        placeholder = { Text("Search emails by subject, sender, body...", fontSize = 13.sp, color = DarkTextMuted) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("mailbox_search_input")
                    )
                }

                // Filtered Email List
                val displayedEmails = emails.filter { email ->
                    val matchesFolder = if (currentFolder == MailFolder.STARRED) {
                        email.isStarred
                    } else {
                        email.folder == currentFolder
                    }
                    val matchesSearch = searchQuery.isEmpty() ||
                            email.senderName.contains(searchQuery, ignoreCase = true) ||
                            email.subject.contains(searchQuery, ignoreCase = true) ||
                            email.snippet.contains(searchQuery, ignoreCase = true)

                    matchesFolder && matchesSearch
                }

                if (displayedEmails.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Inbox, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No messages in ${currentFolder.name.lowercase().replaceFirstChar { it.uppercase() }}", color = DarkTextSecondary, fontSize = 15.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(displayedEmails, key = { it.id }) { email ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (email.isUnread) DarkSurfaceElevated else DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (email.isUnread) NeonCyan.copy(alpha = 0.5f) else DarkBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Mark read and open
                                        emails = emails.map { if (it.id == email.id) it.copy(isUnread = false) else it }
                                        activeEmail = email.copy(isUnread = false)
                                    }
                                    .testTag("email_item_${email.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Avatar
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(email.avatarColorHex),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = email.senderName.firstOrNull()?.uppercase() ?: "M",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (email.isUnread) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = NeonCyan,
                                                        modifier = Modifier.size(7.dp)
                                                    ) {}
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }
                                                Text(
                                                    text = email.senderName,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (email.isUnread) FontWeight.Bold else FontWeight.Medium
                                                    ),
                                                    color = Color.White,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Text(
                                                text = email.timestamp,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (email.isUnread) NeonCyan else DarkTextMuted
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = email.subject,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (email.isUnread) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (email.isUnread) Color.White else DarkTextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Text(
                                            text = email.snippet,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = DarkTextMuted,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        if (email.hasAttachments) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = DarkSurfaceVariant,
                                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "${email.attachments.size} Attachment${if (email.attachments.size > 1) "s" else ""}",
                                                        fontSize = 10.sp,
                                                        color = DarkTextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = {
                                            emails = emails.map {
                                                if (it.id == email.id) it.copy(isStarred = !it.isStarred) else it
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (email.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Star",
                                            tint = if (email.isStarred) WarmAmber else DarkTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Floating Compose Button
            FloatingActionButton(
                onClick = {
                    composeTo = ""
                    composeSubject = ""
                    composeBody = ""
                    composeAttachmentName = null
                    isComposeOpen = true
                },
                containerColor = NeonCyan,
                contentColor = Color(0xFF003544),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("mailbox_compose_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compose", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Compose Email Dialog
        if (isComposeOpen) {
            AlertDialog(
                onDismissRequest = { isComposeOpen = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Dispatch / Mail", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = composeTo,
                            onValueChange = { composeTo = it },
                            label = { Text("To (Recipient Email)") },
                            placeholder = { Text("press@townsquare.gov") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = composeSubject,
                            onValueChange = { composeSubject = it },
                            label = { Text("Subject") },
                            placeholder = { Text("Community Inquiry...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = composeBody,
                            onValueChange = { composeBody = it },
                            label = { Text("Body") },
                            placeholder = { Text("Write your message here...") },
                            minLines = 4,
                            maxLines = 8,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Attachment Simulation Option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = {
                                composeAttachmentName = if (composeAttachmentName == null) "Civic_Report_Appendix.pdf" else null
                            }) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (composeAttachmentName == null) "Attach Document" else "Remove: $composeAttachmentName",
                                    fontSize = 12.sp,
                                    color = if (composeAttachmentName == null) NeonCyan else CoralRed
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { sendEmail() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                        modifier = Modifier.testTag("mailbox_send_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Mail", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isComposeOpen = false }) {
                        Text("Discard", color = DarkTextSecondary)
                    }
                }
            )
        }
    }
}

// -------------------------------------------------------------
// EMAIL DETAIL VIEW
// -------------------------------------------------------------
@Composable
private fun EmailDetailView(
    email: EmailItem,
    onBack: () -> Unit,
    onToggleStar: (String) -> Unit,
    onArchive: (String) -> Unit,
    onDelete: (String) -> Unit,
    onReply: (EmailItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Detail Header
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("email_detail_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onToggleStar(email.id) }) {
                        Icon(
                            imageVector = if (email.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star",
                            tint = if (email.isStarred) WarmAmber else DarkTextSecondary
                        )
                    }
                    IconButton(onClick = { onArchive(email.id) }) {
                        Icon(Icons.Default.Archive, contentDescription = "Archive", tint = DarkTextSecondary)
                    }
                    IconButton(onClick = { onDelete(email.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CoralRed)
                    }
                }
            }
        }

        // Email Content Body
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = email.subject,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(email.avatarColorHex),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = email.senderName.firstOrNull()?.uppercase() ?: "T",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = email.senderName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = email.senderEmail,
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                            Text(
                                text = "To: ${email.recipientEmail}",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMuted
                            )
                        }

                        Text(
                            text = email.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextSecondary
                        )
                    }
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = email.body,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = Color.White
                        )

                        if (email.attachments.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            HorizontalDivider(color = DarkBorder)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "ATTACHMENTS (${email.attachments.size})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            email.attachments.forEach { att ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Description, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(text = att.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Text(text = att.sizeText, fontSize = 11.sp, color = DarkTextSecondary)
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = NeonCyan.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "VIEW",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NeonCyan,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onReply(email) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reply", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onArchive(email.id) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Archive", color = NeonCyan)
                    }
                }
            }
        }
    }
}
