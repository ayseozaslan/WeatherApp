package com.example.weatherapp.model.entities

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class MiniLineCraftView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var prevTemp: Float? = null
    var currTemp: Float = 0f
    var nextTemp: Float? = null

    private val paint = Paint().apply {
        color = Color.YELLOW
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        val startX = 0f
        val endX = width.toFloat()

        val leftX = width * 0.25f
        val rightX = width * 0.75f

        // Sol çizgi (prev ile curr)
        prevTemp?.let { prev ->
            val startY = centerY + (currTemp - prev) * 5  // 5 piksellik ölçekleme, ayarla
            canvas.drawLine(0f, startY, leftX, centerY, paint)
        }

        // Sağ çizgi (curr ile next)
        nextTemp?.let { next ->
            val endY = centerY + (next - currTemp) * 5    // aynı ölçekleme
            canvas.drawLine(rightX, centerY, endX, endY, paint)
        }
    }
}
