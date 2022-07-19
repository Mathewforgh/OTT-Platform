package com.GlobalCinemaRelease.sdc

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.print.PrintAttributes
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.core.widget.doOnTextChanged
import com.GlobalCinemaRelease.sdc.databinding.ActivityOtpVerificationPageBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.SetLogInUserDetailsDC
import com.GlobalCinemaRelease.sdc.response.SignUpSaveData
import com.GlobalCinemaRelease.sdc.response.SocialLogin
import com.GlobalCinemaRelease.sdc.response.VerifyOtp
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpVerificationPage : AppCompatActivity() {
    private val ids by lazy { ActivityOtpVerificationPageBinding.inflate(layoutInflater) }
    lateinit var sharedPref: SharedPreferences
    private lateinit var phNumber: String
    private var otp: Int = 0
    lateinit var loader: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        /*-----------------*/
        loader = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        val view: View = View.inflate(this, R.layout.loader, null)
        loader.setContentView(view)
        loader.setCancelable(true)
        loader.create()
        /*-----------------*/

        edTextChange()

        val userEmail = Store.userEmail
        val userName = Store.userName
        val countryCode = Store.countryCode
        phNumber = Store.phNumber
        ids.enteredNumTv.text = "+$countryCode $phNumber"

        phNumber = if (android.util.Patterns.EMAIL_ADDRESS.matcher(phNumber).matches()){
            Store.phNumber
        }else{
            phNumber
        }

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(phNumber).matches()) ids.textView8.text = "We have sent you an OTP to your registered\nMail Id."
        else ids.textView8.text = "We have sent you an OTP to your registered\nmobile number."


        ids.changeTv.setOnDebounceListener {
            if (Store.from == "signIn") startActivity(Intent(this@OtpVerificationPage, SignInPage::class.java))
            else startActivity(Intent(this@OtpVerificationPage, SignUpPage::class.java))
        }

        timers()

        ids.verifyOtpBtn.setOnDebounceListener {
            loader.show()
            val otp1 = ids.edTx1.text.toString()
            val otp2 = ids.edTx2.text.toString()
            val otp3 = ids.edTx3.text.toString()
            val otp4 = ids.edTx4.text.toString()
            val otp5 = ids.edTx5.text.toString()
            val otp6 = ids.edTx6.text.toString()
            if (otp1.isNotEmpty() && otp2.isNotEmpty() && otp3.isNotEmpty() &&
                otp4.isNotEmpty() && otp5.isNotEmpty() && otp6.isNotEmpty()){
                otp = (otp1 + otp2 + otp3 + otp4 + otp5 + otp6).toInt()
                verifyOtp(otp, userEmail, userName, countryCode, phNumber)
            }else{
                toast("Field can't be empty")
                loader.dismiss()
            }
        }
    }

    private fun verifyOtp(
        otp: Int, userEmail: String,
        userName: String, countryCode: String,
        phNumber: String
    ) {
        Log.i("test", "btn--fun call")
        val jsonObject = JsonObject()

        try {
            jsonObject.addProperty("mobileNumber", phNumber)
            jsonObject.addProperty("otp", otp)
            jsonObject.addProperty("device", "Android")
            jsonObject.addProperty("deviceId", "214")
            jsonObject.addProperty("deviceToken", "2142")

            ///Api call verify otp
            ResponseApi().verifyOtp(jsonObject).enqueue(object : Callback<VerifyOtp?> {
                override fun onResponse(call: Call<VerifyOtp?>, response: Response<VerifyOtp?>) {
                    if (response.isSuccessful) {
                        if (response.body()?.code == 201 && response.body()?.newUser == false) {    // if old user, it will call
                            val userToken = response.body()?.userToken.toString()
                            setUserDetails(userToken)
                            sharedPref.edit().apply {
                                putString("userToken", userToken)                   /*save the userToken in shared preference*/
                                apply()
                            }
                            startActivity(
                                Intent(this@OtpVerificationPage,
                                    HomePage::class.java
                                )
                            )
                            Log.i("test", "btn--verify otp")
                        }
                        else if (Store.socialIdFrom=="google" || Store.socialIdFrom == "facebook"){
                           try {
                               jsonObject.addProperty("device", "Android")
                               jsonObject.addProperty("mobileNumber", phNumber)
                               jsonObject.addProperty("countryCode", countryCode)
                               jsonObject.addProperty("deviceId", "214")
                               jsonObject.addProperty("socialId", Store.socialId)
                               jsonObject.addProperty("deviceToken", "2142")
                               jsonObject.addProperty("userName", userName)
                               jsonObject.addProperty("userEmail", userEmail)

                               ResponseApi().socialLogin(jsonObject).enqueue(object : Callback<SocialLogin?> {
                                   override fun onResponse(
                                       call: Call<SocialLogin?>,
                                       response: Response<SocialLogin?>,
                                   ) {
                                       if (response.isSuccessful && response.body()?.code == 201){

                                           sharedPref.edit().apply {
                                               putString("userToken", response.body()?.userToken.toString())                   /*save the userToken in shared preference*/
                                               apply()
                                           }

                                           toast("Data save")
                                           startActivity(Intent(this@OtpVerificationPage,
                                           LanguageSelectionPage::class.java))

                                           Store.socialIdFrom = ""
                                           loader.dismiss()
                                       }
                                       else{
                                           toast("Data not save")
                                           loader.dismiss()
                                       }
                                   }

                                   override fun onFailure(call: Call<SocialLogin?>, t: Throwable) {
                                       Log.e("test", t.message.toString())
                                       loader.dismiss()
                                   }
                               })
                           }
                           catch (e: JSONException){
                               e.printStackTrace()
                           }
                        }
                        else {
                            val jsonObject2 = JsonObject()
                            try {
                                val userToken = response.body()?.userToken
                                sharedPref.edit().apply {
                                    putString("userToken", userToken)                        /*save the userToken in shared preference*/
                                    apply()
                                }

                                jsonObject2.addProperty("userToken", userToken)
                                jsonObject2.addProperty("userEmail", userEmail)
                                jsonObject2.addProperty("userName", userName)
                                jsonObject2.addProperty("countryCode", countryCode)
                                jsonObject2.addProperty("mobileNumber", phNumber)

                                ResponseApi().signUpSaveData(jsonObject2).enqueue(object : Callback<SignUpSaveData?> {
                                    override fun onResponse(
                                        call: Call<SignUpSaveData?>,
                                        response: Response<SignUpSaveData?>
                                    ) {
                                        if (response.isSuccessful){
                                            if (response.body()?.code == 201){
                                                Log.i("test", "btn--save data")
                                                startActivity(Intent(this@OtpVerificationPage, LanguageSelectionPage::class.java))
                                                loader.dismiss()
                                            }else{
                                                toast("el${response.body()?.message}")
                                                loader.dismiss()
                                            }
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<SignUpSaveData?>,
                                        t: Throwable
                                    ) {
                                        toast("${t.message}")
                                        loader.dismiss()
                                    }
                                })
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<VerifyOtp?>, t: Throwable) {
                    toast("${t.message}")
                    loader.dismiss()
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun setUserDetails(userToken: String){
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", userToken)

            ResponseApi().setUpUserDetails(jsonObject).enqueue(object : Callback<SetLogInUserDetailsDC?> {
                override fun onResponse(
                    call: Call<SetLogInUserDetailsDC?>,
                    response: Response<SetLogInUserDetailsDC?>
                ) {
                    if (response.isSuccessful){
                        if (response.body()?.code == 201){
                            Log.i("test", "btn-- take user details")
                            Store.userName = response.body()?.data?.name.toString()
                            Store.userEmail = response.body()?.data?.email.toString()
                            Store.countryCode = response.body()?.data?.countryCode.toString()
                            Store.phNumber = response.body()?.data?.mobilenumber.toString()

                            ids.enteredNumTv.text = "${Store.countryCode} $phNumber"

                            sharedPref.edit().apply {
                                putString("userName", Store.userName)
                                putString("userEmail", Store.userEmail)
                                putString("phNumber", Store.phNumber)
                                putString("countryCode", Store.countryCode)
                                apply()
                            }
                            loader.dismiss()

                        }else{
                            Toast.makeText(this@OtpVerificationPage, "Error On Receive data", Toast.LENGTH_SHORT).show()
                            loader.dismiss()
                        }
                    }
                }

                override fun onFailure(call: Call<SetLogInUserDetailsDC?>, t: Throwable) {
                    Toast.makeText(this@OtpVerificationPage, "${t.message}", Toast.LENGTH_SHORT).show()
                    loader.dismiss()
                }
            })

        }catch (e: JSONException){
            e.printStackTrace()
        }
    }
    private fun timers(){
        object : CountDownTimer(60000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                ids.counterTv.text = ("00:"+millisUntilFinished / 1000)
                ids.textView11.visibility = View.VISIBLE
            }

            override fun onFinish() {
                ids.counterTv.text = "Resend OTP"
                ids.textView11.visibility = View.GONE
                ids.counterTv.setOnDebounceListener {
                    timers()
                }
            }

        }.start()
    }

    override fun onBackPressed() {
        if (otp == 0) toast("Enter OTP")
        else super.onBackPressed()
    }
    private fun edTextChange(){
        ids.edTx1.doOnTextChanged { _, _, _, _ ->
            if (ids.edTx1.text.isNotEmpty()) ids.edTx2.requestFocus()
        }
        ids.edTx2.doOnTextChanged { text, start, before, count ->
            if (ids.edTx2.text.isNotEmpty()) ids.edTx3.requestFocus()
            else ids.edTx1.requestFocus()
        }
        ids.edTx3.doOnTextChanged { text, start, before, count ->
            if (ids.edTx3.text.isNotEmpty()) ids.edTx4.requestFocus()
            else ids.edTx2.requestFocus()
        }
        ids.edTx4.doOnTextChanged { text, start, before, count ->
            if (ids.edTx4.text.isNotEmpty()) ids.edTx5.requestFocus()
            else ids.edTx3.requestFocus()
        }
        ids.edTx5.doOnTextChanged { text, start, before, count ->
            if (ids.edTx5.text.isNotEmpty()) ids.edTx6.requestFocus()
            else ids.edTx4.requestFocus()
        }
        ids.edTx6.doOnTextChanged { text, start, before, count ->
            if (ids.edTx6.text.isEmpty()) ids.edTx5.requestFocus()
        }
    }
}