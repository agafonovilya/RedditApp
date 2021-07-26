package ru.agafonovilya.redditapp.model.db

import androidx.paging.PagingSource
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import ru.agafonovilya.redditapp.model.data.RedditPost

@Dao
interface RedditPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<RedditPost>)

    @Query("SELECT * FROM redditPosts WHERE subreddit = :subreddit ORDER BY indexInResponse ASC")
    fun postsBySubreddit(subreddit: String): PagingSource<Int, RedditPost>

    @Query("DELETE FROM redditPosts WHERE subreddit = :subreddit")
    suspend fun deleteBySubreddit(subreddit: String)

    @Query("SELECT MAX(indexInResponse) + 1 FROM redditPosts WHERE subreddit = :subreddit")
    suspend fun getNextIndexInSubreddit(subreddit: String): Int
}

