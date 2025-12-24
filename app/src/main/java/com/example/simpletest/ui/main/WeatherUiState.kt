package com.example.simpletest.ui.main

sealed class WeatherUiState {
    object Empty : WeatherUiState()
    object Loading : WeatherUiState()
    object Idle : WeatherUiState()
    data class Success(
        val cityName: String,
        val temperature: Double,
        val weatherCode: Int) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()

}