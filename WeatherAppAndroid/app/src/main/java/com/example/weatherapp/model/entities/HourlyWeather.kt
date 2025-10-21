package com.example.weatherapp.model.entities

data class HourlyWeather(
    val hour: String,
    val temp: Int,
    val icon: String,
    val dayName: String
)
