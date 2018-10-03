package com.sofps.inspirationalquotes.data.source.remote;

import androidx.annotation.NonNull;

import com.sofps.inspirationalquotes.data.Quote;
import com.sofps.inspirationalquotes.data.QuoteApi;
import com.sofps.inspirationalquotes.data.QuotesService;
import com.sofps.inspirationalquotes.data.source.QuotesDataSource;

import java.util.ArrayList;
import java.util.List;

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
    public void getQuotes(@NonNull String language, @NonNull final GetQuoteCallback callback) {
        if (LANGUAGE_SUPPORTED.equalsIgnoreCase(language)) {
            mQuotesService.getQuoteOfTheDay().enqueue(new Callback<QuoteApi>() {
                @Override
                public void onResponse(Call<QuoteApi> call, Response<QuoteApi> response) {
                    QuoteApi quoteApi = response.body();
                    if (response.isSuccessful() && quoteApi != null && quoteApi.isValid()) {
                        Quote quote = mapQuote(quoteApi);
                        List<Quote> quotes = new ArrayList<>(1);
                        quotes.add(quote);
                        callback.onQuotesLoaded(quotes);
                    } else {
                        callback.onDataNotAvailable();
                    }
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

    private Quote mapQuote(@NonNull QuoteApi quoteApi) {
        Quote quote = new Quote();
        quote.setLanguage(LANGUAGE_SUPPORTED);
        quote.setAuthor(quoteApi.getAuthor());
        quote.setText(quoteApi.getQuote());
        return quote;
    }
}
