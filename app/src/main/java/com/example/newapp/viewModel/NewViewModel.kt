package com.example.newapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newapp.model.Article
import com.example.newapp.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewViewModel @Inject
constructor(private val newsRepository: NewsRepository)
    :ViewModel() {

    private val _breakingNews = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val breakingNews = _breakingNews.asStateFlow()

    private val _searchNews = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val searchNews = _searchNews.asStateFlow()

    init {
        getBreakingNews()
    }

    private fun getBreakingNews() = viewModelScope.launch {
        newsRepository.getBreakingNews()
            .cachedIn(viewModelScope)
            .collect { articles ->
                _breakingNews.value = articles
            }
    }

    fun getSearchNews(query: String) = viewModelScope.launch {
        newsRepository.getSearchNews(query)
            .cachedIn(viewModelScope)
            .collect { articles ->
                _searchNews.value = articles
            }
    }
}