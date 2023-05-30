package com.example.cesta.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StopDao {
    @Query("SELECT * FROM stops")
    fun getAll() : List<Stop>

    @Query("SELECT * FROM stops WHERE id = :stopId LIMIT 1")
    fun get(stopId: Long) : Stop

    @Insert
    suspend fun insert(stop: Stop): Long
}