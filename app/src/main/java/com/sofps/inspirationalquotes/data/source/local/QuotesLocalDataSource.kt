package com.sofps.inspirationalquotes.data.source.local

import com.sofps.inspirationalquotes.data.DataBaseHelper
import com.sofps.inspirationalquotes.data.Quote
import com.sofps.inspirationalquotes.data.source.QuotesDataSource
import java.util.ArrayList

class QuotesLocalDataSource(
        private val dataBaseHelper: DataBaseHelper
) : QuotesDataSource {

    override fun getQuotes(language: String, callback: QuotesDataSource.GetQuoteCallback) {
        callback.onQuotesLoaded(getQuotes(language))
    }

    fun persist(quote: Quote) {
        dataBaseHelper.insertQuote(quote)
    }

    fun addOneTimeShowed(quote: Quote) {
        dataBaseHelper.addOneTimeShowed(quote)
    }

    private fun getQuotes(language: String): List<Quote> {
        val cursor = dataBaseHelper.queryQuotes(language)
        val quotes = ArrayList<Quote>()
        while (cursor.moveToNext()) {
            quotes.add(cursor.quote)
        }
        cursor.close()
        dataBaseHelper.close()
        return quotes
    }
}
