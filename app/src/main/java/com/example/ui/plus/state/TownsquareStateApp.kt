package com.example.ui.plus.state

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class Bill(
    val id: String,
    val name: String,
    val amount: Double,
    val dueDate: String,
    val icon: ImageVector,
    var isPaid: Boolean = false
)

data class CivilDocument(
    val id: String,
    val name: String,
    val documentNumber: String,
    val status: String, // "Active", "Pending Action", "Expired"
    val expiryDate: String
)

enum class CitizenRole(
    val title: String,
    val cardName: String,
    val label: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val cardNo: String,
    val status: String,
    val description: String,
    val emoji: String
) {
    CITIZEN(
        "Citizen",
        "Alexander Vance",
        "FULL ACTIVE RESIDENT",
        Color(0xFF00E5FF),
        Color(0xFF003544),
        "TS-8274-118A",
        "Verified • Active",
        "Grants standard access to district public facilities, local marketplaces, and live FM node feeds.",
        "👤"
    ),
    OFFICIAL(
        "Official",
        "Hon. Alexander Vance",
        "MUNICIPAL LEGISLATOR",
        Color(0xFFFFB300),
        Color(0xFF2D1E00),
        "GOV-003-882X",
        "Legislator • Active",
        "Authorizes cryptographic dispatch signatures, legislative veto logs, and secure civil database inquiries.",
        "🏛️"
    ),
    RESPONDER(
        "Responder",
        "Capt. Alex Vance",
        "FIRST RESPONDENT SQUAD",
        Color(0xFFFF3B30),
        Color(0xFF3D0800),
        "EMS-911-304K",
        "First Responder • Active",
        "Authorizes zone restriction bypass, emergency siren dispatch, and first respondent co-ordination.",
        "🚨"
    ),
    PRESS(
        "Press",
        "Alex Vance, Press",
        "OFFICIAL CHRONICLER",
        Color(0xFFD500F9),
        Color(0xFF32003D),
        "PRS-442-990B",
        "Editorial Badge • Active",
        "Permits unhindered district press pass coverage, direct editorial dispatches, and emergency cordon entry.",
        "📰"
    ),
    MERCHANT(
        "Merchant",
        "Alex Vance, Merchant",
        "REGISTERED DISTRICT KIOSK",
        Color(0xFF30D158),
        Color(0xFF063B14),
        "MER-771-002M",
        "Vendor Kiosk • Active",
        "Permits instant listing on Townsquare Marketplace, local food delivery orders, and community kiosk storefront management.",
        "🏪"
    ),
    CREATOR(
        "Creator",
        "Alex Vance, Creator",
        "AUDIO & MEDIA PRODUCER",
        Color(0xFFFF9F1C),
        Color(0xFF381F00),
        "CRT-504-889C",
        "Studio Pass • Active",
        "Grants broadcasting rights on Sonic Waveform FM radio, live audio stream node publishing, and editorial column syndication.",
        "🎨"
    )
}

