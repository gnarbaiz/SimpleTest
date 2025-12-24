package com.example.simpletest.model

sealed class WeatherResult {
    object Loading : WeatherResult()
    object Empty : WeatherResult()
    data class Success(
        val cityName: String,
        val temperature: Double,
        val weatherCode: Int) : WeatherResult()
    data class Error(val message: String) : WeatherResult()
}