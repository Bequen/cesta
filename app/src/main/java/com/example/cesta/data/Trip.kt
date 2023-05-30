package com.example.cesta.data

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Trip(val id: Long,
                @SerialName("agency_id") var agencyId: Long,
                @SerialName("route_id") var routeId: Long,
                var route: Route?,
                var headsign: String)