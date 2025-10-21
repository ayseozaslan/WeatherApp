package com.example.weatherapp.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.model.entities.GeocodingResponse
import com.example.weatherapp.model.entities.HourlyWeather
import com.example.weatherapp.model.entities.WeatherData
import com.example.weatherapp.model.entities.WeatherResponse
import com.example.weatherapp.model.repository.WeatherRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()
    private val apiKey = "3bdcea10f1a6ec3b608f8ff6758ebfd2"

    var currentCityName: String? = null //eklendi

    // Canlı veri: Hava durumu tahmini (5 günlük)
    //Dışarıdan erişilen, sadece gözlemlenebilen versiyonudur. Fragment veya Activity buradan gözlem yapar.
    private val _forecast = MutableLiveData<WeatherResponse?>()
    //Değiştirilebilir bir veri kaynağıdır. ViewModel bu veriyi günceller.
    val forecast: LiveData<WeatherResponse?> = _forecast

    // Yüklenme durumu
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // Hata mesajı
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    //Saatlik hava durumu verisi
    private val _todayHourlyList = MutableLiveData<List<HourlyWeather>>()
    val todayHourlyList: LiveData<List<HourlyWeather>> = _todayHourlyList

    private val _weatherResponse = MutableLiveData<WeatherResponse>()
    val weatherResponse: LiveData<WeatherResponse> = _weatherResponse

    private val _weatherLiveData = MutableLiveData<WeatherResponse>()
    val weatherLiveData: LiveData<WeatherResponse> = _weatherLiveData


    // API çağrısı yap ve sonucu LiveData’ya aktar.Şehir ismine göre tahmin getir.
    fun fetchForecastByCityName(cityName: String) {

        currentCityName = cityName //eklendi
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getFiveDayForecast(cityName)
                if (response != null) {
                    _forecast.value = response
                    // Saatlik listeyi dönüştürüp LiveData'ya ata:
                    _todayHourlyList.value = mapToHourlyForecastItems(response.list)
                    _error.value = null
                } else {
                    _error.value = "Hava durumu verisi alınamadı."
                }
            } catch (e: Exception) {
                _error.value = "Bir hata oluştu: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    /*WeatherData nesnelerinden sadece bugüne ait saatlik verileri
    @RequiresApi(Build.VERSION_CODES.O)
    fun extractTodayHourlyList(list: List<WeatherData>): List<HourlyWeather> {
        val nowEpoch = System.currentTimeMillis() / 1000

        // Şu anki saate en yakın index
        val closestIndex = list.indexOfFirst { it.dateTime >= nowEpoch }.takeIf { it != -1 } ?: 0

        // 8 veri al (3 saatlik 8 veri = 24 saat)
        val endIndex = (closestIndex + 8).coerceAtMost(list.size)

        val sublist = list.subList(closestIndex, endIndex)

        // Map işlemi
        val formatterInput = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatterHour = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formatterDay = SimpleDateFormat("EEEE", Locale("tr"))

        return sublist.map { wd ->
            val date = formatterInput.parse(wd.dateTimeText)!!
            HourlyWeather(
                hour = formatterHour.format(date),
                temp = wd.main.temp.toInt(),
                icon = wd.weather.firstOrNull()?.icon ?: "01d",
                dayName = formatterDay.format(date)
            )
        }
    }
*/


    /*

     */
    //API verisi karmaşıktır ve UI için uygun değildir
    //Bu fonksiyon veriyi sadeleştirir ve gün/saat/sıcaklık/ikon olarak kullanılabilir hale getirir.
    fun mapToHourlyForecastItems(dataList: List<WeatherData>): List<HourlyWeather> {
        val formatterInput = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) //API’den gelen tarihi parse eder (örn: "2025-08-05 15:00:00")
        val formatterHour = SimpleDateFormat("HH:mm", Locale.getDefault())//Sadece saat kısmını alır (örn: "15:00")
        val formatterDay = SimpleDateFormat("EEEE", Locale("tr")) // Türkçe gün adı.Gün adını Türkçe formatlar (örn: "Salı")

        return dataList.mapNotNull { weatherData -> //mapNotNull { ... }	Her WeatherData'yı HourlyWeather’e çevirir, hata olursa geçer
            try {
                val date = formatterInput.parse(weatherData.dateTimeText)
                val hour = formatterHour.format(date!!)
                val dayName = formatterDay.format(date)

                HourlyWeather(
                    hour = hour,
                    temp = weatherData.main.temp.toInt(),
                    icon = weatherData.weather.firstOrNull()?.icon ?: "01d",
                    dayName = dayName
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
/*
         Girdi: WeatherData listesidir (API’den gelen ham veriler)
    Çıktı: HourlyWeather nesneleri içeren liste (UI’da göstermek için sade veri)
Her bir WeatherData nesnesi için:
Tarihi çözer (örneğin: "2025-08-05 15:00:00"
Saatini çıkarır (örneğin: "15:00")
Gün adını çıkarır (örneğin: "Salı")
Sıcaklığı alır (örneğin: 30)
Hava durumu ikon kodunu alır (örneğin: "01d")
Yeni HourlyWeather nesnesi oluşturur.
    HourlyWeather(
    hour = "15:00",
    temp = 30,
    icon = "01d",
    dayName = "Salı"
)veriler sadeleşmiş olur ve doğrudan  uı da kullanılır.recycleview

 */

