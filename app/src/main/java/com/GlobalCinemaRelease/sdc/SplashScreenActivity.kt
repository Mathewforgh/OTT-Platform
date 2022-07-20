package com.GlobalCinemaRelease.sdc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.GlobalCinemaRelease.sdc.databinding.ActivitySplashScreenBinding
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val ids by lazy { ActivitySplashScreenBinding.inflate(layoutInflater) }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        Thread {
            var ct = 0
            while (ct <= 100) {
                ct += 20
                ids.apply {
                    progressBar.setProgressCompat(ct, true)
                }
                Thread.sleep(250)
            }
        }.start()


        val userToken = sharedPreferences.getString("userToken","")

        ids.SplashScreenId.alpha = 0f
        ids.SplashScreenId.animate().setDuration(1000).alpha(1f).withEndAction{
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            if (userToken?.isEmpty() == true){
                startActivity(Intent(this@SplashScreenActivity,MainActivity::class.java))
                finish()
            }
            else {
                startActivity(Intent(this@SplashScreenActivity, HomePage::class.java))
                finish()
            }
        }
    }
}