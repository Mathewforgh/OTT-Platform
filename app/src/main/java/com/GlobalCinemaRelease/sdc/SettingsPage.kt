package com.GlobalCinemaRelease.sdc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.GlobalCinemaRelease.sdc.databinding.ActivitySettingsPageBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.SetLogInUserDetailsDC
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsPage : AppCompatActivity() {
    private val ids by lazy { ActivitySettingsPageBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    private var userToken = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        userToken = sharedPreferences.getString("userToken","").toString()
        val name = sharedPreferences.getString("userName","")
        val phNumber = "${sharedPreferences.getString("countryCode","")}" +
                    " - " + "${sharedPreferences.getString("phNumber","")}"

        if (!name.isNullOrEmpty() && phNumber != ""){
            ids.useNameTv.text = name
            ids.userNumberTv.text = phNumber
        }

        ids.logoutBtn.setOnDebounceListener {
           sharedPreferences.edit().apply {
               remove("userToken")
               remove("userName")
               remove("userEmail")
               remove("phNumber")
               remove("countryCode")
               apply()
           }
            Store.phNumber = ""
            Store.countryCode = ""
            Store.userName = ""
            Store.userEmail = ""
            if (AccessToken.getCurrentAccessToken() != null) {
                GraphRequest(
                    AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE,
                    {
                        AccessToken.setCurrentAccessToken(null)
                        LoginManager.getInstance().logOut()
                        finish()
                    }
                ).executeAsync()
            }
           startActivity(Intent(this@SettingsPage, LoginSignPage::class.java))
        }

        ids.settingBackBtn.setOnDebounceListener {
            onBackPressed()
        }
        ids.editProfileBtn.setOnDebounceListener {
            startActivity(Intent(this@SettingsPage, EditProfile::class.java))
        }
        ids.themeBtn.setOnDebounceListener {
            startActivity(Intent(this@SettingsPage, ThemePage::class.java))
        }
        ids.helpAndSupportBtn.setOnDebounceListener {
            startActivity(Intent(this@SettingsPage, HelpAndSupport::class.java))
        }
    }

    private fun updatedUserName(userToken: String?) {
        try {
            val jsonObject = JsonObject()
            jsonObject.addProperty("userToken", userToken)

            ResponseApi().setUpUserDetails(jsonObject).enqueue(object :
                Callback<SetLogInUserDetailsDC?> {
                override fun onResponse(
                    call: Call<SetLogInUserDetailsDC?>,
                    response: Response<SetLogInUserDetailsDC?>,
                ) {
                    if (response.isSuccessful && response.body()?.code == 201){
                        ids.useNameTv.text = response.body()?.data?.name
                    }
                    else toast("Try Again")
                }

                override fun onFailure(call: Call<SetLogInUserDetailsDC?>, t: Throwable) {
                    Log.i("test", t.message.toString())
                }
            })
        }
        catch (e: JSONException){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        updatedUserName(userToken)
    }
}