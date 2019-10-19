package com.sofps.inspirationalquotes.util

import android.content.SharedPreferences
import java.util.*

class LanguagePreferences(
        private val sharedPreferences: SharedPreferences
) : SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {

        private const val PREF_LANGUAGE = "language"
        private const val DEFAULT_LANGUAGE = "EN"
        private val SUPPORTED_LANGUAGES = arrayOf("EN", "ES")
    }

    private var languagePreferencesListener: LanguagePreferencesListener? = null

    // The language preference is not set, set it
    var language: String
        get() {
            return sharedPreferences.getString(PREF_LANGUAGE, null) ?: getSupportedLanguage()
        }
        set(language) = sharedPreferences.edit().putString(PREF_LANGUAGE, language).apply()

    interface LanguagePreferencesListener {
        fun onLanguageChange()
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (languagePreferencesListener != null && PREF_LANGUAGE == key) {
            languagePreferencesListener!!.onLanguageChange()
        }
    }

    fun setLanguagePreferencesListener(listener: LanguagePreferencesListener) {
        languagePreferencesListener = listener
    }

    fun unregisterListeners() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun getSupportedLanguage(): String {
        val language = Locale.getDefault().language.toUpperCase()
        if (!SUPPORTED_LANGUAGES.contains(language)) {
            return DEFAULT_LANGUAGE
        }
        return language
    }


}
