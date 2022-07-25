package com.GlobalCinemaRelease.sdc

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.GlobalCinemaRelease.sdc.databinding.ActivityLoginSignPageBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store

class LoginSignPage : AppCompatActivity() {
    lateinit var loader: Dialog
    private val ids by lazy { ActivityLoginSignPageBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        /*-----------------*/
        loader = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        val view: View = View.inflate(this, R.layout.loader, null)
        loader.setContentView(view)
        loader.setCancelable(true)
        loader.create()
        /*-----------------*/

        ids.loginSignupBtn.setOnDebounceListener {
            loader.show()
            Store.from = ""
            startActivity(Intent(this@LoginSignPage,SignInPage::class.java))
        }
        ids.guestBtn.setOnDebounceListener {
            loader.show()
            Store.from = "guest"
            startActivity(Intent(this@LoginSignPage, HomePage::class.java))
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        loader.cancel()
    }
}