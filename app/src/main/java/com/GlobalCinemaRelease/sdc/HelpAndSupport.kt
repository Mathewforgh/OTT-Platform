package com.GlobalCinemaRelease.sdc

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.GlobalCinemaRelease.sdc.databinding.ActivityHelpAndSupportBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store

class HelpAndSupport : AppCompatActivity() {
    private val ids by lazy { ActivityHelpAndSupportBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        ids.helpSupportBackBtn.setOnDebounceListener {
            onBackPressed()
        }
        ids.HpSupMobNumber.setOnDebounceListener {
            ids.HpSupMobNumber.startAnimation(Store.zoomOut(this@HelpAndSupport))
            val phNum = Uri.parse("tel:"+ids.HpSupMobNumber.text.toString())
            startActivity(Intent(Intent.ACTION_DIAL, phNum))
        }

    }
}