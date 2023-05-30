package com.example.cesta.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StopFeatureDao {
    @Query("SELECT * FROM stop_features")
    fun getAll(): List<StopFeature>

    @Query("SELECT * FROM stop_features WHERE name LIKE '%' || :like || '%'")
    fun getAllLike(like: String) : List<StopFeature>

    @Query("SELECT * FROM stop_features WHERE agency_id = :agencyId AND name LIKE '%' || :like || '%'")
    fun getAllByAgency(agencyId: Long, like: String): List<StopFeature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(feature: StopFeature) : Long
}