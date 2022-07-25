package com.GlobalCinemaRelease.sdc

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate

class SetTheme(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)

    fun checkTheme(){
        when (sharedPreferences.getString("ThemeName", "")) {
            "Light Theme" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark Theme" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "System Default" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}