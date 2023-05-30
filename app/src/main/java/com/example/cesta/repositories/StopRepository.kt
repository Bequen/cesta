package com.example.cesta.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.example.cesta.data.*
import kotlinx.datetime.Instant
import java.util.*

class StopRepository(val database: AppDb /* local data store */,
                     val remoteDataSource: CestaRemoteDataSource
) {
    suspend fun addStop(stop: Stop) : Stop {
        val stopCopy = stop.copy(id = 0)
        stopCopy.id = database.stopDao().insert(stop)
        return stopCopy
    }

    suspend fun addRoute(route: Route) : Route {
        val routeCopy = route.copy(routeId = 0)
        routeCopy.routeId = database.routeDao().insert(route)
        return routeCopy
    }

    suspend fun addDeparture(departure: Departure) : Departure {
        val departureCopy = departure.copy(departureId = 0)
        departureCopy.departureId = database.departureDao().insert(departure)
        return departureCopy
    }

    suspend fun addStopFeature(feature: StopFeature) : StopFeature {
        val featureCopy = feature.copy(id = 0)
        featureCopy.id = database.stopFeatureDao().insert(feature);
        return featureCopy
    }

    suspend fun getStop(stop_id: Long) : Stop = database.stopDao().get(stop_id)

    suspend fun getStops() : List<Stop> = database.stopDao().getAll()

    suspend fun getTripPath(agencyId: Long, tripId: Long) : Optional<TripDeparturesApiResponse> {
        try {
            return Optional.of(remoteDataSource.fetchTripDepartures(agencyId, tripId));
        } catch (e: Exception) {
            Log.e("REMOTE", "Failed to retrieve departures for trip: ${e.message}");
            return Optional.empty()
        }
    }

    suspend fun getAgencyStopFeatures(agencyId: Long, like: String): List<StopFeature> {
        try {
            val stops = remoteDataSource.fetchStopFeaturesByAgency(agencyId, like)

            stops.forEach {
                addStopFeature(it)
            }

            return stops;
        } catch(e: Exception) {
            Log.e("Remote", e.message.toString())
            return database.stopFeatureDao().getAllByAgency(agencyId, like);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDeparturesForStopFrom(stopId: Long, agencyId: Long, from: Instant): List<DepartureView> {
        try {
            val departures = remoteDataSource.fetchDepartures(stopId, agencyId, from)

            /* cache the results */
            departures.forEach {
                addDeparture(it.departure)
                addRoute(it.route)
            }

            return departures;
        } catch(e: Exception) {
            Log.e("Remote", e.message.toString())
            /* attempt to load from database */
            return database.departureDao().getAllViewsByStop(stopId, from)
        }
    }
}