package com.sofps.inspirationalquotes.data.source.local

import com.sofps.inspirationalquotes.data.source.QuotesDataSource
import com.sofps.inspirationalquotes.data.QuoteDb
import com.sofps.inspirationalquotes.data.QuoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class QuotesLocalDataSource(
        private val quoteDao: QuoteDao
) : QuotesDataSource {

    override fun getQuotes(language: String): Flow<List<QuoteDb>> {
        return flow {
            val quotes = quoteDao.getAllByLanguage(language)
            emit(quotes)
        }.flowOn(Dispatchers.IO)
    }

    fun persist(quotes: List<QuoteDb>) {
        GlobalScope.launch(Dispatchers.IO) {
            quoteDao.insertAll(quotes)
        }
    }

    fun addOneTimeShowed(quote: QuoteDb) {
        val updatedQuote = quote.copy(timesShowed = quote.timesShowed + 1)

        GlobalScope.launch(Dispatchers.IO) {
            quoteDao.update(updatedQuote)
        }
    }
}
