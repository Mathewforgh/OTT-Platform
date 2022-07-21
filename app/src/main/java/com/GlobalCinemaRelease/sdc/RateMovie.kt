package com.GlobalCinemaRelease.sdc

import android.app.Dialog
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.GlobalCinemaRelease.sdc.databinding.ActivityRateMovieBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.ConnectionReceiver
import com.GlobalCinemaRelease.sdc.msg.MyApp
import com.GlobalCinemaRelease.sdc.msg.checkInternetPopUp
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.RateMovieDC
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.rating_done.*
import kotlinx.android.synthetic.main.rating_done.view.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RateMovie : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener {
    private val ids by lazy { ActivityRateMovieBinding.inflate(layoutInflater) }
    lateinit var loader: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

/*--------------for checking the internet is connection---------------*/
        baseContext.registerReceiver(ConnectionReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        MyApp.instance.setConnectionListener(this)
/*--------------------------------------------------------------------*/

        loader = Dialog(this@RateMovie, android.R.style.Theme_Translucent_NoTitleBar)
        val view: View = View.inflate(this@RateMovie, R.layout.loader, null)
        loader.setContentView(view)
        loader.setCancelable(true)
        loader.create()

        if (Store.rating.isNotEmpty() && Store.isRated){
            ids.apply {
                rateMovieTv.text = "You Have Already Rate\nThis Movie"
                ratingBar.progress = Store.rating.toInt()
                ids.rateSubmitBtn.visibility = View.GONE
            }
        }

        ids.rateSubmitBtn.setOnDebounceListener {
            ids.apply {
                loader.show()
                rateSubmitBtn.startAnimation(Store.zoomOut(this@RateMovie))
                Store.rating = ratingBar.rating.toString()
                if (Store.rating != "0.0"){
                    rateSubmitBtn.isEnabled = true
                    saveRating(Store.rating)
                    Store.rating = ""
                }else {
                    loader.dismiss()
                    toast("Give Star Rating")
                }
            }
        }

        ids.apply {
            movieTitle.text = Store.movieName
            movieDescriptionId.text = Store.movieDesc
            Glide.with(this@RateMovie).load(Store.posterLink).into(ratetingBanner)
            ratingCancel.setOnDebounceListener {
                finish()
            }
        }
    }

    private fun saveRating(rating: String) {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", HomePage.userToken)
            jsonObject.addProperty("rentToken", Store.rentToken)
            jsonObject.addProperty("rating", rating)

            ResponseApi().saveRateMovie(jsonObject).enqueue(object : Callback<RateMovieDC?> {
                override fun onResponse(
                    call: Call<RateMovieDC?>,
                    response: Response<RateMovieDC?>,
                ) {
                    if (response.isSuccessful){
                        if (response.body()?.code == 201){

                            val sheet: View = layoutInflater.inflate(R.layout.rating_done, null)
                            val dialog = BottomSheetDialog(this@RateMovie, R.style.ThemeOverlay_App_BottomSheetDialog)
                            dialog.setContentView(sheet)
                            dialog.create()
                            dialog.show()

                            sheet.cancelRatingSheet.setOnDebounceListener {
                                dialog.dismiss()
                                Store.posterLink = ""
                                Store.movieName = ""
                                Store.movieDesc = ""
                                Store.rentToken = ""
                                finish()
                            }
                            sheet.ratingDoneBtn.setOnDebounceListener {
                                dialog.dismiss()
                                Store.posterLink = ""
                                Store.movieName = ""
                                Store.movieDesc = ""
                                Store.rentToken = ""
                                finish()
                            }
                            loader.dismiss()
                        }
                        else{
                            toast("${response.body()?.message}")
                            loader.dismiss()
                        }
                    }else{
                        toast("Please Try Again")
                        loader.dismiss()
                    }
                }

                override fun onFailure(call: Call<RateMovieDC?>, t: Throwable) {
                    toast("Check Internet Connection")
                    loader.dismiss()
                }
            })
        }
        catch (e: JSONException){
            e.printStackTrace()
            loader.dismiss()
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            Store.internet = "1"
            checkInternetPopUp(this@RateMovie, Store.internet)
        } else {
            Store.internet = "0"
            checkInternetPopUp(this@RateMovie, Store.internet)
        }
    }
}