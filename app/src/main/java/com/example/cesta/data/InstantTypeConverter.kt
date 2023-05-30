package com.example.cesta.data

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class InstantTypeConverter {
    // private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromString(value: String): Instant {
        return Instant.parse(value);
        // return LocalDateTime.parse(value, formatter)
    }

    @TypeConverter
    fun toString(instant: Instant): String {
        return instant.toString()
    }
}