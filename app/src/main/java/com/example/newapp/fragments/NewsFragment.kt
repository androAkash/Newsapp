package com.example.newapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newapp.R
import com.example.newapp.adapter.NewsLoadStateAdapter
import com.example.newapp.adapter.PagingNewsAdapter
import com.example.newapp.databinding.FragmentNewsBinding
import com.example.newapp.model.Article
import com.example.newapp.viewModel.NewViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private lateinit var binding : FragmentNewsBinding
    private lateinit var pagingNewsAdapter: PagingNewsAdapter
    private val viewModel: NewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.btRetry.setOnClickListener { pagingNewsAdapter.retry() }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.breakingNews.collect { articles ->
                    pagingNewsAdapter.submitData(articles)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycleScope.launch {
                pagingNewsAdapter.loadStateFlow.collect { newsLoadState ->
                    when (newsLoadState.source.refresh) {
                        is LoadState.Error -> {
                            binding.apply {
                                progressBar.visibility = View.INVISIBLE
                                btRetry.visibility = View.VISIBLE
                                tvError.visibility = View.VISIBLE
                            }
                        }
                        is LoadState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is LoadState.NotLoading -> {
                            binding.apply {
                                progressBar.visibility = View.INVISIBLE
                                btRetry.visibility = View.INVISIBLE
                                tvError.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }
        }
    }
    private fun setupRecyclerView() {
        pagingNewsAdapter = PagingNewsAdapter { article ->
            onClick(article)
        }
        binding.rvBreakingNews.apply {
            adapter = pagingNewsAdapter.withLoadStateFooter(NewsLoadStateAdapter())
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun onClick(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article", article)
        }
        findNavController().navigate(
            R.id.action_newsFragment_to_articleFragment,
            bundle
        )
    }
}