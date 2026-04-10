package com.liftley.habitrek.data.remote.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
data class GNewsResponse(
    @SerializedName("articles")
    val articles: List<GNewsArticle>?
)

@Keep
data class GNewsArticle(
    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("url")
    val url: String?
)

interface SearchApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("apikey") apiKey: String,
        @Query("lang") lang: String = "en"
    ): Response<GNewsResponse>
}
