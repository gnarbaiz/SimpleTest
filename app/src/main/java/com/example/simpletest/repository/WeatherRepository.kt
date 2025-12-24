package com.example.simpletest.repository

import com.example.simpletest.api.WeatherApi
import com.example.simpletest.model.WeatherResult

class WeatherRepository (
    private val weatherApi: WeatherApi
) {
    suspend fun getWeatherForCity(city: String): WeatherResult {
        return try {
            val geoResponse = weatherApi.getGeocodingForCity(city)
            // API returns list, we only need the first match
            val geo = geoResponse.results?.firstOrNull() ?: return WeatherResult.Empty

            val weather = weatherApi.getWeatherForecast(
                latitude = geo.latitude,
                longitude = geo.longitude
            )

            WeatherResult.Success(
                cityName = geo.name,
                temperature = weather.currentWeather.temperature,
                weatherCode = weather.currentWeather.weatherCode
            )

        } catch (e: Exception) {
            // TODO: Could add more specific error handling (network vs parsing, etc)
            WeatherResult.Error(e.message ?: "Unknown error occurred")
        }
    }
}
