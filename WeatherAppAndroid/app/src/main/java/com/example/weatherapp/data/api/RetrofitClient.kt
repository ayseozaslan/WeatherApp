package com.example.weatherapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"


      private  val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Gson burada
            .build()

    val weatherService: WeatherApiService = retrofit.create(WeatherApiService::class.java)
    val geoService: GeocodingApiService = retrofit.create(GeocodingApiService::class.java)
}
     /*
     Bu satırlar, Retrofit'e diyoruz ki:
"Benim elimde bir interface var (örneğin WeatherApiService). Bu arayüzde yazdığım @GET(...) gibi
annotation'lara göre gerçek HTTP istekleri yapacak bir servis objesi oluştur."
weatherService → 5 günlük hava durumu verisini çekecek.
geoService → şehir adından enlem-boylam (koordinat) verisi alacak.
bir kere tanımlayıp,
✅ her yerden kolayca ulaşmak istiyoruz. bu yüzden retrofit içinde
      */
       /*
        WeatherApiService	Örneğin hava durumu verilerini almak için tanımladığın interface
        GeocodingApiService	Şehir adına karşılık gelen koordinatları almak için arayüz
        retrofit.create(...)	Retrofit nesnesiyle, arayüzü bir API servisi haline getirir
        Amaç:API çağrılarını temiz, test edilebilir ve kolay yönetilebilir hale getirmek

 */