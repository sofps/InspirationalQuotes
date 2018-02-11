package com.sofps.inspirationalquotes.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface QuotesService {

    @GET("qod.json")
    Call<QuoteApi> getQuoteOfTheDay();

    @GET("qod.json")
    Call<QuoteApi> getQuoteOfTheDayForCategory(@Query("category") String category);

    @GET("quote/random.json")
    Call<QuoteApi> getRandomQuote();
}
