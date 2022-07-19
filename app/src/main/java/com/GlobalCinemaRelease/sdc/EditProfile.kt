package com.GlobalCinemaRelease.sdc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.GlobalCinemaRelease.sdc.databinding.ActivityEditProfileBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.UserDataUpdateDC
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfile : AppCompatActivity() {
    private val ids by lazy { ActivityEditProfileBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        val userToken = sharedPreferences.getString("userToken","")
        val userName = sharedPreferences.getString("userName", "")
        val userEmail = sharedPreferences.getString("userEmail","")
        val phNumber = sharedPreferences.getString("phNumber","")
        val countryCode = sharedPreferences.getString("countryCode","")

        ids.apply {
            EdProEnterNameExTx.requestFocus()
            EdProEnterNameExTx.setText(userName)
            EdProEnterEmailEdTx.setText(userEmail)
            EdProMobNumEdTx.setText(phNumber)
            CountryCodePic.setCountryForNameCode(countryCode)
        }

        ids.editProfileBackBtn.setOnDebounceListener {
            onBackPressed()
        }
        ids.editProfileSaveBtn.setOnDebounceListener {
            val name = ids.EdProEnterNameExTx.text.toString()
            updateUserDetails(userToken, name, userEmail)
        }
    }

    private fun updateUserDetails(userToken: String?, userName: String?, userEmail: String?) {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", userToken)
            jsonObject.addProperty("userEmail", userEmail)
            jsonObject.addProperty("userName", userName)

            ResponseApi().updateUserDetails(jsonObject).enqueue(object : Callback<UserDataUpdateDC?> {
                override fun onResponse(
                    call: Call<UserDataUpdateDC?>,
                    response: Response<UserDataUpdateDC?>,
                ) {
                    if (response.isSuccessful && response.body()?.code == 201){
                        sharedPreferences.edit().apply {
                            putString("userName", userName)
                            apply()
                        }
                        finish()
                    }else toast("Please try Again Later")
                }

                override fun onFailure(call: Call<UserDataUpdateDC?>, t: Throwable) {
                    Log.i("test", "${t.message}")
                    toast("Check Internet Connection")
                }
            })
        }
        catch (e: JSONException){
            e.printStackTrace()
        }
    }
}