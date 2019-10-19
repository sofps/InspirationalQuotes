package com.sofps.inspirationalquotes.data.source

import com.sofps.inspirationalquotes.data.Quote

interface QuotesDataSource {

    interface GetQuoteCallback {

        fun onQuotesLoaded(quotes: List<Quote>)

        fun onDataNotAvailable()
    }

    fun getQuotes(language: String, callback: GetQuoteCallback)

}
