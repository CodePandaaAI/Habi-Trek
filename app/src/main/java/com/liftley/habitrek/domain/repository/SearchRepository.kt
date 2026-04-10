package com.liftley.habitrek.domain.repository

import com.liftley.habitrek.presentation.featureWebSearch.model.SearchResult

interface SearchRepository {
    suspend fun search(query: String): SearchResult
}