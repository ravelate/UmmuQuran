package com.felina.ummuquran.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RamadanDao {
    @Query("SELECT * FROM ramadan where id = :id")
    fun getRamadanById(id: Int): Flow<Ramadan>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRamadan(ramadan: Ramadan): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg ramadans: Ramadan)
    @Delete(entity = Ramadan::class)
    fun deleteRamadan(ramadan: Ramadan)
    @Query("SELECT * FROM ramadan where date = :date")
    fun getDataRamadanByDate(date: String): List<Ramadan>
}
