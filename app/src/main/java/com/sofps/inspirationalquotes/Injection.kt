package com.sofps.inspirationalquotes

import android.content.Context
import com.sofps.inspirationalquotes.data.DataBaseHelper
import com.sofps.inspirationalquotes.data.QuotesService
import com.sofps.inspirationalquotes.data.source.QuotesRepository
import com.sofps.inspirationalquotes.data.source.local.QuotesLocalDataSource
import com.sofps.inspirationalquotes.data.source.remote.QuotesRemoteDataSource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injection {

    fun provideQuotesRepository(
            quotesLocalDataSource: QuotesLocalDataSource,
            quotesRemoteDataSource: QuotesRemoteDataSource) =
            QuotesRepository(quotesLocalDataSource, quotesRemoteDataSource)

    fun provideDataBaseHelper(context: Context) = DataBaseHelper(context)

    fun provideQuotesService(): QuotesService =
            Retrofit.Builder().baseUrl("http://quotes.rest/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(QuotesService::class.java)

    fun provideQuotesLocalDataSource(dataBaseHelper: DataBaseHelper) =
            QuotesLocalDataSource(dataBaseHelper)

    fun provideQuotesRemoteDataSource(
            quotesService: QuotesService) = QuotesRemoteDataSource(quotesService)
}
