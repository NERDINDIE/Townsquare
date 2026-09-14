package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "retail_kiosks")
data class RetailKioskEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // "Heritage Kiosk", "Transit Newsstand", "Art & Press Bookshop", "Corner Stand"
    val address: String,
    val district: String,
    val distanceMiles: Float,
    val walkingMinutes: Int,
    val openingHours: String,
    val isOpenNow: Boolean = true,
    val mapX: Float, // 0f..1f relative coordinate on custom city map
    val mapY: Float, // 0f..1f relative coordinate on custom city map
    val phoneNumber: String,
    val carriedPublicationTitles: String, // comma-separated titles carried
    val stockStatus: String,
    val availableCopies: Int,
    val isFavorite: Boolean = false,
    val reservedCopiesCount: Int = 0
)
