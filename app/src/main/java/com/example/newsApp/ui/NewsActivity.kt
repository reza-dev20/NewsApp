package com.example.newsApp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsApp.R
import com.example.newsApp.db.ArticleDatabase
import com.example.newsApp.repository.NewsRepository
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory=NewsViewModelProviderFactory(newsRepository)
        viewModel= ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)
        setContentView(R.layout.activity_news)
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

    }
}