package com.sofps.inspirationalquotes.data.source;

import android.support.annotation.NonNull;
import com.sofps.inspirationalquotes.asynctask.QuotesLoader;
import com.sofps.inspirationalquotes.data.Quote;
import com.sofps.inspirationalquotes.data.source.local.QuotesLocalDataSource;
import com.sofps.inspirationalquotes.data.source.remote.QuotesRemoteDataSource;
import java.util.ArrayList;
import java.util.List;

public class QuotesRepository {

    private final QuotesLocalDataSource mQuotesLocalDataSource;
    private final QuotesRemoteDataSource mQuotesRemoteDataSource;

    public QuotesRepository(
            @NonNull final QuotesLocalDataSource quotesLocalDataSource,
            @NonNull final QuotesRemoteDataSource quotesRemoteDataSource) {
        mQuotesLocalDataSource = quotesLocalDataSource;
        mQuotesRemoteDataSource = quotesRemoteDataSource;
    }

    public void loadQuotesForLanguage(final String language, final QuotesLoader.QuotesLoaderTaskListener listener) {
        mQuotesRemoteDataSource.getQuotes(language, new QuotesDataSource.GetQuoteCallback() {
            @Override
            public void onQuotesLoaded(List<Quote> quotes) {
                mQuotesLocalDataSource.persist(quotes.get(0)); // TODO for now the remote DS only returns a list with one new element that needs to be persisted
                listener.onQuotesLoaderTaskComplete(quotes);
            }

            @Override
            public void onDataNotAvailable() {
                mQuotesLocalDataSource.getQuotes(language, new QuotesDataSource.GetQuoteCallback() {
                    @Override
                    public void onQuotesLoaded(List<Quote> quotes) {
                        listener.onQuotesLoaderTaskComplete(quotes);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        // TODO error
                    }
                });
            }
        });
    }

    public void addOneTimeShowed(Quote quote) {
        mQuotesLocalDataSource.addOneTimeShowed(quote);
    }
}
