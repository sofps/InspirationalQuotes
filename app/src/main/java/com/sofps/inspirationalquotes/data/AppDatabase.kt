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
                    .createFromAsset("databases/inspirational_quotes.db")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
    return INSTANCE
}
