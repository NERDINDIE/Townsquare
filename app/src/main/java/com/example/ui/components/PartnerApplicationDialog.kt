package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun PartnerApplicationDialog(
    onDismiss: () -> Unit,
    onSubmitApplication: (
        pubName: String,
        applicant: String,
        email: String,
        medium: String,
        region: String,
        circulation: String,
        charter: String,
        url: String
    ) -> Unit
) {
    var publicationName by remember { mutableStateOf("") }
    var applicantName by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var selectedMedium by remember { mutableStateOf("Print Broadsheet") }
    var regionCoverage by remember { mutableStateOf("") }
    var circulationEst by remember { mutableStateOf("") }
    var editorialCharter by remember { mutableStateOf("") }
    var sampleUrl by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val mediumOptions = listOf(
        "Print Broadsheet",
        "Community Radio",
        "Investigative Wire",
        "Independent Zine",
        "Digital Gazette"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("partner_application_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, DarkBorder),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = WarmAmber.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Newspaper,
                                    contentDescription = null,
                                    tint = WarmAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Townsquare Syndicate",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Partner Publication Application",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("partner_form_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isSubmitted) {
                    // Success View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Application Submitted!",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Thank you for applying to syndicate '$publicationName' with Townsquare. The editorial board reviews applications weekly against our civic charter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("Done", color = Color(0xFF003544), fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Form Content
                    Text(
                        text = "Join our cooperative media syndicate to distribute broadsheets, community audio, and wire dispatches across Townsquare readers and civic kiosks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Publication Name
                    OutlinedTextField(
                        value = publicationName,
                        onValueChange = { publicationName = it },
                        label = { Text("Publication / Press Name *") },
                        placeholder = { Text("e.g. The Coastal Gazette") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = NeonCyan) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("partner_input_pub_name"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Lead Publisher / Editor Name
                    OutlinedTextField(
                        value = applicantName,
                        onValueChange = { applicantName = it },
                        label = { Text("Publisher or Editor-in-Chief *") },
                        placeholder = { Text("e.g. Helena Vance") },
                        leadingIcon = { Icon(Icons.Default.People, contentDescription = null, tint = WarmAmber) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("partner_input_applicant_name"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmAmber,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Contact Email
                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = { contactEmail = it },
                        label = { Text("Editorial Contact Email *") },
                        placeholder = { Text("editor@coastalpress.org") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("partner_input_email"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Medium Selector
                    Text(
                        text = "Syndicate Medium Format *",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        mediumOptions.take(3).forEach { medium ->
                            val isSelected = selectedMedium == medium
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else DarkCardBg,
                                border = BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.clickable { selectedMedium = medium }
                                ) {
                                    Text(
                                        text = medium,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 10.sp
                                        ),
                                        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        mediumOptions.drop(3).forEach { medium ->
                            val isSelected = selectedMedium == medium
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else DarkCardBg,
                                border = BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.clickable { selectedMedium = medium }
                                ) {
                                    Text(
                                        text = medium,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 10.sp
                                        ),
                                        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Regional Coverage & Circulation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = regionCoverage,
                            onValueChange = { regionCoverage = it },
                            label = { Text("Region / District") },
                            placeholder = { Text("e.g. Harbor Basin") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = WarmAmber) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WarmAmber,
                                unfocusedBorderColor = DarkBorder
                            )
                        )

                        OutlinedTextField(
                            value = circulationEst,
                            onValueChange = { circulationEst = it },
                            label = { Text("Circulation") },
                            placeholder = { Text("e.g. 25,000") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Editorial Charter & Mission Pitch
                    OutlinedTextField(
                        value = editorialCharter,
                        onValueChange = { editorialCharter = it },
                        label = { Text("Editorial Charter & Community Mission *") },
                        placeholder = { Text("Describe your publication's civic standards, ethics, and focus areas...") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = NeonCyan) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("partner_input_charter"),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sample URL or RSS Feed
                    OutlinedTextField(
                        value = sampleUrl,
                        onValueChange = { sampleUrl = it },
                        label = { Text("Website or Sample Edition URL") },
                        placeholder = { Text("https://example.org/dispatch.pdf") },
                        leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = WarmAmber) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmAmber,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    // Error Message
                    errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF5252)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (publicationName.isBlank() || applicantName.isBlank() || contactEmail.isBlank() || editorialCharter.isBlank()) {
                                errorMessage = "Please fill in all required fields marked with *"
                                return@Button
                            }
                            isSubmitting = true
                            onSubmitApplication(
                                publicationName.trim(),
                                applicantName.trim(),
                                contactEmail.trim(),
                                selectedMedium,
                                regionCoverage.trim().ifBlank { "Metro & Civic Basin" },
                                circulationEst.trim().ifBlank { "Independent Press" },
                                editorialCharter.trim(),
                                sampleUrl.trim()
                            )
                            isSubmitting = false
                            isSubmitted = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("partner_submit_button"),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color(0xFF003544), modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF261800))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit Partner Application", color = Color(0xFF261800), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
