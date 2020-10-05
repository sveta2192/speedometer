package com.example.speedometer

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class TwoPointersViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if(ev.pointerCount==1 && ev.action==MotionEvent.ACTION_MOVE) {
            return false
        } else return super.onTouchEvent(ev)
    }
}