package ru.agafonovilya.redditapp.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.agafonovilya.redditapp.Injection
import ru.agafonovilya.redditapp.R
import ru.agafonovilya.redditapp.databinding.RedditPostItemBinding
import ru.agafonovilya.redditapp.model.data.RedditPost

class RedditPostViewHolder(
    private val binding: RedditPostItemBinding,
    private val onItemClickListener: (RedditPost) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val imageLoader = Injection.provideImageLoader()

    fun bind(post: RedditPost) {
        binding.title.text = post.title
        binding.score.text = post.score.toString()
        binding.subtitle.text = itemView.context.resources.getString(
            R.string.post_subtitle,
            post.author ?: "unknown"
        )
        binding.root.setOnClickListener { onItemClickListener(post) }

        if (post.thumbnail.isNullOrBlank()) {
            binding.thumbnail.visibility = View.GONE
        } else {
            post.thumbnail.let { imageLoader.loadInto(it, binding.thumbnail, onLoadImageErrorCallback()) }
        }
    }

    private fun onLoadImageErrorCallback(): () -> Unit = {
        binding.thumbnail.visibility = View.GONE
    }


    companion object {
        fun create(
            binding: RedditPostItemBinding,
            onItemClickListener: (RedditPost) -> Unit
        ): RedditPostViewHolder {
            return RedditPostViewHolder(binding, onItemClickListener)
        }
    }
}