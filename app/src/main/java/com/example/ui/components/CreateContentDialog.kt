package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaSpaceEntity
import com.example.data.model.MediaType
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContentDialog(
    userSpaces: List<MediaSpaceEntity>,
    channels: List<MediaChannelEntity>,
    onPublish: (
        type: MediaType,
        title: String,
        subtitle: String,
        body: String,
        space: MediaSpaceEntity?,
        channel: MediaChannelEntity?,
        tags: String,
        readTimeMinutes: Int,
        durationSeconds: Int,
        frequency: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(MediaType.SOCIAL_POST) }
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("#insights #media") }
    var frequency by remember { mutableStateOf("104.7 FM") }
    var selectedSpace by remember { mutableStateOf(userSpaces.firstOrNull()) }
    var selectedChannel by remember { mutableStateOf(channels.firstOrNull()) }

    var spaceDropdownExpanded by remember { mutableStateOf(false) }
    var channelDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("create_content_dialog"),
            color = MaterialTheme.colorScheme.background
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
                    Column {
                        Text(
                            text = "Create & Publish",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Publish to your Media Space and Channels",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dismiss_create_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Format selector chips
                Text(
                    text = "SELECT MEDIA FORMAT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MediaType.entries.forEach { type ->
                        val isSelected = selectedType == type
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedType = type }
                                .testTag("format_tab_${type.name}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = type.displayName.take(7),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color(0xFF003544) else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Media Space selector
                    Text(
                        text = "PUBLISH UNDER MEDIA SPACE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = spaceDropdownExpanded,
                        onExpandedChange = { spaceDropdownExpanded = !spaceDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSpace?.title ?: "Select Media Space",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = spaceDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("select_space_dropdown"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = spaceDropdownExpanded,
                            onDismissRequest = { spaceDropdownExpanded = false }
                        ) {
                            userSpaces.forEach { space ->
                                DropdownMenuItem(
                                    text = { Text("${space.title} (${space.handle})") },
                                    onClick = {
                                        selectedSpace = space
                                        spaceDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Channel selector
                    Text(
                        text = "THEMATIC CHANNEL TO BROADCAST",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = channelDropdownExpanded,
                        onExpandedChange = { channelDropdownExpanded = !channelDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedChannel?.name ?: "Select Channel",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = channelDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("select_channel_dropdown"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = channelDropdownExpanded,
                            onDismissRequest = { channelDropdownExpanded = false }
                        ) {
                            channels.forEach { ch ->
                                DropdownMenuItem(
                                    text = { Text("${ch.iconEmoji} ${ch.name}") },
                                    onClick = {
                                        selectedChannel = ch
                                        channelDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Conditional title / headline fields
                    if (selectedType != MediaType.SOCIAL_POST) {
                        Text(
                            text = if (selectedType == MediaType.RADIO_STATION) "STATION / SHOW NAME" else "HEADLINE / TITLE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = {
                                Text(
                                    when (selectedType) {
                                        MediaType.NEWSPAPER_MAGAZINE -> "e.g. Breakthroughs in Computational Physics"
                                        MediaType.NEWSLETTER -> "e.g. Issue #14: The Architecture of Attention"
                                        MediaType.PODCAST_EPISODE -> "e.g. Episode 28: Designing Ambient Superapps"
                                        MediaType.RADIO_STATION -> "e.g. Neo-Wave 104.7 FM Live"
                                        else -> "Title"
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth().testTag("content_title_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "DECK / SUBTITLE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = subtitle,
                            onValueChange = { subtitle = it },
                            placeholder = { Text("A concise synopsis or summary sentence...") },
                            modifier = Modifier.fillMaxWidth().testTag("content_subtitle_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (selectedType == MediaType.RADIO_STATION) {
                        Text(
                            text = "RADIO FREQUENCY",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = frequency,
                            onValueChange = { frequency = it },
                            placeholder = { Text("e.g. 98.5 FM") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Content Body Text
                    Text(
                        text = when (selectedType) {
                            MediaType.SOCIAL_POST -> "POST TEXT"
                            MediaType.NEWSPAPER_MAGAZINE -> "EDITORIAL ARTICLE BODY"
                            MediaType.NEWSLETTER -> "NEWSLETTER CONTENT"
                            MediaType.PODCAST_EPISODE -> "EPISODE NOTES & SUMMARY"
                            MediaType.RADIO_STATION -> "STATION DESCRIPTION & LINEUP"
                            MediaType.SONG -> "SONG DETAILS & LYRICS"
                            MediaType.PLAYLIST -> "PLAYLIST COMPILER NOTES"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        placeholder = {
                            Text(
                                when (selectedType) {
                                    MediaType.SOCIAL_POST -> "What's happening across your media space? Share a thought, link, or perspective..."
                                    MediaType.NEWSPAPER_MAGAZINE -> "Write your comprehensive multi-paragraph investigative article or magazine piece..."
                                    MediaType.NEWSLETTER -> "Draft your direct reader digest, observations, and links..."
                                    MediaType.PODCAST_EPISODE -> "Describe this episode, guest bios, key timestamps, and talking points..."
                                    MediaType.RADIO_STATION -> "Describe music genres, scheduled broadcast hours, host bios..."
                                    MediaType.SONG -> "Write song details, artist credentials, lyrics or music links..."
                                    MediaType.PLAYLIST -> "List tracks in this playlist, focus theme, compiler notes..."
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("content_body_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tags
                    Text(
                        text = "TOPIC TAGS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        placeholder = { Text("#technology #journalism #future") },
                        modifier = Modifier.fillMaxWidth().testTag("content_tags_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Publish action button
                val isValid = if (selectedType == MediaType.SOCIAL_POST) body.isNotBlank() else (title.isNotBlank() && body.isNotBlank())

                Button(
                    onClick = {
                        onPublish(
                            selectedType,
                            if (selectedType == MediaType.SOCIAL_POST) "Social Post" else title,
                            subtitle,
                            body,
                            selectedSpace,
                            selectedChannel,
                            tags,
                            if (selectedType == MediaType.NEWSPAPER_MAGAZINE) 6 else 4,
                            if (selectedType == MediaType.PODCAST_EPISODE) 1200 else 0,
                            frequency
                        )
                    },
                    enabled = isValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = Color(0xFF003544),
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("publish_content_button")
                ) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish to ${selectedSpace?.title ?: "My Media Space"}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
