package com.example.weatherapp.model.entities

import com.google.gson.annotations.SerializedName

data class WeatherData(
    @SerializedName("dt")
    val dateTime: Long,

    @SerializedName("main")
    val main: MainInfo,

    @SerializedName("weather")
    val weather: List<WeatherDescription>,

    @SerializedName("dt_txt")
    val dateTimeText: String
)

data class MainInfo(//sıcaklık bilgisi nem
    @SerializedName("temp")
    val temp: Double,

    @SerializedName("feels_like")
    val feelsLike: Double,

    @SerializedName("humidity")
    val humidity: Int
)
data class WeatherDescription( //hava durumu açıklması
    @SerializedName("main")
    val main: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("icon")
    val icon: String
)
data class City( //şehir bilgisi
    @SerializedName("name")
    val name: String,

    @SerializedName("country")
    val country: String
)






