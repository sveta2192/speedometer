package com.example.speedometer

import android.content.Context
import android.util.AttributeSet

public class TachometerView(context: Context, attrs: AttributeSet) : CircularGaugeView(context, attrs, 0, 60, 10,2,"1/min\nx100") {
    private fun init() {

        // speedometerPaint.style = Paint.Style.STROKE
        // circlePaint.color = 0xFF444444.toInt()
    }
}