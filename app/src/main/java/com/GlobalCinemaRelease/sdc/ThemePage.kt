package com.GlobalCinemaRelease.sdc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.GlobalCinemaRelease.sdc.databinding.ActivityThemePageBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener

class ThemePage : AppCompatActivity() {
    private val ids by lazy { ActivityThemePageBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        ids.themeBackBtn.setOnDebounceListener {
            onBackPressed()
        }
        ids.themeSaveBtn.setOnDebounceListener {
            onBackPressed()
        }
    }
}