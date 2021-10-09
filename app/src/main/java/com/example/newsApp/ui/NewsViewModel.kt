package com.example.newsApp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsApp.models.Article
import com.example.newsApp.models.NewsResponse
import com.example.newsApp.repository.NewsRepository
import com.example.newsApp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository) : ViewModel() {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakNewsResponse: NewsResponse?=null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResource: NewsResponse?=null

    init {
        getBreakingNews("us")
    }
    fun searchNews(searchQuery: String)=viewModelScope.launch {
        searchNews.value = Resource.Loading()
        val response=newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.value = handleSearchNewsResponse(response)
    }

    fun getBreakingNews(countryCode: String)=viewModelScope.launch {
        breakingNews.value = Resource.Loading()
        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
        breakingNews.value = handleBreakingNewsResponse(response)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++;
                if (breakNewsResponse==null){
                    breakNewsResponse=resultResponse
                }
                else{
                    val oldArticle=breakNewsResponse?.articles
                    val newArticle=resultResponse.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(breakNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article)= viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews()=newsRepository.getSavedNews()

    fun deleteArticle(article: Article)=viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

}