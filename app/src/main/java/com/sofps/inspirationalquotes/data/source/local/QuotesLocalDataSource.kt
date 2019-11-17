package com.sofps.inspirationalquotes.data.source.local

import com.sofps.inspirationalquotes.data.DataBaseHelper
import com.sofps.inspirationalquotes.model.Quote
import com.sofps.inspirationalquotes.data.source.QuotesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*

class QuotesLocalDataSource(
        private val dataBaseHelper: DataBaseHelper
) : QuotesDataSource {

    override fun getQuotes(language: String): Flow<List<Quote>> {
        return flow {
            val cursor = dataBaseHelper.queryQuotes(language)
            val quotes = ArrayList<Quote>()
            while (cursor.moveToNext()) {
                cursor.quote?.let {
                    quotes.add(it)
                }
            }
            cursor.close()
            dataBaseHelper.close()
            emit(quotes)
        }.flowOn(Dispatchers.IO)
    }

    fun persist(quotes: List<Quote>) {
        quotes.forEach {
            dataBaseHelper.insertQuote(it)
        }
    }

    fun addOneTimeShowed(quote: Quote) {
        dataBaseHelper.addOneTimeShowed(quote)
    }
}
