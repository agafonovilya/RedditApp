package ru.agafonovilya.redditapp.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.agafonovilya.redditapp.model.data.RedditPost
import ru.agafonovilya.redditapp.model.data.SubredditRemoteKey

@Database(
    entities = [RedditPost::class, SubredditRemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class RedditDatabase : RoomDatabase() {
    companion object {

        @Volatile
        private var INSTANCE: RedditDatabase? = null

        fun getInstance(context: Context): RedditDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                RedditDatabase::class.java,
                "Reddit.db"
            ).build()

    }

    abstract fun postsDao(): RedditPostDao
    abstract fun remoteKeys(): SubredditRemoteKeyDao
}