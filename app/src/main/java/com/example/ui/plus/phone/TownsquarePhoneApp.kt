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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.plus.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquarePhoneApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Keypad, 1: Recents, 2: Contacts, 3: Voicemail
    var dialedNumber by remember { mutableStateOf("") }
    
    // In-memory state for Phone
    var contacts by remember { mutableStateOf(TownsquarePlusSeed.generateInitialContacts()) }
    var callLogs by remember { mutableStateOf(TownsquarePlusSeed.generateInitialCallLogs()) }
    var voicemails by remember { mutableStateOf(TownsquarePlusSeed.generateInitialVoicemails()) }
    
    // Active Call Simulation State
    var activeCallName by remember { mutableStateOf<String?>(null) }
    var activeCallNumber by remember { mutableStateOf<String?>(null) }
    var activeCallSeconds by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(false) }
    var isOnHold by remember { mutableStateOf(false) }
    
    // Playing voicemail state
    var playingVoicemailId by remember { mutableStateOf<String?>(null) }
    var voicemailProgress by remember { mutableFloatStateOf(0f) }

    // Dialog state for new contact
    var isAddContactOpen by remember { mutableStateOf(false) }
    var searchContactQuery by remember { mutableStateOf("") }

    // Call timer effect
    LaunchedEffect(activeCallNumber) {
        if (activeCallNumber != null) {
            activeCallSeconds = 0
            while (isActive && activeCallNumber != null) {
                delay(1000L)
                activeCallSeconds++
            }
        }
    }

    // Voicemail playback timer effect
    LaunchedEffect(playingVoicemailId) {
        if (playingVoicemailId != null) {
            voicemailProgress = 0f
            while (isActive && playingVoicemailId != null) {
                delay(200L)
                voicemailProgress += 0.05f
                if (voicemailProgress >= 1f) {
                    playingVoicemailId = null
                    voicemailProgress = 0f
                    break
                }
            }
        }
    }

    fun startCall(name: String, number: String) {
        activeCallName = name
        activeCallNumber = number
        isMuted = false
        isSpeakerOn = false
        isOnHold = false
    }

    fun endCall() {
        val durationFormatted = String.format("%02d:%02d", activeCallSeconds / 60, activeCallSeconds % 60)
        val newLog = CallLogItem(
            contactName = activeCallName ?: activeCallNumber ?: "Unknown",
            phoneNumber = activeCallNumber ?: "",
            callType = CallType.OUTGOING,
            timestamp = "Just now",
            durationText = durationFormatted
        )
        callLogs = listOf(newLog) + callLogs
        activeCallName = null
        activeCallNumber = null
        activeCallSeconds = 0
    }

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Bar
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
                        IconButton(onClick = onBack, modifier = Modifier.testTag("phone_back_button")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Townsquare Phone",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = NeonCyan
                                ) {
                                    Text(
                                        text = "PLUS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF003544),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Civic Wire • HD Voice Relays",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextSecondary
                            )
                        }
                    }

                    if (selectedTab == 2) {
                        IconButton(
                            onClick = { isAddContactOpen = true },
                            modifier = Modifier.testTag("phone_add_contact_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Add Contact",
                                tint = NeonCyan
                            )
                        }
                    }
                }
            }

            // Tabs Selector
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = DarkSurfaceVariant,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonCyan
                    )
                },
                divider = { HorizontalDivider(color = DarkBorder) }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dialpad, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Keypad", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Recents", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Contacts, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Contacts (${contacts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        val unreadCount = voicemails.count { !it.isRead }
                        BadgedBox(badge = {
                            if (unreadCount > 0) {
                                Badge(containerColor = WarmAmber) { Text("$unreadCount") }
                            }
                        }) {
                            Icon(Icons.Default.Voicemail, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    },
                    text = { Text("Visual Voicemail", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.SatelliteAlt, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Satellite Link", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    icon = { Icon(Icons.Default.CellTower, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Masthead Radar", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 6,
                    onClick = { selectedTab = 6 },
                    icon = { Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("AI Deflector", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 7,
                    onClick = { selectedTab = 7 },
                    icon = { Icon(Icons.Default.CellTower, contentDescription = null, modifier = Modifier.size(18.dp), tint = CoralRed) },
                    text = { Text("Emergency Pager", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CoralRed) }
                )
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> KeypadView(
                        dialedNumber = dialedNumber,
                        onNumberChange = { dialedNumber = it },
                        onCall = { number ->
                            val matchedContact = contacts.find { it.phoneNumber == number }
                            startCall(matchedContact?.name ?: number, number)
                        }
                    )
                    1 -> RecentsView(
                        callLogs = callLogs,
                        onCallContact = { name, number -> startCall(name, number) },
                        onClearLogs = { callLogs = emptyList() }
                    )
                    2 -> ContactsView(
                        contacts = contacts.filter {
                            searchContactQuery.isEmpty() ||
                            it.name.contains(searchContactQuery, ignoreCase = true) ||
                            it.roleOrOrg.contains(searchContactQuery, ignoreCase = true) ||
                            it.phoneNumber.contains(searchContactQuery)
                        },
                        searchQuery = searchContactQuery,
                        onSearchChange = { searchContactQuery = it },
                        onCallContact = { name, number -> startCall(name, number) },
                        onToggleFavorite = { contactId ->
                            contacts = contacts.map {
                                if (it.id == contactId) it.copy(isFavorite = !it.isFavorite) else it
                            }
                        }
                    )
                    3 -> VisualVoicemailComponent(
                        onCallBack = { name, number -> startCall(name, number) },
                        onSendSms = { /* open SMS / dispatch */ }
                    )
                    4 -> SatellitePhoneComponent(
                        onInitiateSatCall = { number -> startCall("Satellite Relay Call", number) }
                    )
                    5 -> SignalMastheadComponent()
                    6 -> AiRobocallDeflectorComponent()
                    7 -> EmergencyPagerComponent()
                }
            }
        }

        // Active Call Overlay
        if (activeCallNumber != null) {
            ActiveCallOverlay(
                contactName = activeCallName ?: activeCallNumber ?: "Unknown Caller",
                phoneNumber = activeCallNumber ?: "",
                durationSeconds = activeCallSeconds,
                isMuted = isMuted,
                isSpeakerOn = isSpeakerOn,
                isOnHold = isOnHold,
                onToggleMute = { isMuted = !isMuted },
                onToggleSpeaker = { isSpeakerOn = !isSpeakerOn },
                onToggleHold = { isOnHold = !isOnHold },
                onEndCall = { endCall() }
            )
        }

        // Add Contact Dialog
        if (isAddContactOpen) {
            AddContactDialog(
                onDismiss = { isAddContactOpen = false },
                onSave = { name, org, phone, email ->
                    val newContact = ContactItem(
                        name = name,
                        roleOrOrg = org,
                        phoneNumber = phone,
                        avatarLetter = name.firstOrNull()?.uppercase() ?: "C",
                        avatarColorHex = 0xFF00D2FF,
                        email = email
                    )
                    contacts = listOf(newContact) + contacts
                    isAddContactOpen = false
                }
            )
        }
    }
}

// -------------------------------------------------------------
// 1. KEYPAD VIEW
// -------------------------------------------------------------
@Composable
private fun KeypadView(
    dialedNumber: String,
    onNumberChange: (String) -> Unit,
    onCall: (String) -> Unit
) {
    val keypadButtons = listOf(
        Pair("1", ""), Pair("2", "ABC"), Pair("3", "DEF"),
        Pair("4", "GHI"), Pair("5", "JKL"), Pair("6", "MNO"),
        Pair("7", "PQRS"), Pair("8", "TUV"), Pair("9", "WXYZ"),
        Pair("*", ""), Pair("0", "+"), Pair("#", "")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Number Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (dialedNumber.isEmpty()) "Enter number..." else dialedNumber,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (dialedNumber.length > 12) 24.sp else 30.sp
                ),
                color = if (dialedNumber.isEmpty()) DarkTextMuted else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (dialedNumber.isNotEmpty()) {
                Text(
                    text = "Townsquare Civic Link Ready",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan.copy(alpha = 0.8f)
                )
            }
        }

        // Dial Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (row in 0..3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..2) {
                        val item = keypadButtons[row * 3 + col]
                        Surface(
                            shape = CircleShape,
                            color = DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier
                                .size(70.dp)
                                .clickable { onNumberChange(dialedNumber + item.first) }
                                .testTag("keypad_btn_${item.first}")
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = item.first,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (item.second.isNotEmpty()) {
                                    Text(
                                        text = item.second,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Call & Backspace Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder or Quick Clear
            if (dialedNumber.isNotEmpty()) {
                IconButton(
                    onClick = { onNumberChange("") },
                    modifier = Modifier.size(54.dp)
                ) {
                    Text("Clear", color = DarkTextSecondary, fontSize = 12.sp)
                }
            } else {
                Spacer(modifier = Modifier.size(54.dp))
            }

            // Green Call Button
            Surface(
                shape = CircleShape,
                color = Color(0xFF30D158),
                modifier = Modifier
                    .size(68.dp)
                    .clickable {
                        if (dialedNumber.isNotBlank()) {
                            onCall(dialedNumber)
                        }
                    }
                    .testTag("keypad_call_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Backspace Button
            if (dialedNumber.isNotEmpty()) {
                IconButton(
                    onClick = {
                        if (dialedNumber.isNotEmpty()) {
                            onNumberChange(dialedNumber.dropLast(1))
                        }
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("keypad_backspace_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Backspace,
                        contentDescription = "Delete",
                        tint = DarkTextSecondary
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(54.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// 2. RECENTS VIEW
// -------------------------------------------------------------
@Composable
private fun RecentsView(
    callLogs: List<CallLogItem>,
    onCallContact: (String, String) -> Unit,
    onClearLogs: () -> Unit
) {
    if (callLogs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.PhoneMissed,
                    contentDescription = null,
                    tint = DarkTextMuted,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("No recent call logs", color = DarkTextSecondary, fontSize = 15.sp)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CALL HISTORY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = NeonCyan
                    )
                    TextButton(onClick = onClearLogs) {
                        Text("Clear All", fontSize = 12.sp, color = DarkTextSecondary)
                    }
                }
            }
            items(callLogs, key = { it.id }) { log ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCallContact(log.contactName, log.phoneNumber) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (log.callType) {
                                CallType.INCOMING -> Icons.Default.CallReceived
                                CallType.OUTGOING -> Icons.Default.CallMade
                                CallType.MISSED -> Icons.Default.CallMissed
                            }
                            val iconColor = when (log.callType) {
                                CallType.INCOMING -> Color(0xFF30D158)
                                CallType.OUTGOING -> NeonCyan
                                CallType.MISSED -> CoralRed
                            }
                            Surface(
                                shape = CircleShape,
                                color = iconColor.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = log.contactName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (log.callType == CallType.MISSED) CoralRed else Color.White
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = log.phoneNumber,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkTextSecondary
                                    )
                                    if (log.durationText != null) {
                                        Text(text = " • ${log.durationText}", style = MaterialTheme.typography.labelSmall, color = DarkTextMuted)
                                    }
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = log.timestamp,
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMuted
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onCallContact(log.contactName, log.phoneNumber) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. CONTACTS VIEW
// -------------------------------------------------------------
@Composable
private fun ContactsView(
    contacts: List<ContactItem>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onCallContact: (String, String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = DarkTextMuted)
                    }
                }
            },
            placeholder = { Text("Search name, desk, or number...", color = DarkTextMuted, fontSize = 14.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().testTag("contacts_search_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contacts, key = { it.id }) { contact ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(contact.avatarColorHex),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = contact.avatarLetter,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = Color(0xFF003544)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = contact.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    if (contact.isFavorite) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Favorite",
                                            tint = WarmAmber,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = contact.roleOrOrg,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan
                                )
                                Text(
                                    text = contact.phoneNumber,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onToggleFavorite(contact.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (contact.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Star",
                                    tint = if (contact.isFavorite) WarmAmber else DarkTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { onCallContact(contact.name, contact.phoneNumber) },
                                modifier = Modifier.size(36.dp).testTag("contact_call_btn_${contact.name}")
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF30D158).copy(alpha = 0.2f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF30D158), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. VOICEMAIL VIEW
// -------------------------------------------------------------
@Composable
private fun VoicemailView(
    voicemails: List<VoicemailItem>,
    playingVoicemailId: String?,
    progress: Float,
    onTogglePlay: (VoicemailItem) -> Unit,
    onCallBack: (VoicemailItem) -> Unit,
    onDeleteVoicemail: (String) -> Unit
) {
    if (voicemails.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Voicemail, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Voicemail inbox empty", color = DarkTextSecondary, fontSize = 15.sp)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(voicemails, key = { it.id }) { vm ->
                val isPlaying = playingVoicemailId == vm.id
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (!vm.isRead) WarmAmber.copy(alpha = 0.5f) else DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (!vm.isRead) WarmAmber else NeonCyan,
                                    modifier = Modifier.size(8.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = vm.callerName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Text(text = vm.timestamp, style = MaterialTheme.typography.labelSmall, color = DarkTextMuted)
                        }

                        Text(
                            text = vm.phoneNumber,
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextSecondary,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Audio Player Simulation Bar
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { onTogglePlay(vm) },
                                        modifier = Modifier.size(36.dp).testTag("voicemail_play_btn_${vm.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isPlaying) "Pause" else "Play",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    LinearProgressIndicator(
                                        progress = { if (isPlaying) progress else 0f },
                                        color = NeonCyan,
                                        trackColor = DarkBorder,
                                        modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "${vm.durationSeconds}s",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Transcript
                        Text(
                            text = "VOICEMAIL TRANSCRIPT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 0.8.sp),
                            color = NeonCyan
                        )
                        Text(
                            text = "\"${vm.transcript}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = LightTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Actions: Call Back / Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { onDeleteVoicemail(vm.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = CoralRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", color = CoralRed, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onCallBack(vm) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call Back", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. ACTIVE CALL OVERLAY SCREEN
// -------------------------------------------------------------
@Composable
private fun ActiveCallOverlay(
    contactName: String,
    phoneNumber: String,
    durationSeconds: Int,
    isMuted: Boolean,
    isSpeakerOn: Boolean,
    isOnHold: Boolean,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleHold: () -> Unit,
    onEndCall: () -> Unit
) {
    val durationFormatted = String.format("%02d:%02d", durationSeconds / 60, durationSeconds % 60)

    Surface(
        color = Color(0xFF070B13).copy(alpha = 0.96f),
        modifier = Modifier.fillMaxSize().testTag("active_call_overlay")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = NeonCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, NeonCyan),
                    modifier = Modifier.size(90.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = contactName.firstOrNull()?.uppercase() ?: "T",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = contactName,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isOnHold) "CALL ON HOLD" else durationFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isOnHold) WarmAmber else Color(0xFF30D158)
                )
                Text(
                    text = "Townsquare HD Wire • Encrypted Civic Audio",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // In-call Control Pad
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CallActionButton(
                        icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = if (isMuted) "Unmute" else "Mute",
                        isActive = isMuted,
                        onClick = onToggleMute
                    )
                    CallActionButton(
                        icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        label = "Speaker",
                        isActive = isSpeakerOn,
                        onClick = onToggleSpeaker
                    )
                    CallActionButton(
                        icon = if (isOnHold) Icons.Default.PlayArrow else Icons.Default.Pause,
                        label = if (isOnHold) "Resume" else "Hold",
                        isActive = isOnHold,
                        onClick = onToggleHold
                    )
                }
            }

            // End Call Red Button
            Surface(
                shape = CircleShape,
                color = CoralRed,
                modifier = Modifier
                    .size(72.dp)
                    .clickable { onEndCall() }
                    .testTag("end_call_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = if (isActive) NeonCyan else DarkSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) NeonCyan else DarkBorder),
            modifier = Modifier
                .size(60.dp)
                .clickable { onClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) Color(0xFF003544) else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = DarkTextSecondary)
    }
}

// -------------------------------------------------------------
// 6. ADD CONTACT DIALOG
// -------------------------------------------------------------
@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, org: String, phone: String, email: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var org by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add New Contact", fontWeight = FontWeight.Bold, color = Color.White)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name / Desk") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = org,
                    onValueChange = { org = it },
                    label = { Text("Department / Organization") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(name, org.ifBlank { "Civic Contact" }, phone, email)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
            ) {
                Text("Save Contact", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DarkTextSecondary)
            }
        }
    )
}
