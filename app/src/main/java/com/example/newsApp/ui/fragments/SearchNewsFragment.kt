package com.example.newsApp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsApp.R
import com.example.newsApp.adapters.NewsAdapter
import com.example.newsApp.models.Article
import com.example.newsApp.ui.NewsActivity
import com.example.newsApp.ui.NewsViewModel
import com.example.newsApp.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private val TAG: String = "breaking news"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel = (activity as NewsActivity).viewModel

        newsAdapter.setOnItemClickListener{ article: Article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        val itemTouchHelperCallback =object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view , "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvSearchNews)


        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job= MainScope().launch {
                delay(1200L)
                editable?.let {
                    if (editable.toString().isNotEmpty()){
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner , Observer { searchResponse ->
            when(searchResponse){
                is Resource.Success ->{
                    hideProgressBar()
                    searchResponse.data?.let { response ->
                        newsAdapter.differ.submitList(response.articles)
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    searchResponse.message?.let { message ->
                        Log.e(TAG , "An error occured $message")
                    }
                }

                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

    fun setupRecyclerView(){
        newsAdapter= NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
    }

    fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
    }
}