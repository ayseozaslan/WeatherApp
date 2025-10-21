package com.example.weatherapp.model.entities

data class WeatherResponse (
    val cod :String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherData>,
    val city: City
)
data class GeocodingResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)
/*
json formatına uygun response (cevap) sınıfı oluşturuyoruz.
 */