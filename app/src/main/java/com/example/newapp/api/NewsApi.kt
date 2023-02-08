package com.example.newapp.api

import com.example.newapp.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String,
        @Query("page")
        pageNumber: Int,
        @Query("apiKey")
        apiKey: String = "9842e66548144e379bdf869ee338e359"
    ): NewsResponse

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int,
        @Query("pageSize")
        pageSize: Int = 20,
        @Query("apiKey")
        apiKey: String = "9842e66548144e379bdf869ee338e359"
    ): NewsResponse
}