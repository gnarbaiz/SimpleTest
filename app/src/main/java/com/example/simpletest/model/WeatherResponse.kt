package com.example.simpletest.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather
)

data class CurrentWeather(
    val temperature: Double,
    @SerializedName("weathercode")
    val weatherCode: Int // WMO weather code - could map to descriptions later
)
