package com.sofps.inspirationalquotes.ui

import com.sofps.inspirationalquotes.BasePresenter
import com.sofps.inspirationalquotes.BaseView

/**
 * This specifies the contract between the view and the presenter.
 */
interface QuotesSlidePageContract {

    interface View : BaseView<Presenter>

    interface Presenter : BasePresenter
}