data class ElectionOption(
    val id: String,
    val name: String,
    val party: String,
    val description: String,
    var votesCount: Int,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareStateApp(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Services, 1: Bills & Taxes, 2: Vote & Live Tally
    var activeRole by remember { mutableStateOf(CitizenRole.CITIZEN) }
    
    // Live Bills State
    var bills by remember {
        mutableStateOf(
            listOf(
                Bill("bill1", "Municipal Sewerage & Waste", 34.50, "Oct 12, 2026", Icons.Default.Delete),
                Bill("bill2", "Smart Grid Electrical Power", 85.20, "Oct 15, 2026", Icons.Default.Bolt),
                Bill("bill3", "District Fiber Broadband", 45.00, "Oct 18, 2026", Icons.Default.Router)
            )
        )
    }
    
    // Taxes filing State
    var isTaxesFiled by remember { mutableStateOf(false) }
    var voluntaryGreenContribution by remember { mutableFloatStateOf(50f) }
    var filingStatusMessage by remember { mutableStateOf("") }
    
    // Election State
    var hasVoted by remember { mutableStateOf(false) }
    var selectedCandidateId by remember { mutableStateOf("") }
    var candidates by remember {
        mutableStateOf(
            listOf(
                ElectionOption("cand1", "Elena Rostova", "Civic Progress League", "Focus: Pedestrianization, micro-hydro systems, local pottery guilds.", 14820, Color(0xFF00D2FF)),
                ElectionOption("cand2", "Marcus Vance", "Industrial Development Alliance", "Focus: High-tech manufacturing grants, automated freight lanes, airport expansion.", 12150, Color(0xFFFF9F1C))
            )
        )
    }
    
    // Live ticker straight from counting room
    var tallyTickers by remember {
        mutableStateOf(
            listOf(
                "Tally Room Feed: 86% of subdistricts verified.",
                "Subdistrict 7 (East Arts Quarter) reports high turnout for Rostova.",
                "Central Postal ballots scan process started under multi-partisan surveillance."
            )
        )
    }
    
    // Simulate real-time votes accumulating live before hitting press!
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(4000L)
            candidates = candidates.map { candidate ->
                val addition = if (candidate.id == "cand1") {
                    (5..15).random()
                } else {
                    (4..14).random()
                }
                candidate.copy(votesCount = candidate.votesCount + addition)
            }
            
            val reports = listOf(
                "Telemetry Desk: Subdistrict ${ (1..12).random() } certified successfully.",
                "Tally Feed: Current margin ${candidates[0].votesCount - candidates[1].votesCount} votes.",
                "Live Log: Ward ${ (1..4).random() } reporting count complete.",
                "Central Ledger: Cryptographic vote audit verified 100% integral."
            )
            tallyTickers = listOf(reports.random()) + tallyTickers.take(4)
        }
    }

    val unbilledTotal = bills.filter { !it.isPaid }.sumOf { it.amount }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // App Header
        TopAppBar(
            title = {
                Column {
                    Text("State Services Portal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Official Department of Citizen Affairs", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Plus", tint = NeonCyan)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        // Sub Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceElevated,
            contentColor = NeonCyan,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Civil Documents", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Bills & Taxes", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("🗳️ Live Election", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Civil Documents Screen
                    item {
                        // Role chips selectors
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(CitizenRole.entries.toTypedArray()) { role ->
                                val isSelected = activeRole == role
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) role.primaryColor else DarkSurface,
                                    border = BorderStroke(1.dp, if (isSelected) role.primaryColor else Color.Gray.copy(alpha = 0.3f)),
                                    modifier = Modifier.clickable { activeRole = role }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = role.emoji, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = role.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Holographic 3D credit card-like identity card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black,
                            border = BorderStroke(
                                1.5.dp,
                                Brush.sweepGradient(
                                    listOf(
                                        activeRole.primaryColor,
                                        Color.White,
                                        activeRole.primaryColor,
                                        Color.Gray,
                                        activeRole.primaryColor
                                    )
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("role_id_3d_card")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                activeRole.secondaryColor,
                                                activeRole.secondaryColor.copy(alpha = 0.7f),
                                                Color(0xFF020408)
                                            )
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                // Background watermark logo
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .offset(x = 10.dp, y = 5.dp)
                                ) {
                                    Text(
                                        text = activeRole.emoji,
                                        fontSize = 120.sp,
                                        color = Color.White.copy(alpha = 0.05f)
                                    )
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Card Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column {
                                            Text(
                                                text = "TOWNSQUARE CIVIL LEDGER",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = activeRole.primaryColor
                                            )
                                            Text(
                                                text = activeRole.label,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                        }

                                        // Wireless NFC waves icon
                                        Icon(
                                            imageVector = Icons.Default.Wifi,
                                            contentDescription = "Contactless NFC enabled",
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(28.dp))

                                    // Gold metallic Smart Chip and Signature line
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Metallic Golden Brass Chip with microscopic grid line drawings
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFFD700),
                                            border = BorderStroke(0.5.dp, Color(0xFFB8860B)),
                                            modifier = Modifier.size(36.dp, 28.dp)
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                Canvas(modifier = Modifier.fillMaxSize()) {
                                                    // Drawing Smart chip internal connectors
                                                    drawLine(Color(0xFF8B7500), Offset(size.width * 0.33f, 0f), Offset(size.width * 0.33f, size.height), strokeWidth = 1f)
                                                    drawLine(Color(0xFF8B7500), Offset(size.width * 0.66f, 0f), Offset(size.width * 0.66f, size.height), strokeWidth = 1f)
                                                    drawLine(Color(0xFF8B7500), Offset(0f, size.height * 0.5f), Offset(size.width, size.height * 0.5f), strokeWidth = 1f)
                                                }
                                            }
                                        }

                                        // Translucent holographic badge overlay
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = activeRole.primaryColor.copy(alpha = 0.15f),
                                            border = BorderStroke(0.5.dp, activeRole.primaryColor)
                                        ) {
                                            Text(
                                                text = activeRole.status.uppercase(),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = activeRole.primaryColor,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Card Holder Details
                                    Text(
                                        text = activeRole.cardName,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Column {
                                            Text("CARD NUMBER", fontSize = 8.sp, color = Color.Gray)
                                            Text(
                                                text = activeRole.cardNo,
                                                fontSize = 13.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = activeRole.primaryColor
                                            )
                                        }

                                        // Barcode
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(1.dp),
                                            modifier = Modifier
                                                .background(Color.White, RoundedCornerShape(2.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            val barcodeLines = listOf(3, 1, 4, 1, 5, 9, 2, 6, 5)
                                            barcodeLines.forEach { width ->
                                                Spacer(
                                                    modifier = Modifier
                                                        .width(width.dp)
                                                        .height(14.dp)
                                                        .background(Color.Black)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Privilege details card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = activeRole.primaryColor, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${activeRole.title.uppercase()} SECURITY PRIVILEGES",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = activeRole.primaryColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = activeRole.description,
                                    fontSize = 11.sp,
                                    color = Color.LightGray,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { /* Simulated NFC share */ },
                            colors = ButtonDefaults.buttonColors(containerColor = activeRole.primaryColor.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, activeRole.primaryColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = activeRole.primaryColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verify Digital Identity", color = activeRole.primaryColor)
                        }
                    }

                    item {
                        Text(
                            text = "OFFICIAL REGISTRATIONS & PERMITS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = Color.Gray
                        )
                    }

                    val docs = listOf(
                        CivilDocument("doc1", "Biometric Passport Book", "PP-9284920", "Active", "Jun 14, 2034"),
                        CivilDocument("doc2", "District Driving License", "DL-8820491", "Active", "Sep 20, 2031"),
                        CivilDocument("doc3", "Green Zone Parking Permit", "PR-1209", "Pending Action", "Oct 01, 2026")
                    )

                    items(docs) { doc ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(doc.name, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Number: ${doc.documentNumber}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                    Text("Expires: ${doc.expiryDate}", fontSize = 11.sp, color = Color.Gray)
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (doc.status == "Active") Color(0xFF30D158).copy(alpha = 0.15f) else Color(0xFFFF9F1C).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = doc.status.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (doc.status == "Active") Color(0xFF30D158) else Color(0xFFFF9F1C),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { /* Simulate applying */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Request New Document / Permit", color = Color.Black)
                        }
                    }
                }

                1 -> {
                    // Bills & Taxes filing
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("OUTSTANDING CIVIC BILLS", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$${String.format("%.2f", unbilledTotal)} Due",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (unbilledTotal > 0) Color(0xFFFF5252) else Color(0xFF30D158)
                                    )
                                    if (unbilledTotal > 0) {
                                        Button(
                                            onClick = {
                                                bills = bills.map { it.copy(isPaid = true) }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                                        ) {
                                            Text("Pay All Due", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    items(bills) { bill ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (bill.isPaid) Color(0xFF30D158).copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = bill.icon,
                                                contentDescription = null,
                                                tint = if (bill.isPaid) Color(0xFF30D158) else Color.White
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(bill.name, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Due date: ${bill.dueDate}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("$${String.format("%.2f", bill.amount)}", fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (bill.isPaid) {
                                        Text("PAID", color = Color(0xFF30D158), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Button(
                                            onClick = {
                                                bills = bills.map { if (it.id == bill.id) it.copy(isPaid = true) else it }
                                            },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(28.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                                        ) {
                                            Text("Pay", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Taxes filing section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ANNUAL CIVIL TAX FILING (YEAR 2026)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = Color.Gray
                        )
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, if (isTaxesFiled) Color(0xFF30D158) else Color.Gray.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Income Tax Assistant", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isTaxesFiled) Color(0xFF30D158).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (isTaxesFiled) "FILED" else "DRAFT",
                                            color = if (isTaxesFiled) Color(0xFF30D158) else Color.Gray,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Reported Salary Income:", fontSize = 13.sp, color = Color.LightGray)
                                    Text("$64,250.00", fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Standard Deductions:", fontSize = 13.sp, color = Color.LightGray)
                                    Text("-$12,400.00", fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(10.dp))

                                // Deductible voluntary slider
                                Text("Voluntary Eco Solar Grid Contribution: $${voluntaryGreenContribution.toInt()}", fontSize = 13.sp, color = Color.White)
                                Text("Deductible from aggregate tax liabilities", fontSize = 11.sp, color = Color.Gray)
                                Slider(
                                    value = voluntaryGreenContribution,
                                    onValueChange = { if (!isTaxesFiled) voluntaryGreenContribution = it },
                                    valueRange = 0f..250f,
                                    colors = SliderDefaults.colors(
                                        activeTrackColor = NeonCyan,
                                        thumbColor = NeonCyan
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text("ESTIMATED REFUND", fontSize = 10.sp, color = Color.Gray)
                                        Text(
                                            text = "$${String.format("%.2f", 420.00 + (voluntaryGreenContribution * 0.4))}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan
                                        )
                                    }

                                    if (!isTaxesFiled) {
                                        Button(
                                            onClick = {
                                                isTaxesFiled = true
                                                filingStatusMessage = "Income Tax filed successfully! Receipt: #TX-98420-SLR"
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                                        ) {
                                            Text("File Taxes Now", color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (filingStatusMessage.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(filingStatusMessage, color = Color(0xFF30D158), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Vote & Live Election results dashboard (before hitting the press)
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF3700B3),
                                                Color(0xFF12005E)
                                            )
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(shape = CircleShape, color = Color.Red, modifier = Modifier.size(8.dp)) {}
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("LIVE UNPUBLISHED ELECTION FEED", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = NeonCyan.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, NeonCyan)
                                        ) {
                                            Text("TALLY ROOM LEDGER", fontSize = 9.sp, color = NeonCyan, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Elections for Townsquare District Mayor", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                    Text("Direct real-time fiber feed from Central Voting Hall count. This data is private and has not yet hit public newspapers/press feeds.", fontSize = 12.sp, color = Color.LightGray)
                                }
                            }
                        }
                    }

                    // Render Vote Card if not voted
                    if (!hasVoted) {
                        item {
                            Text(
                                text = "CAST YOUR ANONYMOUS BALLOT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = Color.Gray
                            )
                        }

                        items(candidates) { candidate ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedCandidateId == candidate.id) DarkSurfaceElevated else DarkSurface,
                                border = BorderStroke(1.dp, if (selectedCandidateId == candidate.id) NeonCyan else Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCandidateId = candidate.id }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedCandidateId == candidate.id,
                                        onClick = { selectedCandidateId = candidate.id },
                                        colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(candidate.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                        Text(candidate.party, fontSize = 11.sp, color = candidate.color, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(candidate.description, fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    if (selectedCandidateId.isNotEmpty()) {
                                        // Record anonymous vote locally
                                        candidates = candidates.map { candidate ->
                                            if (candidate.id == selectedCandidateId) {
                                                candidate.copy(votesCount = candidate.votesCount + 1)
                                            } else {
                                                candidate
                                            }
                                        }
                                        hasVoted = true
                                    }
                                },
                                enabled = selectedCandidateId.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, disabledContainerColor = Color.Gray.copy(alpha = 0.2f))
                            ) {
                                Icon(Icons.Default.HowToVote, contentDescription = null, tint = if (selectedCandidateId.isNotEmpty()) Color.Black else Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cast Secure Ballot", color = if (selectedCandidateId.isNotEmpty()) Color.Black else Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF30D158).copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, Color(0xFF30D158))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF30D158))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Ballot cast anonymously & cryptographically signed. Thank you for voting!", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Tally results (interactive progress bars)
                    item {
                        Text(
                            text = "LIVE TALLY DESK RECONCILIATION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = Color.Gray
                        )
                    }

                    item {
                        val totalVotes = candidates.sumOf { it.votesCount }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Consolidated Votes Tally: $totalVotes cast", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))

                                candidates.forEach { candidate ->
                                    val percentage = if (totalVotes > 0) (candidate.votesCount.toFloat() / totalVotes.toFloat()) else 0f
                                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            Column {
                                                Text(candidate.name, fontWeight = FontWeight.Bold, color = Color.White)
                                                Text(candidate.party, fontSize = 10.sp, color = candidate.color)
                                            }
                                            Text(
                                                text = "${candidate.votesCount} (${String.format("%.1f", percentage * 100)}%)",
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 13.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { percentage },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = candidate.color,
                                            trackColor = Color.Gray.copy(alpha = 0.2f),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Real-time ticker list
                    item {
                        Text(
                            text = "TALLY LEDGER LOGS (EXCLUDE PRESS RELEASE)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = Color.Gray
                        )
                    }

                    items(tallyTickers) { ticker ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(ticker, fontSize = 11.sp, color = Color.LightGray, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
