package com.example.speedometer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock

class DataService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
    private val binder = object : ISpeedDataAidlInterface.Stub() {
        override fun getSpeed(): Int {
            return (Math.sin(SystemClock.elapsedRealtime().toDouble())*200).toInt()
        }
    }

}