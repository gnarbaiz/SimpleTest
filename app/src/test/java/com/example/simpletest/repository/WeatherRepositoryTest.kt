package com.example.simpletest.repository

import com.example.simpletest.api.WeatherApi
import com.example.simpletest.model.CurrentWeather
import com.example.simpletest.model.GeocodingResult
import com.example.simpletest.model.GeocodingResponse
import com.example.simpletest.model.WeatherResponse
import com.example.simpletest.model.WeatherResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class WeatherRepositoryTest {

    private lateinit var weatherApi: WeatherApi
    private lateinit var repository: WeatherRepository

    @Before
    fun setup() {
        weatherApi = mockk()
        repository = WeatherRepository(weatherApi)
    }

    @Test
    fun `getWeatherForCity with valid response returns Success`() = runTest {
        // Given
        val cityName = "London"
        val latitude = 51.5074
        val longitude = -0.1278
        val temperature = 15.5
        val weatherCode = 61

        val geocodingResponse = GeocodingResponse(
            results = listOf(
                GeocodingResult(
                    name = cityName,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        )

        val weatherResponse = WeatherResponse(
            currentWeather = CurrentWeather(
                temperature = temperature,
                weatherCode = weatherCode
            )
        )

        coEvery {
            weatherApi.getGeocodingForCity(cityName)
        } returns geocodingResponse

        coEvery {
            weatherApi.getWeatherForecast(latitude, longitude, true)
        } returns weatherResponse

        // When
        val result = repository.getWeatherForCity(cityName)

        // Then
        assertTrue(result is WeatherResult.Success)
        val successResult = result as WeatherResult.Success
        assertEquals(cityName, successResult.cityName)
        assertEquals(temperature, successResult.temperature, 0.01)
        assertEquals(weatherCode, successResult.weatherCode)

        coVerify(exactly = 1) { weatherApi.getGeocodingForCity(cityName) }
        coVerify(exactly = 1) { weatherApi.getWeatherForecast(latitude, longitude, true) }
    }

    @Test
    fun `getWeatherForCity with empty geocoding results returns Empty`() = runTest {
        // Given
        val cityName = "NonExistentCity"
        val geocodingResponse = GeocodingResponse(results = null)

        coEvery {
            weatherApi.getGeocodingForCity(cityName)
        } returns geocodingResponse

        // When
        val result = repository.getWeatherForCity(cityName)

        // Then
        assertTrue(result is WeatherResult.Empty)
        coVerify(exactly = 1) { weatherApi.getGeocodingForCity(cityName) }
        coVerify(exactly = 0) { weatherApi.getWeatherForecast(any(), any(), any()) }
    }
}

