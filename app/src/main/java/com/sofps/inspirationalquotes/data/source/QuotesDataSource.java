package com.sofps.inspirationalquotes.data.source;

import androidx.annotation.NonNull;

import com.sofps.inspirationalquotes.data.Quote;

import java.util.List;

public interface QuotesDataSource {

    interface GetQuoteCallback {

        void onQuotesLoaded(List<Quote> quotes);

        void onDataNotAvailable();
    }

    void getQuotes(@NonNull String language, @NonNull GetQuoteCallback callback);

}
