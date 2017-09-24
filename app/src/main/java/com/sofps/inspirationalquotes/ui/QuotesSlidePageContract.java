package com.sofps.inspirationalquotes.ui;

import com.sofps.inspirationalquotes.BasePresenter;
import com.sofps.inspirationalquotes.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface QuotesSlidePageContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {

    }
}
