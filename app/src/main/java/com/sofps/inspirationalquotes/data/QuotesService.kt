package com.sofps.inspirationalquotes.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QuotesService {

    @get:GET("qod.json")
    val quoteOfTheDay: Call<QuoteApi>

    @get:GET("quote/random.json")
    val randomQuote: Call<QuoteApi>

    @GET("qod.json")
    fun getQuoteOfTheDayForCategory(@Query("category") category: String): Call<QuoteApi>
}
