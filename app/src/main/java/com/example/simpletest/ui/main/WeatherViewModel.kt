package com.example.simpletest.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletest.model.WeatherResult
import com.example.simpletest.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState

    init {
        observeQuery()
    }

    fun searchQuery(query: String) {
        queryFlow.value = query
    }

    // Debounce search to avoid too many API calls while typing
    // 500ms seems like a good balance - not too slow, not too fast
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeQuery() {
        viewModelScope.launch {
            queryFlow
                .debounce(500)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .flatMapLatest { query ->
                    flow {
                        emit(WeatherResult.Loading)
                        emit(weatherRepository.getWeatherForCity(query))
                    }
                }
                .collect { result ->
                    _uiState.value = when (result) {
                        is WeatherResult.Empty -> WeatherUiState.Empty
                        is WeatherResult.Success -> WeatherUiState.Success(
                            result.cityName,
                            result.temperature,
                            result.weatherCode
                        )
                        is WeatherResult.Error -> WeatherUiState.Error(result.message)
                        is WeatherResult.Loading -> WeatherUiState.Loading
                    }
                }
        }
    }
}