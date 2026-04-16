package com.liftley.habitrek.domain.repository

import com.liftley.habitrek.domain.model.SearchArticle

interface SearchRepository {
    suspend fun search(query: String): List<SearchArticle>
}