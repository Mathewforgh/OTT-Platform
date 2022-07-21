package com.GlobalCinemaRelease.sdc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.GlobalCinemaRelease.sdc.databinding.ActivitySignUpPageBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.SendOtp
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpPage : AppCompatActivity() {
    private val ids by lazy { ActivitySignUpPageBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        clickEvents()

        ids.enterNameExTx.requestFocus()
        if (Store.socialIdFrom == "google" || Store.socialIdFrom == "facebook"){
            ids.apply {
                enterNameExTx.setText(Store.userName)
                enterEmailEdTx.setText(Store.userEmail)
                mobNumEdTx.requestFocus()
            }
        }

        ids.continueSignupBtn.setOnDebounceListener {
            val userName = ids.enterNameExTx.text.toString()
            val userEmail = ids.enterEmailEdTx.text.toString()
            val phNumber = ids.mobNumEdTx.text.toString()
            val countryCode = ids.countryCodePic.selectedCountryCodeWithPlus

            val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()

            if (userName.isEmpty() || userEmail.isEmpty() || phNumber.isEmpty()) {
                toast("Field can't be empty")
            } else if (!validEmail) {
                toast("Invalid Email")
            } else {
                sendOtp(userEmail, userName, countryCode, phNumber)
            }

            sharedPreferences.edit().apply {
                putString("userName", userName)
                putString("userEmail", userEmail)
                putString("phNumber", phNumber)
                putString("countryCode", countryCode)
                apply()
            }
            Store.from = "signUp"
        }


        if (Store.userName.isNotEmpty() && Store.userEmail.isNotEmpty() && Store.phNumber.isNotEmpty()){
            ids.enterNameExTx.setText(Store.userName)
            ids.enterEmailEdTx.setText(Store.userEmail)
            ids.mobNumEdTx.setText(Store.phNumber)
        }
        else if (android.util.Patterns.EMAIL_ADDRESS.matcher(Store.tempVar).matches()){
            ids.enterEmailEdTx.setText(Store.tempVar)
        }else {
            ids.mobNumEdTx.setText(Store.tempVar)
        }
    }

    private fun clickEvents() {
        ids.signTvFromSignupPg.setOnDebounceListener {
            startActivity(Intent(this, SignInPage::class.java))
        }
    }

    private fun sendOtp(
        userEmail: String,
        userName: String,
        countryCode: String,
        phNumber: String
    ) {
        val jsonObject = JsonObject()

        try {
            jsonObject.addProperty("mobileNumber", phNumber)
            jsonObject.addProperty("type", "sendotp")

            // api call
            ResponseApi().signUpSendOtp(jsonObject).enqueue(object : Callback<SendOtp?> {
                override fun onResponse(call: Call<SendOtp?>, response: Response<SendOtp?>) {
                    if (response.isSuccessful && response.body()?.code == 201) {

                        Store.phNumber = phNumber
                        Store.countryCode = countryCode
                        Store.userName = userName
                        Store.userEmail = userEmail
                        startActivity(
                            Intent(this@SignUpPage, OtpVerificationPage::class.java))
                    }
                    else toast("${response.body()?.message}")
                }

                override fun onFailure(call: Call<SendOtp?>, t: Throwable) {
                    toast("${t.message}")
                    toast("No Internet Connection")
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(this@SignUpPage, LoginSignPage::class.java))
        ids.apply {
            enterNameExTx.text.clear()
            enterEmailEdTx.text.clear()
            mobNumEdTx.text.clear()
        }
        super.onBackPressed()
    }
}