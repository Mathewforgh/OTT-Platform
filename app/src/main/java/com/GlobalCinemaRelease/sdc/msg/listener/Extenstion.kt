package com.GlobalCinemaRelease.sdc.msg.listener
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.widget.SwitchCompat

fun View.setOnDebounceListener(onClick: (View) -> Unit) {
    val debounceOnClickListener = object : DebounceClickListener() {
        override fun onDebounceClick(view: View) {
            onClick(view)
        }
    }
    setOnClickListener(debounceOnClickListener)
}

fun SwitchCompat.setOnCustomCheckedListener(
    value: Boolean,
    listener: CompoundButton.OnCheckedChangeListener
) {
    setOnCheckedChangeListener(null)
    isChecked = value
    setOnCheckedChangeListener(listener)
}