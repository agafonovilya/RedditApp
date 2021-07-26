package ru.agafonovilya.redditapp.model.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.agafonovilya.redditapp.api.RedditService
import ru.agafonovilya.redditapp.model.db.RedditDatabase
import ru.agafonovilya.redditapp.model.db.RedditPostDao
import ru.agafonovilya.redditapp.model.db.SubredditRemoteKeyDao
import java.io.IOException

@ExperimentalPagingApi
class RedditRemoteMediator(
    private val db: RedditDatabase,
    private val remoteProvider: RedditService,
    private val subredditName: String
) :
    RemoteMediator<Int, RedditPost>() {

    private val postDao: RedditPostDao = db.postsDao()
    private val remoteKeyDao: SubredditRemoteKeyDao = db.remoteKeys()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RedditPost>
    ): MediatorResult {
        try {
            // Get the closest item from PagingState that we want to load data around.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.withTransaction {
                        remoteKeyDao.remoteKeyByPost(subredditName)
                    }
                    if (remoteKey.nextPageKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextPageKey
                }
            }

            val data = remoteProvider.getHotList(
                subreddit = subredditName,
                after = loadKey,
                limit = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            ).data

            val items = data.children.map { it.data }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDao.deleteBySubreddit(subredditName)
                    remoteKeyDao.deleteBySubreddit(subredditName)
                }

                remoteKeyDao.insert(SubredditRemoteKey(subredditName, data.after))
                postDao.insertAll(items)
            }

            return MediatorResult.Success(endOfPaginationReached = items.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}