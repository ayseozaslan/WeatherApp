package com.example.weatherapp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.weatherapp.R
import com.example.weatherapp.adapter.DailyWeatherAdapter
import com.example.weatherapp.adapter.HourlyWeatherAdapter
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.model.entities.DailyWeather
import com.example.weatherapp.model.entities.HourlyWeather
import com.example.weatherapp.model.entities.TemperatureLineChartView
import com.example.weatherapp.model.entities.WeatherData
import com.example.weatherapp.viewModel.WeatherViewModel
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.Duration
import androidx.recyclerview.widget.RecyclerView




class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: DailyWeatherAdapter

    // Saatlik adapteri ekle
    private lateinit var hourlyAdapter: HourlyWeatherAdapter

   // var  lottieView = view?.findViewById<LottieAnimationView>(R.id.lottieWeather)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]//WeatherViewModel sınıfından bir ViewModel nesnesi oluşturur



        observeViewModel()

        // İlk veri çekimi - İstanbul
        viewModel.fetchForecastByCityName("Istanbul")

        adapter = DailyWeatherAdapter(emptyList())
        binding.recyclerViewDaily.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDaily.adapter = adapter

        // Saatlik RecyclerView için yatay layout ve adapter ayarla
        hourlyAdapter  = HourlyWeatherAdapter(emptyList())
        binding.recyclerViewHourly.adapter = hourlyAdapter
        binding.recyclerViewHourly.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        binding.btnGetWeather.setOnClickListener {
            val cityName = binding.etCity.text.toString().trim()
            if (cityName.isNotEmpty()) {
                viewModel.fetchForecastByCityName(cityName)
                //hideKeyboard()
            } else {
                Toast.makeText(requireContext(), "Lütfen şehir adı girin", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }


//kullanıcı arayüzü, veri güncellendikçe kendiliğinden yenilenir.
    //WeatherViewModel içindeki forecast verisini gözlemlemek (observe etmek).
//Bu veri değiştiğinde otomatik olarak UI (görsel arayüz) güncellenebilir.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        viewModel.forecast.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                // --- Günlük veriler ---
                val dailyList = convertToDailyList(response.list)
                adapter.updateList(dailyList)

                val cityName = viewModel.currentCityName ?: "Bilinmeyen"
                binding.tvCityName.text = cityName

                val feelsLike = response.list[0].main.feelsLike
                binding.tvFeelsLike.text = "Hissedilen Sıcaklık: ${feelsLike.toInt()}°"

                // Lottie animasyonu sadece günlük hava durumu açıklamasına göre ayarlanır
                if (dailyList.isNotEmpty()) {
                    updateLottieAnimation(dailyList[0].mainCondition)
                }

                /* --- Saatlik veriler ---
                val hourlyList = viewModel.mapToHourlyForecastItems(response.list)

                val nowHour = LocalTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")

                val currentHour = hourlyList.minByOrNull {
                    val itemTime = LocalTime.parse(it.hour, formatter)
                    Duration.between(itemTime, nowHour).abs()
                }

                val listWithCurrent = mutableListOf<HourlyWeather>()
                currentHour?.let { listWithCurrent.add(it) }
                listWithCurrent.addAll(hourlyList.filter { it.hour != currentHour?.hour })

                hourlyAdapter.updateList(listWithCurrent)

                 */
            } else {
                Toast.makeText(requireContext(), "Hava durumu bilgisi alınamadı.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.todayHourlyList.observe(viewLifecycleOwner) { hourlyList ->
            if (hourlyList.isNotEmpty()) {
                val temps = hourlyList.map { it.temp.toFloat() }

                hourlyAdapter.updateList(hourlyList)
                binding.tempChart.setTemperatures(temps)

                binding.recyclerViewHourly.visibility = View.VISIBLE
                binding.horizontalScrollChart.visibility = View.VISIBLE

                binding.recyclerViewHourly.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        binding.horizontalScrollChart.scrollBy(dx, 0)
                    }
                })

                val itemWidth = 100
                val chartWidth = hourlyList.size * itemWidth
                binding.tempChart.layoutParams.width = chartWidth
                binding.tempChart.requestLayout()
                binding.tempChart.setTemperatures(temps)

            } else {
                binding.recyclerViewHourly.visibility = View.GONE
                binding.horizontalScrollChart.visibility = View.GONE
            }
        }
    }



    fun updateLottieAnimation(weatherDescription: String) {
        val desc = weatherDescription.lowercase()

        val animationFile = when {
            desc.contains("güneşli") || desc.contains("açık") -> "weather-sunny.json"
            desc.contains("yağmur") || desc.contains("yağmurlu") -> "weather-rainy.json"
            desc.contains("bulut") -> "weather-windy.json"   // Bulutlu, parçalı bulutlu vs.
            desc.contains("kar") -> "weather-snowy.json"
            else -> "weather-sunny.json"
        }

        binding.lottieWeather.setAnimation(animationFile)
        binding.lottieWeather.playAnimation()
    }


}

