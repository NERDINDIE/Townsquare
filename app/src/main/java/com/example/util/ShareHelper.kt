package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import com.example.data.model.JournalEditionEntity
import com.example.data.model.MediaItemEntity

object ShareHelper {

    fun shareMediaItem(context: Context, item: MediaItemEntity) {
        val typeLabel = when (item.type) {
            "NEWSPAPER_MAGAZINE" -> "📰 Townsquare Broadsheet / Magazine"
            "NEWSLETTER" -> "✉️ Townsquare Newsletter"
            "RADIO_STATION" -> "📻 Townsquare Radio Station"
            "PODCAST_EPISODE" -> "🎙️ Townsquare Audio Podcast"
            else -> "💬 Townsquare Dispatch"
        }

        val shareText = buildString {
            append(typeLabel)
            append("\n\n")
            append("\"${item.title}\"")
            if (item.subtitle.isNotBlank()) {
                append("\n— ${item.subtitle}")
            }
            append("\n\nBy ${item.authorName} (${item.authorHandle.ifBlank { "@townsquare" }})")
            append("\nChannel: ${item.channelName}")
            if (item.issueEdition.isNotBlank()) {
                append("\nEdition: ${item.issueEdition}")
            }
            append("\n\n")
            append(item.bodyText.take(320))
            if (item.bodyText.length > 320) append("...")
            append("\n\nRead the full dispatch on Townsquare — Civic Media Hub.")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, item.title)
            putExtra(Intent.EXTRA_SUBJECT, item.title)
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share \"${item.title}\" via")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareJournalEdition(context: Context, journal: JournalEditionEntity) {
        val shareText = buildString {
            append("🗞️ Townsquare Journal Press Edition\n\n")
            append("\"${journal.newspaperTitle}\"\n")
            append("Issue #${journal.issueNumber} • Volume ${journal.volumeNumber} (${journal.issueDate})\n")
            append("Motto: \"${journal.motto}\"\n\n")
            append("Lead Headline: ${journal.leadHeadline}\n")
            append("By ${journal.leadAuthor}\n\n")
            append(journal.leadArticleBody.take(280))
            if (journal.leadArticleBody.length > 280) append("...")
            append("\n\nCreated and published in Townsquare Journal Press.")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, journal.newspaperTitle)
            putExtra(Intent.EXTRA_SUBJECT, journal.newspaperTitle)
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share \"${journal.newspaperTitle}\" via")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareChannel(context: Context, channelName: String, channelDescription: String) {
        val shareText = buildString {
            append("📡 Townsquare Editorial Channel: $channelName\n\n")
            append(channelDescription)
            append("\n\nFollow this broadcast channel and read daily dispatches on Townsquare Media Hub.")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, channelName)
            putExtra(Intent.EXTRA_SUBJECT, channelName)
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share \"$channelName\" via")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareTvBroadcast(context: Context, channel: com.example.data.model.TvChannelEntity) {
        val shareText = buildString {
            append("📺 ${channel.networkTitle} • ${channel.name} (CH ${channel.channelNumber})\n\n")
            append("ON AIR NOW: \"${channel.currentShowTitle}\"\n")
            append("⏱️ ${channel.currentShowTime} • ${channel.resolutionBadge}\n")
            append("🎙️ ${channel.hostPresenter}\n\n")
            append(channel.currentShowSynopsis)
            append("\n\nWatch live streaming on Townsquare Media Superapp.")
        }
        shareText(context, "${channel.name} Live Broadcast", shareText)
    }

    fun shareBulletin(context: Context, bulletin: com.example.data.model.LocalBulletinEntity) {
        val shareText = buildString {
            append("📢 Townsquare Local Bulletin [${bulletin.category}]\n\n")
            append("${bulletin.iconEmoji} \"${bulletin.title}\"\n")
            append("📍 ${bulletin.locationName}\n")
            append("Urgency: ${bulletin.urgencyLevel}\n\n")
            append(bulletin.description)
            append("\n\nReported by ${bulletin.reporterName} (${bulletin.reporterHandle}) on Townsquare.")
        }
        shareText(context, bulletin.title, shareText)
    }

    fun sharePlainText(context: Context, title: String, content: String) {
        shareText(context, title, content)
    }

    fun shareText(context: Context, title: String, content: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share \"$title\" via")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
}
