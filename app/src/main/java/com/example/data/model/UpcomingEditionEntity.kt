package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upcoming_editions")
data class UpcomingEditionEntity(
    @PrimaryKey
    val id: String,
    val publicationTitle: String,
    val channelId: String,
    val editionType: String, // "BROADSHEET", "MAGAZINE", "WEEKEND_SPECIAL", "INVESTIGATION"
    val volumeIssue: String,
    val releaseDate: String, // e.g. "2026-09-11"
    val releaseDayLabel: String, // e.g. "Tomorrow • Friday"
    val releaseTime: String, // e.g. "06:00 AM"
    val coverHeadline: String,
    val leadTeaser: String,
    val editorNotes: String,
    val bannerColorHex: Long = 0xFF00D2FF,
    val isSubscribed: Boolean = true,
    val isReminderSet: Boolean = false,
    val specialSection: String = "Front Page Edition"
)
