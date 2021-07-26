package ru.agafonovilya.redditapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.agafonovilya.redditapp.model.repository.RedditRepository
import ru.agafonovilya.redditapp.ui.UiModel

class MainViewModel(private val repository: RedditRepository) : ViewModel() {
    private var currentSubredditValue: String? = null

    private var currentRequestResult: Flow<PagingData<UiModel>>? = null

    fun getRedditPosts(subredditString: String): Flow<PagingData<UiModel>> {
        var counter = 0

        val lastResult = currentRequestResult
        if (subredditString == currentSubredditValue && lastResult != null) {
            return lastResult
        }
        currentSubredditValue = subredditString

        val newResult: Flow<PagingData<UiModel>> = repository.getRedditPostsStream(subredditString)
            .map { pagingData -> pagingData.map { UiModel.PostDataItem(it) } }
            .map {
                it.insertSeparators {  before, after->
                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }

                    if (before == null) {
                        // we're at the beginning of the list
                        return@insertSeparators UiModel.SeparatorItem("Place for your advertisement")
                    }

                    if (counter == UiModel.SeparatorItem.periodOfAdvertising) {
                        counter = 0
                        return@insertSeparators UiModel.SeparatorItem("Place for your advertisement")
                    } else {
                        counter++
                    }

                    return@insertSeparators null
                }
            }
            .cachedIn(viewModelScope)
        currentRequestResult = newResult
        return newResult
    }
}

class ViewModelFactory(private val repository: RedditRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}