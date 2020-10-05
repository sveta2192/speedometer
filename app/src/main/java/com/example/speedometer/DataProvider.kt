package com.example.speedometer

import android.util.Log

class DataProvider(private val dataService: ISpeedDataAidlInterface?, private var gaugeView: CircularGaugeView?) {
    var start : Boolean = true
    var dataProviderThread: Thread? = null
    fun startDataThread() {
        start = true
        if(dataProviderThread==null) {
            dataProviderThread =
                Thread {
                    while (start) {
                        try {
                            Thread.sleep(200)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        val gaugeValue = this.dataService?.speed ?: 0
                        gaugeView?.onDataChanged(gaugeValue)
                    }

                }
            dataProviderThread?.start()
        } else {
            Log.e("test","thread already running")
        }
    }
    fun stopDataThread(){
        start = false
        dataProviderThread = null
    }
    fun onChangeView(pagedView: CircularGaugeView?) {
        gaugeView = pagedView
    }
}