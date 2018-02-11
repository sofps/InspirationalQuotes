package com.sofps.inspirationalquotes;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sofps.inspirationalquotes.data.DataBaseHelper;
import com.sofps.inspirationalquotes.data.source.QuotesRepository;

public class Injection {

    public static QuotesRepository provideQuotesRepository(@NonNull DataBaseHelper dataBaseHelper) {
        return new QuotesRepository(dataBaseHelper);
    }

    public static DataBaseHelper provideDataBaseHelper(@NonNull Context context) {
        return new DataBaseHelper(context);
    }
}
