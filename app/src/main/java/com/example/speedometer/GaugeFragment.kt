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
        (activity as FullscreenActivity).notifyChangeGauge()
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}