// Saatlik hava durumu verilerini gruplayarak, her gün için özet bir günlük hava durumu listesi oluşturmak
    private fun convertToDailyList(weatherDataList: List<WeatherData>): List<DailyWeather> {
        val grouped = weatherDataList.groupBy { it.dateTimeText.substring(0, 10) } // yyyy-MM-dd
        return grouped.map { entry ->
            val mainConditions = entry.value.mapNotNull { it.weather.firstOrNull()?.main }
            val descriptions = entry.value.mapNotNull { it.weather.firstOrNull()?.description }

            val mostFrequentMainRaw = mainConditions.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "Clear"
            val mostFrequentDescRaw = descriptions.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "clear sky"

            val mostFrequentMain = getTurkishMainCondition(mostFrequentMainRaw)
            val mostFrequentDesc = getTurkishDescription(mostFrequentDescRaw)
            Log.d("DailyWeather", "Tarih: ${entry.key}, Durum: $mostFrequentMain")

            DailyWeather(
                date = entry.key,
                hours = entry.value,
                mainCondition = mostFrequentMain, //eklendi
                description = mostFrequentDesc.replaceFirstChar { it.uppercase() } //eklendi

            )
        }
    }

    /*bu fonksiyon eklendi
    private fun getBackgroundResourceByCondition(condition: String): Int {
        return when (condition.lowercase()) {
            "clear" -> R.drawable.sunny
            "clouds" -> R.drawable.cloudy
            "rain", "drizzle" -> R.drawable.rainy
           // "thunderstorm" -> R.drawable.bg_thunderstorm
            "snow" -> R.drawable.snowy
           // "mist", "fog", "haze" -> R.drawable.bg_foggy
            else -> R.drawable.partlycloudy
        }
    }
*/
/*
    @SuppressLint("ServiceCast")
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etCity.windowToken, 0)
    }

 */

    private fun getTurkishMainCondition(condition: String): String {
        return when (condition.lowercase()) {
            "clear" -> "Açık"
            "clouds" -> "Bulutlu"
            "rain" -> "Yağmurlu"
            "drizzle" -> "Çiseleme"
            "thunderstorm" -> "Gök gürültülü"
            "snow" -> "Karlı"
            "mist", "fog", "haze" -> "Sisli"
            else -> "Bilinmeyen"
        }
    }

    private fun getTurkishDescription(description: String): String {
        return when (description.lowercase()) {
            "clear sky" -> "Açık gökyüzü"
            "few clouds" -> "Az bulutlu"
            "scattered clouds" -> "Parçalı bulutlu"
            "broken clouds" -> "Çok bulutlu"
            "overcast clouds" -> "Kapalı"
            "light rain" -> "Hafif yağmur"
            "moderate rain" -> "Orta şiddette yağmur"
            "heavy intensity rain" -> "Yoğun yağmur"
            "thunderstorm" -> "Gök gürültüsü"
            "snow" -> "Kar yağışı"
            "light snow" -> "Hafif kar"
            "mist" -> "Sis"
            else -> description.replaceFirstChar { it.uppercase() } // Çevrilmemişse olduğu gibi yaz
        }
    }








//}









