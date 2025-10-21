package com.example.weatherapp.model.entities

data class DailyWeather(
    val date: String, // Örn: "2025-07-30"
    val hours: List<WeatherData>  ,// O günün 3 saatlik verileri
    val mainCondition: String, // örn: "Clear", "Clouds", "Rain"
    val description: String            // Kullanıcıya gösterilecek açıklama
//ek olarak bu eklendi
)