package com.example.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemDailyWeatherBinding
import com.example.weatherapp.model.entities.DailyWeather
import com.example.weatherapp.model.entities.HourlyWeather
import com.example.weatherapp.model.entities.WeatherData
import java.text.SimpleDateFormat
import java.util.Locale

class DailyWeatherAdapter(private var items: List<DailyWeather>) :
    RecyclerView.Adapter<DailyWeatherAdapter.DailyViewHolder>() {

    class DailyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvDesc: TextView = view.findViewById(R.id.tvDescription)
        val tvTempRange: TextView = view.findViewById(R.id.tvTempRange)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_weather, parent, false)
        return DailyViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val item = items[position]

        // Gün adını yıl, gün , ay şeklinden alır türkçe  haftanın günlerine çevirir
        val dayName = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(item.date)
            SimpleDateFormat("EEEE", Locale("tr")).format(date!!)
        } catch (e: Exception) {
            item.date
        }
        holder.tvDay.text = dayName

        // Icon drawable’dan yükle
        val iconCode = item.hours.firstOrNull()?.weather?.firstOrNull()?.icon ?: "01d"
        val iconResId = when (iconCode) {
            "01d" -> R.drawable.sssunnyy
            "01n" -> R.drawable.sssunnyy
            "02d", "02n" -> R.drawable.pppartlycloudy
            "03d", "03n" -> R.drawable.sssnowyy
            // Diğer icon kodları eklenebilir
            else -> R.drawable.pppartlycloudy
        }
        holder.ivIcon.setImageResource(iconResId)

        // Açıklama
        holder.tvDesc.text = item.description

        // --- Buraya ekle: Min ve Max sıcaklıkları göster ---

        // Eğer DailyWeather içinde direkt temp ve feelsLike varsa kullan
        // Aksi halde saatlik verilerden min ve max hesapla:
        val temps = item.hours.mapNotNull { it.main.temp }
        val feelsLikes = item.hours.mapNotNull { it.main.feelsLike }
/*
mapnotNull veriyi val nulll olanları atla
Eğer main.temp bazen null olabiliyorsa,
Uygulamanın çökmesini önlemek için bu değerleri görmezden gelmek en doğru yaklaşımdır.
 */
        val minTemp = temps.minOrNull() ?: 0.0
        val maxTemp = feelsLikes.maxOrNull() ?: 0.0

        holder.tvTempRange.text = "${minTemp.toInt()}°  ${maxTemp.toInt()}° "

    }

    // Bu fonksiyon adapterin dışından listeyi yenilemek için gerekli
    fun updateList(newItems: List<DailyWeather>) {
        items = newItems
        notifyDataSetChanged()
    }
}

// Min sıcaklık = temp, Max sıcaklık = feelsLik
