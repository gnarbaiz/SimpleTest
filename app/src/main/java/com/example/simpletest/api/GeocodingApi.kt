package com.example.simpletest.api

import com.example.simpletest.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("v1/search")
    suspend fun getGeocodingForCity(@Query("name") city: String): GeocodingResponse
}

