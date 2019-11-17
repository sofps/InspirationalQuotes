package com.sofps.inspirationalquotes.data.source.remote

import com.sofps.inspirationalquotes.model.Quote
import com.sofps.inspirationalquotes.data.QuoteApi
import com.sofps.inspirationalquotes.data.QuotesService
import com.sofps.inspirationalquotes.data.source.QuotesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class QuotesRemoteDataSource(private val quotesService: QuotesService) : QuotesDataSource {

    companion object {

        private const val LANGUAGE_SUPPORTED = "EN"
    }

    override fun getQuotes(language: String): Flow<List<Quote>> {
        return flow {
            val quoteApi = quotesService.getQuoteOfTheDay()
            emit(listOf(mapQuote(quoteApi)))
        }.flowOn(Dispatchers.IO)
    }

    private fun mapQuote(quoteApi: QuoteApi) =
            Quote().apply {
                language = LANGUAGE_SUPPORTED
                author = quoteApi.author
                text = quoteApi.quote
            }

}
