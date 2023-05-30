package com.example.cesta.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes")
    fun getAll() : List<Route>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(route: Route) : Long
}