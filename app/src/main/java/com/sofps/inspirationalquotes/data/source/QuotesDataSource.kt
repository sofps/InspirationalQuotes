package com.sofps.inspirationalquotes.data.source

import com.sofps.inspirationalquotes.model.Quote
import kotlinx.coroutines.flow.Flow

interface QuotesDataSource {

    fun getQuotes(language: String): Flow<List<Quote>>

}
