package com.sofps.inspirationalquotes.data.source

import com.sofps.inspirationalquotes.data.source.local.QuotesLocalDataSource
import com.sofps.inspirationalquotes.data.source.remote.QuotesRemoteDataSource
import com.sofps.inspirationalquotes.model.Quote
import com.sofps.inspirationalquotes.model.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber

class QuotesRepository(
        private val quotesLocalDataSource: QuotesLocalDataSource,
        private val quotesRemoteDataSource: QuotesRemoteDataSource
) {

    suspend fun getQuotesForLanguage(language: String): Flow<ViewState<List<Quote>>> {
        return flow {
            emit(ViewState.loading())

            try {
                quotesRemoteDataSource.getQuotes(language)
                        .collect { quotes ->
                            Timber.d("quotes=$quotes")
                            quotesLocalDataSource.persist(quotes)
                        }
            } catch (e: Exception) {
                Timber.d("Error when trying to get quotes from remote: $e")
            }

            quotesLocalDataSource.getQuotes(language)
                    .collect {
                        emit(ViewState.success(it))
                    }
        }.catch {
            emit(ViewState.error(it.message.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }

    fun addOneTimeShowed(quote: Quote) {
        quotesLocalDataSource.addOneTimeShowed(quote)
    }
}
