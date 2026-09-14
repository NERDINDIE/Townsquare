package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.JournalEditionEntity
import com.example.data.model.LiveNewsblogEntity
import com.example.data.model.LocalBulletinEntity
import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaSpaceEntity
import com.example.data.model.NotepadDraftEntity
import com.example.data.model.RetailKioskEntity
import com.example.data.model.TvChannelEntity
import com.example.data.model.UpcomingEditionEntity
import com.example.data.model.VisualPostEntity

@Database(
    entities = [
        MediaItemEntity::class,
        MediaChannelEntity::class,
        MediaSpaceEntity::class,
        JournalEditionEntity::class,
        UpcomingEditionEntity::class,
        RetailKioskEntity::class,
        LocalBulletinEntity::class,
        TvChannelEntity::class,
        LiveNewsblogEntity::class,
        NotepadDraftEntity::class,
        VisualPostEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "townsquare.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
