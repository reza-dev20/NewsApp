package com.example.newsApp.repository

import com.example.newsApp.api.RetrofitInstance
import com.example.newsApp.db.ArticleDao
import com.example.newsApp.db.ArticleDatabase
import com.example.newsApp.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {
    private val dao: ArticleDao = db.getArticleDao()

    suspend fun getBreakingNews(countryCode: String , pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode , pageNumber)

    suspend fun searchNews(searchQuery: String , pagingNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery , pagingNumber)

    suspend fun upsert(article: Article)=
        dao.upsert(article)

    fun getSavedNews()=
        dao.getAllArticles()

    suspend fun deleteArticle(article: Article)=
        dao.deleteArticle(article)
}
