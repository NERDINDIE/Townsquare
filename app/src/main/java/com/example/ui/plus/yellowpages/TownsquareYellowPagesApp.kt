package com.example.ui.plus.yellowpages

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.theme.*

data class YellowPagesGig(
    val id: String,
    val title: String,
    val category: String,
    val clientName: String,
    val rateText: String,
    val isHourly: Boolean,
    val location: String,
    val urgencyText: String,
    val description: String,
    val tags: List<String>,
    val verifiedClient: Boolean = true
)

@Composable
fun TownsquareYellowPagesApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var isPostGigOpen by remember { mutableStateOf(false) }
    var applyingGig by remember { mutableStateOf<YellowPagesGig?>(null) }
    var applyProposalText by remember { mutableStateOf("") }
    var successToast by remember { mutableStateOf<String?>(null) }

    // New Gig Form State
    var newGigTitle by remember { mutableStateOf("") }
    var newGigCategory by remember { mutableStateOf("Design & Creative") }
    var newGigRate by remember { mutableStateOf("$50 / hr") }
    var newGigDesc by remember { mutableStateOf("") }
    var newGigLocation by remember { mutableStateOf("Townsquare Metro") }

    var gigs by remember {
        mutableStateOf(
            listOf(
                YellowPagesGig(
                    id = "gig_1",
                    title = "Broadsheet Front-Page Vector Illustrator",
                    category = "Design & Creative",
                    clientName = "Marcus Chen (Townsquare Press)",
                    rateText = "$450 Fixed",
                    isHourly = false,
                    location = "Remote / Civic Press Room",
                    urgencyText = "Needed in 3 days",
                    description = "Looking for an editorial illustrator to create a 3-column panoramic woodcut/vector artwork depicting the Waterfront Promenade ribbon cutting.",
                    tags = listOf("Illustration", "Vector", "Print Media", "Editorial")
                ),
                YellowPagesGig(
                    id = "gig_2",
                    title = "Line 3 Metro Signage Firmware Integrator",
                    category = "Tech & DevOps",
                    clientName = "Sarah Vance (Transit Dept)",
                    rateText = "$65 / hr",
                    isHourly = true,
                    location = "Central Rail Depot (On-site)",
                    urgencyText = "Immediate Start",
                    description = "Assist in reflashing 24 LED passenger information display matrixes with Townsquare GTFS real-time feeds.",
                    tags = listOf("Embedded", "C++", "GTFS", "Transit Tech")
                ),
                YellowPagesGig(
                    id = "gig_3",
                    title = "Restoration Carpenter for Historic Harbor Pergola",
                    category = "Civic Maintenance",
                    clientName = "Civic Heritage Guild",
                    rateText = "$850 Fixed",
                    isHourly = false,
                    location = "Harbor Promenade Pier 3",
                    urgencyText = "Starts Next Monday",
                    description = "Repair vintage marine teak benches and joinery trellises ahead of the Autumn Harvest Festival.",
                    tags = listOf("Carpentry", "Woodwork", "Historic Preservation")
                ),
                YellowPagesGig(
                    id = "gig_4",
                    title = "Culinary Writer for Downtown Dish Column",
                    category = "Writing & Journalism",
                    clientName = "Downtown Dish Media",
                    rateText = "$45 / hr",
                    isHourly = true,
                    location = "Bazaar & Old Town Cafes",
                    urgencyText = "Weekly Column",
                    description = "Review new artisan bakeries, espresso micro-roasters, and rooftop bistros for the bi-weekly media edition.",
                    tags = listOf("Food Writing", "Interviews", "Photography")
                ),
                YellowPagesGig(
                    id = "gig_5",
                    title = "Solar Microgrid Inverter Specialist",
                    category = "Civic Maintenance",
                    clientName = "Townsquare Energy Co-op",
                    rateText = "$60 / hr",
                    isHourly = true,
                    location = "East Hill Solar Array",
                    urgencyText = "2-Week Contract",
                    description = "Inspect 48kW rooftop solar photovoltaic panels and balance battery storage charge controllers.",
                    tags = listOf("Renewables", "Electrical", "Solar PV")
                )
            )
        )
    }

    val categories = listOf("ALL", "Design & Creative", "Tech & DevOps", "Civic Maintenance", "Writing & Journalism", "Logistics & Moving")

    val filteredGigs = gigs.filter { gig ->
        (selectedCategory == "ALL" || gig.category == selectedCategory) &&
        (searchQuery.isBlank() || gig.title.contains(searchQuery, ignoreCase = true) || gig.description.contains(searchQuery, ignoreCase = true) || gig.tags.any { it.contains(searchQuery, ignoreCase = true) })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("townsquare_yellow_pages_screen")
    ) {
        // Yellow Pages Golden Header
        Surface(
            color = Color(0xFFF59E0B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("yellow_pages_back_button")) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "TOWNSQUARE YELLOW PAGES",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Civic Freelance Gig & Skilled Trades Directory",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F)
                            )
                        }
                    }

                    // Post a Gig Button
                    Button(
                        onClick = { isPostGigOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post Gig", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search gigs, skills, trades e.g. 'Illustration', 'Solar'...", fontSize = 12.sp, color = Color(0xFF78350F)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("yellow_pages_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFFEF3C7),
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color(0xFFD97706),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true
                )
            }
        }

        // Category Filter Chips
        Surface(
            color = DarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF1E293B),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFFF59E0B) else DarkBorder),
                        modifier = Modifier
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            color = if (isSelected) Color.Black else Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Success Toast Notification
        if (successToast != null) {
            Surface(
                color = Color(0xFF064E3B),
                border = BorderStroke(1.dp, Color(0xFF10B981)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(successToast!!, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                    IconButton(onClick = { successToast = null }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Gigs Stream
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredGigs, key = { it.id }) { gig ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = gig.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(gig.clientName, fontSize = 11.sp, color = NeonCyan)
                                    if (gig.verifiedClient) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = NeonCyan, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            // Rate badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B))
                            ) {
                                Text(
                                    text = gig.rateText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFF59E0B),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = gig.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tags & Location
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                gig.tags.take(3).forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkSurfaceVariant
                                    ) {
                                        Text(tag, fontSize = 9.sp, color = DarkTextSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }

                            Text(
                                text = "📍 ${gig.location}",
                                fontSize = 10.sp,
                                color = DarkTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Apply Button
                        Button(
                            onClick = {
                                applyingGig = gig
                                applyProposalText = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7), contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Apply for Gig (${gig.urgencyText})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Apply Proposal Dialog
        if (applyingGig != null) {
            val gig = applyingGig!!
            AlertDialog(
                onDismissRequest = { applyingGig = null },
                containerColor = DarkSurface,
                title = {
                    Text("Apply: ${gig.title}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Client: ${gig.clientName} • Rate: ${gig.rateText}", fontSize = 12.sp, color = NeonCyan)
                        Text("Introduce yourself and detail your experience:", fontSize = 12.sp, color = DarkTextSecondary)
                        OutlinedTextField(
                            value = applyProposalText,
                            onValueChange = { applyProposalText = it },
                            placeholder = { Text("e.g. Hello, I have 5 years experience with vector illustration and can deliver within 2 days...") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            successToast = "Proposal successfully submitted to ${gig.clientName}!"
                            applyingGig = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color.Black)
                    ) {
                        Text("Submit Application", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { applyingGig = null }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }

        // Post a Gig Dialog
        if (isPostGigOpen) {
            AlertDialog(
                onDismissRequest = { isPostGigOpen = false },
                containerColor = DarkSurface,
                title = {
                    Text("Post a Freelance Gig / Job Listing", color = Color.White, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newGigTitle,
                            onValueChange = { newGigTitle = it },
                            label = { Text("Gig Title e.g. Vintage Typewriter Repair") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newGigRate,
                            onValueChange = { newGigRate = it },
                            label = { Text("Compensation e.g. $40 / hr or $300 Fixed") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newGigDesc,
                            onValueChange = { newGigDesc = it },
                            label = { Text("Project Scope & Requirements") },
                            modifier = Modifier.fillMaxWidth().height(90.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val created = YellowPagesGig(
                                id = "gig_${System.currentTimeMillis()}",
                                title = newGigTitle.ifBlank { "Civic Community Project" },
                                category = newGigCategory,
                                clientName = "You (Verified Citizen)",
                                rateText = newGigRate.ifBlank { "$50 / hr" },
                                isHourly = newGigRate.contains("/"),
                                location = newGigLocation,
                                urgencyText = "New Listing",
                                description = newGigDesc.ifBlank { "Community task posted via Yellow Pages." },
                                tags = listOf("Community", "Freelance", "Townsquare")
                            )
                            gigs = listOf(created) + gigs
                            successToast = "Gig successfully published to Yellow Pages!"
                            isPostGigOpen = false
                            newGigTitle = ""
                            newGigDesc = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color.Black)
                    ) {
                        Text("Publish Gig", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isPostGigOpen = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }
    }
}
