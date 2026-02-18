package com.example.uitesting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitesting.ui.elements.AllergenItem
import com.example.uitesting.ui.elements.Forecast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val pollenRepository = PollenRepository()

    private val _allergens = MutableStateFlow<List<AllergenItem>>(emptyList())
    val allergens: StateFlow<List<AllergenItem>> = _allergens.asStateFlow()
    private val _forecast = MutableStateFlow<List<Forecast>>(emptyList())
    val forecast: StateFlow<List<Forecast>> = _forecast.asStateFlow()

    init {
        getCurrentPollenLevels()
        getFourDayPollenForecast()
    }

    private fun getCurrentPollenLevels() {
        viewModelScope.launch {
            val pollenData = pollenRepository.getCurrentPollenLevels()
            _allergens.value = pollenData
        }
    }

    private fun getFourDayPollenForecast() {
        viewModelScope.launch {
            val forecastData = pollenRepository.getFourDayPollenForecast()
            _forecast.value = forecastData
        }
    }
}
