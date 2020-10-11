package com.sofps.inspirationalquotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface QuoteDao {

    @Query("SELECT * FROM quote")
    suspend fun getAll(): List<QuoteDb>

    @Query("SELECT * FROM quote WHERE language = :language")
    suspend fun getAllByLanguage(language: String): List<QuoteDb>

    @Insert
    suspend fun insertAll(quotes: List<QuoteDb>)

    @Update
    suspend fun update(quote: QuoteDb)

    @Delete
    suspend fun delete(user: QuoteDb)
}