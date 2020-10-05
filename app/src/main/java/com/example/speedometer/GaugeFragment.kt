package com.example.speedometer
import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class GaugeFragment(val layoutRes: Int, val viewId: Int) : Fragment() {
    var speedometer : CircularGaugeView? = null
    @Nullable override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(layoutRes, container, false)
        speedometer = view.findViewById(viewId)
        LocalBroadcastManager.getInstance(view.context)
            .registerReceiver(localBroadcastReceiver, IntentFilter(BROADCAST_ACTION))
        return view
    }
    private val notifIntentFilter = IntentFilter(BROADCAST_ACTION);
    val localBroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(contxt: Context?, intent: Intent?) {
            speedometer?.dataService = (activity as FullscreenActivity).aidlDataGeneratorService
            speedometer?.startAnimatingIndicator(60)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this.requireContext())
            .unregisterReceiver(localBroadcastReceiver)
    }

}