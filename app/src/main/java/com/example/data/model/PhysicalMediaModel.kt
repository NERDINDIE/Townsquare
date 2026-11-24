package com.example.data.model

import java.util.UUID

enum class PhysicalMediaFormat(val label: String, val iconEmoji: String, val defaultAppTarget: String) {
    VINYL("Vinyl Record", "💿", "Waves"),
    CASSETTE_TAPE("Cassette Tape", "📻", "Waves"),
    DISC_ALBUM("Music CD Disc", "📀", "Waves"),
    DISC_MOVIE("Movie DVD / Blu-Ray", "📀", "Stream"),
    VHS_TAPE("VHS Video Tape", "📼", "Stream"),
    MOVIE_REEL("35mm / 8mm Theatre Reel", "🎞️", "Stream"),
    BOOK_PDF("Physical Book Scan", "📚", "Audiobook Replica")
}

data class DigitalFingerprint(
    val fingerprintHash: String = UUID.randomUUID().toString().take(12).uppercase(),
    val originalUploaderId: String = "usr_creator_01",
    val originalUploaderName: String = "Original Curator",
    val isDownloadedFromElsewhere: Boolean = false,
    val downloadedAtTimestamp: Long = System.currentTimeMillis(),
    val derivativePercentage: Float = 0f, // 0 to 100%
    val isResharePermitted: Boolean = true
)

data class ProofOfPurchase(
    val isVerified: Boolean = false,
    val receiptId: String? = null,
    val verifiedAtTimestamp: Long? = null,
    val storeName: String? = null,
    val verificationBadgeLabel: String = "Townsquare Verified Purchase 🛡️"
)

data class PhysicalMediaItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val creatorOrArtist: String,
    val format: PhysicalMediaFormat,
    val coverArtUrl: String = "",
    val description: String = "",
    val tracksOrPages: List<String> = emptyList(),
    val totalDurationOrPages: String = "45:00",
    val isPrivate: Boolean = true,
    val proofOfPurchase: ProofOfPurchase = ProofOfPurchase(),
    val fingerprint: DigitalFingerprint = DigitalFingerprint(),
    val audioSampleUrl: String = "",
    val isCommunityShared: Boolean = false,
    val downloadCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
