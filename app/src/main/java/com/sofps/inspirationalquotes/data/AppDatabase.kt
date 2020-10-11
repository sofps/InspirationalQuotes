package com.sofps.inspirationalquotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
        entities = [
            QuoteDb::class
        ],
        version = 1,
        exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun quoteDao(): QuoteDao

    companion object {

        private const val databaseName = "inspirational_quotes.db"
        private const val prepackagedDatabase = "database/${databaseName}"

        fun buildDefault(context: Context) =
                Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                        .createFromAsset(prepackagedDatabase)
                        .fallbackToDestructiveMigration()
                        .build()
    }
}
