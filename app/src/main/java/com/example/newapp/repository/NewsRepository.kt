package com.example.newapp.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newapp.api.NewsApi
import com.example.newapp.model.Article
import com.example.newapp.paging.BreakingPagingSource
import com.example.newapp.paging.SearchNewsPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsRepository @Inject constructor(private val newsApi : NewsApi) {

    fun getBreakingNews(): Flow<PagingData<Article>>{
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { BreakingPagingSource(newsApi) }
        ).flow
    }

    fun getSearchNews(query: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchNewsPagingSource(newsApi, query) }
        ).flow
    }
}