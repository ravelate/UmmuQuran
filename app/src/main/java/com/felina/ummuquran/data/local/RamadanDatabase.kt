package com.felina.ummuquran.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Ramadan::class], version = 1, exportSchema = false)
abstract class RamadanDatabase : RoomDatabase() {
    abstract fun ramadanDao(): RamadanDao
}