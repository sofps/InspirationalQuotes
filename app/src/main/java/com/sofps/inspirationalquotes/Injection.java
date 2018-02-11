package com.sofps.inspirationalquotes;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sofps.inspirationalquotes.data.DataBaseHelper;
import com.sofps.inspirationalquotes.data.QuotesService;
import com.sofps.inspirationalquotes.data.source.QuotesRepository;
import com.sofps.inspirationalquotes.data.source.local.QuotesLocalDataSource;
import com.sofps.inspirationalquotes.data.source.remote.QuotesRemoteDataSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Injection {

    public static QuotesRepository provideQuotesRepository(
            @NonNull final QuotesLocalDataSource quotesLocalDataSource,
            @NonNull final QuotesRemoteDataSource quotesRemoteDataSource) {
        return new QuotesRepository(quotesLocalDataSource, quotesRemoteDataSource);
    }

    public static DataBaseHelper provideDataBaseHelper(@NonNull Context context) {
        return new DataBaseHelper(context);
    }

    public static QuotesService provideQuotesService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://quotes.rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(QuotesService.class);
    }

    public static QuotesLocalDataSource provideQuotesLocalDataSource(
            @NonNull DataBaseHelper dataBaseHelper) {
        return new QuotesLocalDataSource(dataBaseHelper);
    }

    public static QuotesRemoteDataSource provideQuotesRemoteDataSource(
            @NonNull QuotesService quotesService) {
        return new QuotesRemoteDataSource(quotesService);
    }
}
