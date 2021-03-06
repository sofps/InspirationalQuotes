package com.sofps.inspirationalquotes.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.sofps.inspirationalquotes.data.AppDatabase
import com.sofps.inspirationalquotes.data.QuotesService
import com.sofps.inspirationalquotes.data.source.QuotesRepository
import com.sofps.inspirationalquotes.data.source.local.QuotesLocalDataSource
import com.sofps.inspirationalquotes.data.source.remote.QuotesRemoteDataSource
import com.sofps.inspirationalquotes.ui.QuotesSlideContract
import com.sofps.inspirationalquotes.ui.QuotesSlidePresenter
import com.sofps.inspirationalquotes.util.LanguagePreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {

    single { QuotesLocalDataSource(get()) }

    single { QuotesRemoteDataSource(get()) }

    single { QuotesRepository(get(), get()) }

    single { LanguagePreferences(get()) }

    single { provideQuotesService() }

    single { provideSharedPreferences(androidApplication()) }

    single { provideAppDatabase(androidContext()) }

    single { provideQuoteDao(get()) }

    factory { (view: QuotesSlideContract.View) -> QuotesSlidePresenter(view, get(), get()) as QuotesSlideContract.Presenter }

}

private fun provideQuotesService(): QuotesService =
        Retrofit.Builder().baseUrl("http://quotes.rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuotesService::class.java)

private fun provideSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences("InspirationalQuotes", Context.MODE_PRIVATE)

private fun provideAppDatabase(context: Context) = AppDatabase.buildDefault(context)

private fun provideQuoteDao(appDatabase: AppDatabase) = appDatabase.quoteDao()
