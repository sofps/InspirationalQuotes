package com.sofps.inspirationalquotes.data.source.local

import android.content.Context
import com.sofps.inspirationalquotes.data.getDatabase
import com.sofps.inspirationalquotes.data.source.QuotesDataSource
import com.sofps.inspirationalquotes.model.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class QuotesLocalDataSource(
        private val context: Context
) : QuotesDataSource {

    override fun getQuotes(language: String): Flow<List<Quote>> {
        return flow {
            getDatabase(context).apply {
                val quotes = quoteDao().getAllByLanguage(language)
                emit(quotes)
            }
        }.flowOn(Dispatchers.IO)
    }

    fun persist(quotes: List<Quote>) {
        GlobalScope.launch(Dispatchers.IO) {
            getDatabase(context).apply {
                quoteDao().insertAll(quotes)
            }
        }
    }

    fun addOneTimeShowed(quote: Quote) {
        quote.timesShowed++

        GlobalScope.launch(Dispatchers.IO) {
            getDatabase(context).apply {
                quoteDao().update(quote)
            }
        }
    }
}
