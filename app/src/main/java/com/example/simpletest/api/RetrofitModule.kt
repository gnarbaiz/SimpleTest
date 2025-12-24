package com.example.simpletest.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitModule {
    
    private val geocodingRetrofit = Retrofit.Builder()
        .baseUrl("https://geocoding-api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val weatherRetrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val geocodingApi: GeocodingApi = geocodingRetrofit.create(GeocodingApi::class.java)
    private val weatherForecastApi: WeatherForecastApi = weatherRetrofit.create(WeatherForecastApi::class.java)
    
    // Both APIs have different base URLs, so I combine them here
    val weatherApi: WeatherApi = WeatherApiImpl(geocodingApi, weatherForecastApi)
}

