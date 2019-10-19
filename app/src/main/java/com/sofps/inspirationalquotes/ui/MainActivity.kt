package com.sofps.inspirationalquotes.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.sofps.inspirationalquotes.AlarmReceiver
import com.sofps.inspirationalquotes.Injection
import com.sofps.inspirationalquotes.R
import com.sofps.inspirationalquotes.asynctask.ScreenshotLoader
import com.sofps.inspirationalquotes.util.LanguagePreferences
import com.sofps.inspirationalquotes.util.ScreenshotUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FilenameFilter

class MainActivity :
        AppCompatActivity(),
        ScreenshotLoader.ScreenshotLoaderTaskListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LanguagePreferences.LanguagePreferencesListener {

    companion object {

        const val PREF_NOTIFICATION_ENABLED = "notificationEnabled"

        private const val TAG = "MainActivity"

        private const val ALARM_SET = "alarm_set"
        private const val LANGUAGE = "language"
    }

    private var alarmSet: Boolean = false

    private var preferences: SharedPreferences? = null
    private var languagePreferences: LanguagePreferences? = null

    private var quotesSlideFragment: QuotesSlideFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadToolbar()
        loadPreferences()
        loadAlarm(savedInstanceState)

        if (savedInstanceState == null) {
            quotesSlideFragment = QuotesSlideFragment()
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, quotesSlideFragment!!)
                    .commit()
        }

        // TODO not sure if we need a reference to the presenter here
        val dataBaseHelper = Injection.provideDataBaseHelper(applicationContext)
        val quotesLocalDataSource = Injection.provideQuotesLocalDataSource(dataBaseHelper)
        val quotesService = Injection.provideQuotesService()
        val quotesRemoteDataSource = Injection.provideQuotesRemoteDataSource(quotesService)
        QuotesSlidePresenter(quotesSlideFragment,
                Injection.provideQuotesRepository(quotesLocalDataSource, quotesRemoteDataSource),
                languagePreferences)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_quotes_slide, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        deletePrivateFiles()
        languagePreferences!!.unregisterListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (alarmSet) {
            // Save alarm status only when it's set
            outState.putBoolean(ALARM_SET, alarmSet)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                loadScreenshot()
                return true
            }
            R.id.action_settings -> {
                showSettingsDialog()
                return true
            }
            R.id.action_shuffle -> {
                // TODO improve
                quotesSlideFragment?.shuffle()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onScreenshotLoaderTaskComplete(file: File) {
        shareScreenshot(file)

        loader!!.visibility = View.GONE
    }

    override fun onScreenshotLoaderTaskInProgress() {
        loader!!.visibility = View.VISIBLE
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        val newValue = sharedPreferences.getBoolean(PREF_NOTIFICATION_ENABLED, true)
        if (newValue && !alarmSet) {
            // Notifications were enabled and alarm is not set
            setAlarm()
        } else if (!newValue && alarmSet) {
            // Notifications were disabled and alarm is set
            cancelAlarm()
        }
    }

    private fun loadToolbar() {
        setSupportActionBar(toolbar)
        // Hide app name and show logo instead
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar!!.setLogo(R.drawable.ic_launcher)
    }

    private fun loadPreferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this).apply {
            registerOnSharedPreferenceChangeListener(this@MainActivity)
        }

        languagePreferences = LanguagePreferences(preferences).apply {
            setLanguagePreferencesListener(this@MainActivity)
        }
    }

    private fun loadAlarm(savedInstanceState: Bundle?) {
        if ((savedInstanceState == null || !savedInstanceState.getBoolean(ALARM_SET))
                && preferences!!.getBoolean(PREF_NOTIFICATION_ENABLED, true)) {
            // First onCreate or alarm not set but should be
            Log.d(TAG, "Setting the alarm")
            setAlarm()
        } else {
            alarmSet = savedInstanceState != null && savedInstanceState.getBoolean(ALARM_SET)
        }
    }

    private fun loadScreenshot() {
        val screenshot = ScreenshotUtils.getScreenshot(container!!)
        if (screenshot == null) {
            Toast.makeText(this, R.string.quote_share_failed, Toast.LENGTH_SHORT)
                    .show()
            return
        }
        ScreenshotLoader(this, this).execute(screenshot)
    }

    private fun showSettingsDialog() {
        val builder = MaterialDialog.Builder(this).title(R.string.action_settings)
                .customView(R.layout.dialog_settings, true)
                .positiveText(R.string.button_ok)
                .negativeText(R.string.button_cancel)

        val view = builder.build().customView

        val notificationsEnabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_NOTIFICATION_ENABLED, true)

        val enableNotificationsSwitch = view!!.findViewById<Switch>(R.id.enable_notifications)
        enableNotificationsSwitch.isChecked = notificationsEnabled

        val languageSelected = languagePreferences!!.language
        val spinner = view.findViewById<Spinner>(R.id.language_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(this, R.array.language_labels, R.layout.spinner_textview)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner.adapter = adapter
        // Select current language
        val position = listOf(*resources.getStringArray(R.array.language_values))
                .indexOf(languageSelected)
        spinner.setSelection(position)

        builder.onPositive { _, _ ->
            // Check if notifications settings were changed
            val currentValue = enableNotificationsSwitch.isChecked
            if (currentValue != notificationsEnabled) {
                // If value was changed, persist
                PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                        .edit()
                        .putBoolean(PREF_NOTIFICATION_ENABLED, currentValue)
                        .apply()
            }

            // Check if language was changed
            val currentLang = resources.getStringArray(R.array.language_values)[spinner.selectedItemPosition]
            if (currentLang != languageSelected) {
                languagePreferences!!.language = currentLang
            }
        }.show()
    }

    private fun shareScreenshot(file: File) {
        val uri = Uri.fromFile(file) // Convert file path into Uri for sharing
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.quote_share_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.quote_share_text))
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.app_name)))
    }

    private fun setAlarm() {
        AlarmReceiver().apply {
            setAlarm(this@MainActivity)
        }
        alarmSet = true
    }

    private fun cancelAlarm() {
        AlarmReceiver().apply {
            cancelAlarm(this@MainActivity)
        }
        alarmSet = false
    }

    private fun deletePrivateFiles() {
        val dir: File = if (Environment.MEDIA_MOUNTED == Environment
                        .getExternalStorageState()) {
            Environment.getExternalStorageDirectory()
        } else {
            cacheDir
        }
        val fileNameFilter = FilenameFilter { _, name -> name.startsWith("IQ") }
        val children = dir.listFiles(fileNameFilter) ?: return
        for (i in children.indices) {
            val file = children[i]
            if (file.isFile) {
                val success = file.delete()
                if (success) {
                    Log.d(TAG, "File deleted")
                } else {
                    Log.d(TAG, "Couldn't delete file: " + file.name)
                }
            }
        }
    }

    override fun onLanguageChange() {
        quotesSlideFragment?.onLanguageChange()
    }
}
