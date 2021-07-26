package ru.agafonovilya.redditapp.model.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import ru.agafonovilya.redditapp.api.RedditService
import ru.agafonovilya.redditapp.model.data.RedditPost
import ru.agafonovilya.redditapp.model.data.RedditRemoteMediator
import ru.agafonovilya.redditapp.model.db.RedditDatabase

class RedditRepository (private val service: RedditService, private val database: RedditDatabase) {

    /**
     * Search subreddits whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    @OptIn(ExperimentalPagingApi::class)
    fun getRedditPostsStream(subreddit: String): Flow<PagingData<RedditPost>> = Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = RedditRemoteMediator(database, service, subreddit)
        ){
            database.postsDao().postsBySubreddit(subreddit)
        }.flow


    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}
