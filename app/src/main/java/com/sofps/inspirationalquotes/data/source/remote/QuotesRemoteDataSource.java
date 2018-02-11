package com.sofps.inspirationalquotes.data.source.remote;

import android.support.annotation.NonNull;
import com.sofps.inspirationalquotes.data.Quote;
import com.sofps.inspirationalquotes.data.QuoteApi;
import com.sofps.inspirationalquotes.data.QuotesService;
import com.sofps.inspirationalquotes.data.source.QuotesDataSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuotesRemoteDataSource implements QuotesDataSource {

    private final static String LANGUAGE_SUPPORTED = "EN";

    private final QuotesService mQuotesService;

    public QuotesRemoteDataSource(QuotesService quotesService) {
        mQuotesService = quotesService;
    }

    @Override
    public void getQuote(@NonNull String language, @NonNull final GetQuoteCallback callback) {
        if (LANGUAGE_SUPPORTED.equalsIgnoreCase(language)) {
            mQuotesService.getQuoteOfTheDay().enqueue(new Callback<QuoteApi>() {
                @Override
                public void onResponse(Call<QuoteApi> call, Response<QuoteApi> response) {
                    callback.onQuoteLoaded(mapQuote(response.body()));
                }

                @Override
                public void onFailure(Call<QuoteApi> call, Throwable t) {
                    callback.onDataNotAvailable();
                }
            });
        } else {
            callback.onDataNotAvailable();
        }
    }

    private Quote mapQuote(QuoteApi quoteApi) {
        Quote quote = new Quote();
        quote.setLanguage(LANGUAGE_SUPPORTED);
        quote.setAuthor(quoteApi.getAuthor());
        quote.setText(quoteApi.getQuote());
        return quote;
    }
}
