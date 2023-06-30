package com.e_kuhipath.android.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class NetworkChangeReceiver : BroadcastReceiver() {

    interface NetworkChangeListener {
        fun onNetworkChanged(isConnected: Boolean, shouldCallApi: Boolean)
    }

    var listener: NetworkChangeListener? = null
    private var shouldCallApi: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("zz","br onreceive--->")
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork =  cm.activeNetworkInfo
            val isConnected = activeNetwork?.isConnectedOrConnecting == true
            if (isConnected && shouldCallApi) {
                shouldCallApi = false
                listener?.onNetworkChanged(isConnected, true)
            } else {
                listener?.onNetworkChanged(isConnected, false)
            }

        }
    }

    fun setShouldCallApi() {
        shouldCallApi = true
        Log.i("zz","br onreceive2--->")

    }
}
