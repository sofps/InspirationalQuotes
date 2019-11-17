package com.sofps.inspirationalquotes.data

import retrofit2.http.GET
import retrofit2.http.Query

interface QuotesService {

    @GET("qod.json")
    suspend fun getQuoteOfTheDay(): QuoteApi

    @GET("quote/random.json")
    suspend fun getRandomQuote(): QuoteApi

    @GET("qod.json")
    suspend fun getQuoteOfTheDayForCategory(@Query("category") category: String): QuoteApi
}
