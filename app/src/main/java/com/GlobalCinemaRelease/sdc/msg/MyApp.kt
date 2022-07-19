package com.GlobalCinemaRelease.sdc.msg

import android.app.Application

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun setConnectionListener(listener: ConnectionReceiver.ConnectionReceiverListener){
        ConnectionReceiver.connectionReceiverListener = listener
    }

    companion object{
        @get:Synchronized
        lateinit var instance: MyApp
    }

}