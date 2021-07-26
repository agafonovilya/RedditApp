package ru.agafonovilya.redditapp

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import ru.agafonovilya.redditapp.api.RedditService
import ru.agafonovilya.redditapp.model.db.RedditDatabase
import ru.agafonovilya.redditapp.model.repository.RedditRepository
import ru.agafonovilya.redditapp.utils.imageloader.GlideImageLoader
import ru.agafonovilya.redditapp.viewmodel.ViewModelFactory

object Injection {

    /**
     * Creates an instance of [RedditRepository] based on the [RedditService] and a
     * [RedditDatabase]
     */
    private fun provideRedditRepository(context: Context): RedditRepository {
        return RedditRepository(RedditService.create(), RedditDatabase.getInstance(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [MainViewModel] objects.
     */
    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideRedditRepository(context))
    }

    /**
     * Provides the image loader.
     */
    fun provideImageLoader() = GlideImageLoader()

}