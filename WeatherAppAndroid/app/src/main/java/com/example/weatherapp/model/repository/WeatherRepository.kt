package com.example.weatherapp.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.model.entities.GeocodingResponse
import com.example.weatherapp.model.entities.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository {

    private val weatherService = RetrofitClient.weatherService
    private val geoService = RetrofitClient.geoService
    private val apiKey = "3bdcea10f1a6ec3b608f8ff6758ebfd2"

    suspend fun getFiveDayForecast(cityName: String): WeatherResponse? = withContext(Dispatchers.IO) {
        try {
            // 1. Şehir adına göre koordinatları al
            val geoResponse = geoService
                .getCoordinatesByCityName(cityName, 1, apiKey)//1: Sadece ilk sonuç gelsin demek.
                .execute()

            val geoData = geoResponse.body()?.firstOrNull()
            /*
            API yanıtının body() kısmını alır (yani JSON verisi).
            Eğer bu liste boş değilse ilk elemanı al.Boşsa null döner.
             */

            if (geoData != null) {
                // 2. Koordinatlara göre hava durumu verisini al
                val weatherResponse = weatherService
                    .getFiveDayForecast(
                        lat = geoData.lat,
                        lon = geoData.lon,
                        apiKey = apiKey
                    )
                    .execute()//Retrofit çağrısını senkron (bekleyerek) çalıştırır.sonucu hemen verir. await() veya enqueue() gibi asenkron değildir.
                    // API çağrısını yapar ve sonucu döndürür
                return@withContext weatherResponse.body()
                /*
                weatherResponse.body() ile hava durumu verisini döner.
                Eğer bu satır withContext(Dispatchers.IO) { ... } içindeyse,
                bu return@withContext ifadesi o bloğa özgü dönüş demektir.
                 */
            }

            return@withContext null// Eğer koordinat bilgisi yoksa, hava durumu da alınamaz.Boş döner

        } catch (e: Exception) {
            e.printStackTrace()//konsola hatayı yaz.
            return@withContext null
        }
    }
}
