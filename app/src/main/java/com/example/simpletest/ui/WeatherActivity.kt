package com.example.simpletest.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simpletest.R
import com.example.simpletest.ui.main.WeatherFragment

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, WeatherFragment.newInstance())
                .commitNow()
        }
    }
}