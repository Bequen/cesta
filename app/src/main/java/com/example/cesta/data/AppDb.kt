package com.example.cesta.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Stop::class, Departure::class, Route::class, StopFeature::class], version = 12)
@TypeConverters(InstantTypeConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun stopDao(): StopDao
    abstract fun departureDao(): DepartureDao
    abstract fun routeDao(): RouteDao
    abstract fun stopFeatureDao(): StopFeatureDao

    companion object {
        private var INSTANCE: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "my-database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}