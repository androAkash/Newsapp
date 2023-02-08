package com.example.newapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newapp.R
import com.example.newapp.adapter.NewsLoadStateAdapter
import com.example.newapp.adapter.PagingNewsAdapter
import com.example.newapp.databinding.FragmentSearchBinding
import com.example.newapp.model.Article
import com.example.newapp.viewModel.NewViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {
    private val viewModel: NewViewModel by activityViewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var pagingNewsAdapter: PagingNewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.btRetry.setOnClickListener { pagingNewsAdapter.retry() }

        binding.svSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.getSearchNews(query)
                }
                binding.svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.searchNews.collectLatest { articles ->
                    pagingNewsAdapter.apply {
                        submitData(PagingData.empty())
                        submitData(articles)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycleScope.launch {
                pagingNewsAdapter.loadStateFlow.collectLatest { newsLoadState ->
                    when (newsLoadState.source.refresh) {
                        is LoadState.Error -> {
                            binding.apply {
                                progressBar.visibility = View.INVISIBLE
                                btRetry.visibility = View.VISIBLE
                                tvError.visibility = View.VISIBLE
                                rvSearchNews.visibility = View.INVISIBLE
                            }
                        }
                        is LoadState.Loading -> {
                            binding.apply {
                                rvSearchNews.visibility = View.INVISIBLE
                                progressBar.visibility = View.VISIBLE
                            }
                        }
                        is LoadState.NotLoading -> {
                            binding.apply {
                                progressBar.visibility = View.INVISIBLE
                                btRetry.visibility = View.INVISIBLE
                                tvError.visibility = View.INVISIBLE
                                rvSearchNews.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        pagingNewsAdapter = PagingNewsAdapter{ article ->
            onClick(article)
        }
        binding.rvSearchNews.apply {
            adapter = pagingNewsAdapter.withLoadStateFooter(NewsLoadStateAdapter())
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun onClick(article: Article){
        val bundle = Bundle().apply {
            putSerializable("article", article)
        }
    }

}