package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DraftType
import com.example.data.model.NotepadDraftEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCardBackground
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan

val NotepadDraftEntity.hasAudioNote: Boolean get() = audioMemoDurationSec > 0 || audioTranscript.isNotEmpty()
val NotepadDraftEntity.audioDurationSeconds: Int get() = audioMemoDurationSec
val NotepadDraftEntity.hasPhotoAttachment: Boolean get() = attachedPhotoUrl.isNotEmpty()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultimediaNotepadScreen(
    drafts: List<NotepadDraftEntity>,
    selectedFilter: String,
    onSelectFilter: (String) -> Unit,
    onSaveDraft: (NotepadDraftEntity) -> Unit,
    onToggleStar: (NotepadDraftEntity) -> Unit,
    onDeleteDraft: (Long) -> Unit,
    onConvertToJournal: (NotepadDraftEntity) -> Unit,
    onBackToJournal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditorOpen by remember { mutableStateOf(false) }
    var draftToEdit by remember { mutableStateOf<NotepadDraftEntity?>(null) }
    var activeAudioPlayingDraftId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToJournal,
                    modifier = Modifier.testTag("notepad_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Journal",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Multimedia Notepad",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Idea & draft holder for personal journal",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                }
            }

            Button(
                onClick = {
                    draftToEdit = null
                    isEditorOpen = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("create_new_draft_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Draft", fontWeight = FontWeight.Bold)
            }
        }

        // Info Banner
        Surface(
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Journal Idea Incubator",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Collect thoughts, voice memos, photo stories & publish to your broadsheet edition.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonCyan.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${drafts.size} Drafts",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Filter chips row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filterOptions = listOf(
                "ALL" to "📁 All Drafts",
                "STARRED" to "⭐ Starred",
                "IDEA" to "💡 Ideas",
                "ARTICLE_DRAFT" to "✍️ Articles",
                "VOICE_MEMO" to "🎙️ Voice Notes",
                "PHOTO_STORY" to "📷 Photo Stories",
                "QUOTE_SNIPPET" to "💬 Quotes",
                "CHECKLIST" to "📋 Checklists"
            )

            filterOptions.forEach { (typeKey, label) ->
                val isSelected = selectedFilter == typeKey
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectFilter(typeKey) },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                        selectedLabelColor = NeonCyan
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) NeonCyan else DarkBorder,
                        selectedBorderColor = NeonCyan,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        // Drafts List
        if (drafts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📝",
                        fontSize = 44.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No drafts found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Capture a fresh thought or voice memo to preserve for your journal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            draftToEdit = null
                            isEditorOpen = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                    ) {
                        Text("Create First Draft")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                items(drafts, key = { it.id }) { draft ->
                    NotepadDraftCard(
                        draft = draft,
                        isPlayingAudio = activeAudioPlayingDraftId == draft.id,
                        onTogglePlayAudio = {
                            activeAudioPlayingDraftId = if (activeAudioPlayingDraftId == draft.id) null else draft.id
                        },
                        onEdit = {
                            draftToEdit = draft
                            isEditorOpen = true
                        },
                        onToggleStar = { onToggleStar(draft) },
                        onDelete = { onDeleteDraft(draft.id) },
                        onConvertToJournal = { onConvertToJournal(draft) }
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (isEditorOpen) {
        NotepadDraftEditorDialog(
            initialDraft = draftToEdit,
            onDismiss = { isEditorOpen = false },
            onSave = { draft ->
                onSaveDraft(draft)
                isEditorOpen = false
            }
        )
    }
}

@Composable
fun NotepadDraftCard(
    draft: NotepadDraftEntity,
    isPlayingAudio: Boolean,
    onTogglePlayAudio: () -> Unit,
    onEdit: () -> Unit,
    onToggleStar: () -> Unit,
    onDelete: () -> Unit,
    onConvertToJournal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(draft.accentColorHex)
    val typeEnum = try {
        DraftType.valueOf(draft.draftType)
    } catch (_: Exception) {
        DraftType.IDEA
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
        border = BorderStroke(1.dp, if (draft.isStarred) accentColor.copy(alpha = 0.8f) else DarkBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("draft_card_${draft.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Type badge, target journal section, Star & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${typeEnum.emoji} ${typeEnum.displayName}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkSurfaceElevated
                    ) {
                        Text(
                            text = "→ ${draft.targetJournalSection}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleStar,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (draft.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star Draft",
                            tint = if (draft.isStarred) Color(0xFFFFD60A) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Draft",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Draft",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Draft Title
            Text(
                text = draft.title.ifEmpty { "Untitled Draft Idea" },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Body Text Preview
            if (draft.bodyText.isNotEmpty()) {
                Text(
                    text = draft.bodyText,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    maxLines = 4
                )
            }

            // Audio Voice Note Player (if has audio)
            if (draft.hasAudioNote) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1B232E),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isPlayingAudio) NeonCyan else DarkSurfaceElevated,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { onTogglePlayAudio() }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                            contentDescription = "Play Audio Memo",
                                            tint = if (isPlayingAudio) Color(0xFF003544) else NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Voice Memo (${draft.audioDurationSeconds}s)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = NeonCyan
                                    )
                                    Text(
                                        text = if (isPlayingAudio) "Playing sound note..." else "Recorded voice memo",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Simulated Waveform Bars
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val heights = listOf(8, 14, 20, 12, 18, 24, 16, 10, 22, 14, 8)
                                heights.forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(h.dp)
                                            .background(
                                                if (isPlayingAudio) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                RoundedCornerShape(2.dp)
                                            )
                                    )
                                }
                            }
                        }

                        if (draft.audioTranscript.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Transcript: \"${draft.audioTranscript}\"",
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }

            // Photo Attachment (if has photo)
            if (draft.hasPhotoAttachment) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonCyan.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Photo Field Note Attached",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (draft.attachedPhotoCaption.isNotEmpty()) {
                                Text(
                                    text = draft.attachedPhotoCaption,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Quote Attributed Excerpt (if present)
            if (draft.quoteAttribution.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E2630),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = draft.quoteAttribution,
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Checklist Preview (if present)
            if (draft.checklistItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                val items = draft.checklistItems.lines().filter { it.isNotBlank() }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items.take(3).forEach { item ->
                        val isDone = item.startsWith("[x]") || item.startsWith("✓")
                        val cleanText = item.removePrefix("[x]").removePrefix("[ ]").removePrefix("✓").trim()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDone) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = null,
                                tint = if (isDone) Color(0xFF30D158) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = cleanText,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    if (items.size > 3) {
                        Text(
                            text = "+ ${items.size - 3} more checklist tasks",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = DarkBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Tags, Date & "Publish into Journal Edition" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (draft.tags.isNotEmpty()) draft.tags else "Draft holder",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onConvertToJournal,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (draft.isConvertedToJournal) Color(0xFF30D158).copy(alpha = 0.2f) else NeonCyan,
                        contentColor = if (draft.isConvertedToJournal) Color(0xFF30D158) else Color(0xFF003544)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("publish_draft_to_journal_${draft.id}")
                ) {
                    Icon(
                        imageVector = if (draft.isConvertedToJournal) Icons.Default.Check else Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (draft.isConvertedToJournal) "In Journal Broadsheet" else "Publish to Journal",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun NotepadDraftEditorDialog(
    initialDraft: NotepadDraftEntity?,
    onDismiss: () -> Unit,
    onSave: (NotepadDraftEntity) -> Unit
) {
    var title by remember { mutableStateOf(initialDraft?.title ?: "") }
    var body by remember { mutableStateOf(initialDraft?.bodyText ?: "") }
    var draftType by remember { mutableStateOf(initialDraft?.draftType ?: DraftType.IDEA.name) }
    var tags by remember { mutableStateOf(initialDraft?.tags ?: "#Journal #Idea") }
    var targetSection by remember { mutableStateOf(initialDraft?.targetJournalSection ?: "Lead Editorial") }
    var quote by remember { mutableStateOf(initialDraft?.quoteAttribution ?: "") }
    var checklist by remember { mutableStateOf(initialDraft?.checklistItems ?: "") }
    var hasAudio by remember { mutableStateOf(initialDraft?.hasAudioNote ?: false) }
    var audioTranscript by remember { mutableStateOf(initialDraft?.audioTranscript ?: "") }
    var isRecordingAudio by remember { mutableStateOf(false) }
    var hasPhoto by remember { mutableStateOf(initialDraft?.hasPhotoAttachment ?: false) }
    var photoCaption by remember { mutableStateOf(initialDraft?.attachedPhotoCaption ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialDraft == null) "💡 New Multimedia Draft" else "✏️ Edit Journal Draft",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Draft Type Selector Chips
                Text(
                    text = "DRAFT TYPE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DraftType.values().forEach { type ->
                        val isSelected = draftType == type.name
                        FilterChip(
                            selected = isSelected,
                            onClick = { draftType = type.name },
                            label = { Text("${type.emoji} ${type.displayName}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                                selectedLabelColor = NeonCyan
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Draft Title / Idea Headline") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Draft Body / Investigation Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder
                    ),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Voice Memo Recorder Simulator
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, if (hasAudio) NeonCyan.copy(alpha = 0.4f) else DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (hasAudio) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (hasAudio) "Voice Note Transcribed" else "Record Audio Memo",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Button(
                                onClick = {
                                    if (isRecordingAudio) {
                                        isRecordingAudio = false
                                        hasAudio = true
                                        if (audioTranscript.isEmpty()) {
                                            audioTranscript = "Field investigation observation: Civic waterfront reconstruction is ahead of schedule."
                                        }
                                    } else {
                                        isRecordingAudio = true
                                        hasAudio = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRecordingAudio) Color(0xFFFF3B30) else NeonCyan.copy(alpha = 0.2f),
                                    contentColor = if (isRecordingAudio) Color.White else NeonCyan
                                ),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(if (isRecordingAudio) "Stop Recording" else if (hasAudio) "Re-record" else "Tap to Record")
                            }
                        }

                        if (hasAudio) {
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = audioTranscript,
                                onValueChange = { audioTranscript = it },
                                label = { Text("Voice Memo Transcript") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = DarkBorder
                                ),
                                maxLines = 2
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Target Journal Section & Tags
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetSection,
                        onValueChange = { targetSection = it },
                        label = { Text("Target Journal Section") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Tags") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quote,
                    onValueChange = { quote = it },
                    label = { Text("Notable Quote / Excerpt") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = checklist,
                    onValueChange = { checklist = it },
                    label = { Text("Checklist Tasks (one per line)") },
                    placeholder = { Text("[x] Interview archivist\n[ ] Collect photography\n[ ] Fact-check dates") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() || body.isNotBlank()) {
                                val draft = (initialDraft ?: NotepadDraftEntity(
                                    title = title,
                                    bodyText = body
                                )).copy(
                                    title = title,
                                    bodyText = body,
                                    draftType = draftType,
                                    targetJournalSection = targetSection,
                                    tags = tags,
                                    quoteAttribution = quote,
                                    checklistItems = checklist,
                                    audioTranscript = audioTranscript,
                                    audioMemoDurationSec = if (hasAudio) 42 else 0,
                                    attachedPhotoUrl = if (hasPhoto) "https://images.unsplash.com/photo-1497366216548-37526070297c?w=800&q=80" else "",
                                    attachedPhotoCaption = photoCaption
                                )
                                onSave(draft)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                        enabled = title.isNotBlank() || body.isNotBlank()
                    ) {
                        Text("Save Draft", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
