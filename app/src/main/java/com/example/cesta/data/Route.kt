package com.example.cesta.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
@kotlinx.serialization.Serializable
data class Route(@ColumnInfo(name = "route_id") @PrimaryKey(autoGenerate = true) var routeId: Long,
                 var headsign: String,
                 @ColumnInfo(name = "short_name") var shortName: String,
                 var color: String,
                 var type: Int) {
}