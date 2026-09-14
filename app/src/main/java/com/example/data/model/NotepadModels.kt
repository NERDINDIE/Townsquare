package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DraftType(val displayName: String, val emoji: String, val defaultColorHex: Long) {
    IDEA("Quick Idea", "💡", 0xFFFFD60A),
    ARTICLE_DRAFT("Article Draft", "✍️", 0xFF00D2FF),
    VOICE_MEMO("Voice Memo", "🎙️", 0xFFFF375F),
    PHOTO_STORY("Photo Story", "📷", 0xFF30D158),
    QUOTE_SNIPPET("Quote Excerpt", "💬", 0xFFBF5AF2),
    CHECKLIST("Investigation Checklist", "📋", 0xFFFF9F0A)
}

enum class TargetJournalSection(val displayName: String) {
    LEAD_STORY("Lead Frontpage Story"),
    SECOND_FEATURE("Secondary Feature"),
    OPINION("Editorial & Opinion Column"),
    LOCAL_NOTICES("Community Bulletin & Notices"),
    PHOTO_ESSAY("Centerfold Photo Essay"),
    FIELD_REPORT("Field Reporting Dispatch")
}

@Entity(tableName = "notepad_drafts")
data class NotepadDraftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val bodyText: String = "",
    val draftType: String = "IDEA", // from DraftType.name
    val tags: String = "Editorial,Draft",
    val accentColorHex: Long = 0xFF00D2FF,
    val audioMemoDurationSec: Int = 0,
    val audioTranscript: String = "",
    val attachedPhotoUrl: String = "",
    val attachedPhotoCaption: String = "",
    val quoteAttribution: String = "",
    val targetJournalSection: String = "LEAD_STORY",
    val checklistItems: String = "", // newline separated or pipe separated items
    val isStarred: Boolean = false,
    val isConvertedToJournal: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
)
