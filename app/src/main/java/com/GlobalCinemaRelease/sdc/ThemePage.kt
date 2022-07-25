package com.GlobalCinemaRelease.sdc

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import com.GlobalCinemaRelease.sdc.databinding.ActivityThemePageBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store

class ThemePage : AppCompatActivity() {
    private val ids by lazy { ActivityThemePageBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        ids.themeBackBtn.setOnDebounceListener {
            onBackPressed()
        }
        val theme = sharedPreferences.getString("ThemeName","")
        ids.apply {
            when (theme) {
                "Light Theme" -> lightTheme.isChecked = true
                "Dark Theme" -> darkTheme.isChecked = true
                "System Default" -> systemDefault.isChecked = true
                else -> systemDefault.isChecked = true
            }
        }


        ids.themeSaveBtn.setOnClickListener {
           val themeID: Int = ids.radioGroup.checkedRadioButtonId
            val themeName = findViewById<Button>(themeID)

            when (themeName.text) {
                "Dark Theme" -> {
                    sharedPreferences.edit().apply{
                        putString("ThemeName", themeName.text.toString())
                        apply()
                    }
                    Store.theme = "theme"
                    SetTheme(this@ThemePage).checkTheme()
                }
                "Light Theme" -> {
                    sharedPreferences.edit().apply{
                        putString("ThemeName", themeName.text.toString())
                        apply()
                    }
                    Store.theme = "theme"
                    SetTheme(this@ThemePage).checkTheme()
                }
                "System Default" ->{
                    sharedPreferences.edit().apply{
                        putString("ThemeName", themeName.text.toString())
                        apply()
                    }
                    Store.theme = "theme"
                    SetTheme(this@ThemePage).checkTheme()
                }
            }
        }
    }
}