package com.sofps.inspirationalquotes

import android.app.Application
import com.sofps.inspirationalquotes.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate(){
        super.onCreate()

        startKoin {
            // Android context
            androidContext(this@MyApplication)
            // modules
            modules(appModule)
        }
    }
}