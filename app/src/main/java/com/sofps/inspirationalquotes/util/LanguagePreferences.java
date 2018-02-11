package com.sofps.inspirationalquotes.util;

import android.content.SharedPreferences;
import java.util.Arrays;
import java.util.Locale;

public class LanguagePreferences implements SharedPreferences.OnSharedPreferenceChangeListener {

    public interface LanguagePreferencesListener {
        void onLanguageChange();
    }

    private static final String PREF_LANGUAGE = "language";
    private static final String[] SUPPORTED_LANGUAGES = { "EN", "ES" };
    private static final String DEFAULT_LANGUAGE = "EN";

    private final SharedPreferences mSharedPreferences;
    private LanguagePreferencesListener mLanguagePreferencesListener;

    public LanguagePreferences(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (mLanguagePreferencesListener != null && PREF_LANGUAGE.equals(key)) {
            mLanguagePreferencesListener.onLanguageChange();
        }
    }

    public String getLanguage() {
        String language = mSharedPreferences.getString(PREF_LANGUAGE, null);
        if (language == null) {
            // The language preference is not set, set it
            language = Locale.getDefault().getLanguage().toUpperCase();
            if (!Arrays.asList(SUPPORTED_LANGUAGES).contains(language)) {
                language = DEFAULT_LANGUAGE;
            }
            setLanguage(language);
        }
        return language;
    }

    public void setLanguage(String language) {
        mSharedPreferences.edit().putString(PREF_LANGUAGE, language).apply();
    }

    public void setLanguagePreferencesListener(LanguagePreferencesListener listener) {
        mLanguagePreferencesListener = listener;
    }

    public void unregisterListeners() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
