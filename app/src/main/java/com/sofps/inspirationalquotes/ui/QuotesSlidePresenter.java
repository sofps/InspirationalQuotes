package com.sofps.inspirationalquotes.ui;

import com.sofps.inspirationalquotes.asynctask.QuotesLoader;
import com.sofps.inspirationalquotes.data.Quote;
import com.sofps.inspirationalquotes.data.source.QuotesRepository;
import com.sofps.inspirationalquotes.util.LanguagePreferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QuotesSlidePresenter
        implements QuotesSlideContract.Presenter, QuotesLoader.QuotesLoaderTaskListener {

    private final QuotesSlideContract.View mView;
    private final QuotesRepository mQuotesRepository;
    private final LanguagePreferences mLanguagePreferences;

    private ArrayList<Quote> mQuotes = new ArrayList<>();

    QuotesSlidePresenter(QuotesSlideContract.View view, QuotesRepository quotesRepository, LanguagePreferences languagePreferences) {
        mView = view;
        mQuotesRepository = quotesRepository;
        mLanguagePreferences = languagePreferences;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        loadQuotesForCurrentLanguage();
    }

    private void loadQuotesForCurrentLanguage() {
        mView.showProgress(true);
        mQuotesRepository.loadQuotesForLanguage(mLanguagePreferences.getLanguage(), this);
    }

    @Override
    public void onQuotesLoaderTaskComplete(@NotNull List<Quote> quoteList) {
        mQuotes.clear();
        mQuotes.addAll(quoteList);

        mView.initialize();
        mView.setQuotes(mQuotes);
        mView.showProgress(false);
    }

    @Override
    public void onQuotesLoaderTaskInProgress() {
        mView.showProgress(true);
    }

    @Override
    public void onLanguageChange() {
        loadQuotesForCurrentLanguage();
    }

    @Override
    public void setQuotes(List<Quote> quotes) {
        mQuotes.addAll(quotes);
    }

    @Override
    public List<Quote> getQuotes() {
        return mQuotes;
    }

    @Override
    public void onQuoteShow(Quote quote) {
        mQuotesRepository.addOneTimeShowed(quote);
    }
}
