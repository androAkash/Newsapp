package com.example.newapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newapp.api.NewsApi
import com.example.newapp.model.Article

class SearchNewsPagingSource(private val newsApi: NewsApi,private val query : String):PagingSource<Int,Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: 1
        return try {
            val response = newsApi.searchForNews(query, position)
            val news = response.articles
            LoadResult.Page(
                data = response.articles,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (news.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}