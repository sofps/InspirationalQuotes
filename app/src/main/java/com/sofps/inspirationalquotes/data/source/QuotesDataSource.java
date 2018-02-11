package com.sofps.inspirationalquotes.data.source;

import android.support.annotation.NonNull;
import com.sofps.inspirationalquotes.data.Quote;

public interface QuotesDataSource {

    interface GetQuoteCallback {

        void onQuoteLoaded(Quote quote);

        void onDataNotAvailable();
    }

    void getQuote(@NonNull String language, @NonNull GetQuoteCallback callback);
}
