package ru.agafonovilya.redditapp.model.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "redditPosts",
    indices = [Index(value = ["subreddit"], unique = false)])
data class RedditPost(
    @PrimaryKey @field:SerializedName("name") val name: String,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("score") val score: Int,
    @field:SerializedName("author") val author: String,
    @field:SerializedName("subreddit") val subreddit: String,
    @field:SerializedName("num_comments") val num_comments: Int,
    @field:SerializedName("created_utc") val created: Long,
    @field:SerializedName("thumbnail") val thumbnail: String?,

    var indexInResponse: Int = -1
)
