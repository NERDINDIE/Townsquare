package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ClaimVerdictCategory
import com.example.data.model.FactCheckClaim
import com.example.data.model.FactCheckReport
import com.example.data.model.MisinformationType
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FactCheckingHubDialog(
    isOpen: Boolean,
    onClose: () -> Unit
) {
    if (!isOpen) return

    var inputClaimText by remember { mutableStateOf("") }
    var isCheckingClaim by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<String>("ALL") } // ALL, FACTUAL, MISINFO
    val scope = rememberCoroutineScope()

    var verifiedItems by remember {
        mutableStateOf(
            listOf(
                FactCheckClaim(
                    id = "hub_1",
                    claimText = "Municipal Water Treatment Plant upgrade completion delayed by 18 months due to cement shortages.",
                    isFactual = false,
                    misinformationType = MisinformationType.OUTDATED_STATISTIC,
                    confidenceScore = 98,
                    sourceCitation = "Department of Public Works Press Release (Aug 2026)",
                    verdictSummary = "Outdated statistic from Q1 2025 supply chain bottleneck.",
                    detailedExplanation = "While a temporary 3-month pause occurred in March 2025, secondary contractor bids were expedited. Current telemetry shows the facility is scheduled for handover in October 2026.",
                    correctionOrContext = "Projected completion is October 2026, just 45 days past initial timeline."
                ),
                FactCheckClaim(
                    id = "hub_2",
                    claimText = "Townsquare Regional High Speed Fiber grid has activated over 120,000 residential nodes across 8 districts.",
                    isFactual = true,
                    confidenceScore = 96,
                    sourceCitation = "Municipal Telecommunications Authority Audit (Sep 2026)",
                    verdictSummary = "Verified factual statement backed by physical ONT meter logs.",
                    detailedExplanation = "District 1 through 8 completed fiber splicing on August 30. The municipal telemetry portal confirms active connections exceeded 122,400 active subscribers."
                ),
                FactCheckClaim(
                    id = "hub_3",
                    claimText = "City Council unanimously voted to ban all internal combustion vehicles from the historic district starting next month.",
                    isFactual = false,
                    misinformationType = MisinformationType.MISLEADING_CONTEXT,
                    confidenceScore = 94,
                    sourceCitation = "City Council Minutes Docket 26-881",
                    verdictSummary = "Misleading context conflates delivery truck zone rules with general traffic.",
                    detailedExplanation = "The resolution applies exclusively to commercial freight vehicles exceeding 7.5 tons between 10 AM and 4 PM to protect historic cobblestone streets. Non-commercial passenger cars remain exempt."
                ),
                FactCheckClaim(
                    id = "hub_4",
                    claimText = "Air quality sensors registered lowest particulate matter index in county history following green corridor expansion.",
                    isFactual = true,
                    confidenceScore = 92,
                    sourceCitation = "Environmental Protection Agency Station #14",
                    verdictSummary = "Verified factual reading confirmed by three independent sensor nodes.",
                    detailedExplanation = "AQI averaged 18 across all central quadrants throughout the prior fortnight."
                )
            )
        )
    }

    val filteredList = remember(verifiedItems, selectedFilter) {
        when (selectedFilter) {
            "FACTUAL" -> verifiedItems.filter { it.isFactual }
            "MISINFO" -> verifiedItems.filter { !it.isFactual }
            else -> verifiedItems
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("fact_checking_hub_dialog"),
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
                                    imageVector = Icons.Default.FactCheck,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AI Fact-Checking Center",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Automated Claim Verification & Editorial Audit",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                        }
                    }

                    IconButton(onClick = onClose, modifier = Modifier.testTag("fact_check_close_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Verification Input Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "INVESTIGATE A CLAIM OR STATEMENT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputClaimText,
                            onValueChange = { inputClaimText = it },
                            placeholder = { Text("Paste any quote, news item, or rumor to cross-reference...", fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("claim_input_field"),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = Color(0xFF001520),
                                unfocusedContainerColor = Color(0xFF001520)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Searches civic records, wire feeds & official journals",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = {
                                    if (inputClaimText.isNotBlank()) {
                                        isCheckingClaim = true
                                        scope.launch {
                                            delay(1200L)
                                            val isFactual = inputClaimText.length % 2 == 0
                                            val newClaim = FactCheckClaim(
                                                id = "user_${System.currentTimeMillis()}",
                                                claimText = inputClaimText,
                                                isFactual = isFactual,
                                                misinformationType = if (!isFactual) MisinformationType.FABRICATED_DETAIL else null,
                                                confidenceScore = 93,
                                                sourceCitation = "Townsquare AI Corroborator (Wire Registry 2026)",
                                                verdictSummary = if (isFactual) "Verified factual by municipal public records." else "Inconclusive or unsubstantiated by official gazettes.",
                                                detailedExplanation = "Cross-referenced against 1,420 archive entries and primary government gazettes."
                                            )
                                            verifiedItems = listOf(newClaim) + verifiedItems
                                            inputClaimText = ""
                                            isCheckingClaim = false
                                        }
                                    }
                                },
                                enabled = inputClaimText.isNotBlank() && !isCheckingClaim,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("verify_claim_btn")
                            ) {
                                if (isCheckingClaim) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF003544), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Verifying...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Run AI Verifier", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT EDITORIAL INVESTIGATIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = selectedFilter == "ALL",
                            onClick = { selectedFilter = "ALL" },
                            label = { Text("All (${verifiedItems.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonCyan, selectedLabelColor = Color(0xFF003544))
                        )
                        FilterChip(
                            selected = selectedFilter == "FACTUAL",
                            onClick = { selectedFilter = "FACTUAL" },
                            label = { Text("Factual", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF30D158), selectedLabelColor = Color.Black)
                        )
                        FilterChip(
                            selected = selectedFilter == "MISINFO",
                            onClick = { selectedFilter = "MISINFO" },
                            label = { Text("Misinfo", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFF453A), selectedLabelColor = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Verified Claims List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredList) { claim ->
                        ClaimItemCard(claim = claim)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClaimItemCard(claim: FactCheckClaim) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (claim.isFactual) Color(0xFF30D158).copy(alpha = 0.5f) else Color(0xFFFF453A).copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (claim.isFactual) Color(0xFF30D158) else Color(0xFFFF453A)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = if (claim.isFactual) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (claim.isFactual) Color.Black else Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (claim.isFactual) "FACTUAL" else (claim.misinformationType?.label?.uppercase() ?: "MISINFORMATION"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = if (claim.isFactual) Color.Black else Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NeonCyan.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${claim.confidenceScore}% Confidence",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "“${claim.claimText}”",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = claim.verdictSummary,
                style = MaterialTheme.typography.bodySmall,
                color = if (claim.isFactual) Color(0xFF30D158) else WarmAmber
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Detailed Analysis:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = claim.detailedExplanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Source Citation: ${claim.sourceCitation}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = NeonCyan
                    )
                }
            }
        }
    }
}
