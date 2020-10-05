package com.example.speedometer

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin


abstract class CircularGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    minValue: Int = 0,
    maxValue: Int = 60,
    valueStep: Int = 10,
    private val tickStep: Int = 2,
    note: String = ""
) : View(context, attrs) {

    private  val mNote = note
    private var attachedToWindow = false
    //lets get 300Grad as gauge value
    private val startDegree = -240
    private val endDegree = 60


    private val valueStep = valueStep
    private val maxValue = maxValue

    private var valuesNum = maxValue/valueStep
    private var tickNumber = (endDegree - startDegree) / tickStep

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val valuesTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var valuesTextRadius = 0.8F
    private val indicator = LineIndicator()

    private var speedAnimator: ValueAnimator? = null
    var dataService : ISpeedDataAidlInterface? = null
    var gaugeValue = 0

    private var start : Boolean = true
    init {
        valuesNum = maxValue/valueStep
        tickNumber = maxValue / tickStep

        centerPaint.color = Color.WHITE

        valuesTextPaint.style = Paint.Style.FILL
        valuesTextPaint.color = Color.WHITE

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircularGaugeView,
            0, 0
        )
        var color = a.getString(R.styleable.CircularGaugeView_gauge_background_color)

        backgroundPaint.color = Color.parseColor(if (color==null || color?.isEmpty()) "gray" else color);
        valuesTextPaint.textSize = a.getInt(R.styleable.CircularGaugeView_font_size, 100).toFloat()
        valuesTextRadius = a.getFloat(R.styleable.CircularGaugeView_note_margin, 0.8F)

    }
    override fun onDraw(canvas: Canvas) {

        var radius = width/2
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (radius).toFloat(),
            backgroundPaint
        )
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (radius / 10).toFloat(),
            centerPaint
        )

        drawTick(canvas)
        drawValues(canvas)
        drawNote(canvas, mNote)
        indicator.draw(canvas, gaugeValue)
    }

    fun drawTick(canvas: Canvas) {
        val radius = width/2
        tickPaint.color = Color.RED
        tickPaint.strokeWidth = 3F
        for ( i in 0..tickNumber) {
            if((i*tickStep)%valueStep==0) {
                tickPaint.color = Color.RED
            } else {
                tickPaint.color = Color.WHITE
            }
            val angle = toRadians(tickAngle(i, tickNumber).toDouble())
            val x = moveToCenterX((radius * cos(angle)).toFloat())
            val y =  moveToCenterY((radius * sin(angle)).toFloat())
            val x1 = moveToCenterX((radius * 0.9 * cos(angle)).toFloat())
            val y1 = moveToCenterY((radius * 0.9 * sin(angle)).toFloat())
            canvas.drawLine(x, y, x1, y1, tickPaint)
        }
    }

    fun onDataChanged(value: Int){
        gaugeValue = value
        invalidate()
    }

    private fun drawValues(canvas: Canvas) {
        val radius = (width/2*valuesTextRadius).toInt()
        valuesTextPaint.textAlign = Paint.Align.CENTER

        for ( i in 0..valuesNum) {
            val angle = toRadians(tickAngle(i, valuesNum).toDouble())
            val r = Rect()
            val text = (valueStep * i).toString()
            valuesTextPaint.getTextBounds(text, 0, text.length, r)
            val xPos = moveToCenterX((radius * cos(angle)).toFloat())
            var yPos = moveToCenterY((radius * sin(angle)).toFloat())
            yPos += Math.abs(r.height()) / 2
            canvas.drawText(text, xPos, yPos, valuesTextPaint)
        }
    }

    private fun drawNote(canvas: Canvas, note: String) {
        val noteTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        noteTextPaint.textSize = valuesTextPaint.textSize/2
        val radius = (width/2*0.8).toInt()
        val angle = toRadians((startDegree + 15).toDouble())
        val xPos = moveToCenterX((radius * cos(angle)).toFloat())
        var yPos = moveToCenterY((radius * sin(angle)).toFloat())
        canvas.drawText(note, xPos, yPos, noteTextPaint)
    }

    fun moveToCenterX(x: Float) =  width/2+x
    fun moveToCenterY(y: Float) =  height/2+y
    fun getCenterX() = width/2
    fun getCenterY() = height/2

    fun tickAngle(i: Int, step: Int) = startDegree+i*(endDegree-startDegree)/step
    fun valueToDegree(value: Int) = startDegree + (endDegree-startDegree)/maxValue*value
    inner class LineIndicator() {
        private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        init {
            indicatorPaint.color = Color.BLUE
            indicatorPaint.strokeWidth = 6F
        }
        fun draw(canvas: Canvas, value: Int) {
            val radius = width/2*0.9
            val angle = toRadians(valueToDegree(value).toDouble())
            val x = moveToCenterX((radius * cos(angle)).toFloat())
            val y =  moveToCenterY((radius * sin(angle)).toFloat())
            canvas.drawLine(getCenterX().toFloat(), getCenterY().toFloat(), x, y, indicatorPaint)

        }
    }
}