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
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager.PageTransformer


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public val BROADCAST_ACTION = "com.androidaidl.androidaidlservice.started"
class FullscreenActivity : AppCompatActivity() {

    val ACTION_START_DATA_GENERATOR = "com.androidaidl.androidaidlservice.ProductService"
    var dataProvider : DataProvider? = null
    var viewPager: ViewPager? = null
    lateinit var adapter : FragmentAdapter
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = Intent(ACTION_START_DATA_GENERATOR)
        val component = ComponentName(packageName, "com.example.speedometer.DataService")


        val explicitIntent = Intent(intent)

        explicitIntent.component = component
        bindService(explicitIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        adapter = FragmentAdapter(supportFragmentManager)

        viewPager = findViewById(R.id.viewpager)
        viewPager?.adapter = adapter

        viewPager?.currentItem = 0

        viewPager?.setPageTransformer(false, object : PageTransformer {
            override fun transformPage(v: View, pos: Float) {
                val opacity = Math.abs(Math.abs(pos) - 1)
                v.setAlpha(opacity)
            }
        })
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                dataProvider?.onChangeView((adapter.getItem(position) as GaugeFragment)?.speedometer)
            }
        })
    }

    var aidlDataGeneratorService : ISpeedDataAidlInterface? = null
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {

            aidlDataGeneratorService = ISpeedDataAidlInterface.Stub.asInterface(service);
            dataProvider?.stopDataThread()
            //view еще может быть не создана, сделала костыль во фрагменте
            dataProvider = DataProvider(aidlDataGeneratorService, (adapter.getItem(viewPager?.currentItem?:0 ) as GaugeFragment)?.speedometer)
            dataProvider?.startDataThread()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            aidlDataGeneratorService = null;
            dataProvider?.stopDataThread()
        }
    }

    inner class FragmentAdapter internal constructor(fm: FragmentManager) :  FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        val speedometerFragment = GaugeFragment(R.layout.speedometer_fragment, R.id.speedometer)
        val tachometerFragment = GaugeFragment(R.layout.tachometer_fragment, R.id.tachometer)
        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> speedometerFragment
                1 -> tachometerFragment
                else -> speedometerFragment
            }
        }
    }

    override fun onPause() {
        dataProvider?.stopDataThread()
        super.onPause()
    }

    public fun notifyChangeGauge(){
        dataProvider?.onChangeView((adapter.getItem(viewPager?.currentItem?:0) as GaugeFragment)?.speedometer)
        dataProvider?.startDataThread()
    }
    override fun onResume() {
        notifyChangeGauge()
        super.onResume()
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