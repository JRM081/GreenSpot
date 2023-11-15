package com.example.greenspot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class PlantDetailViewModel (plantId: UUID): ViewModel() {
    private val plantRepository = PlantRepository.get()
    private val _plant: MutableStateFlow<Plant?> = MutableStateFlow(null)
    var isFirstTime = true


    val plant: StateFlow<Plant?> = _plant.asStateFlow()

    init {
        viewModelScope.launch {
            _plant.value = plantRepository.getPlant(plantId)
            Log.d("PlantDetailFragment", "Init run with $plantId")
        }
    }




    fun updatePlant(onUpdate: (Plant) -> Plant) {
        _plant.update { oldPlant ->
            oldPlant?.let { onUpdate(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        plant.value?.let { plantRepository.updatePlant(it) }
    }

    fun deletePlant() {
        plant.value?.let { plantRepository.deletePlant(it) }
    }
}


class PlantDetailViewModelFactory(
    private val plantId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlantDetailViewModel(plantId) as T
    }
}

