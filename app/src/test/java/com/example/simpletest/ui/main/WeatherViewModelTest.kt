package com.example.simpletest.ui.main

import com.example.simpletest.model.WeatherResult
import com.example.simpletest.repository.WeatherRepository
import com.example.simpletest.ui.ui.main.WeatherUiState
import com.example.simpletest.ui.ui.main.WeatherViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: WeatherRepository = mockk()
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Idle`() = runTest(testDispatcher) {
        val initialState = viewModel.uiState.value
        assertTrue(initialState is WeatherUiState.Idle)
    }

    @Test
    fun `search with success result should emit Success state`() = runTest(testDispatcher) {
        // Given
        val cityName = "London"
        val temperature = 15.5
        val weatherCode = 61
        val query = "London"
        
        coEvery {
            repository.getWeatherForCity(query)
        } returns WeatherResult.Success(cityName, temperature, weatherCode)

        // When
        viewModel.searchQuery(query)
        advanceTimeBy(600) // Wait for debounce (500ms) + some buffer

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
        assertEquals(cityName, (state as WeatherUiState.Success).cityName)
        assertEquals(temperature, (state as WeatherUiState.Success).temperature, 0.01)
        assertEquals(weatherCode, (state as WeatherUiState.Success).weatherCode)
    }

    @Test
    fun `search with empty result should emit Empty state`() = runTest(testDispatcher) {
        // Given
        val query = "NonExistentCity"
        coEvery {
            repository.getWeatherForCity(query)
        } returns WeatherResult.Empty

        // When
        viewModel.searchQuery(query)
        advanceTimeBy(600)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Empty)
    }

    @Test
    fun `search with error should emit Error state`() = runTest(testDispatcher) {
        // Given
        val query = "ErrorCity"
        val errorMessage = "Network error"
        coEvery {
            repository.getWeatherForCity(query)
        } returns WeatherResult.Error(errorMessage)

        // When
        viewModel.searchQuery(query)
        advanceTimeBy(600)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Error)
        assertEquals(errorMessage, (state as WeatherUiState.Error).message)
    }

}

