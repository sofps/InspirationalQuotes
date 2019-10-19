package com.sofps.inspirationalquotes.data.source

import com.sofps.inspirationalquotes.asynctask.QuotesLoader
import com.sofps.inspirationalquotes.data.Quote
import com.sofps.inspirationalquotes.data.source.local.QuotesLocalDataSource
import com.sofps.inspirationalquotes.data.source.remote.QuotesRemoteDataSource

class QuotesRepository(
        private val quotesLocalDataSource: QuotesLocalDataSource,
        private val quotesRemoteDataSource: QuotesRemoteDataSource
) {

    fun loadQuotesForLanguage(language: String, listener: QuotesLoader.QuotesLoaderTaskListener) {
        quotesRemoteDataSource.getQuotes(language, object : QuotesDataSource.GetQuoteCallback {

            override fun onQuotesLoaded(quotes: List<Quote>) {
                quotesLocalDataSource.persist(quotes[0]) // TODO for now the remote DS only returns a list with one new element that needs to be persisted
                listener.onQuotesLoaderTaskComplete(quotes)
            }

            override fun onDataNotAvailable() {
                quotesLocalDataSource.getQuotes(language, object : QuotesDataSource.GetQuoteCallback {
                    override fun onQuotesLoaded(quotes: List<Quote>) {
                        listener.onQuotesLoaderTaskComplete(quotes)
                    }

                    override fun onDataNotAvailable() {
                        // TODO error
                    }
                })
            }
        })
    }

    fun addOneTimeShowed(quote: Quote) {
        quotesLocalDataSource.addOneTimeShowed(quote)
    }
}
