package com.GlobalCinemaRelease.sdc

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.GlobalCinemaRelease.sdc.adapters.SeeAllMovieListAdapter
import com.GlobalCinemaRelease.sdc.databinding.ActivitySeeAllMoviesBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.ConnectionReceiver
import com.GlobalCinemaRelease.sdc.msg.MyApp
import com.GlobalCinemaRelease.sdc.msg.checkInternetPopUp
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.SeeAllSearchResDC
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeeAllMovies : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener {
    private val ids by lazy { ActivitySeeAllMoviesBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var loader: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

/*for checking the internet is connection*/
        baseContext.registerReceiver(ConnectionReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        MyApp.instance.setConnectionListener(this)
/*----------------------------------*/

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val userToken = sharedPreferences.getString("userToken", "").toString()

        ids.apply {
            searchTitle.text = Store.resTitle
        }

        ids.headTabBar.setOnDebounceListener {
            if (Store.from_SeeAll == "MDSeeAll"){
                Store.from_SeeAll =""
                onBackPressed()
            }else finish()
        }

        setUpSearchMovieRes(userToken)

        /*-----------------*/
        loader = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        val view: View = View.inflate(this, R.layout.loader, null)
        loader.setContentView(view)
        loader.setCancelable(true)
        loader.create()
        loader.show()
        /*-----------------*/
    }
    private fun setUpSearchMovieRes(userToken: String){
        try {
            val jsonObject = JsonObject()
            jsonObject.addProperty("userToken", userToken)
            jsonObject.addProperty("searchToken", Store.searchToken)
            jsonObject.addProperty("type", Store.type)

            ResponseApi().setUpSearchRes(jsonObject).enqueue(object : Callback<SeeAllSearchResDC?> {
                override fun onResponse(
                    call: Call<SeeAllSearchResDC?>,
                    response: Response<SeeAllSearchResDC?>
                ) {
                    if (response.isSuccessful){
                        loader.dismiss()
                        if (response.body()?.code == 201){
                            ids.apply {
                                recyclerViewMovieList.layoutManager =
                                    GridLayoutManager(this@SeeAllMovies, 2)
                                recyclerViewMovieList.adapter =
                                    response.body()?.data?.let { SeeAllMovieListAdapter(it) }
                            }
                        }
                    }
                    else toast("Connection Error")
                    loader.dismiss()
                }

                override fun onFailure(call: Call<SeeAllSearchResDC?>, t: Throwable) {
                    Toast.makeText(this@SeeAllMovies, "${t.message}", Toast.LENGTH_SHORT).show()
                    loader.dismiss()
                }
            })
        }
        catch (e: JSONException){
            e.printStackTrace()
            loader.dismiss()
        }
    }

    override fun onBackPressed() {
        if (Store.from_SeeAll == "MDSeeAll"){
            super.onBackPressed()
        }else {
            finish()
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