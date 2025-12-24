package com.example.simpletest.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.simpletest.api.RetrofitModule
import com.example.simpletest.databinding.FragmentMainBinding
import com.example.simpletest.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherFragment()
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by lazy {
        val repository = WeatherRepository(RetrofitModule.weatherApi)
        val factory = WeatherViewModelFactory(repository)
        ViewModelProvider(this, factory)[WeatherViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchInput()
        observeUiState()
    }

    private fun setupSearchInput() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchQuery(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
                // Could add input validation here if needed
            }
        })
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is WeatherUiState.Idle -> {
                        showIdleState()
                    }
                    is WeatherUiState.Loading -> {
                        showLoadingState()
                    }
                    is WeatherUiState.Success -> {
                        showSuccessState(state)
                    }
                    is WeatherUiState.Error -> {
                        showErrorState(state.message)
                    }
                    is WeatherUiState.Empty -> {
                        showEmptyState()
                    }
                }
            }
        }
    }

    private fun showIdleState() {
        binding.progressBar.visibility = View.GONE
        binding.weatherCard.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.emptyText.visibility = View.GONE
    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.weatherCard.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.emptyText.visibility = View.GONE
    }

    private fun showSuccessState(state: WeatherUiState.Success) {
        binding.progressBar.isVisible = false
        binding.weatherCard.isVisible = true
        binding.errorText.isVisible = false
        binding.emptyText.isVisible = false

        binding.cityName.text = state.cityName
        binding.temperature.text = "${state.temperature}°C"
        // TODO: Could map weatherCode to actual descriptions (sunny, cloudy, etc)
        binding.weatherDescription.text = "Weather Code: ${state.weatherCode}"
    }

    private fun showErrorState(message: String) {
        binding.progressBar.isVisible = false
        binding.weatherCard.isVisible = false
        binding.errorText.isVisible = true
        binding.emptyText.isVisible = false
        binding.errorText.text = message
    }

    private fun showEmptyState() {
        binding.progressBar.isVisible = false
        binding.weatherCard.isVisible = false
        binding.errorText.isVisible = false
        binding.emptyText.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}