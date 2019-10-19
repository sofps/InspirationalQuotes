package com.sofps.inspirationalquotes.ui

import com.sofps.inspirationalquotes.asynctask.QuotesLoader
import com.sofps.inspirationalquotes.data.Quote
import com.sofps.inspirationalquotes.data.source.QuotesRepository
import com.sofps.inspirationalquotes.util.LanguagePreferences

import java.util.ArrayList

class QuotesSlidePresenter internal constructor(
        private val view: QuotesSlideContract.View,
        private val quotesRepository: QuotesRepository,
        private val languagePreferences: LanguagePreferences
) : QuotesSlideContract.Presenter, QuotesLoader.QuotesLoaderTaskListener {

    private val internalQuotes = ArrayList<Quote>()

    override var quotes: List<Quote>
        get() = internalQuotes
        set(quotes) {
            internalQuotes.addAll(quotes)
        }

    override fun start() {
        loadQuotesForCurrentLanguage()
    }

    private fun loadQuotesForCurrentLanguage() {
        view.showProgress(true)
        quotesRepository.loadQuotesForLanguage(languagePreferences.language, this)
    }

    override fun onQuotesLoaderTaskComplete(quoteList: List<Quote>) {
        internalQuotes.clear()
        internalQuotes.addAll(quoteList)

        view.initialize()
        view.setQuotes(internalQuotes)
        view.showProgress(false)
    }

    override fun onQuotesLoaderTaskInProgress() {
        view.showProgress(true)
    }

    override fun onLanguageChange() {
        loadQuotesForCurrentLanguage()
    }

    override fun onQuoteShow(quote: Quote) {
        quotesRepository.addOneTimeShowed(quote)
    }
}
