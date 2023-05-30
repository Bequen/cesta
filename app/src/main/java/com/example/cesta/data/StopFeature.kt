package com.example.cesta.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName

@Entity(tableName = "stop_features")
@kotlinx.serialization.Serializable
data class StopFeature(@ColumnInfo(name = "stop_id") @PrimaryKey var id: Long,
                       @SerialName("agency_id")  @ColumnInfo(name = "agency_id") var agencyId: Long,
                       var name: String,
                       var lat: Double,
                       var lon: Double) {
}