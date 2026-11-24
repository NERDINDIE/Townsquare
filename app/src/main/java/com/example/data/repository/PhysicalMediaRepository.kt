package com.example.data.repository

import com.example.data.model.DigitalFingerprint
import com.example.data.model.PhysicalMediaFormat
import com.example.data.model.PhysicalMediaItem
import com.example.data.model.ProofOfPurchase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object PhysicalMediaRepository {
    private val initialVaultItems = listOf(
        PhysicalMediaItem(
            id = "pm_01",
            title = "Abbey Road Vinyl Master Edition",
            creatorOrArtist = "The Beatles",
            format = PhysicalMediaFormat.VINYL,
            description = "Recorded directly via modern Hi-Fi turntable line input. 180g Vinyl Pressing.",
            tracksOrPages = listOf("Come Together", "Something", "Maxwell's Silver Hammer", "Oh! Darling", "Octopus's Garden"),
            totalDurationOrPages = "47:23",
            isPrivate = true,
            proofOfPurchase = ProofOfPurchase(
                isVerified = true,
                receiptId = "RCP-98402-UK",
                storeName = "HMV Record Store London",
                verifiedAtTimestamp = System.currentTimeMillis() - 86400000L
            ),
            fingerprint = DigitalFingerprint(
                originalUploaderId = "usr_me",
                originalUploaderName = "My Vault",
                isDownloadedFromElsewhere = false,
                derivativePercentage = 0f,
                isResharePermitted = true
            )
        ),
        PhysicalMediaItem(
            id = "pm_02",
            title = "Interstellar 4K Ultra HD Collector Disc",
            creatorOrArtist = "Christopher Nolan",
            format = PhysicalMediaFormat.DISC_MOVIE,
            description = "Direct profile disc rip archive from original optical Blu-Ray disc.",
            tracksOrPages = listOf("Main Theme", "Wormhole Traversal", "Miller's Planet", "No Time For Caution", "Tesseract"),
            totalDurationOrPages = "169 mins",
            isPrivate = true,
            proofOfPurchase = ProofOfPurchase(
                isVerified = true,
                receiptId = "RCP-11928-US",
                storeName = "Best Buy Cinema Hub",
                verifiedAtTimestamp = System.currentTimeMillis() - 172800000L
            ),
            fingerprint = DigitalFingerprint(
                originalUploaderId = "usr_me",
                originalUploaderName = "My Vault",
                isDownloadedFromElsewhere = false,
                derivativePercentage = 0f,
                isResharePermitted = true
            )
        ),
        PhysicalMediaItem(
            id = "pm_03",
            title = "Dune: Expanded Universe Hardcover Scan",
            creatorOrArtist = "Frank Herbert",
            format = PhysicalMediaFormat.BOOK_PDF,
            description = "Scanned physical book replica converted to interactive PDF & audiobook text.",
            tracksOrPages = listOf("Chapter 1: Caladan", "Chapter 2: Arrakis Arrival", "Chapter 3: Fremen Lore"),
            totalDurationOrPages = "412 Pages",
            isPrivate = true,
            proofOfPurchase = ProofOfPurchase(
                isVerified = true,
                receiptId = "RCP-77319-BK",
                storeName = "Townsquare Bookstore",
                verifiedAtTimestamp = System.currentTimeMillis() - 259200000L
            ),
            fingerprint = DigitalFingerprint(
                originalUploaderId = "usr_me",
                originalUploaderName = "My Vault",
                isDownloadedFromElsewhere = false,
                derivativePercentage = 0f,
                isResharePermitted = true
            )
        ),
        PhysicalMediaItem(
            id = "pm_04",
            title = "Dark Side of the Moon (Live Community Copy)",
            creatorOrArtist = "Pink Floyd (Shared by @AudioPhile_Dave)",
            format = PhysicalMediaFormat.VINYL,
            description = "Community shared vinyl capture downloaded into personal player.",
            tracksOrPages = listOf("Speak to Me", "Breathe", "On the Run", "Time", "The Great Gig in the Sky"),
            totalDurationOrPages = "43:09",
            isPrivate = false,
            isCommunityShared = true,
            proofOfPurchase = ProofOfPurchase(
                isVerified = true,
                receiptId = "RCP-COMM-991",
                storeName = "Verified Vinyl Vault",
                verifiedAtTimestamp = System.currentTimeMillis() - 345600000L
            ),
            fingerprint = DigitalFingerprint(
                fingerprintHash = "FP-COMM-9921X",
                originalUploaderId = "usr_dave99",
                originalUploaderName = "@AudioPhile_Dave",
                isDownloadedFromElsewhere = true,
                derivativePercentage = 15f,
                isResharePermitted = false // BLOCKED: Needs >50% derivative work
            )
        )
    )

    private val _vaultItems = MutableStateFlow<List<PhysicalMediaItem>>(initialVaultItems)
    val vaultItems: StateFlow<List<PhysicalMediaItem>> = _vaultItems.asStateFlow()

    fun addItem(item: PhysicalMediaItem) {
        _vaultItems.value = listOf(item) + _vaultItems.value
    }

    fun toggleCommunitySharing(itemId: String, share: Boolean) {
        _vaultItems.value = _vaultItems.value.map { item ->
            if (item.id == itemId) {
                item.copy(
                    isPrivate = !share,
                    isCommunityShared = share
                )
            } else item
        }
    }

    fun verifyPurchase(itemId: String, receiptId: String, storeName: String) {
        _vaultItems.value = _vaultItems.value.map { item ->
            if (item.id == itemId) {
                item.copy(
                    proofOfPurchase = ProofOfPurchase(
                        isVerified = true,
                        receiptId = receiptId,
                        storeName = storeName,
                        verifiedAtTimestamp = System.currentTimeMillis()
                    )
                )
            } else item
        }
    }

    fun downloadCommunityMedia(originalItem: PhysicalMediaItem): PhysicalMediaItem {
        val downloadedItem = originalItem.copy(
            id = "downloaded_${UUID.randomUUID().toString().take(8)}",
            isPrivate = true, // Off to public sharing by default
            isCommunityShared = false,
            fingerprint = DigitalFingerprint(
                fingerprintHash = "FP-${UUID.randomUUID().toString().take(8).uppercase()}",
                originalUploaderId = originalItem.fingerprint.originalUploaderId.ifBlank { "community_user" },
                originalUploaderName = originalItem.creatorOrArtist,
                isDownloadedFromElsewhere = true,
                downloadedAtTimestamp = System.currentTimeMillis(),
                derivativePercentage = 0f,
                isResharePermitted = false // Must create >50% derivative work to reshare
            )
        )
        addItem(downloadedItem)
        return downloadedItem
    }

    fun applyDerivativeWork(itemId: String, derivativeScore: Float) {
        _vaultItems.value = _vaultItems.value.map { item ->
            if (item.id == itemId) {
                val isPermitted = derivativeScore >= 50f || !item.fingerprint.isDownloadedFromElsewhere
                item.copy(
                    fingerprint = item.fingerprint.copy(
                        derivativePercentage = derivativeScore,
                        isResharePermitted = isPermitted
                    )
                )
            } else item
        }
    }
}
