package com.GlobalCinemaRelease.sdc.msg

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import androidx.navigation.NavController
import me.ibrahimsn.lib.SmoothBottomBar

class ConnectionReceiver: BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val isConnected = checkConnection(context)

        if (connectionReceiverListener != null)
            connectionReceiverListener?.onNetworkConnectionChanged(isConnected)
    }

    interface ConnectionReceiverListener{
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }
    companion object{
        var connectionReceiverListener: ConnectionReceiverListener? = null
        val isConnected: Boolean
        get() {
            val cm = MyApp.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return (((activeNetwork != null) && activeNetwork.isConnectedOrConnecting))
        }
    }

    private fun checkConnection(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return (((activeNetwork != null) && activeNetwork.isConnectedOrConnecting))
    }
}