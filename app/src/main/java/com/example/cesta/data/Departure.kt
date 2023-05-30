package com.example.cesta.data

import androidx.room.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName

@Entity(tableName = "departures")
@kotlinx.serialization.Serializable
data class Departure(@SerialName("departure_id") @ColumnInfo(name = "departure_id") @PrimaryKey(autoGenerate = true) var departureId: Long,
                     @SerialName("agency_id") @ColumnInfo(name = "agency_id") var agencyId: Long,
                     @SerialName("stop_id") @ColumnInfo(name="stop_id") var stopId: Long,
                     @SerialName("route_id") @ColumnInfo(name="route_id") var routeId: Long,
                     @SerialName("trip_id") @ColumnInfo(name="trip_id") var tripId: Long,
                     var sequence: Int,
                     var time: Instant) {

}

data class DepartureView(@Embedded val departure: Departure,
                         @Relation(
                             parentColumn = "route_id",
                             entityColumn = "route_id"
                         )
                         val route: Route)