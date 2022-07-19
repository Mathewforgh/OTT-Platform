package com.GlobalCinemaRelease.sdc

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.GlobalCinemaRelease.sdc.adapters.SuggestMovieTitlesAdapter
import com.GlobalCinemaRelease.sdc.databinding.ActivitySuggestedMovieForYouBinding
import com.GlobalCinemaRelease.sdc.msg.ConnectionReceiver
import com.GlobalCinemaRelease.sdc.msg.MyApp
import com.GlobalCinemaRelease.sdc.msg.checkInternetPopUp
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.DataXXXXX

class SuggestedMovieForYou : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener {
    private  val ids by lazy { ActivitySuggestedMovieForYouBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        baseContext.registerReceiver(ConnectionReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        MyApp.instance.setConnectionListener(this)

        ids.headTabBar2.setOnDebounceListener {
            finish()
        }

        ids.apply {
            suggestMovieRecyclerView.layoutManager =
                LinearLayoutManager(this@SuggestedMovieForYou, LinearLayoutManager.VERTICAL, false)
            suggestMovieRecyclerView.adapter =
                SuggestMovieTitlesAdapter(Store.tempSuggestMovieTitle)
        }

    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected){
            Store.internet = "1"
            checkInternetPopUp(this, Store.internet)
        }
        else{
            Store.internet = "0"
            checkInternetPopUp(this, Store.internet)
        }
    }
}