package com.example.simpletest.api

import com.example.simpletest.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForecastApi {
    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true
    ): WeatherResponse
}

