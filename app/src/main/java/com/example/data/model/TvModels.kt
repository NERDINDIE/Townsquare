package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tv_channels")
data class TvChannelEntity(
    @PrimaryKey val id: String, // e.g. "tctv_ch1"
    val channelNumber: Int, // 1 to 6
    val name: String, // "First Programme", "Second Programme", ...
    val networkTitle: String = "Townsquare Central Television",
    val callsign: String, // "TCTV-1"
    val tagline: String,
    val category: String, // News, Arts, Science, Sports, Drama, Youth
    val themeColorHex: Long,
    val iconEmoji: String,
    val resolutionBadge: String = "4K UHD 60FPS",
    val audioFormat: String = "Dolby 5.1 Surround",
    val signalStrength: String = "100% High Definition (MUX-A)",
    val currentShowTitle: String,
    val currentShowCategory: String,
    val currentShowTime: String, // "19:00 - 20:00"
    val currentShowSynopsis: String,
    val currentShowProgress: Float = 0.42f,
    val hostPresenter: String,
    val liveViewersCount: Int = 14200,
    val isFavorite: Boolean = false,
    val isRecording: Boolean = false,
    val isReminderSet: Boolean = false,
    val nextShowTitle: String,
    val nextShowTime: String,
    val scheduleItemsJson: String = "",
    val onDemandItemsJson: String = ""
)

data class TvScheduleItem(
    val timeSlot: String,
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val synopsis: String,
    val isLiveNow: Boolean = false,
    val isPrimeTime: Boolean = false
)

data class TvOnDemandItem(
    val id: String,
    val channelNumber: Int,
    val title: String,
    val episodeInfo: String,
    val category: String,
    val duration: String,
    val description: String,
    val badge: String = "HD",
    val viewsCount: String = "32K views",
    val airDate: String = "Yesterday"
)
