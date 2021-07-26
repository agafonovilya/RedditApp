package ru.agafonovilya.redditapp.ui

import ru.agafonovilya.redditapp.model.data.RedditPost

sealed class UiModel {
    data class PostDataItem(val post: RedditPost) : UiModel()
    data class SeparatorItem(val description: String) : UiModel() {
        companion object {
            val periodOfAdvertising: Int
                get() = 10
        }
    }
}
