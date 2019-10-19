package com.sofps.inspirationalquotes.data.source.remote

import com.sofps.inspirationalquotes.data.Quote
import com.sofps.inspirationalquotes.data.QuoteApi
import com.sofps.inspirationalquotes.data.QuotesService
import com.sofps.inspirationalquotes.data.source.QuotesDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuotesRemoteDataSource(private val quotesService: QuotesService) : QuotesDataSource {

    companion object {

        private const val LANGUAGE_SUPPORTED = "EN"
    }

    override fun getQuotes(language: String, callback: QuotesDataSource.GetQuoteCallback) {
        if (LANGUAGE_SUPPORTED.equals(language, ignoreCase = true)) {
            quotesService.quoteOfTheDay.enqueue(object : Callback<QuoteApi> {
                override fun onResponse(call: Call<QuoteApi>, response: Response<QuoteApi>) {
                    val quoteApi = response.body()
                    if (response.isSuccessful && quoteApi != null && quoteApi.isValid) {
                        val quotes = arrayListOf(mapQuote(quoteApi))
                        callback.onQuotesLoaded(quotes)
                    } else {
                        callback.onDataNotAvailable()
                    }
                }

                override fun onFailure(call: Call<QuoteApi>, t: Throwable) {
                    callback.onDataNotAvailable()
                }
            })
        } else {
            callback.onDataNotAvailable()
        }
    }

    private fun mapQuote(quoteApi: QuoteApi) =
            Quote().apply {
                language = LANGUAGE_SUPPORTED
                author = quoteApi.author
                text = quoteApi.quote
            }

}
