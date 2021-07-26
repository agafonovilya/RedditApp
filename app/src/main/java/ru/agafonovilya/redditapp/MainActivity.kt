package ru.agafonovilya.redditapp

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.agafonovilya.redditapp.databinding.ActivityMainBinding
import ru.agafonovilya.redditapp.model.data.RedditPost
import ru.agafonovilya.redditapp.ui.adapter.RedditPostsAdapter
import ru.agafonovilya.redditapp.ui.adapter.ReposLoadStateAdapter
import ru.agafonovilya.redditapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LAST_SEARCH_SUBREDDIT: String = "last_search_subreddit"
        private const val DEFAULT_SEARCH = "Android"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: RedditPostsAdapter

    private var requestPostsJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initAdapter()

        val query = savedInstanceState?.getString(LAST_SEARCH_SUBREDDIT) ?: DEFAULT_SEARCH
        search(query)
        initSearch(query)

        binding.retryButton.setOnClickListener { adapter.retry() }

    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(this)
        ).get(MainViewModel::class.java)
    }

    private fun initAdapter() {
        adapter = RedditPostsAdapter(onItemClickListener())
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = ReposLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)

            // Only show the list if refresh succeeds.
            binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this,
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun search(query: String) {
        requestPostsJob?.cancel()
        requestPostsJob = lifecycleScope.launch {
            viewModel.getRedditPosts(query).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun initSearch(query: String) {
        binding.input.setText(query)

        binding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }

        binding.input.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.recyclerView.scrollToPosition(0) }
        }
    }

    private fun updateRepoListFromInput() {
        binding.input.text?.trim().let {
            if (it != null && it.isNotEmpty()) {
                    search(it.toString())
                }
            }

    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }


    private fun onItemClickListener(): (RedditPost) -> Unit = {
        Toast.makeText(this, it.title, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_SUBREDDIT, binding.input.text?.trim().toString())
    }

}

