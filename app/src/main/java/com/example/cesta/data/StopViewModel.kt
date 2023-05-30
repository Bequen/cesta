package com.example.cesta.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import com.example.cesta.repositories.StopRepository
import androidx.lifecycle.viewModelScope
import com.example.cesta.ui.theme.Blue
import com.example.cesta.ui.theme.Red
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

data class StopPageUiState(
    val stops: List<StopInfoUiState> = mutableListOf(),
    val features: List<StopFeature> = mutableListOf()
)

class StopViewModel(val repo: StopRepository) : ViewModel() {
    var uiState by mutableStateOf(StopPageUiState())
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetch() {
        viewModelScope.launch(Dispatchers.IO) {
            val stops = repo.getStops()

            uiState = uiState.copy(stops.map { item ->
                StopInfoUiState(item, repo.getDeparturesForStopFrom(item.id, item.agencyId, Clock.System.now()))
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchStopFeatures(like: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // uiState = uiState.copy(features = repo.getAgencyStopFeatures(1, like))
            val features = repo.getAgencyStopFeatures(1, like)
            uiState = uiState.copy(features = features, stops = features.map { item ->
                StopInfoUiState(Stop(item.id, item.agencyId, item.name), repo.getDeparturesForStopFrom(item.id, item.agencyId, Clock.System.now()))
            })
        }
    }

    fun populate() {
        /* viewModelScope.launch(Dispatchers.IO) {
            val neredin = repo.addStop(Stop(0, "Neredin, Krematorium"))
            val posta = repo.addStop(Stop(0, "Foerstrova Posta"))

            val route1 = repo.addRoute(Route(0, "Nereding", "7", Red.toArgb().toString(), 1))
            val route2 = repo.addRoute(Route(0, "Fibichova", "3", Blue.toArgb().toString(), 1))

            repo.addDeparture(Departure(0, neredin.id, route1.routeId, Clock.System.now()))
            repo.addDeparture(Departure(0, neredin.id, route2.routeId, Clock.System.now().plus(10, DateTimeUnit.MINUTE)))
            repo.addDeparture(Departure(0, neredin.id, route1.routeId, Clock.System.now().plus(32, DateTimeUnit.MINUTE)))
            repo.addDeparture(Departure(0, neredin.id, route2.routeId, Clock.System.now().plus(54, DateTimeUnit.MINUTE)))
        } */
    }
}