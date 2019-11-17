package com.sofps.inspirationalquotes.di

import android.app.Application
import android.content.SharedPreferences
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

    single { QuotesLocalDataSource(androidContext()) }

    single { QuotesRemoteDataSource(get()) }

    single { QuotesRepository(get(), get()) }

    single { LanguagePreferences(get()) }

    single { provideQuotesService() }

    single { provideSharedPreferences(androidApplication()) }

    factory { (view: QuotesSlideContract.View) -> QuotesSlidePresenter(view, get(), get()) as QuotesSlideContract.Presenter }

}

fun provideQuotesService(): QuotesService =
        Retrofit.Builder().baseUrl("http://quotes.rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuotesService::class.java)

private fun provideSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences("InspirationalQuotes", android.content.Context.MODE_PRIVATE)
