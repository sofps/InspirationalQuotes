package com.sofps.inspirationalquotes.ui

import com.sofps.inspirationalquotes.BasePresenter
import com.sofps.inspirationalquotes.BaseView
import com.sofps.inspirationalquotes.model.QuoteModel

/**
 * This specifies the contract between the view and the presenter.
 */
interface QuotesSlideContract {

    interface View : BaseView<Presenter> {

        fun initialize()

        fun setQuotes(quotes: List<QuoteModel>)

        fun showProgress(show: Boolean)
    }

    interface Presenter : BasePresenter, ScreenSlidePagerAdapter.QuoteListener {

        var quotes: List<QuoteModel>

        fun onLanguageChange()
    }
}
