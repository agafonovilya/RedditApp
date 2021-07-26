package ru.agafonovilya.redditapp.api

import com.google.gson.annotations.SerializedName
import ru.agafonovilya.redditapp.model.data.RedditPost

data class RedditResponse (
    @SerializedName("kind") val kind: String,
    @SerializedName("data") val data: Data
)

data class Data (
    @field:SerializedName("after") val after: String?,
    @field:SerializedName("dist") val dist: Long,
    @field:SerializedName("modhash") val modhash: String,
    @field:SerializedName("geo_filter") val geoFilter: Any? = null,
    @field:SerializedName("children") val children: List<PostData>,
)

data class PostData (
    @SerializedName("kind") val kind : String,
    @SerializedName("data") val data : RedditPost
)