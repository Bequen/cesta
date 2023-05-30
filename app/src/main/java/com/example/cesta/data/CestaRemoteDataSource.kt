package com.example.cesta.data

import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL


class CestaRemoteDataSource(private val cestaApi: CestaApi = CestaApi(),
                            private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    suspend fun fetchStopFeatures(like: String): List<StopFeature> =
        withContext(ioDispatcher) {
            cestaApi.fetchStops(like)
        }

    suspend fun fetchStopFeaturesByAgency(agencyId: Long, like: String): List<StopFeature> =
        withContext(ioDispatcher) {
            cestaApi.fetchStopsByAgency(agencyId, like)
        }

    suspend fun fetchTripDepartures(agencyId: Long, tripId: Long) : TripDeparturesApiResponse =
        withContext(ioDispatcher) {
            cestaApi.fetchTripDepartures(agencyId, tripId)

            /* Pair(
                    Trip(
                        result.trip.id,
                        result.trip.agencyId,
                        result.trip.routeId,
                        null,
                        result.trip.headsign
                    ),
            result.departures.map { x ->
                Departure(
                    x.id,
                    x.agencyId,
                    x.stopId,
                    x.stop,
                    result.trip.routeId,
                    x.tripId,
                    x.sequence,
                    LocalDateTime(
                        Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
                        x.departure
                    ).toInstant(TimeZone.UTC)
                )
            }) */
        }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchDepartures(stopId: Long, agencyId: Long, time: Instant) : List<DepartureView> =
        withContext(ioDispatcher) {
            cestaApi.fetchDepartures(stopId, agencyId, time).map { x ->
                DepartureView(
                    Departure(
                        x.id,
                        x.agencyId,
                        x.stopId,
                        0,
                        x.tripId,
                        x.sequence,
                        LocalDateTime(Clock.System.now().toLocalDateTime(TimeZone.UTC).date, x.departure).toInstant(TimeZone.UTC)),
                    Route(
                        x.trip!!.route!!.id,
                        x.trip.headsign,
                        x.trip.route!!.shortName,
                        Color.parseColor("#" + x.trip.route.color).toColor().toArgb().toString(),
                        x.trip.route.type))
            }
        }
}

@kotlinx.serialization.Serializable
data class RouteResponse (
    val id: Long,
    @SerialName("short_name") val shortName: String,
    @SerialName("long_name")val longName: String,
    val color: String,
    @SerialName("route_type") val type: Int
);

@kotlinx.serialization.Serializable
data class TripResponse (
    val id: Long,
    @SerialName("agency_id") val agencyId: Long,
    @SerialName("route_id")val routeId: Long,
    val route: RouteResponse?,
    val headsign: String
);

@kotlinx.serialization.Serializable
data class DepartureResponse(
    @SerialName("departure_id") val id: Long,
    @SerialName("agency_id") val agencyId: Long,
    @SerialName("trip_id") val tripId: Long,
    val trip: TripResponse?,
    val arrival: LocalTime,
    val departure: LocalTime,
    @SerialName("stop_id") val stopId: Long,
    val stop: Stop?,
    val sequence: Int
);

@kotlinx.serialization.Serializable
data class TripDeparturesApiResponse (val trip: TripResponse, val departures: List<DepartureResponse>)

class CestaApi {
    private val address = "142.93.161.44"
    private val port = "8080"

    fun fetchStops(like: String) : List<StopFeature> {
        val urlConnection =
            URL("http://${address}:${port}/api/v1/features/stops?like=${like}").openConnection() as HttpURLConnection
        val data = urlConnection.inputStream.bufferedReader().readText()
        return Json.decodeFromString<List<StopFeature>>(data)
    }

    fun fetchStopsByAgency(agencyId: Long, like: String) : List<StopFeature> {
        val urlConnection =
            URL("http://${address}:${port}/api/v1/features/stops/agency/${agencyId}?like=${like}").openConnection() as HttpURLConnection
        val data = urlConnection.inputStream.bufferedReader().readText()
        return Json.decodeFromString<List<StopFeature>>(data)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchDepartures(stopId: Long, agencyId: Long, time: Instant) : List<DepartureResponse> {
        val urlConnection =
            URL("http://${address}:${port}/api/v1/stop/${stopId}/departures?agency_id=${agencyId}&day=${time.toLocalDateTime(TimeZone.UTC).year}-${time.toLocalDateTime(TimeZone.UTC).monthNumber}-${time.toLocalDateTime(TimeZone.UTC).dayOfMonth}&time=${time.toLocalDateTime(TimeZone.UTC).hour}:${time.toLocalDateTime(TimeZone.UTC).minute}:${time.toLocalDateTime(TimeZone.UTC).second}").openConnection() as HttpURLConnection
        val data = urlConnection.inputStream.bufferedReader().readText()
        return Json.decodeFromString(data)
    }

    fun fetchTripDepartures(agencyId: Long, tripId: Long) : TripDeparturesApiResponse {
        val urlConnection =
            URL("http://${address}:${port}/api/v1/agency/${agencyId}/trip/${tripId}/departures").openConnection() as HttpURLConnection
        val data = urlConnection.inputStream.bufferedReader().readText()
        return Json.decodeFromString(data)
    }
}