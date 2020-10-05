package com.example.speedometer

import android.content.Context
import android.util.AttributeSet

public class SpeedometerView(context: Context, attrs: AttributeSet) : CircularGaugeView(context, attrs, 0,220,20,10,"km/h") {
    private fun init() {
       // speedometerPaint.style = Paint.Style.STROKE
       // circlePaint.color = 0xFF444444.toInt()
    }
}