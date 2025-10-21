package com.example.weatherapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemHourlyWeatherBinding
import com.example.weatherapp.model.entities.HourlyWeather
import com.example.weatherapp.model.entities.WeatherData
import com.example.weatherapp.model.entities.TemperatureLineChartView


class HourlyWeatherAdapter(private var items: List<HourlyWeather>) : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder>() {

    inner class HourlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHour: TextView = view.findViewById(R.id.tvHour)
        val tvTemp: TextView = view.findViewById(R.id.tvTemp)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_weather, parent, false)
        return HourlyViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = items[position]
        holder.tvHour.text = item.hour
        holder.tvTemp.text = "${item.temp}°C"
        holder.ivIcon.setImageResource(getWeatherIconRes(item.icon))
    }

    fun updateList(newItems: List<HourlyWeather>) {//newItems dışarıdan gelen veri , items=Adapter içinde olan veri
        items = newItems
        notifyDataSetChanged()
    }
}

private fun getWeatherIconRes(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.sssunnyy
        "01n" -> R.drawable.sssunnyy
        "02d", "02n" -> R.drawable.pppartlycloudy
        "03d", "03n" -> R.drawable.pppartlycloudy
        "04d", "04n" -> R.drawable.pppartlycloudy
        "09d", "09n" -> R.drawable.rrrainyy
        "10d", "10n" -> R.drawable.rrrainyy
        "11d", "11n" -> R.drawable.rrrainyy
        "13d", "13n" -> R.drawable.sssnowyy
        "50d", "50n" -> R.drawable.sssnowyy
        else -> R.drawable.pppartlycloudy
    }
}
