package com.sofps.inspirationalquotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sofps.inspirationalquotes.model.Quote
import com.sofps.inspirationalquotes.model.QuoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [Quote::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun quoteDao(): QuoteDao
}

private lateinit var INSTANCE: AppDatabase

/**
 * Instantiate a database from a context.
 */
fun getDatabase(context: Context): AppDatabase {

    synchronized(AppDatabase::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                    .databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "inspirational_quotes"
                    )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // insert the data on the IO Thread
                            GlobalScope.launch(Dispatchers.IO) {
// TODO there is a race condition with this, the first time the app is launched it shows nothing because the database is not populated yet
                                INSTANCE.quoteDao().insertAll(PREPOPULATE_DATA)
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
    return INSTANCE
}
