package com.example.newsApp.repository

import com.example.newsApp.api.RetrofitInstance

class NewsRepository(

) {
    suspend fun getBreakingNews(countryCode: String , pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode , pageNumber)

    suspend fun searchNews(searchQuery: String , pagingNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery , pagingNumber)
}
