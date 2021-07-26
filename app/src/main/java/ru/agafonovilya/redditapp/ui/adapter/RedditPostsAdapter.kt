package ru.agafonovilya.redditapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.agafonovilya.redditapp.R
import ru.agafonovilya.redditapp.databinding.RedditPostItemBinding
import ru.agafonovilya.redditapp.model.data.RedditPost
import ru.agafonovilya.redditapp.ui.UiModel

class RedditPostsAdapter(private val onItemClickListener: (RedditPost) -> Unit) :
    PagingDataAdapter<UiModel, ViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.reddit_post_item) {
            val binding =
                RedditPostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RedditPostViewHolder.create(binding, onItemClickListener)
        } else {
            SeparatorViewHolder.create(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.PostDataItem -> R.layout.reddit_post_item
            is UiModel.SeparatorItem -> R.layout.separator_view_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.PostDataItem -> (holder as RedditPostViewHolder).bind(uiModel.post)
                is UiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(uiModel.description)
            }
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.PostDataItem && newItem is UiModel.PostDataItem &&
                        oldItem.post.title == newItem.post.title) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem &&
                                oldItem.description == newItem.description)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }
}
