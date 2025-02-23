package com.felina.ummuquran.data.local
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ramadan")
data class Ramadan(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "startTime")
    val startTime: String,
    @ColumnInfo(name = "priorityLevel")
    val priorityLevel: String,
    @ColumnInfo(name = "isDone")
    val isDone: Boolean
)