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
        mQuotesRemoteDataSource.getQuote(language, new QuotesDataSource.GetQuoteCallback() {
            @Override
            public void onQuoteLoaded(Quote quote) {
                mQuotesLocalDataSource.persist(quote);

                // TODO this list is temporary
                List<Quote> quotes = new ArrayList<>(1);
                quotes.add(quote);
                listener.onQuotesLoaderTaskComplete(quotes);
            }

            @Override
            public void onDataNotAvailable() {
                mQuotesLocalDataSource.getQuote(language, new QuotesDataSource.GetQuoteCallback() {
                    @Override
                    public void onQuoteLoaded(Quote quote) {
                        // TODO this list is temporary
                        List<Quote> quotes = new ArrayList<>(1);
                        quotes.add(quote);
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
