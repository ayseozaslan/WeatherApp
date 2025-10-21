package com.example.weatherapp.data.api

import com.example.weatherapp.model.entities.GeocodingResponse
import com.example.weatherapp.model.entities.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/forecast")
    fun getFiveDayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "tr"
    ): Call<WeatherResponse>
/*
    @GET("geo/1.0/direct")
    fun getCoordinatesByCityName(
        @Query("q") cityName: String,//Şehir adı
        @Query("limit") limit: Int = 1, //En fazla kaç sonuç dönsün (tek şehir için 1)
        @Query("appid") apiKey: String //
    ): Call<List<GeocodingResponse>>

 */
}/*
BASE_URL + endpoint şeklinde oluşur
Bunu OpenWeatherMap sitesindeki Geocoding API dokümantasyonundan alıyoruz:
🔗 https://openweathermap.org/api/geocoding-api
api.openweathermap.org/geo/1.0/direct?q=London&limit=5&appid={API key}
*/
/*
Bu endpoint, OpenWeatherMap'in 5 Day / 3 Hour Forecast API sayfasında açıklanır:
🔗 https://openweathermap.org/forecast5
https://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&appid={API key}
 */

