package com.example.simpletest.api

import com.example.simpletest.model.GeocodingResponse
import com.example.simpletest.model.WeatherResponse

interface WeatherApi {
    suspend fun getGeocodingForCity(city: String): GeocodingResponse
    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        currentWeather: Boolean = true
    ): WeatherResponse
}

// Wrapper to combine both APIs into one interface
// Could use Hilt/Dagger later but as we discussed during the call manual DI is fine for now
class WeatherApiImpl(
    private val geocodingApi: GeocodingApi,
    private val weatherForecastApi: WeatherForecastApi
) : WeatherApi {
    override suspend fun getGeocodingForCity(city: String): GeocodingResponse {
        return geocodingApi.getGeocodingForCity(city)
    }

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        currentWeather: Boolean
    ): WeatherResponse {
        return weatherForecastApi.getWeatherForecast(latitude, longitude, currentWeather)
    }
}