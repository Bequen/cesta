package com.example.cesta.data

import androidx.room.*
import kotlinx.datetime.Instant

@Dao
interface DepartureDao {
    @Query("SELECT * FROM departures")
    fun getAll() : List<Departure>

    @Query("SELECT * FROM departures WHERE stop_id = :stopId AND time(time) > time(:from) ORDER BY time ASC LIMIT 10")
    fun getAllByStop(stopId: Long, from: Instant) : List<Departure>

    @Transaction
    @Query("SELECT * FROM departures WHERE stop_id = :stopId AND time(time) > time(:from) ORDER BY time ASC LIMIT 10")
    fun getAllViewsByStop(stopId: Long, from: Instant) : List<DepartureView>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(departure: Departure) : Long
}