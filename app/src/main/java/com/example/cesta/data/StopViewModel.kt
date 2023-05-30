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
    fun fetchStopFeatures(like: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val features = repo.getAgencyStopFeatures(1, like)
            uiState = uiState.copy(features = features, stops = features.map { item ->
                StopInfoUiState(Stop(item.id, item.agencyId, item.name), repo.getDeparturesForStopFrom(item.id, item.agencyId, Clock.System.now()))
            })
        }
    }
}