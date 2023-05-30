package com.example.cesta.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cesta.repositories.StopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class StopInfoUiState(
    val stop: Stop,
    val departures: List<DepartureView> = mutableListOf()
)

class StopInfoViewModel(val repo: StopRepository,
                        val stop: Stop) : ViewModel() {
    var uiState by mutableStateOf(StopInfoUiState(stop))
        private set

}