package com.example.speedometer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.PageTransformer


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public val BROADCAST_ACTION = "com.androidaidl.androidaidlservice.started"
class FullscreenActivity : AppCompatActivity() {

    val ACTION_START_DATA_GENERATOR = "com.androidaidl.androidaidlservice.ProductService"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = Intent(ACTION_START_DATA_GENERATOR)
        val component = ComponentName(packageName, "com.example.speedometer.DataService")


        val explicitIntent = Intent(intent)

        explicitIntent.component = component
        val res = bindService(explicitIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        log("bind res $res")

        val adapter = MyAdapter(supportFragmentManager)

        val viewPager: ViewPager = findViewById(R.id.viewpager)
        viewPager.adapter = adapter // устанавливаем адаптер

        viewPager.currentItem = 0 // выводим второй экран

        viewPager.setPageTransformer(false, object : PageTransformer {
            override fun transformPage(v: View, pos: Float) {
                val opacity = Math.abs(Math.abs(pos) - 1)
                v.setAlpha(opacity)
            }
        })
    }

    var aidlDataGeneratorService : ISpeedDataAidlInterface? = null
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // приводим IBinder к нужному нам типу через Stub реализацию интерфейса
            aidlDataGeneratorService = ISpeedDataAidlInterface.Stub.asInterface(service);

            val intent = Intent()
            intent.action = BROADCAST_ACTION
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            aidlDataGeneratorService = null;
            log("onServiceDisconnected")
        }
    }

    class MyAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GaugeFragment(R.layout.speedometer_fragment, R.id.speedometer)
                1 -> GaugeFragment(R.layout.tachometer_fragment, R.id.tachometer)
                else -> GaugeFragment(R.layout.speedometer_fragment, R.id.speedometer)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}