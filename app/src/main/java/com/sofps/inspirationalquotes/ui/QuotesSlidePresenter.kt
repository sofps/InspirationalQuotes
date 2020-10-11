package com.sofps.inspirationalquotes.ui

import com.sofps.inspirationalquotes.data.source.QuotesRepository
import com.sofps.inspirationalquotes.model.QuoteModel
import com.sofps.inspirationalquotes.model.ViewState
import com.sofps.inspirationalquotes.util.LanguagePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class QuotesSlidePresenter internal constructor(
        private val view: QuotesSlideContract.View,
        private val quotesRepository: QuotesRepository,
        private val languagePreferences: LanguagePreferences
) : QuotesSlideContract.Presenter {

    private val internalQuotes = mutableListOf<QuoteModel>()

    override var quotes: List<QuoteModel>
        get() = internalQuotes
        set(quotes) {
            internalQuotes.addAll(quotes)
        }

    override fun start() {
        loadQuotesForCurrentLanguage()
    }

    private fun loadQuotesForCurrentLanguage() {
        GlobalScope.launch(Dispatchers.Main) {
            quotesRepository.getQuotesForLanguage(languagePreferences.language)
                    .collect { state ->
                        when (state) {
                            is ViewState.Success -> {
                                internalQuotes.clear()
                                internalQuotes.addAll(state.data)

                                view.initialize()
                                view.setQuotes(internalQuotes)
                                view.showProgress(false)
                            }
                            is ViewState.Loading -> view.showProgress(true)
                            is ViewState.Error -> {
                                // TODO show error toast("Something went wrong ¯\\_(ツ)_/¯ => ${state.message}")
                            }
                        }
                    }
        }
    }

    override fun onLanguageChange() {
        loadQuotesForCurrentLanguage()
    }

    override fun onQuoteShow(quote: QuoteModel) {
        quotesRepository.addOneTimeShowed(quote)
    }
}
