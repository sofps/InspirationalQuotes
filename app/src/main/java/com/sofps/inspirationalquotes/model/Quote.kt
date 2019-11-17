package com.sofps.inspirationalquotes.model

import androidx.room.*

@Entity(tableName = "quote")
class Quote(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        var id: Long = 0L,

        var text: String?,

        var author: String?,

        @ColumnInfo(name = "times_showed", defaultValue = "0")
        var timesShowed: Long = 0L,

        var language: String?
)

@Dao
interface QuoteDao {

    @Query("SELECT * FROM quote")
    suspend fun getAll(): List<Quote>

    @Query("SELECT * FROM quote WHERE language = :language")
    suspend fun getAllByLanguage(language: String): List<Quote>

    @Insert
    suspend fun insertAll(quotes: List<Quote>)

    @Update
    suspend fun update(quote: Quote)

    @Delete
    suspend fun delete(user: Quote)
}
