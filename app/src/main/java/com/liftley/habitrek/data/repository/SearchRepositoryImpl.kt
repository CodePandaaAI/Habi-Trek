package com.liftley.habitrek.data.repository

import android.util.Log
import com.liftley.habitrek.data.remote.api.SearchApi
import com.liftley.habitrek.domain.model.SearchArticle
import com.liftley.habitrek.domain.repository.SearchRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(private val api: SearchApi) : SearchRepository {
    private val apiKey = "1099319b95017269d180c79fbc8b6143"

    override suspend fun search(query: String): List<SearchArticle> {
        val response = api.search(query = query, apiKey = apiKey)

        if (!response.isSuccessful) {
            throw Exception("Search failed: ${response.code()}")
        }

        val articles = response.body()?.articles ?: emptyList()
        Log.d("SearchRepo", "Articles fetched: ${articles.size}")

        return articles.map {
            SearchArticle(
                title = it.title ?: "No Title",
                url = it.url ?: "",
                description = it.description ?: "No description"
            )
        }
    }
}