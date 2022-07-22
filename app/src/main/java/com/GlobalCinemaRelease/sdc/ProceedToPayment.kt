package com.GlobalCinemaRelease.sdc

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.GlobalCinemaRelease.sdc.databinding.ActivityProceedToPymentBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.ConnectionReceiver
import com.GlobalCinemaRelease.sdc.msg.MyApp
import com.GlobalCinemaRelease.sdc.msg.checkInternetPopUp
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.CompleatePaymentDataC
import com.GlobalCinemaRelease.sdc.response.CouponClaimDC
import com.GlobalCinemaRelease.sdc.response.CreateOrderIdDataC
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProceedToPayment : AppCompatActivity(), PaymentResultListener,
    ConnectionReceiver.ConnectionReceiverListener {
    private val ids by lazy { ActivityProceedToPymentBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cost: String
    private var orderId: String? = null
    lateinit var loader: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        /*for checking the internet is connection*/
        baseContext.registerReceiver(ConnectionReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        MyApp.instance.setConnectionListener(this)
/*----------------------------------*/

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val userToken = sharedPreferences.getString("userToken", "").toString()
        val userEmail = sharedPreferences.getString("userEmail", "")
        val phNumber = sharedPreferences.getString("phNumber", "")
        val movieToken = Store.movieTokenSet
        Log.d("tag", "$userEmail\n$phNumber")

        Glide.with(this@ProceedToPayment).load(Store.posterLink).into(ids.imageView4)
        ids.descriptionPageMovieName.text = Store.movieName
        cost = Store.movieCost
        ids.textView17.text = cost
        ids.textView18.text = Store.movieDesc

        ids.descriptionPageMovieName.apply {
            ellipsize = TextUtils.TruncateAt.MARQUEE
            isSelected = true
        }

        ids.proceedToPaymentBtn.setOnDebounceListener {
            /*-----------------*/
            loader = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
            val view: View = View.inflate(this, R.layout.loader, null)
            loader.setContentView(view)
            loader.setCancelable(true)
            loader.create()
            loader.show()
            /*-----------------*/
            if (ids.EnterGiftCodeLayout.visibility == View.GONE) {
                setPayment(userToken, movieToken, "inr")
            } else {
                val giftCode = ids.giftCodeEdTx.text.toString()
                claimNow(giftCode)
            }
        }
        ids.paymentPPayNowBtn.setOnDebounceListener {
            ids.apply {
                EnterGiftCodeLayout.clearAnimation()
                EnterGiftCodeLayout.visibility = View.GONE
                payNowCheckTick.setImageResource(R.drawable.ic_check)
                giftBtnImgTick.setImageResource(R.drawable.ic_unselect_button)
                proceedToPaymentBtn.text = getString(R.string.proceed_to_payment)
            }
        }
        ids.giftVoucherBtn.setOnDebounceListener {
            ids.apply {
                EnterGiftCodeLayout.startAnimation(Store.slideDown(this@ProceedToPayment))
                EnterGiftCodeLayout.visibility = View.VISIBLE
                payNowCheckTick.setImageResource(R.drawable.ic_unselect_button)
                giftBtnImgTick.setImageResource(R.drawable.ic_check)
                proceedToPaymentBtn.text = getString(R.string.cliam_now)
                giftCodeEdTx.requestFocus()
            }
        }
    }

    private fun claimNow(giftCode: String) {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", HomePage.userToken)
            jsonObject.addProperty("coupanCode", giftCode)

            ResponseApi().claimGiftCoupon(jsonObject).enqueue(object : Callback<CouponClaimDC?> {
                override fun onResponse(
                    call: Call<CouponClaimDC?>,
                    response: Response<CouponClaimDC?>,
                ) {
                    if (response.isSuccessful){
                        loader.dismiss()
                        if ( response.body()?.code == 201){
                            startActivity(Intent(this@ProceedToPayment, MovieDescription::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            Store.code = "redeemed"
                        }else{
                            loader.dismiss()
                            toast("${response.body()?.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<CouponClaimDC?>, t: Throwable) {
                    loader.dismiss()
                    Log.i("tag", t.message.toString())
                    toast("Check Internet Connection")
                }
            })
        }
        catch (e: JSONException){
            e.printStackTrace()
            loader.dismiss()
        }
    }

    private fun setPayment(userToken: String, movieToken: String, type: String) {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", userToken)
            jsonObject.addProperty("movieToken", movieToken)
            jsonObject.addProperty("type", type)
/*----------------------Api call---------------*/
            ResponseApi().createOrderId(jsonObject).enqueue(object : Callback<CreateOrderIdDataC?> {
                override fun onResponse(
                    call: Call<CreateOrderIdDataC?>,
                    response: Response<CreateOrderIdDataC?>,
                ) {
                    if (response.isSuccessful) {

                        if (response.body()?.code == 201) {
                            orderId = response.body()?.orderId!!
                            paymentGateWay(sharedPreferences.getString("userEmail", "").toString(),
                                sharedPreferences.getString("phNumber", "").toString())
                        } else {
                            loader.dismiss()
                            toast("Payment ${response.body()?.message.toString()}")
                        }
                    } else {
                        loader.dismiss()
                        Log.i("test", "resError--${!response.isSuccessful}")
                        toast("Payment Error--${!response.isSuccessful}")
                    }
                }

                override fun onFailure(call: Call<CreateOrderIdDataC?>, t: Throwable) {
                    Log.i("test", "paymentFail--${t.message}")
                    loader.dismiss()
                }
            })
        } catch (e: Exception) {
            loader.dismiss()
            toast("Error in payment:" + e.message)
            e.printStackTrace()
        }
    }

    private fun paymentGateWay(userEmail: String, phNumber: String) {
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name", "GLOBAL CINEMA RELEASE")
            options.put("description", "${R.string.watch_unlimited_movies_series_anywhere_anytime}")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "${R.drawable.sdc_logo}")
            options.put("theme.color", R.color.indicatorColor)
            options.put("currency", "INR")
            options.put("amount", cost + "00")
            options.put("order_id", orderId)

            val prefill = JSONObject()
            prefill.put("email", userEmail)
            prefill.put("contact", phNumber)

            options.put("prefill", prefill)
            co.open(this, options)
            loader.dismiss()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /*----------------------complete payment Api call-------------------------------*/
    private fun completePayment(userToken: String, orderId: String, signatureId: String) {
        try {
            val jsonObject = JsonObject()
            jsonObject.addProperty("userToken", userToken)
            jsonObject.addProperty("orderId", orderId)
            jsonObject.addProperty("payment_signature", signatureId)

            ResponseApi().completePayments(jsonObject)
                .enqueue(object : Callback<CompleatePaymentDataC?> {
                    override fun onResponse(
                        call: Call<CompleatePaymentDataC?>,
                        response: Response<CompleatePaymentDataC?>,
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.code == 201) {
                                Log.i("test", "payment Done--${response.body()?.message}")
                                Store.rentToken = response.body()?.rentToken.toString()
                            }
                        }
                    }

                    override fun onFailure(call: Call<CompleatePaymentDataC?>, t: Throwable) {
                        Log.i("test", t.message.toString())
                    }
                })

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.i("test", "id-->$p0")
        val userToken = sharedPreferences.getString("userToken", "").toString()
        completePayment(userToken, orderId!!, "$p0")
        Store.payState = "true"
        Store.backOnPayDone = "done"
        startActivity(Intent(this, MovieDescription::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Store.payState = "false"
        if (Checkout.PAYMENT_CANCELED == 0) {
            super.onBackPressed()
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            Store.internet = "1"
            checkInternetPopUp(this, Store.internet)
        } else {
            Store.internet = "0"
            checkInternetPopUp(this, Store.internet)
        }
    }
}