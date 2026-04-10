package com.liftley.habitrek.data.repository

import android.util.Log
import com.liftley.habitrek.data.remote.api.SearchApi
import com.liftley.habitrek.domain.repository.SearchRepository
import com.liftley.habitrek.presentation.featureWebSearch.model.ResultItem
import com.liftley.habitrek.presentation.featureWebSearch.model.SearchResult
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(private val api: SearchApi): SearchRepository {
    private val apiKey = "1099319b95017269d180c79fbc8b6143"

    override suspend fun search(query: String): SearchResult {
        try {
            val response = api.search(
                query = query,
                apiKey = apiKey
            )

            if (response.isSuccessful) {
                val body = response.body()

                // Map GNews articles to our UI format
                val links = body?.articles?.map {
                    ResultItem(
                        title = it.title ?: "No Title",
                        link = it.url ?: "No Link",
                        snippet = it.description ?: "No description"
                    )
                } ?: emptyList()
                Log.d("Search Result", "$links")
                return SearchResult(results = links, error = null)

            } else {
                return SearchResult(error = "GNews Error: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return SearchResult(error = "Network Error: ${e.message}")
        }
    }
}