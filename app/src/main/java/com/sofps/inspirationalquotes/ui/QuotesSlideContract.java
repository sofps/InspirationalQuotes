package com.sofps.inspirationalquotes.ui;

import com.sofps.inspirationalquotes.BasePresenter;
import com.sofps.inspirationalquotes.BaseView;
import com.sofps.inspirationalquotes.data.Quote;
import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface QuotesSlideContract {

    interface View extends BaseView<Presenter> {

        void initialize();

        void setQuotes(List<Quote> quotes);

        void showProgress(boolean show);
    }

    interface Presenter extends BasePresenter, ScreenSlidePagerAdapter.QuoteListener {

        void onLanguageChange();

        void setQuotes(List<Quote> quotes);

        List<Quote> getQuotes();
    }
}
