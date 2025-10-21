package com.example.weatherapp.model.entities

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.graphics.Path
import android.graphics.PointF

class TemperatureLineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var temps: List<Float> = emptyList()

    private val linePaint = Paint().apply {
        color = Color.YELLOW
        strokeWidth = 6f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val pointPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }

    private val path = Path()

    fun setTemperatures(temperatures: List<Float>) {
        temps = temperatures
       requestLayout() // ölçüm tekrar yapılsın
        invalidate()    // çizim güncellensin
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dpPerPoint = 80 // her sıcaklık değeri için genişlik (dp)
        val density = resources.displayMetrics.density
        val desiredWidth = (temps.size * dpPerPoint * density).toInt()
        val desiredHeight = MeasureSpec.getSize(heightMeasureSpec)

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }


    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 36f
        textAlign = Paint.Align.CENTER // Ortalamak için
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (temps.isEmpty()) return

        val spacing = 220f // Noktalar arası mesafe daha kısa
        val chartHeight = height.toFloat()

        val maxTemp = temps.maxOrNull() ?: return
        val minTemp = temps.minOrNull() ?: return
        val tempRange = maxTemp - minTemp

        val points = mutableListOf<PointF>()

        temps.forEachIndexed { index, temp ->
            val x = spacing * index + spacing / 2f // Ortaya hizalama
            val y = chartHeight - ((temp - minTemp) / tempRange) * chartHeight
            points.add(PointF(x, y))
        }

        // Çizgileri birleştir
        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        canvas.drawPath(path, linePaint)

        // Noktaları çiz
        for (point in points) {
            canvas.drawCircle(point.x, point.y, 10f, pointPaint)
        }

        // Sıcaklık metinlerini yazmıyoruz — bu blok kaldırıldı
    }


}
