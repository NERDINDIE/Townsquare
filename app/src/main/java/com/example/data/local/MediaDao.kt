package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.JournalEditionEntity
import com.example.data.model.LocalBulletinEntity
import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaSpaceEntity
import com.example.data.model.RetailKioskEntity
import com.example.data.model.UpcomingEditionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY timestamp DESC")
    fun getAllMediaItems(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE type = :type ORDER BY timestamp DESC")
    fun getMediaItemsByType(type: String): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE type = :type ORDER BY timestamp DESC")
    suspend fun getMediaItemsListByType(type: String): List<MediaItemEntity>

    @Query("SELECT * FROM media_items WHERE channelId = :channelId ORDER BY timestamp DESC")
    fun getMediaItemsByChannel(channelId: String): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE spaceId = :spaceId ORDER BY timestamp DESC")
    fun getMediaItemsBySpace(spaceId: Long): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE isUserCreated = 1 ORDER BY timestamp DESC")
    fun getUserCreatedItems(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedItems(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE isSavedOffline = 1 OR isBookmarked = 1 ORDER BY timestamp DESC")
    fun getOfflineSavedItems(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE id = :id LIMIT 1")
    suspend fun getMediaItemById(id: Long): MediaItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(item: MediaItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(items: List<MediaItemEntity>)

    @Update
    suspend fun updateMediaItem(item: MediaItemEntity)

    @Query("UPDATE media_items SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :id")
    suspend fun updateLike(id: Long, isLiked: Boolean, likesCount: Int)

    @Query("UPDATE media_items SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Query("UPDATE media_items SET isSavedOffline = :isSaved WHERE id = :id")
    suspend fun updateSavedOffline(id: Long, isSaved: Boolean)

    @Query("UPDATE media_items SET sharesCount = sharesCount + 1 WHERE id = :id")
    suspend fun incrementShareCount(id: Long)

    @Query("SELECT COUNT(*) FROM media_items")
    suspend fun getMediaItemsCount(): Int

    // Channels
    @Query("SELECT * FROM media_channels ORDER BY isFollowed DESC, followersCount DESC")
    fun getAllChannels(): Flow<List<MediaChannelEntity>>

    @Query("SELECT * FROM media_channels WHERE isFollowed = 1")
    fun getFollowedChannels(): Flow<List<MediaChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<MediaChannelEntity>)

    @Query("UPDATE media_channels SET isFollowed = :isFollowed WHERE id = :channelId")
    suspend fun updateChannelFollow(channelId: String, isFollowed: Boolean)

    @Query("SELECT COUNT(*) FROM media_channels")
    suspend fun getChannelsCount(): Int

    // Media Spaces
    @Query("SELECT * FROM media_spaces ORDER BY createdTimestamp DESC")
    fun getAllSpaces(): Flow<List<MediaSpaceEntity>>

    @Query("SELECT * FROM media_spaces WHERE isOwner = 1 ORDER BY createdTimestamp DESC")
    fun getUserSpaces(): Flow<List<MediaSpaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpace(space: MediaSpaceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpaces(spaces: List<MediaSpaceEntity>)

    @Query("SELECT COUNT(*) FROM media_spaces")
    suspend fun getSpacesCount(): Int

    @Query("DELETE FROM media_spaces WHERE id = :spaceId")
    suspend fun deleteSpace(spaceId: Long)

    // Journal & Newspapers Archive
    @Query("SELECT * FROM journal_editions ORDER BY createdTimestamp DESC")
    fun getAllJournalEditions(): Flow<List<JournalEditionEntity>>

    @Query("SELECT * FROM journal_editions WHERE isArchived = 1 ORDER BY createdTimestamp DESC")
    fun getArchivedJournalEditions(): Flow<List<JournalEditionEntity>>

    @Query("SELECT * FROM journal_editions WHERE id = :id LIMIT 1")
    suspend fun getJournalEditionById(id: Long): JournalEditionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEdition(edition: JournalEditionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEditions(editions: List<JournalEditionEntity>)

    @Query("DELETE FROM journal_editions WHERE id = :id")
    suspend fun deleteJournalEdition(id: Long)

    @Query("SELECT COUNT(*) FROM journal_editions")
    suspend fun getJournalEditionsCount(): Int

    // Upcoming Editions Calendar
    @Query("SELECT * FROM upcoming_editions ORDER BY releaseDate ASC, releaseTime ASC")
    fun getAllUpcomingEditions(): Flow<List<UpcomingEditionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcomingEdition(edition: UpcomingEditionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcomingEditions(editions: List<UpcomingEditionEntity>)

    @Query("UPDATE upcoming_editions SET isReminderSet = :isReminderSet WHERE id = :id")
    suspend fun updateEditionReminder(id: String, isReminderSet: Boolean)

    @Query("UPDATE upcoming_editions SET isSubscribed = :isSubscribed WHERE id = :id")
    suspend fun updateEditionSubscription(id: String, isSubscribed: Boolean)

    @Query("SELECT COUNT(*) FROM upcoming_editions")
    suspend fun getUpcomingEditionsCount(): Int

    // Retail Kiosks & Press Stores
    @Query("SELECT * FROM retail_kiosks ORDER BY distanceMiles ASC")
    fun getAllRetailKiosks(): Flow<List<RetailKioskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailKiosks(kiosks: List<RetailKioskEntity>)

    @Query("UPDATE retail_kiosks SET reservedCopiesCount = :reservedCount, availableCopies = :availableCount WHERE id = :id")
    suspend fun updateKioskReservation(id: String, reservedCount: Int, availableCount: Int)

    @Query("UPDATE retail_kiosks SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateKioskFavorite(id: String, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM retail_kiosks")
    suspend fun getRetailKiosksCount(): Int

    // Local News Bulletins (Community Traffic, Weather, Events, Alerts)
    @Query("SELECT * FROM local_bulletins ORDER BY timestamp DESC")
    fun getAllBulletins(): Flow<List<LocalBulletinEntity>>

    @Query("SELECT * FROM local_bulletins WHERE category = :category ORDER BY timestamp DESC")
    fun getBulletinsByCategory(category: String): Flow<List<LocalBulletinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBulletin(bulletin: LocalBulletinEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBulletins(bulletins: List<LocalBulletinEntity>)

    @Query("UPDATE local_bulletins SET isUpvoted = :isUpvoted, upvotesCount = :upvotesCount WHERE id = :id")
    suspend fun updateBulletinUpvote(id: Long, isUpvoted: Boolean, upvotesCount: Int)

    @Query("DELETE FROM local_bulletins WHERE id = :id")
    suspend fun deleteBulletin(id: Long)

    @Query("SELECT COUNT(*) FROM local_bulletins")
    suspend fun getBulletinsCount(): Int

    // Townsquare Central Television (TCTV) Channels & Programmes
    @Query("SELECT * FROM tv_channels ORDER BY channelNumber ASC")
    fun getAllTvChannels(): Flow<List<com.example.data.model.TvChannelEntity>>

    @Query("SELECT * FROM tv_channels WHERE id = :id LIMIT 1")
    suspend fun getTvChannelById(id: String): com.example.data.model.TvChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvChannels(channels: List<com.example.data.model.TvChannelEntity>)

    @Query("UPDATE tv_channels SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateTvChannelFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE tv_channels SET isReminderSet = :isReminderSet WHERE id = :id")
    suspend fun updateTvChannelReminder(id: String, isReminderSet: Boolean)

    @Query("UPDATE tv_channels SET isRecording = :isRecording WHERE id = :id")
    suspend fun updateTvChannelRecording(id: String, isRecording: Boolean)

    @Query("SELECT COUNT(*) FROM tv_channels")
    suspend fun getTvChannelsCount(): Int

    // 24/7 Live Newsblog / Wire Feed
    @Query("SELECT * FROM live_newsblog_entries ORDER BY isPinned DESC, timestampMillis DESC")
    fun getAllNewsblogEntries(): Flow<List<com.example.data.model.LiveNewsblogEntity>>

    @Query("SELECT * FROM live_newsblog_entries WHERE categoryTag = :category ORDER BY isPinned DESC, timestampMillis DESC")
    fun getNewsblogEntriesByCategory(category: String): Flow<List<com.example.data.model.LiveNewsblogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsblogEntry(entry: com.example.data.model.LiveNewsblogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsblogEntries(entries: List<com.example.data.model.LiveNewsblogEntity>)

    @Query("UPDATE live_newsblog_entries SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :id")
    suspend fun updateNewsblogLike(id: Long, isLiked: Boolean, likesCount: Int)

    @Query("UPDATE live_newsblog_entries SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateNewsblogBookmark(id: Long, isBookmarked: Boolean)

    @Query("SELECT COUNT(*) FROM live_newsblog_entries")
    suspend fun getNewsblogCount(): Int

    // Multimedia Notepad Drafts (Journal Idea & Draft Holder)
    @Query("SELECT * FROM notepad_drafts ORDER BY isStarred DESC, updatedTimestamp DESC")
    fun getAllNotepadDrafts(): Flow<List<com.example.data.model.NotepadDraftEntity>>

    @Query("SELECT * FROM notepad_drafts WHERE id = :id LIMIT 1")
    suspend fun getNotepadDraftById(id: Long): com.example.data.model.NotepadDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotepadDraft(draft: com.example.data.model.NotepadDraftEntity): Long

    @Update
    suspend fun updateNotepadDraft(draft: com.example.data.model.NotepadDraftEntity)

    @Query("UPDATE notepad_drafts SET isStarred = :isStarred WHERE id = :id")
    suspend fun updateNotepadDraftStarred(id: Long, isStarred: Boolean)

    @Query("UPDATE notepad_drafts SET isConvertedToJournal = 1 WHERE id = :id")
    suspend fun markDraftConverted(id: Long)

    @Query("DELETE FROM notepad_drafts WHERE id = :id")
    suspend fun deleteNotepadDraft(id: Long)

    @Query("SELECT COUNT(*) FROM notepad_drafts")
    suspend fun getNotepadDraftsCount(): Int

    // Visual & Photojournalism Posts
    @Query("SELECT * FROM visual_posts ORDER BY timestampMillis DESC")
    fun getAllVisualPosts(): Flow<List<com.example.data.model.VisualPostEntity>>

    @Query("SELECT * FROM visual_posts WHERE category = :category ORDER BY timestampMillis DESC")
    fun getVisualPostsByCategory(category: String): Flow<List<com.example.data.model.VisualPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisualPosts(posts: List<com.example.data.model.VisualPostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisualPost(post: com.example.data.model.VisualPostEntity): Long

    @Query("UPDATE visual_posts SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :id")
    suspend fun updateVisualPostLike(id: Long, isLiked: Boolean, likesCount: Int)

    @Query("UPDATE visual_posts SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateVisualPostBookmark(id: Long, isBookmarked: Boolean)

    @Query("SELECT COUNT(*) FROM visual_posts")
    suspend fun getVisualPostsCount(): Int
}
