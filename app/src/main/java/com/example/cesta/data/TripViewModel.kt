package com.example.cesta.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cesta.Trip
import com.example.cesta.repositories.StopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class TripPageUiState (
    val trip: TripDeparturesApiResponse?
)

class TripViewModel(val agencyId: Long,
                    val repo: StopRepository) : ViewModel() {
    var uiState by mutableStateOf(TripPageUiState(null))
        private set

    fun fetch(tripId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.getTripPath(agencyId, tripId)
            if(result.isPresent) {
                uiState = uiState.copy(trip = result.get())
            }
        }
    }
}