package com.sofps.inspirationalquotes.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "quote")
data class QuoteDb(

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        val id: Long = 0L,

        val text: String?,

        val author: String?,

        @ColumnInfo(name = "times_showed", defaultValue = "0")
        val timesShowed: Long = 0L,

        val language: String?
) : Parcelable
